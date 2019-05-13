package com.emmamilverts.friendfinder;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import java.util.concurrent.Executor;

public class LocationService extends Service {
    public static final String ACTION_GET_LOCATION = "ACTION_GET_LOCATION";

    public static final String RESULT_LOCATION_OBJECT = "RESULT_LOCATION_OBJECT";

    private final IBinder mBinder = new LocalBinder();
    private FusedLocationProviderClient fusedLocationClient;

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

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: This should be called from an Activity based upon the location

            /* ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_LOCATION);
           */

            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener((Executor) this, location -> {
            if (location != null){
                Intent intent = new Intent(ACTION_GET_LOCATION);
                intent.putExtra(RESULT_LOCATION_OBJECT, location);
                sendBroadcast(intent);
            }
        });
    }
}
