package com.example.sebastianesau.camera;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CameraHelper {

    private static File file;
    private static CharSequence title = "";
    private static boolean includeCamera = false;
    private static boolean includeDocuments = false;
    private static boolean includeMultipleSelect = false;

    private static Context context;

    /**
     * @param context used to access Android APIs, like content resolve, it is your
     *                activity/fragment/widget.
     */
    public CameraHelper(Context context) {
        this.context = context;
    }


    public static Configuration activity(Activity activity) {
        return new Configuration(activity);
    }

    public static void start(@NonNull Activity activity, @NonNull File file, int requestCode) {
        activity.startActivityForResult(getPickImageChooserIntent(file), requestCode);
    }

    /**
     * Create a chooser intent to select the source to get image from.<br>
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br>
     * All possible sources are added to the intent chooser.
     * <p>
     * <p>
     * activity/fragment/widget.
     *
     * @param file file to save the image
     */
    private static Intent getPickImageChooserIntent(File file) {
        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();

        if (includeCamera) {
            allIntents.addAll(getCameraIntents(packageManager, file));
        }

        List<Intent> galleryIntents = getGalleryIntents(packageManager, Intent.ACTION_GET_CONTENT);
        if (galleryIntents.size() == 0) {
            // if no intents found for get-content try pick intent action (Huawei P9).
            galleryIntents = getGalleryIntents(packageManager, Intent.ACTION_PICK);
        }
        allIntents.addAll(galleryIntents);
        Intent target;
        if (allIntents.isEmpty()) {
            target = new Intent();
        } else {
            target = allIntents.get(allIntents.size() - 1);
            allIntents.remove(allIntents.size() - 1);
        }

        // Create a chooser from the main  intent
        Intent chooserIntent = Intent.createChooser(target, title);

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    /**
     * Get all Camera intents for capturing image using device camera apps.
     */
    private static List<Intent> getCameraIntents(@NonNull PackageManager packageManager, File file) {
        CameraHelper.file = file;
        List<Intent> allIntents = new ArrayList<>();

        // Determine Uri of camera image to  save.
        Uri outputFileUri = getCaptureImageOutputUri();

        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        return allIntents;
    }

    /**
     * Get all Gallery intents for getting image from one of the apps of the device that handle
     * images.
     */
    private static List<Intent> getGalleryIntents(
            @NonNull PackageManager packageManager, String action) {
        List<Intent> intents = new ArrayList<>();
        Intent galleryIntent = action == Intent.ACTION_GET_CONTENT ? new Intent(action) : new Intent(action, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        if (includeMultipleSelect) {
            galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            intents.add(intent);
        }

        // remove documents intent
        if (!includeDocuments) {
            for (Intent intent : intents) {
                if (intent
                        .getComponent()
                        .getClassName()
                        .equals("com.android.documentsui.DocumentsActivity")) {
                    intents.remove(intent);
                    break;
                }
            }
        }
        return intents;
    }

    /**
     * Get URI to image received from capture by camera.
     */
    private static Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        if (file != null) {
            outputFileUri = Uri.fromFile(file);
        }
        return outputFileUri;
    }

    /**
     * Get URI to image received from capture by camera.
     */
    public static Uri getCaptureImageOutputUri(File file) {
        Uri outputFileUri = null;
        if (file != null) {
            outputFileUri = Uri.fromFile(file);
        }
        return outputFileUri;
    }

    /**
     * Get the URI of the selected image from {@link #getPickImageChooserIntent(File)}.<br>
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */
    public static Uri getPickImageResultUri(@Nullable Intent data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera || data.getData() == null ? getCaptureImageOutputUri() : data.getData();
    }

    public static CharSequence getTitle() {
        return title;
    }


    public static boolean isIncludeCamera() {
        return includeCamera;
    }


    public static boolean isIncludeDocuments() {
        return includeDocuments;
    }

    public static boolean isIncludeMultipleSelect() {
        return includeMultipleSelect;
    }

    public static final class Configuration {
        Activity activity;
        public Configuration(@NonNull Activity activity) {
            this.activity = activity;
            context = activity;
        }

        public void start(@NonNull File file, int requestCode) {
            CameraHelper.start(activity, file, requestCode);
        }

        public Configuration title(CharSequence title) {
            CameraHelper.title = title;
            return this;
        }

        public Configuration includeCamera(boolean includeCamera) {
            CameraHelper.includeCamera = includeCamera;
            return this;
        }

        public Configuration includeDocuments(boolean includeDocuments) {
            CameraHelper.includeDocuments = includeDocuments;
            return this;
        }

        public Configuration includeMultipleSelect(boolean includeMultipleSelect) {
            CameraHelper.includeMultipleSelect = includeMultipleSelect;
            return this;
        }
    }
}
