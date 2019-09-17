package kr.co.valuesys.vlog.mobile.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kr.co.valuesys.vlog.mobile.R;
import kr.co.valuesys.vlog.mobile.databinding.FragmentCameraBinding;


public class CameraFragment extends Fragment implements View.OnClickListener, MediaRecorder.OnInfoListener {

    private static final String TAG = "VideoFragment";
    //Superbrain 대신 원하는 폴더이름을 만들면 됩니다.
    private static final String DETAIL_PATH = "DCIM/TestVideo/";

    FragmentCameraBinding binding;

    // 카메라 광각, 전면, 후면
    private static final String CAM_WHAT = "2";
    private static final String CAM_FRONT = "1";
    private static final String CAM_REAR = "0";

    private String mCamId;

    CameraCaptureSession mCameraCaptureSession;
    CameraDevice mCameraDevice;
    CameraManager mCameraManager;

    Size mVideoSize;
    Size mPreviewSize;
    CaptureRequest.Builder mCaptureRequestBuilder;

    int mSensorOrientation;

    Semaphore mSemaphore = new Semaphore(1);

    HandlerThread mBackgroundThread;
    Handler mBackgroundHandler;

    MediaRecorder mMediaRecorder;

    private String mNextVideoAbsolutePath;
    private boolean mIsRecordingVideo;

    public static CameraFragment newInstance() {
        return new CameraFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_camera, container, false);
        mCamId = CAM_REAR;
        mCompositeDisposable = new CompositeDisposable();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.pictureBtn.setOnClickListener(this);
        binding.switchImgBtn.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        assert getActivity() != null;
    }

    @Override
    public void onResume() {
        super.onResume();

        startBackgroundThread();
        if (binding.preview.isAvailable()) {
            openCamera(binding.preview.getWidth(), binding.preview.getHeight());
        } else {
            binding.preview.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    @Override
    public void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera(binding.preview.getWidth(), binding.preview.getHeight());
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            Log.d("aaa", "onSurfaceTextureSizeChanged  w = " + width + "  h  = " + height );
            if(!mIsRecordingVideo) configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mCameraDevice = camera;
            startPreview();
            mSemaphore.release();
            if (null != binding.preview) {
                configureTransform(binding.preview.getWidth(), binding.preview.getHeight());
            }

        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            mSemaphore.release();
            camera.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            mSemaphore.release();
            camera.close();
            mCameraDevice = null;
            Activity activity = getActivity();
            assert activity != null;
            activity.finish();
        }
    };


    //카메라 기능 호출
    private void openCamera(int width, int height) {
        Activity activity = getActivity();
        assert activity != null;
        mCameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!mSemaphore.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            CameraCharacteristics cc = mCameraManager.getCameraCharacteristics(mCamId);
            StreamConfigurationMap scm = cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            mSensorOrientation = cc.get(CameraCharacteristics.SENSOR_ORIENTATION);
            if (scm == null) {
                throw new RuntimeException("Cannot get available preview/video sizes");
            }

            mVideoSize = chooseVideoSize(scm.getOutputSizes(MediaRecorder.class));
            mPreviewSize = chooseOptimalSize2(scm.getOutputSizes(SurfaceTexture.class), width, height, mVideoSize);
//            mPreviewSize = getOptimalSize(scm.getOutputSizes(SurfaceTexture.class), width, height);

            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                binding.preview.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            } else {
                binding.preview.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
            }

            configureTransform(width, height);
            mMediaRecorder = new MediaRecorder();
            mCameraManager.openCamera(mCamId, mStateCallback, mBackgroundHandler);
        } catch (CameraAccessException | SecurityException | NullPointerException | InterruptedException e) {
            e.printStackTrace();
            activity.finish();
        }

    }

    private static Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
// 해상도에 맞게 설정하면 될듯?
            Log.d(TAG, " video size ==  w : " + size.getWidth() + "  h : " + size.getHeight() );
            if(size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080){
                return size;
            }
        }
        return choices[choices.length - 1];
    }

    private static Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio) {
        List<Size> bigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        Log.d(TAG, " chooseOptimalSize size ==  w : " +w + "  h : " + h );
        for (Size ops : choices) {

            Log.d(TAG, " SurfaceTexture size ==  w : " + ops.getWidth() + "  h : " + ops.getHeight() );

            if(ops.getHeight() == ops.getWidth() * h / w && ops.getWidth() >= width && ops.getHeight() >= height) {
                bigEnough.add(ops);
            }
        }

        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            return choices[0];
        }
    }

    private static Size chooseOptimalSize2(Size[] choices, int width, int height, Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<Size>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        double ratio = (double) h / w;
        for (Size option : choices) {
            double optionRatio = (double) option.getHeight() / option.getWidth();
            if (ratio == optionRatio) {
                bigEnough.add(option);
            }
        }

        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[1];
        }
    }

    private Size getOptimalSize(Size[] sizes, int w, int h) {

        Log.d(TAG, " getOptimalSize size ==  w : " + w + "  h : " + h );
        final double ASPECT_TOLERANCE = 0.2;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;
        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = h;
        // Try to find an size match aspect ratio and size
        for (Size size : sizes)
        {
//          Log.d("CameraActivity", "Checking size " + size.width + "w " + size.height + "h");
            double ratio = (double) size.getWidth() / size.getHeight();
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.getHeight() - targetHeight) < minDiff)
            {
                optimalSize = size;
                minDiff = Math.abs(size.getHeight() - targetHeight);
            }
        }
        // Cannot find the one match the aspect ratio, ignore the requirement

        if (optimalSize == null)
        {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.getHeight() - targetHeight) < minDiff)
                {
                    optimalSize = size;
                    minDiff = Math.abs(size.getHeight() - targetHeight);
                }
            }
        }
        Log.d(TAG, " getOptimalSize size return ==  w : " + optimalSize.getWidth() + "  h : " + optimalSize.getHeight() );
        return optimalSize;
//        SharedPreferences previewSizePref;
//        if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
//            previewSizePref = getSharedPreferences("PREVIEW_PREF",MODE_PRIVATE);
//        } else {
//            previewSizePref = getSharedPreferences("FRONT_PREVIEW_PREF",MODE_PRIVATE);
//        }
//
//        SharedPreferences.Editor prefEditor = previewSizePref.edit();
//        prefEditor.putInt("width", optimalSize.width);
//        prefEditor.putInt("height", optimalSize.height);
//
//        prefEditor.commit();

//      Log.d("CameraActivity", "Using size: " + optimalSize.width + "w " + optimalSize.height + "h");

    }


    // 카메라 닫기
    private void closeCamera() {
        try {
            mSemaphore.acquire();
            closePreviewSession();
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mMediaRecorder) {
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } finally {
            mSemaphore.release();
        }
    }

    //미리보기 기능
    private void startPreview() {
        if(null == mCameraDevice || !binding.preview.isAvailable() || null == mPreviewSize) {
            return;
        }
        try {
            closePreviewSession();
            SurfaceTexture texture = binding.preview.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            Surface previewSurface = new Surface(texture);
            mCaptureRequestBuilder.addTarget(previewSurface);
            mCameraDevice.createCaptureSession(Collections.singletonList(previewSurface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    mCameraCaptureSession = session;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Activity activity = getActivity();
                    assert activity != null;
                    Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        if (null == mCameraDevice) {
            return;
        }
        try {
            setUpCaptureRequestBuilder(mCaptureRequestBuilder);
            HandlerThread thread = new HandlerThread("CameraPreview");
            thread.start();
            mCameraCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setUpCaptureRequestBuilder(CaptureRequest.Builder builder) {
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
    }


    private void configureTransform(int viewWidth, int viewHeight) {
        Log.d("aaa", "configureTransform  w = " + viewWidth + "  h  = " + viewHeight );
        Activity activity = getActivity();
        if (null == binding.preview || null == mPreviewSize || null == activity) {
            return;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getWidth(), mPreviewSize.getHeight());

        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();

        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth()
            );
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        activity.runOnUiThread(() -> binding.preview.setTransform(matrix));
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closePreviewSession() {
        if (mCameraCaptureSession != null) {
            mCameraCaptureSession.close();
            mCameraCaptureSession = null;
        }
    }

    private static final int SENSOR_ORIENTATION_DEFAULT_DEGREES = 90;
    private static final int SENSOR_ORIENTATION_INVERSE_DEGREES = 270;
    private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();
    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();
    static {
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    static {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }

    //영상녹화 설정
    private void setUpMediaRecorder() throws IOException {
        final Activity activity = getActivity();
        if(null == activity) return;

        if (mNextVideoAbsolutePath == null || mNextVideoAbsolutePath.isEmpty()) {
            mNextVideoAbsolutePath = getVideoFilePath();
        }

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
//        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);



        mMediaRecorder.setOutputFile(mNextVideoAbsolutePath);
//        mMediaRecorder.setVideoEncodingBitRate(10000000);
//        mMediaRecorder.setVideoFrameRate(30);

        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

//        mMediaRecorder.setMaxDuration(5000);
//        mMediaRecorder.setOnInfoListener(this);
//        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
//        mMediaRecorder.setVideoSize(displayMetrics.widthPixels, displayMetrics.heightPixels);

//        mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
//
//        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
//        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        switch (mSensorOrientation) {
            case SENSOR_ORIENTATION_DEFAULT_DEGREES:
                mMediaRecorder.setOrientationHint(DEFAULT_ORIENTATIONS.get(rotation));
                break;
            case SENSOR_ORIENTATION_INVERSE_DEGREES:
                mMediaRecorder.setOrientationHint(INVERSE_ORIENTATIONS.get(rotation));
                break;
        }
        mMediaRecorder.prepare();
    }

    //파일 이름 및 저장경로를 만듭니다.
    private String getVideoFilePath() {

        final File dir = Environment.getExternalStorageDirectory().getAbsoluteFile();
        String path = dir.getPath() + "/" + DETAIL_PATH;
        File dst = new File(path);
        if(!dst.exists()) dst.mkdirs();
        return path + System.currentTimeMillis() + ".mp4";
    }

    //녹화시작
    private void startRecordingVideo() {
        if (null == mCameraDevice || !binding.preview.isAvailable() || null == mPreviewSize) {
            return;
        }
        assert getActivity() != null;

        try {
            closePreviewSession();
            setUpMediaRecorder();
            SurfaceTexture texture = binding.preview.getSurfaceTexture();
            assert texture != null;
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);

            List<Surface> surfaces = new ArrayList<>();

            Surface previewSurface = new Surface(texture);
            surfaces.add(previewSurface);
            mCaptureRequestBuilder.addTarget(previewSurface);

            Surface recordSurface = mMediaRecorder.getSurface();
            surfaces.add(recordSurface);
            mCaptureRequestBuilder.addTarget(recordSurface);

            mCameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    mCameraCaptureSession = session;
                    updatePreview();
                    getActivity().runOnUiThread(() -> {
                        binding.pictureBtn.setText(R.string.stop);
                        mIsRecordingVideo = true;

                        mMediaRecorder.start();
                    });
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Activity activity = getActivity();
                    if (null != activity) {
                        Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }, mBackgroundHandler);
            timer();
        } catch (CameraAccessException | IOException e) {
            e.printStackTrace();
        }
    }

    //녹화 중지
    private void stopRecordingVideo() {
        mIsRecordingVideo = false;
        binding.pictureBtn.setText(R.string.record);
        mMediaRecorder.stop();
        mMediaRecorder.reset();

        Activity activity = getActivity();
        if (null != activity) {
            Toast.makeText(activity, "Video saved: " + mNextVideoAbsolutePath,
                    Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Video saved: " + mNextVideoAbsolutePath);
            File file = new File(mNextVideoAbsolutePath);
// 아래 코드가 없으면 갤러리 저장 적용이 안됨.
            if(!file.exists()) file.mkdir();
            getActivity().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        }
        mNextVideoAbsolutePath = null;
        stop();
        startPreview();
    }

    //카메라 전, 후, 광각 변경
// 본인 카메라에 맞게 적용하면 됨.
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.pictureBtn:
                if (mIsRecordingVideo) stopRecordingVideo();
                else startRecordingVideo();
                break;
            case R.id.switchImgBtn:
                switch (mCamId) {
                    case CAM_REAR:
                        mCamId = CAM_FRONT;
                        break;
                    case CAM_FRONT:
                        mCamId = CAM_WHAT;
                        break;
                    case CAM_WHAT:
                        mCamId = CAM_REAR;
                        break;
                }
                closeCamera();
                openCamera(binding.preview.getWidth(), binding.preview.getHeight());
                break;
        }
    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {

        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
            Log.d("VIDEOCAPTURE","Maximum Duration Reached");
//            mr.stop();
            stopRecordingVideo();
        }
    }

    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() - (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    // 여기부터
//녹화시간 카운트 시작
    private void timer() {
        binding.recordTimeTxtView.setVisibility(View.VISIBLE);
        Log.e("timer()", "started");
        Observable<Long> duration = Observable.interval(1, TimeUnit.SECONDS)
                .map(sec -> sec += 1);
        Disposable disposable = duration.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(timeout -> {

//                    if (timeout == 15) {
//                        stopRecordingVideo();
//                        return;
//                    }
                    Log.d("aaa", "timeout = " + timeout );
                    long min = timeout / 60;
                    long sec = timeout % 60;
                    String sMin;
                    String sSec;
                    if (min < 10) sMin = "0" + min;
                    else sMin = String.valueOf(min);

                    if (sec < 10) sSec = "0" + sec;
                    else sSec = String.valueOf(sec);

                    String elapseTime = sMin + ":" + sSec;
                    binding.recordTimeTxtView.setText(elapseTime);

//                    Log.d("aaa", "sec = " + sec );
//                    if (sec == 15) {
//                        stopRecordingVideo();
//                    }
                });
        mCompositeDisposable.add(disposable);

    }

    //녹화시간 카운트 정지
    private void stop() {
        binding.recordTimeTxtView.setVisibility(View.INVISIBLE);
        if (!mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
        }
    }
    CompositeDisposable mCompositeDisposable;
// 여기까지는 타이머 부분이기 때문에 사용안하셔도 됩니다.

    private Timer timer;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (!mCompositeDisposable.isDisposed())
            mCompositeDisposable.dispose();

        if(timer != null){
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }


    private void startTimer() {
        timer = new Timer();                  // 3초 후에 3초 간격으로 실행
        timer.scheduleAtFixedRate(new SliderTimer(), 1000, 1000);
    }

    private int time = 0;

    private class SliderTimer extends TimerTask {

        @Override
        public void run() {

            time += 1;
        }
    }
}


