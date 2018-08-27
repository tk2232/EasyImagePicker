package com.example.sebastianesau.camera.ImagePicker;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.example.sebastianesau.camera.R;

public class FileConfiguration {

    private static String TAG;

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
    private static final String ENVIRONMENT_DEFAULT = Environment.DIRECTORY_DCIM;
    private static final boolean CREATE_TEMP_FILE_DEFAULT = true;
    private static final String SUFFIX_DEFAULT = ".jpg";
    private static final boolean AUTO_IMAGE_FILE_NAME_DEFAULT = true;
    private static String INERNAL_IMAGE_TEMP_FILENAME_DEFAULT;
    private static final String PRIVATE_TEMP_FILE_PATH_CHILD_DEFAULT = "ImageTemp";
    private static final boolean WRITE_TO_EXTERNAL_STORRAGE_DEFAULT = true;

    private static final String CAPTURE_IMAGE_FILE_PROVIDER = "com.example.sebastianesau.fileprovider";
    private static String folderPath;
    private static String environment;
    private static boolean createTempFile;
    private static boolean autoImageFileName;
    private static String internalImageFilename;
    private static String privateTempFilePathChild;
    private static String imageFileName;
    private static String suffix;
    private static boolean writeToExternalStorrage;

    private Context context;

    public FileConfiguration(@NonNull Context context) {
        this.context = context;
        TAG = context.getClass().getSimpleName();
        //TODO den anhang bei FolderPath ändern
        EXTERNAL_FOLDER_PATH_DEFAULT = ((Activity) context).getText(R.string.app_name).toString() + "_Test";
        INERNAL_IMAGE_TEMP_FILENAME_DEFAULT = context.getClass().getSimpleName();
        setDefaultConfig();
    }

    public void setDefaultConfig() {
        folderPath = EXTERNAL_FOLDER_PATH_DEFAULT;
        environment = ENVIRONMENT_DEFAULT;
        createTempFile = CREATE_TEMP_FILE_DEFAULT;
        autoImageFileName = AUTO_IMAGE_FILE_NAME_DEFAULT;
        internalImageFilename = INERNAL_IMAGE_TEMP_FILENAME_DEFAULT;
        privateTempFilePathChild = PRIVATE_TEMP_FILE_PATH_CHILD_DEFAULT;
        suffix = SUFFIX_DEFAULT;
        writeToExternalStorrage = WRITE_TO_EXTERNAL_STORRAGE_DEFAULT;
        imageFileName = "";
    }

    public FileConfiguration folderPath(String folderPath) {
        this.folderPath = folderPath;
        return this;
    }

    public FileConfiguration environment(String environment) {
        this.environment = environment;
        return this;
    }

    public FileConfiguration createTempFile(boolean createTempFile) {
        this.createTempFile = createTempFile;
        return this;
    }

    public FileConfiguration setAutoImageFileName(boolean autoImageFileName) {
        this.autoImageFileName = autoImageFileName;
        return this;
    }

    public FileConfiguration internalImageFilename(String internalImageFilename) {
        this.internalImageFilename = internalImageFilename;
        return this;
    }

    public FileConfiguration privateTempFilePathChild(String privateTempFilePathChild) {
        this.privateTempFilePathChild = privateTempFilePathChild;
        return this;
    }

    public FileConfiguration imageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
        this.autoImageFileName = true;
        return this;
    }

    public FileConfiguration writeToExternalStorrage(boolean writeToExternalStorrage) {
        this.writeToExternalStorrage = writeToExternalStorrage;
        return this;
    }

    public FileConfiguration suffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    public static String getTAG() {
        return TAG;
    }

    public static String getFolderPath() {
        return folderPath;
    }

    public static String getEnvironment() {
        return environment;
    }

    public static boolean isCreateTempFile() {
        return createTempFile;
    }

    public static boolean isAutoImageFileName() {
        return autoImageFileName;
    }

    public static String getInternalImageFilename() {
        return internalImageFilename;
    }

    public static String getPrivateTempFilePathChild() {
        return privateTempFilePathChild;
    }

    public static String getImageFileName() {
        return imageFileName;
    }

    public static String getSuffix() {
        return suffix;
    }

    public static boolean isWriteToExternalStorrage() {
        return writeToExternalStorrage;
    }

    public Context getContext() {
        return context;
    }
}
