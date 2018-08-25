package com.example.sebastianesau.camera;

import android.app.Activity;
import android.content.ClipData;
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
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CameraHelper {

    private final static int PICK_IMAGE_REQUEST = 0;
    private final static int PICK_IMAGE_MULTIPLE_REQUEST = 1;

    private static final String CAPTURE_IMAGE_FILE_PROVIDER = "com.example.sebastianesau.fileprovider";

    private static File file;
    private static CharSequence title = "";
    private static boolean includeCamera = false;
    private static boolean includeDocuments = false;
    private static boolean includeMultipleSelect = false;
    private static boolean copyPickedImagesToPublicGallery = true;

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

    public static void start(@NonNull Activity activity) {
        try {
            activity.startActivityForResult(getPickImageChooserIntent(), PICK_IMAGE_REQUEST);
        } catch (IOException io) {
            //TODO
            Log.e(activity.getClass().getSimpleName(), io.getMessage(), io);
        }
    }

    /**
     * Create a chooser intent to select the source to get image from.<br>
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br>
     * All possible sources are added to the intent chooser.
     * <p>
     * <p>
     * activity/fragment/widget.
     */
    private static Intent getPickImageChooserIntent() throws IOException {
        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();

        if (includeCamera) {
            List<Intent> cameraIntents = getCameraIntents(packageManager);
            if (cameraIntents != null) {
                allIntents.addAll(cameraIntents);
            }
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
    private static List<Intent> getCameraIntents(@NonNull PackageManager packageManager) throws IOException {
        List<Intent> allIntents = new ArrayList<>();

        // Determine Uri of camera image to  save.
//        Uri outputFileUri = getCaptureImageOutputUri();
//        Uri imageUri = FileProvider.getUriForFile(context, "your/path/", FileHelper.getFileFromProvider(context));

        File imagePath = FileHelper.getCameraPicturesLocation(context);
        Uri uri = FileHelper.getUriToFile(context, imagePath);


        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (uri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
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
    private static Uri getCaptureImageOutputUri() throws IOException, NullPointerException {
        file = FileHelper.tempImageDirectory(context);
        try {
            Uri uri = Uri.fromFile(file);
            return uri;
        } catch (NullPointerException e) {
            //TODO
            Log.e(context.getClass().getSimpleName(), e.getMessage(), e);
            return null;
        }
    }

    public static boolean deleteFile() {
        return FileHelper.removeFile(file);
    }

    /**
     * Get the URI of the selected image from {@link #getPickImageChooserIntent()}.<br>
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */
    public static Uri getPickImageResultUri(@Nullable Intent data) throws IOException {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera || data.getData() == null ? getCaptureImageOutputUri() : data.getData();
    }

    public static void handleActivityResult(int requestCode, int resultCode, Intent data, Activity activity, Callbacks callbacks) {
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == PICK_IMAGE_REQUEST && !isPhoto(data)) {
                    onPictureReturnedFromGallery(data, activity, callbacks);
                } else if (isPhoto(data)) {

                }
            } else {
                if (requestCode == PICK_IMAGE_REQUEST) {
                    callbacks.onCanceled(ImageSource.GALLERY, 0);
                } else {

                }
            }
        }
    }

    private static boolean isPhoto(Intent data) {
        return data == null || data.getData() == null && data.getClipData() == null;
    }

    private static void onPictureReturnedFromGallery(Intent data, Activity activity, @NonNull Callbacks callbacks) {
        try {
            ClipData clipData = data.getClipData();
            List<File> files = new ArrayList();
            if (clipData == null) {
                Uri uri = data.getData();
                File file = FileHelper.pickedExistingPicture(activity, uri);
                files.add(file);
            } else {
                for (int i = 0; i < clipData.getItemCount(); ++i) {
                    Uri uri = clipData.getItemAt(i).getUri();
                    File file = FileHelper.pickedExistingPicture(activity, uri);
                    files.add(file);
                }
            }

            if (copyPickedImagesToPublicGallery) {
                FileHelper.copyFilesInSeparateThread(activity, files);
            }

            callbacks.onImagesPicked(files, ImageSource.GALLERY, 0);
        } catch (Exception var8) {
            var8.printStackTrace();
            callbacks.onImagePickerError(var8, ImageSource.GALLERY, 0);
        }

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

        public void start() {
            CameraHelper.start(activity);
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

        public Configuration shouldCopyPickedImagesToPublicGalleryAppFolder(boolean copyPickedImagesToPublicGallery) {
            CameraHelper.copyPickedImagesToPublicGallery = copyPickedImagesToPublicGallery;
            return this;
        }

        public Configuration filePath(String filePath) {
            FileHelper.configuration(context).folderPath(filePath);
            return this;
        }

        public Configuration imageFileName(String fileName) {
            FileHelper.configuration(context).imageFileName(fileName);
            return this;
        }

        public Configuration writeToExternalStorrage(boolean writeToExternalStorrage) {
            FileHelper.configuration(context).writeToExternalStorrage(writeToExternalStorrage);
            return this;
        }

        public Configuration environment(String environment) {
            FileHelper.configuration(context).environment(environment);
            return this;
        }

        public Configuration createTempFile(boolean createTempFile) {
            FileHelper.configuration(context).createTempFile(createTempFile);
            return this;
        }

        public Configuration suffix(String suffix) {
            FileHelper.configuration(context).suffix(suffix);
            return this;
        }

        public Configuration setAutoImageFileName(boolean autoImageFileName) {
            FileHelper.configuration(context).setAutoImageFileName(autoImageFileName);
            return this;
        }
    }

    public interface Callbacks {
        void onImagePickerError(Exception var1, ImageSource var2, int var3);

        void onImagesPicked(@NonNull List<File> var1, ImageSource var2, int var3);

        void onCanceled(ImageSource var1, int var2);
    }

    public static enum ImageSource {
        GALLERY,
        DOCUMENTS,
        CAMERA_IMAGE,
        CAMERA_VIDEO;

        private ImageSource() {
        }
    }

    public abstract static class DefaultCallback implements Callbacks {
        public DefaultCallback() {
        }

        public void onImagePickerError(Exception e, ImageSource source, int type) {
        }

        public void onCanceled(ImageSource source, int type) {
        }
    }
}
