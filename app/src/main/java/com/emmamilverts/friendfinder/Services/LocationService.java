package com.emmamilverts.friendfinder.Services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;



public class LocationService extends Service {
    public static final String ACTION_GET_LOCATION = "ACTION_GET_LOCATION";
    public static final String ACTION_REQUEST_LOCATION_PERMISSION = "ACTION_REQUEST_LOCATION_PERMISSION";

    public static final String RESULT_LOCATION_OBJECT = "RESULT_LOCATION_OBJECT";
    public static final String RESULT_USER_ID = "RESULT_USER_ID";
    public Context mContext;
    private final IBinder mBinder = new LocalBinder();
    private FusedLocationProviderClient fusedLocationClient;

    public void setmContext(Context context) {
        this.mContext = context;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    public class LocalBinder extends Binder {
        public LocationService getService() {
            // Return object that can call public methods
            return LocationService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void getLocation(String userId) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(ACTION_REQUEST_LOCATION_PERMISSION);
            intent.putExtra(RESULT_USER_ID, userId);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        } else {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    Log.d("ButtonClick","InService");
                    Intent intent = new Intent(ACTION_GET_LOCATION);
                    intent.putExtra(RESULT_LOCATION_OBJECT, location);
                    intent.putExtra(RESULT_USER_ID, userId);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                }
            });
        }
    }
}
