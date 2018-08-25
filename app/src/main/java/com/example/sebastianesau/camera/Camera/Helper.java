package com.example.sebastianesau.camera.Camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Helper {

    private static File file;
    private static CharSequence title = "";
    private static boolean includeCamera = false;
    private static boolean includeDocuments = false;
    private static boolean includeMultipleSelect = false;

    private static Context context;


//    private static void start(@NonNull Activity activity, int requestCode) {
//        activity.startActivityForResult(createChooserIntent(), requestCode);
//    }
//
//    private static Intent createChooserIntent() {
//        List<Intent> allIntents = new ArrayList<>();
//        PackageManager packageManager = context.getPackageManager();
//
//        if (includeCamera) {
//            List<Intent> cameraIntents = getCameraIntents(packageManager);
//            if (cameraIntents != null) {
//                allIntents.addAll(cameraIntents);
//            }
//        }
//    }
//
//    /**
//     * Get all Camera intents for capturing image using device camera apps.
//     */
//    private static List<Intent> getCameraIntents(PackageManager packageManager) {
//        List<Intent> allIntents = new ArrayList<>();
//
//        // Determine Uri of camera image to  save.
//        Uri outputFileUri = createCameraPictureFile(context);
//    }
//
//    private static Uri createCameraPictureFile(Context context) throws IOException {
//        File imagePath = EasyImageFiles.getCameraPicturesLocation(context);
//        String packageName = context.getApplicationContext().getPackageName();
//        String authority = packageName + ".easyphotopicker.fileprovider";
//        Uri uri = FileProvider.getUriForFile(context, authority, imagePath);
//        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
//        editor.putString(KEY_PHOTO_URI, uri.toString());
//        editor.putString(KEY_LAST_CAMERA_PHOTO, imagePath.toString());
//        editor.apply();
//        return uri;
//    }
//
//
//    public static final class Configuration {
//        Activity activity;
//
//        public Configuration(@NonNull Activity activity) {
//            this.activity = activity;
//            context = activity;
//        }
//
//        public void start(int requestCode) {
//            Helper.start(activity, requestCode);
//        }
//
//        public Configuration title(CharSequence title) {
//            Helper.title = title;
//            return this;
//        }
//
//        public Configuration includeCamera(boolean includeCamera) {
//            Helper.includeCamera = includeCamera;
//            return this;
//        }
//
//        public Configuration includeDocuments(boolean includeDocuments) {
//            Helper.includeDocuments = includeDocuments;
//            return this;
//        }
//
//        public Configuration includeMultipleSelect(boolean includeMultipleSelect) {
//            Helper.includeMultipleSelect = includeMultipleSelect;
//            return this;
//        }
//
//        public Configuration filePath(String filePath) {
//            FileHelper.configuration(context).folderPath(filePath);
//            return this;
//        }
//
//        public Configuration imageFileName(String fileName) {
//            FileHelper.configuration(context).imageFileName(fileName);
//            return this;
//        }
//
//        public Configuration writeToExternalStorrage(boolean writeToExternalStorrage) {
//            FileHelper.configuration(context).writeToExternalStorrage(writeToExternalStorrage);
//            return this;
//        }
//
//        public Configuration environment(String environment) {
//            FileHelper.configuration(context).environment(environment);
//            return this;
//        }
//
//        public Configuration createTempFile(boolean createTempFile) {
//            FileHelper.configuration(context).createTempFile(createTempFile);
//            return this;
//        }
//
//        public Configuration suffix(String suffix) {
//            FileHelper.configuration(context).suffix(suffix);
//            return this;
//        }
//
//        public Configuration setAutoImageFileName(boolean autoImageFileName) {
//            FileHelper.configuration(context).setAutoImageFileName(autoImageFileName);
//            return this;
//        }
//    }
//
//    public static Configuration activity(Activity activity) {
//        return new Configuration(activity);
//    }
}
