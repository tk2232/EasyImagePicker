package com.example.sebastianesau.camera;

import android.util.Log;

/**
 * Custom Log class
 * Falls die Fehler an den Server geschickt werden sollen dann können die Methoden hier hinzugefügt werden.
 */
public class CLog {

    public static void e(String tag, String msg) {
        Log.e(tag, msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        Log.e(tag, msg, tr);
    }
}
