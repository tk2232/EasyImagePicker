package com.devtk.tk2232.EasyImagePicker.ImagePicker;

public interface ImageLogCallback {

    void log(String tag, String msg, Throwable tr);

    void log(String tag, String msg);
}
