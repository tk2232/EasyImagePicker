package com.example.sebastianesau.camera;

import android.content.ClipData;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
//        EasyImage
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

        CameraHelper.handleActivityResult(requestCode, resultCode, data, this, new CameraHelper.DefaultCallback() {
            @Override
            public void onImagesPicked(@NonNull List<File> var1, CameraHelper.ImageSource var2, int var3) {
                Uri uri = Uri.fromFile(var1.get(0));
                mImageView.setImageURI(uri);
                System.out.println();
            }

            @Override
            public void onImagePickerError(Exception e, CameraHelper.ImageSource source, int type) {
                super.onImagePickerError(e, source, type);
            }
        });


        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }

            @Override
            public void onImagesPicked(List<File> imagesFiles, EasyImage.ImageSource source, int type) {
                //Handle the images
            }
        });
    }

    private void resultSingleImage(Intent data) {
        Bitmap bitmap;
        Bundle extras = data.getExtras();
//                    Bitmap imageBitmap = (Bitmap) extras.get("data");
//                    mImageView.setImageBitmap(imageBitmap);
//                    return;
        try {
            picUri = mCameraHelper.getPickImageResultUri(data);
        } catch (IOException io) {
            Log.e(this.getClass().getSimpleName(), io.getMessage(), io);
        }
        if (picUri != null) {
            try {
                myBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);
//                            myBitmap = rotateImageIfRequired(myBitmap, picUri);
//                            myBitmap = getResizedBitmap(myBitmap, 500);

//                            CircleImageView croppedImageView = (CircleImageView) findViewById(R.id.img_profile);
//                            croppedImageView.setImageBitmap(myBitmap);
                mImageView.setImageBitmap(myBitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (data != null) {
            bitmap = (Bitmap) data.getExtras().get("data");
//
            myBitmap = bitmap;
////                        CircleImageView croppedImageView = (CircleImageView) findViewById(R.id.img_profile);
////                        if (croppedImageView != null) {
////                            croppedImageView.setImageBitmap(myBitmap);
////                        }
//
            mImageView.setImageBitmap(myBitmap);
        } else {
            //TODO error data is null
        }
        Toast.makeText(MainActivity.this, "finish", Toast.LENGTH_LONG).show();
    }

    private void resultMultipleImage(Intent data) {
        // Get the Image from data

        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        List imagesEncodedList = new ArrayList<String>();
        String imageEncoded;
        ClipData d = data.getClipData();
        if (data.getData() != null && data.getClipData() != null && data.getClipData().getItemCount() == 1) {

            Uri mImageUri = data.getData();

            // Get the cursor
            Cursor cursor = getContentResolver().query(mImageUri,
                    filePathColumn, null, null, null);
            // Move to first row
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imageEncoded = cursor.getString(columnIndex);
            cursor.close();

        } else {
            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                for (int i = 0; i < mClipData.getItemCount(); i++) {

                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();
                    mArrayUri.add(uri);
                    // Get the cursor
                    Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor.getString(columnIndex);
                    imagesEncodedList.add(imageEncoded);
                    cursor.close();

                }
                Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
            }
        }
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
