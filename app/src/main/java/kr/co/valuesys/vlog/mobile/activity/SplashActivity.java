package kr.co.valuesys.vlog.mobile.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import kr.co.valuesys.vlog.mobile.R;
import kr.co.valuesys.vlog.mobile.common.FileManager;
import kr.co.valuesys.vlog.mobile.common.LogUtil;
import kr.co.valuesys.vlog.mobile.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";

    private ActivitySplashBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);

//        getHashKey();
        if (FileManager.checkStoragePermission(this)) {
            FileManager.deleteTempFiles(this);
        }

        binding.spLogo.postDelayed(() -> {

            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        }, 1000);

    }

    /**
     * 카카오 sdk 홈페이지에 입력 해야할 해시키값 , 페이스북도 마찬가지
     */
    private void getHashKey() {
        try {                                                        // 패키지이름을 입력해줍니다.
            PackageInfo info = getPackageManager().getPackageInfo("kr.co.valuesys.vlog.mobile", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                LogUtil.d(TAG, "key_hash=" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


}
