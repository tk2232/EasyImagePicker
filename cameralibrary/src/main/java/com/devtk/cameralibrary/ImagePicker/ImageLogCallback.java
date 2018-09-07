package com.devtk.cameralibrary.ImagePicker;

public interface ImageLogCallback {

    void log(String tag, String msg, Throwable tr);

    void log(String tag, String msg);
}
