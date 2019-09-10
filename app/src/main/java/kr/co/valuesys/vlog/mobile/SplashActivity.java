package kr.co.valuesys.vlog.mobile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private static final int Permission_Request_Code = 1000;

    private boolean isPermission = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        requestPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case Permission_Request_Code:

                for (int i=0; i<grantResults.length; i++) {

                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {

                        isPermission = false;
                        Toast.makeText(this,"permission not granted = " + permissions[i], Toast.LENGTH_LONG).show();
                        Log.d(TAG, "permission not granted = " + permissions[i] );
                    }
                }

                if (isPermission) {
                    presentMain();
                }
                break;
        }

    }

    private void requestPermission() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions(this
                    , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE
                            , Manifest.permission.CAMERA
                            , Manifest.permission.RECORD_AUDIO}
                    , Permission_Request_Code);
        }else {

            presentMain();
        }
    }


    private void presentMain() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
