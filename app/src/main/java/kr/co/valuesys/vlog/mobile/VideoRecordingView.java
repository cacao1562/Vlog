package kr.co.valuesys.vlog.mobile;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

import kr.co.valuesys.vlog.mobile.common.LogUtil;

public class VideoRecordingView extends TextureView {

    int mRatioWidth = 0;
    int mRatioHeight = 0;

    public VideoRecordingView(Context context) {
        this(context, null);
    }
    public VideoRecordingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoRecordingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        LogUtil.d("aaa", "setAspectRatio  mRatioWidth = " + mRatioWidth + "  mRatioHeight  = " + mRatioHeight );
        LogUtil.d("aaa", "setAspectRatio  width = " + width + "  height  = " + height );
        mRatioWidth = width;
        mRatioHeight = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        LogUtil.d("aaa", "onMeasure  w = " + width + "  h  = " + height );

        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);
        } else {
            setMeasuredDimension(width, height);
//            if (width < height * mRatioWidth / mRatioHeight) {
//                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
//                LogUtil.d("aaa", "w < h  // w = " + width + "  h  = " + width * mRatioHeight / mRatioWidth );
//            } else {
//                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
//                LogUtil.d("aaa", "w > h //  w = " + height * mRatioWidth / mRatioHeight + "  h  = " + height );
//            }
        }
    }
}
