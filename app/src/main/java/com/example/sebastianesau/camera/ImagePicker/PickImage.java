package com.example.sebastianesau.camera.ImagePicker;

import android.app.Activity;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.sebastianesau.camera.Storrage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PickImage {

    /**
     * startActivityForResult requestCode
     */
    private final static int PICK_IMAGE_REQUEST = 0;

    /**
     * Defaults
     */
    private static CharSequence title = "";
    private static boolean includeCamera = false;
    private static boolean includeDocuments = false;
    private static boolean includeMultipleSelect = false;
    private static boolean copyPickedImagesToPublicGallery = true;

    private static File file;
    private static FileConfiguration fileConfiguration;
    private static Context context;

    /**
     * @param context used to access Android APIs, like content resolve, it is your
     *                activity/fragment/widget.
     */
    public PickImage(Context context) {
        this.context = context;
    }

    /**
     * Configuration Klasse ist unten eingefügt.
     *
     * @param activity um startActivityForResult aufrufen zu können
     * @return eine neue Coniguration entweder mit default Werten oder den Benutzerdaten
     */
    public static Configuration activity(Activity activity) {
        return new Configuration(activity);
    }

    /**
     * Diese Methode kann nur aus der Coniguration Klasse aufgerufen werden um zu gewährleisten
     * das die default configuration durchgeführt wurde.
     *
     * @param activity um startActivityForResult aufrufen zu können
     */
    private static void start(@NonNull Activity activity) {
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

        // Determine Uri of camera image to  save. (TempFile)
        File imagePath = FileHelper.getCameraTempFile(fileConfiguration);
        Uri uri = FileHelper.getUriToFile(fileConfiguration, imagePath);

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
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, includeMultipleSelect);
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
    private static Uri getCaptureImageOutputUri() throws IOException {
        file = FileHelper.getTempImageDirectory(fileConfiguration);
        Uri uri = Uri.fromFile(file);
        return uri;
    }

    /**
     * @return the romovestate
     */
    public static boolean deleteFile() {
        return FileHelper.removeFile(fileConfiguration, file);
    }

    /**
     * Get the URI of the selected image from {@link #getPickImageChooserIntent()}.<br>
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */
    @Deprecated
    public static Uri getPickImageResultUri(@Nullable Intent data) throws IOException {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera || data.getData() == null ? getCaptureImageOutputUri() : data.getData();
    }

    /**
     * Diese Methode wird in der aufrufenden Activity in onActivityResult aufgerufen damit kein
     * überladener Code entsteht.
     *
     * @param requestCode 0 == PICK_IMAGE_REQUEST
     * @param resultCode  -1 == RESULT_OK, 0 == RESULT_CANCELED
     * @param data        Result data
     * @param activity    calling activity
     * @param callbacks   Interface
     */
    public static void handleActivityResult(int requestCode, int resultCode, Intent data, Activity activity, Callbacks callbacks) {
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == PICK_IMAGE_REQUEST && !isPhoto(data)) {
                    onPictureReturnedFromGallery(data, activity, callbacks);
                } else if (isPhoto(data)) {
                    onPictureReturnedFromCamera(activity, callbacks);
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
                File file = FileHelper.pickedExistingPicture(fileConfiguration, uri);
                files.add(file);
            } else {
                for (int i = 0; i < clipData.getItemCount(); ++i) {
                    Uri uri = clipData.getItemAt(i).getUri();
                    File file = FileHelper.pickedExistingPicture(fileConfiguration, uri);
                    files.add(file);
                }
            }

            // Kopiere das File in den external storrage
            if (copyPickedImagesToPublicGallery) {
                FileHelper.copyFilesInSeparateThread(fileConfiguration, files);
            }

            callbacks.onImagesPicked(files, ImageSource.GALLERY, 0);
        } catch (Exception var8) {
            var8.printStackTrace();
            callbacks.onImagePickerError(var8, ImageSource.GALLERY, 0);
        }

    }

    private static void onPictureReturnedFromCamera(Activity activity, @NonNull Callbacks callbacks) {
        try {
            List<File> files = new ArrayList();
            File photoFile = FileHelper.getCameraTempFile(fileConfiguration);
            if (photoFile == null) {
                //TODO exception message
                callbacks.onImagePickerError(new Exception("Unable to get the picture returned from camera"), ImageSource.CAMERA_IMAGE, 0);
            }
            if (photoFile == null) {
                Exception e = new IllegalStateException("Unable to get the picture returned from camera");
                callbacks.onImagePickerError(e, ImageSource.CAMERA_IMAGE, 0);
            } else {
                files.add(photoFile);
                // Kopiere das File in den external storrage
                if (copyPickedImagesToPublicGallery) {
                    FileHelper.copyFilesInSeparateThread(fileConfiguration, files);
                }
                callbacks.onImagesPicked(files, ImageSource.CAMERA_IMAGE, 0);
            }
        } catch (NullPointerException npe) {
            callbacks.onImagePickerError(npe, ImageSource.CAMERA_IMAGE, 0);
        } catch (IllegalArgumentException ie) {
            callbacks.onImagePickerError(ie, ImageSource.CAMERA_IMAGE, 0);
        } catch (IOException io) {
            callbacks.onImagePickerError(io, ImageSource.CAMERA_IMAGE, 0);
        } catch (Exception e) {
            callbacks.onImagePickerError(e, ImageSource.CAMERA_IMAGE, 0);
        }
    }

    /**
     * Prüfe ob genug speicher auf dem internal und dem external storrage vorhanden ist und
     * genug Speicher verfügbar ist.
     *
     * @return
     */
    private static boolean checkSpace() {
        if (!Storrage.isExternalStorrageReady()) {
            return false;
        }
        int minStorragePercent = 10;
        if (freeStorrageInPercent(Storrage.getInternalStorrageSize(), Storrage.getFreeInternalStorrageSize()) < minStorragePercent) {
            return false;
        } else if (freeStorrageInPercent(Storrage.getExternalStorrageSize(), Storrage.getFreeExternalStorrageSize()) < minStorragePercent) {
            return false;
        } else {
            return true;
        }
    }

    private static double freeStorrageInPercent(double available, double free) {
        return (100 / available * free);
    }

    public static CharSequence getTitle() {
        return title;
    }

    /**
     * Hier kann die Default config geändert werden. In der Configuration Klasse wird auch auf die
     *
     * @FileHelper Configuration zugegriffen. In dieser Klasse kann der chooser gestartet werden.
     */
    public static final class Configuration {
        private Activity activity;
        private FileConfiguration fileConfiguration;

        public Configuration(@NonNull Activity activity) {
            this.activity = activity;
            context = activity;
            fileConfiguration = new FileConfiguration(context);
            PickImage.fileConfiguration = fileConfiguration;
        }

        /**
         * Starte das chooser Intent
         */
        public void start() {
            if (!checkSpace()) {
                //TODO string.xml
                Toast.makeText(context, "Test not enough space", Toast.LENGTH_LONG).show();
                return;
            }
            PickImage.start(activity);
        }

        /**
         * @param title Bootomsheet title
         * @return Configuration
         */
        public Configuration title(CharSequence title) {
            PickImage.title = title;
            return this;
        }

        public Configuration includeCamera(boolean includeCamera) {
            PickImage.includeCamera = includeCamera;
            return this;
        }

        public Configuration includeDocuments(boolean includeDocuments) {
            PickImage.includeDocuments = includeDocuments;
            return this;
        }

        /**
         * @param includeMultipleSelect erlaubt es mehrere Bilder aus der Gallerie auszuwählen.
         * @return
         */
        public Configuration includeMultipleSelect(boolean includeMultipleSelect) {
            PickImage.includeMultipleSelect = includeMultipleSelect;
            return this;
        }

        /**
         * @param copyPickedImagesToPublicGallery erlaubt es die Bilder auf den öffentlichen Ordner zu
         *                                        speichern damit andere Apps die Bilder finden können
         * @return
         */
        public Configuration shouldCopyPickedImagesToPublicGalleryAppFolder(boolean copyPickedImagesToPublicGallery) {
            PickImage.copyPickedImagesToPublicGallery = copyPickedImagesToPublicGallery;
            fileConfiguration.writeToExternalStorrage(copyPickedImagesToPublicGallery);
            return this;
        }

        /**
         * Default filePath = appname
         *
         * @param filePath
         * @return
         */
        public Configuration filePath(String filePath) {
            fileConfiguration.folderPath(filePath);
            return this;
        }

        /**
         * Default imageFilename suffix.toUpperCase() + "_" + timeStamp + "_" + suffix
         *
         * @param filename der filename kann manuell gewählt werden oder Automatisch erstellt werden
         * @return
         */
        public Configuration imageFilename(String filename) {
            fileConfiguration.imageFileName(filename);
            setAutoImageFileName(false);
            return this;
        }

        /**
         * imageFilename auto == suffix.toUpperCase() + "_" + timeStamp + "_" + suffix
         *
         * @param autoImageFileName
         * @return
         */
        public Configuration setAutoImageFileName(boolean autoImageFileName) {
            fileConfiguration.setAutoImageFileName(autoImageFileName);
            return this;
        }


        /**
         * defautl environment = DIRECTORY_DCIM
         *
         * @param environment DIRECTORY_MUSIC
         *                    DIRECTORY_PODCASTS
         *                    DIRECTORY_RINGTONES
         *                    DIRECTORY_ALARMS
         *                    DIRECTORY_NOTIFICATIONS
         *                    DIRECTORY_PICTURES damit gibt es probleme beim auslesen onActivityResult.
         *                    bei manchen devices ist der Zugriff nicht erlaubt. Besser
         *                    DIRECTORY_DCIM wählen
         *                    <p>
         *                    DIRECTORY_MOVIES
         *                    DIRECTORY_DOWNLOADS
         *                    DIRECTORY_DCIM
         *                    DIRECTORY_DOCUMENTS
         * @return
         */
        public Configuration environment(String environment) {
            fileConfiguration.environment(environment);
            return this;
        }

        /**
         * default suffix = .jpg
         *
         * @param suffix z.Bsp. .jpg, .PNG
         * @return
         */
        public Configuration suffix(String suffix) {
            fileConfiguration.suffix(suffix);
            return this;
        }
    }
}
