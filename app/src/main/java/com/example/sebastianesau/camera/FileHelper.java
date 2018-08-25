package com.example.sebastianesau.camera;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileHelper {

    private static final String TAG = FileHelper.class.getSimpleName();

    /**
     * Coniguration
     */
    private static String EXTERNAL_FOLDER_PATH_DEFAULT;
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
    private static final boolean CREATE_TEMP_FILE_DEFAULT = true;
    private static final String SUFFIX_DEFAULT = ".jpg";
    private static final boolean AUTO_IMAGE_FILE_NAME_DEFAULT = true;

    private static String folderPath;
    private static String imageFileName;
    private static boolean autoImageFileName;
    private static String environment;
    private static boolean createTempFile;
    private static String suffix;

    private static boolean writeToExternalStorrage = true;
    private static Context context;

    private static Configuration configuration;
    private static boolean hasConfig;

    public static Configuration configuration(Context context) {
        return configuration == null ? getConfiguration(context) : configuration;
    }

    private static Configuration getConfiguration(Context context) {
        return new Configuration(context);
    }

    public static File getImageFile(Context context) throws IOException {
        configuration(context);
        // Create an image file name
        if (writeToExternalStorrage) {
            if (isExternalStorageReadable() && isExternalStorageWritable()) {
                return createImageFile();
            } else {
                //TODO read/write error
            }
        } else {

        }
        return null;
    }

    public static File createImageFile() throws IOException {
        File folderPath = createExternalPublicFolder();
        if (folderPath == null) {
            return null;
        }
        if (createTempFile) {
            File image = File.createTempFile(getImageFileName(), suffix, createExternalPublicFolder());
            return image;
        } else {
            File image = new File(createExternalPublicFolder(), getImageFileName() + suffix);
            return image;
        }
    }


    /**
     * Erstellt einen Ordner auf dem externen Speicher falls dieser noch nicht exestiert
     *
     * @return FolderPath
     */
    private static File createExternalPublicFolder() {
        //Ref. https://stackoverflow.com/questions/22366217/cant-create-folder-on-external-storage-on-android
//        File file = new File(Environment.getExternalStorageDirectory() + File.separator + FOLDER_PATH);
        File file = new File(Environment.getExternalStoragePublicDirectory(environment) + File.separator + getFolderPath());

        if (!file.exists()) {
            Log.d(TAG, "Folder doesn't exist, creating it...");
            boolean rv = file.mkdir();
            Log.d(TAG, "Folder creation " + (rv ? "success" : "failed"));
//            if (!rv) {
//                return null;
//            }
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

    public static boolean removeFile(@NonNull File file) {
        try {
            if (file.exists()) {
                if (file.delete()) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (NullPointerException e) {
            //TODO
            Log.e(context.getClass().getSimpleName(), e.getMessage(), e);
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
        return autoImageFileName ? getDefaultImageFileName() : imageFileName;
    }

    private static String getDefaultImageFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return "JPEG_" + timeStamp + "_";
    }

    public static String getFolderPath() {
        return (folderPath == null || folderPath.isEmpty()) ? EXTERNAL_FOLDER_PATH_DEFAULT : folderPath;
    }

    public boolean isWriteToExternalStorrage() {
        return writeToExternalStorrage;
    }

    public static boolean isCreateTempFile() {
        return createTempFile;
    }

    public static final class Configuration {

        private Configuration(Context context) {
            FileHelper.context = context;
            if (!hasConfig) {
                //TODO den anhang bei FolderPath ändern
                EXTERNAL_FOLDER_PATH_DEFAULT = ((Activity) context).getText(R.string.app_name).toString() + "\\Test";
                folderPath = EXTERNAL_FOLDER_PATH_DEFAULT;
                environment = ENVIRONMENT_DEFAULT;
                createTempFile = CREATE_TEMP_FILE_DEFAULT;
                suffix = SUFFIX_DEFAULT;
                autoImageFileName = AUTO_IMAGE_FILE_NAME_DEFAULT;
                hasConfig = true;
            }
        }

        public Configuration folderPath(String folderPath) {
            FileHelper.folderPath = folderPath;
            return this;
        }

        public Configuration imageFileName(String imageFileName) {
            FileHelper.imageFileName = imageFileName;
            FileHelper.autoImageFileName = true;
            return this;
        }

        public Configuration setAutoImageFileName(boolean autoImageFileName) {
            FileHelper.autoImageFileName = autoImageFileName;
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

        public Configuration createTempFile(boolean createTempFile) {
            FileHelper.createTempFile = createTempFile;
            return this;
        }

        public Configuration suffix(String suffix) {
            FileHelper.suffix = suffix;
            return this;
        }
    }
}
