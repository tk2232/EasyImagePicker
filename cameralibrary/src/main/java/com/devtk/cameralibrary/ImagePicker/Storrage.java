package com.devtk.cameralibrary.ImagePicker;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;

public class Storrage {

    public static boolean isExternalStorrageReady() {
        return isExternalStorageAvailable() && !isExternalStorageReadOnly();
    }

    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static long getFreeInternalStorrageSize() {
        File path = Environment.getDataDirectory();
        return path.getUsableSpace();
    }

    public static long getUsedInternalStorrageSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long free_memory = (stat.getBlockCountLong() - stat.getAvailableBlocksLong()) * stat.getBlockSizeLong(); //return value is in bytes
        return free_memory;
    }

    public static long getInternalStorrageSize() {
        File path = Environment.getDataDirectory();
        return path.getTotalSpace();
    }

    public static long getFreeExternalStorrageSize() {
        File path = Environment.getExternalStorageDirectory();
        return path.getUsableSpace();
    }

    public static long getUsedExternalStorrageSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long free_memory = (stat.getBlockCountLong() - stat.getAvailableBlocksLong()) * stat.getBlockSizeLong(); //return value is in bytes
        return free_memory;
    }

    public static long getExternalStorrageSize() {
        File path = Environment.getExternalStorageDirectory();
        return path.getTotalSpace();
    }
}
