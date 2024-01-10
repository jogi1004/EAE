package com.example.eaeprojekt.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.eaeprojekt.R;

public class Shared {
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 10;
    //private static final int MY_PERMISSIONS_REQUEST_CAMERA = 11;
    public static final int MY_PERMISSIONS_REQUEST_READ_MEDIA_IMAGES = 12;


    public static boolean checkPermission(final Context context, boolean reminder)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>= Build.VERSION_CODES.TIRAMISU)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_MEDIA_IMAGES)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle(R.string.permissionNecessary);
                    alertBuilder.setMessage(R.string.pleaseAllowGalleryAccess);
                    alertBuilder.setPositiveButton(android.R.string.ok, (dialog, which) ->
                            // ask user to grant permission
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, MY_PERMISSIONS_REQUEST_READ_MEDIA_IMAGES)
                    );
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                }
                else if (reminder){
                    // ask the user to allow the permission in the settings
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle(R.string.permissionNecessary);
                    alertBuilder.setMessage(R.string.pleaseAllowGalleryAccessInSettings);
                    alertBuilder.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss());
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                }
                return false;
            } else {
                // Permission already granted
                return true;
            }
        } else {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_MEDIA_IMAGES)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle(R.string.permissionNecessary);
                    alertBuilder.setMessage(R.string.pleaseAllowStorageAccess);
                    alertBuilder.setPositiveButton(android.R.string.ok, (dialog, which) ->
                            // ask user to grant permission
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
                    );
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                }
                else {
                    // ask the user to allow the permission in the settings
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle(R.string.permissionNecessary);
                    alertBuilder.setMessage(R.string.pleaseAllowStorageAccessInSettings);
                    alertBuilder.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss());
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                }
                return false;
            } else {
                // Permission already granted
                return true;
            }
        }
    }

    public static int dpToPx(int dp, float density) {
        return Math.round(dp * density);
    }
}
