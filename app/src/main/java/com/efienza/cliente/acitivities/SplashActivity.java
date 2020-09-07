package com.efienza.cliente.acitivities;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;

import com.efienza.cliente.session.SessionManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.efienza.cliente.R;
import com.thebrownarrow.permissionhelper.ActivityManagePermission;
import com.thebrownarrow.permissionhelper.PermissionResult;
import com.thebrownarrow.permissionhelper.PermissionUtils;
/**
 * Created by android on 7/3/17.
 */
public class SplashActivity extends ActivityManagePermission {
    private static final String TAG = "SplashActivity";
    String permissionAsk[] = {
            //PermissionUtils.Manifest_CAMERA,
            //PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE,
            //PermissionUtils.Manifest_READ_EXTERNAL_STORAGE,
            //PermissionUtils.Manifest_SEND_SMS,
            PermissionUtils.Manifest_ACCESS_FINE_LOCATION,
            PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};
            //PermissionUtils.Manifest_CALL_PHONE};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //set content view AFTER ABOVE sequence (to avoid crash)
        setContentView(R.layout.splash_activity);
        int SPLASH_TIME_OUT = 2000;
        isGooglePlayServicesAvailable(SplashActivity.this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                askCompactPermissions(permissionAsk, new PermissionResult() {
                    @Override
                    public void permissionGranted() { changement(); }
                    @Override
                    public void permissionDenied() {
                        changement();
                    }
                    @Override
                    public void permissionForeverDenied() {
                        changement();
                    }
                });
            }
        }, SPLASH_TIME_OUT);

    }
    public void changement() {
        if (SessionManager.isLoggedIn()) {
            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
        } else {
            Intent i = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(i);
        }
        finish();
    }
    public boolean isGooglePlayServicesAvailable(Context context) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
        return resultCode == ConnectionResult.SUCCESS;
    }

    public boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        } else {
        }
        return true;
    }
}
