package com.example.sebastianesau.camera.Camera;

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

import com.example.sebastianesau.camera.CameraHelper;
import com.example.sebastianesau.camera.MainActivity;
import com.example.sebastianesau.camera.Permissions;
import com.example.sebastianesau.camera.R;
import com.theartofdev.edmodo.cropper.CropImage;

import pl.aprilapps.easyphotopicker.EasyImage;

public class CameraTest extends AppCompatActivity {

    private static final String TAG = CameraTest.class.getSimpleName();

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
        setContentView(R.layout.activity_camer_test);

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
                if (Permissions.needPermissionRequestStorrage(CameraTest.this)) {
                    Permissions.requestCameraStorragePermission(CameraTest.this);
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
        EasyImage.openChooserWithDocuments(this, "", 0);
//        EasyImage
        CameraHelper
                .activity(this)
                .includeCamera(true)
                .includeDocuments(true)
                .includeMultipleSelect(false)
                .start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            Permissions.showSnackbar(CameraTest.this, mPermissionSnackBar, R.string.setting, R.string.permission_denied_explanation);
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
