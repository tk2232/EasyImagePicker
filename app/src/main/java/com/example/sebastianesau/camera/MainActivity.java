package com.example.sebastianesau.camera;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.sebastianesau.camera.ImagePicker.Callbacks;
import com.example.sebastianesau.camera.ImagePicker.CameraHelper;
import com.example.sebastianesau.camera.ImagePicker.ImageSource;

import java.io.File;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private final static int PICK_IMAGE_REQUEST = 0;
    private final static int PICK_IMAGE_MULTIPLE_REQUEST = 1;
    private final static boolean INCLUDE_MULTIPLE = true;

    private Button mCameraBtn;
    private ImageView mImageView;
    private Snackbar mPermissionSnackBar;
    private CameraHelper mCameraHelper;

    private Bitmap myBitmap;
    private Uri picUri;

    private Uri outputFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mFileHelper = new FileHelper(this);
//        mCameraHelper = new CameraHelper(this);

        initViews();
        initClickListeneer();
//        startActivity(new Intent(this, FileProviderTest.class));

        EasyImage.configuration(this).setAllowMultiplePickInGallery(false);
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
        int requestCode = PICK_IMAGE_REQUEST;
        if (INCLUDE_MULTIPLE) {
            requestCode = PICK_IMAGE_MULTIPLE_REQUEST;
        }
//        EasyImage.openChooserWithGallery(this, "", 0);
        //TODO space und storrage isReadable/isWritable
        CameraHelper
                .activity(this)
                .includeCamera(true)
                .includeDocuments(true)
                .includeMultipleSelect(false)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        CameraHelper.handleActivityResult(requestCode, resultCode, data, this, new Callbacks() {
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

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }

            @Override
            public void onImagesPicked(List<File> imagesFiles, EasyImage.ImageSource source, int type) {
                Uri uri = Uri.fromFile(imagesFiles.get(0));
                System.out.println();
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
