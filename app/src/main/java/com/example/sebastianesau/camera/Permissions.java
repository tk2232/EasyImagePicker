package com.example.sebastianesau.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class Permissions {

    /**
     * TODO Permissions muss komplett überarbeitet werden und allgemein gestalltet werden um alle Permissions in einer Klasse abfragen zu können
     */

    public static final int REQUEST_EXTERNAL_STORAGE = 0;
    public static final int REQUEST_READ_EXTERNAL_STORAGE = 1;
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    public static final int REQUEST_READ_CONTACTS = 3;
    public static final int REQUEST_CAMERA = 4;
    public static final int REQUEST_CAMERA_AND_EXTERNAL_STORRAGE = 5;

    public static boolean needPermissionRequestStorrage(Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            String[] permissions = getCameraStorragePermissionString(context);
            boolean needPermission = false;
            for (String permission : permissions) {
                int permissionState = ActivityCompat.checkSelfPermission(context, permission);
                if (permissionState != PackageManager.PERMISSION_GRANTED) {
                    needPermission = true;
                }
            }
            return needPermission;
        } else {
            return false;
        }
    }

    public static void requestCameraStorragePermission(Context context) {
        String toast = context.getText(R.string.permission_external_storrage).toString();
        String[] permissions = getCameraStorragePermissionString(context);
        requestPermissions(context, permissions, toast, REQUEST_CAMERA_AND_EXTERNAL_STORRAGE);
    }

    public static void requestPermissions(Context context, String[] permissions, String toast, int PERMISSION_REQUEST) {
        boolean shouldProvideRationale = true;
        for (String permission : permissions) {
            // Should we show an explanation?
            shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission);
        }
        if (shouldProvideRationale) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            Toasty.info(context, toast, Toast.LENGTH_LONG, true).show();
        }
        // MY_PERMISSIONS_REQUEST is an
        // app-defined int constant. The callback method gets the
        // result of the request.
        ActivityCompat.requestPermissions((Activity) context, permissions, PERMISSION_REQUEST);
    }

    /**
     * Check if explicetly requesting camera permission is required.<br>
     * It is required in Android Marshmellow and above if "CAMERA" permission is requested in the
     * manifest.<br>
     * See <a
     * href="http://stackoverflow.com/questions/32789027/android-m-camera-intent-permission-bug">StackOverflow
     * question</a>.
     */
    private static boolean isExplicitCameraPermissionRequired(@NonNull Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && context.checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED;
    }


    private static String[] getCameraStorragePermissionString(Context context) {
        if (isExplicitCameraPermissionRequired(context)) {
            return new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
        } else {
            return new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }
    }

    /**
     * Check if the app requests a specific permission in the manifest.
     *
     * @param permissionName the permission to check
     * @return true - the permission in requested in manifest, false - not.
     */
    private static boolean hasPermissionInManifest(
            @NonNull Context context, @NonNull String permissionName) {
        String packageName = context.getPackageName();
        try {
            PackageInfo packageInfo =
                    context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            final String[] declaredPermisisons = packageInfo.requestedPermissions;
            if (declaredPermisisons != null && declaredPermisisons.length > 0) {
                for (String p : declaredPermisisons) {
                    if (p.equalsIgnoreCase(permissionName)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
        return false;
    }

    public static void showSnackbar(final Context context, Snackbar snackbar, final int actionTitleID, final int actionDescriptionID) {
        Log.i("maps_workflow", "showSnackbar");
        //Wenn eine Snackbar schon angezeigt wird soll die alte geschlossen werden und eine neue erstellt werden.
        if (snackbar != null) {
            snackbar.dismiss();
        }
        snackbar = Snackbar.make(((Activity) context).findViewById(android.R.id.content), ((Activity) context).getString(actionDescriptionID), Snackbar.LENGTH_INDEFINITE);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Build intent that displays the App settings screen.
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                intent.setData(uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ((Activity) context).startActivity(intent);
            }
        };
        snackbar.setAction(((Activity) context).getString(actionTitleID), onClickListener).show();
    }
}
