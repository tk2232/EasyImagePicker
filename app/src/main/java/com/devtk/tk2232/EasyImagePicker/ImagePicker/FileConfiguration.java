package com.devtk.tk2232.EasyImagePicker.ImagePicker;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.devtk.tk2232.EasyImagePicker.R;

public class FileConfiguration {

    private String TAG;

    /**
     * Coniguration
     */
    private String EXTERNAL_FOLDER_PATH_DEFAULT;

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
    private final String ENVIRONMENT_DEFAULT = Environment.DIRECTORY_DCIM;
    private final boolean CREATE_TEMP_FILE_DEFAULT = true;
    private final String SUFFIX_DEFAULT = ".jpg";
    private final boolean AUTO_IMAGE_FILE_NAME_DEFAULT = true;
    private String INERNAL_IMAGE_TEMP_FILENAME_DEFAULT = "tempImage";
    private final String PRIVATE_TEMP_FILE_PATH_CHILD_DEFAULT = "ImageTemp";
    private final boolean WRITE_TO_EXTERNAL_STORRAGE_DEFAULT = true;

    private String folderPath;
    private String environment;
    private boolean createTempFile;
    private boolean autoImageFileName;
    private String internalImageFilename;
    private String privateTempFilePathChild;
    private String imageFileName;
    private String suffix;
    private boolean writeToExternalStorrage;
    private ImageLogCallback imageLogCallback;

    private Context context;

    public FileConfiguration(@NonNull Context context) {
        this.context = context;
        TAG = context.getClass().getSimpleName();
        EXTERNAL_FOLDER_PATH_DEFAULT = ((Activity) context).getText(R.string.app_name).toString();
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
        imageLogCallback = null;
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

    public FileConfiguration logCallback(ImageLogCallback imageLogCallback) {
        this.imageLogCallback = imageLogCallback;
        return this;
    }

    public String getTAG() {
        return TAG;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public String getEnvironment() {
        return environment;
    }

    @Deprecated
    public boolean isCreateTempFile() {
        return createTempFile;
    }

    public boolean isAutoImageFileName() {
        return autoImageFileName;
    }

    public String getInternalImageFilename() {
        return internalImageFilename;
    }

    public String getPrivateTempFilePathChild() {
        return privateTempFilePathChild;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public String getSuffix() {
        return suffix;
    }

    public boolean isWriteToExternalStorrage() {
        return writeToExternalStorrage;
    }

    public Context getContext() {
        return context;
    }

    public ImageLogCallback getImageLogCallback() {
        return imageLogCallback;
    }
}
