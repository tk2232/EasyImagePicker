package com.example.sebastianesau.camera;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileHelper {

    /**
     * Coniguration
     */
    private static String EXTERNAL_FOLDER_PATH_DEFAULT;
    private static String IMAGE_FILE_NAME_DEFAULT;
    /**
     * DIRECTORY_MUSIC
     * DIRECTORY_PODCASTS
     * DIRECTORY_RINGTONES
     * DIRECTORY_ALARMS
     * DIRECTORY_NOTIFICATIONS
     * DIRECTORY_PICTURES damit gibt es probleme beim auslesen onActivityResult
     * DIRECTORY_MOVIES
     * DIRECTORY_DOWNLOADS
     * DIRECTORY_DCIM
     * DIRECTORY_DOCUMENTS
     */
    private static String ENVIRONMENT_DEFAULT = Environment.DIRECTORY_DCIM;

    private static String folderPath;
    private static String imageFileName;
    private static String environment;

    private static boolean writeToExternalStorrage = true;
    private static Context context;

    private static Configuration configuration;


    private static Configuration getConfiguration(Context context) {
        return new Configuration(context);
    }

    private static void checkConfiguration(Context context) {
        if (configuration == null) {
            getConfiguration(context);
        }
    }


    public static File getImageFile(Context context, String TAG) {
        checkConfiguration(context);
        try {
            // Create an image file name
            if (writeToExternalStorrage) {
                if (isExternalStorageReadable() && isExternalStorageWritable()) {
                    File image = File.createTempFile(getImageFileName(), ".jpg", createExternalPublicFolder(TAG));
                    return image;
                } else {
                    //TODO read/write error
                }
            } else {

            }
        } catch (Exception io) {
            //TODO
            Log.e(TAG, io.getMessage(), io);
            io.printStackTrace();
        }
        return null;
    }

    /**
     * Erstellt einen Ordner auf dem externen Speicher falls dieser noch nicht exestiert
     *
     * @return FolderPath
     */
    private static File createExternalPublicFolder(String TAG) {
        //Ref. https://stackoverflow.com/questions/22366217/cant-create-folder-on-external-storage-on-android
//        File file = new File(Environment.getExternalStorageDirectory() + File.separator + FOLDER_PATH);
        File file = new File(Environment.getExternalStoragePublicDirectory(environment) + File.separator + folderPath);

        if (!file.exists()) {
            Log.d(TAG, "Folder doesn't exist, creating it...");
            boolean rv = file.mkdir();
            Log.d(TAG, "Folder creation " + (rv ? "success" : "failed"));
        } else {
            Log.d(TAG, "Folder already exists.");
        }
        return file;
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    //TODO einbauen

    /**
     * Test if we can open the given Android URI to test if permission required error is thrown.<br>
     * Only relevant for API version 23 and above.
     *
     * @param context used to access Android APIs, like content resolve, it is your
     *                activity/fragment/widget.
     * @param uri     the result URI of image pick.
     */
    public static boolean isUriRequiresPermissions(@NonNull Context context, @NonNull Uri uri) {
        try {
            ContentResolver resolver = context.getContentResolver();
            InputStream stream = resolver.openInputStream(uri);
            if (stream != null) {
                stream.close();
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public static String getImageFileName() {
        return (imageFileName == null || imageFileName.isEmpty()) ? getDefaultImageFileName() : imageFileName;
    }

    public static String getDefaultImageFileName() {
        return IMAGE_FILE_NAME_DEFAULT;
    }

    public String getFolderPath() {
        return (folderPath == null || folderPath.isEmpty()) ? EXTERNAL_FOLDER_PATH_DEFAULT : folderPath;
    }

    public boolean isWriteToExternalStorrage() {
        return writeToExternalStorrage;
    }


    public static final class Configuration {

        private Configuration(Context context) {
            FileHelper.context = context;
            EXTERNAL_FOLDER_PATH_DEFAULT = ((Activity) context).getText(R.string.app_name).toString();
            folderPath = EXTERNAL_FOLDER_PATH_DEFAULT;

            defaultImageFileName();
            environment = ENVIRONMENT_DEFAULT;
        }

        public void defaultImageFileName() {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            IMAGE_FILE_NAME_DEFAULT = imageFileName;
        }

        public Configuration folderPath(String folderPath) {
            FileHelper.folderPath = folderPath;
            return this;
        }

        public Configuration imageFileName(String imageFileName) {
            FileHelper.imageFileName = imageFileName;
            return this;
        }

        public Configuration writeToExternalStorrage(boolean writeToExternalStorrage) {
            FileHelper.writeToExternalStorrage = writeToExternalStorrage;
            return this;
        }


        public Configuration environment(String environment) {
            FileHelper.environment = environment;
            return this;
        }
    }
}
