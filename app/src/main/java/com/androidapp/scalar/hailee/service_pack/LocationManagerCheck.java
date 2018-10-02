package com.androidapp.scalar.hailee.service_pack;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

/**
 * Created by pasin on 10/2/2018.
 */

public class LocationManagerCheck {

    LocationManager locationManager;
    Boolean locationServiceBoolean = false;
    int providerType = 0;
    AlertDialog alert;

    public LocationManagerCheck(Context context) {
        locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        boolean gpsIsEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkIsEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (networkIsEnabled == true && gpsIsEnabled == true) {
            locationServiceBoolean = true;
            providerType = 1;

        } else if (networkIsEnabled != true && gpsIsEnabled == true) {
            locationServiceBoolean = true;
            providerType = 2;

        } else if (networkIsEnabled == true && gpsIsEnabled != true) {
            locationServiceBoolean = true;
            providerType = 1;
        }

    }


    public Boolean isLocationServiceAvailable() {
        return locationServiceBoolean;
    }

    public int getProviderType() {
        return providerType;
    }

    public void createLocationServiceError(final Activity activityObj) {

        // show alert dialog if Internet is not connected
        AlertDialog.Builder builder = new AlertDialog.Builder(activityObj);

        builder.setMessage(
                "Please enable location services from your settings")
                .setTitle("Location Update Failed")
                .setCancelable(false)
                .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                activityObj.startActivity(intent);
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
        alert = builder.create();
        alert.show();
    }

}
