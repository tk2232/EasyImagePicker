package com.example.sebastianesau.camera;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;

public class Storrage {

    //TODO testen mit File.getUsableSpace()

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
        StatFs stat = new StatFs(path.getPath());
        long free_memory = stat.getAvailableBlocksLong() * stat.getBlockSizeLong(); //return value is in bytes

        return free_memory;
    }

    public static long getUsedInternalStorrageSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long free_memory = (stat.getBlockCountLong() - stat.getAvailableBlocksLong()) * stat.getBlockSizeLong(); //return value is in bytes

        return free_memory;
    }

    public static long getInternalStorrageSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long free_memory = stat.getBlockCountLong() * stat.getBlockSizeLong(); //return value is in bytes

        return free_memory;
    }

    public static long getFreeExternalStorrageSize(){

        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long free_memory = stat.getAvailableBlocksLong() * stat.getBlockSizeLong(); //return value is in bytes

        return free_memory;
    }
    public static long getUsedExternalStorrageSize(){

        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long free_memory = (stat.getBlockCountLong() - stat.getAvailableBlocksLong()) * stat.getBlockSizeLong(); //return value is in bytes

        return free_memory;
    }
    public static long getExternalStorrageSize(){

        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long free_memory = stat.getBlockCountLong() * stat.getBlockSizeLong(); //return value is in bytes

        return free_memory;
    }
}
