package com.devtk.cameralibrary.ImagePicker;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.List;

public interface Callbacks {
    void onImagesPicked(@NonNull List<File> files, ImageSource imageSource, int type);

    void onCanceled(ImageSource imageSource, int type);

    void onImagePickerError(Exception e, ImageSource imageSource, int type);
}