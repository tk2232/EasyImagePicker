package com.example.sebastianesau.camera;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.sebastianesau.camera.ImagePicker.Callbacks;
import com.example.sebastianesau.camera.ImagePicker.ImageLogCallback;
import com.example.sebastianesau.camera.ImagePicker.PickImage;
import com.example.sebastianesau.camera.ImagePicker.ImageSource;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Button mCameraBtn;
    private ImageView mImageView;
    private Snackbar mPermissionSnackBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initClickListeneer();
    }

    private void initViews() {
        mCameraBtn = (Button) findViewById(R.id.cameraBtn);
        mImageView = (ImageView) findViewById(R.id.imageView);
    }

    private void initClickListeneer() {
        mCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Permissions.needPermissionRequestStorrage(MainActivity.this)) {
                    Permissions.requestCameraStorragePermission(MainActivity.this);
                } else {
                    openIntent();
                }
            }
        });
    }

    private void openIntent() {
        PickImage
                .activity(this)
                .includeCamera(true)
                .includeDocuments(true)
                .includeMultipleSelect(false)
                .logCallback(imageLogCallback)
                .start();
    }

    ImageLogCallback imageLogCallback = new ImageLogCallback() {
        @Override
        public void log(String tag, String msg, Throwable tr) {

        }

        @Override
        public void log(String tag, String msg) {
            Log.d(tag, msg);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        PickImage.handleActivityResult(requestCode, resultCode, data, this, new Callbacks() {
            @Override
            public void onImagesPicked(@NonNull List<File> files, ImageSource imageSource, int type) {
                Uri uri = Uri.fromFile(files.get(0));
                mImageView.setImageURI(null);
                mImageView.setImageURI(uri);
                System.out.println();
            }

            @Override
            public void onCanceled(ImageSource imageSource, int type) {

            }

            @Override
            public void onImagePickerError(Exception e, ImageSource imageSource, int type) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            Permissions.showSnackbar(MainActivity.this, mPermissionSnackBar, R.string.setting, R.string.permission_denied_explanation);
        }
        switch (requestCode) {
            case Permissions.REQUEST_CAMERA_AND_EXTERNAL_STORRAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mPermissionSnackBar != null) {
                        mPermissionSnackBar.dismiss();
                    }
                    openIntent();
                }
                break;
        }
    }
}
