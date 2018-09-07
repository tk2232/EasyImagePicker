package com.example.sebastianesau.camera.ImagePicker;

public interface ImageLogCallback {

    void log(String tag, String msg, Throwable tr);

    void log(String tag, String msg);
}
