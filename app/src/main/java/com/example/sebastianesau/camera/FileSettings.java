package com.example.sebastianesau.camera;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FileSettings {

    private String EXTERNAL_FOLDER_PATH_DEFAULT;
    private String IMAGE_FILE_NAME_DEFAULT;
    /**
     * DIRECTORY_MUSIC
     * DIRECTORY_PODCASTS
     * DIRECTORY_RINGTONES
     * DIRECTORY_ALARMS
     * DIRECTORY_NOTIFICATIONS
     * DIRECTORY_PICTURES
     * DIRECTORY_MOVIES
     * DIRECTORY_DOWNLOADS
     * DIRECTORY_DCIM
     * DIRECTORY_DOCUMENTS
     */
    private String ENVIRONMENT_DEFAULT = Environment.DIRECTORY_DCIM;

    private String folderPath;
    private String imageFileName;
    private String environment;

    private boolean writeToExternalStorrage = true;


    public FileSettings(Context context) {
        EXTERNAL_FOLDER_PATH_DEFAULT = ((Activity) context).getText(R.string.app_name).toString();
        folderPath = EXTERNAL_FOLDER_PATH_DEFAULT;

        setDefaultImageFileName();
        environment = ENVIRONMENT_DEFAULT;
    }

    public void setDefaultImageFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        IMAGE_FILE_NAME_DEFAULT = imageFileName;
    }

    public String getFolderPath() {
        return (folderPath == null || folderPath.isEmpty()) ? EXTERNAL_FOLDER_PATH_DEFAULT : folderPath;
    }

    public FileSettings setFolderPath(String folderPath) {
        this.folderPath = folderPath;
        return this;
    }

    public String getImageFileName() {
        return (imageFileName == null || imageFileName.isEmpty()) ? getDefaultImageFileName() : imageFileName;
    }

    public FileSettings setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
        return this;
    }

    public String getDefaultImageFileName() {
        return IMAGE_FILE_NAME_DEFAULT;
    }

    public boolean isWriteToExternalStorrage() {
        return writeToExternalStorrage;
    }

    public FileSettings setWriteToExternalStorrage(boolean writeToExternalStorrage) {
        this.writeToExternalStorrage = writeToExternalStorrage;
        return this;
    }

    public String getEnvironment() {
        return environment;
    }

    public FileSettings setEnvironment(String environment) {
        this.environment = environment;
        return this;
    }
}
