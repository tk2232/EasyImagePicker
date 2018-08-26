package com.example.sebastianesau.camera;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FileProviderTest extends AppCompatActivity {

    private Button btn;

    private static final String TAG = FileProviderTest.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_provider_test);

        btn = (Button) findViewById(R.id.providerBtn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                providerTest();
            }
        });
    }


    private void providerTest() {
        try {
            File file = getCameraPicturesLocation(this);
            Uri uri = getUriToFile(this, file);
            Log.d(TAG, file.getPath());
            System.out.println();
        } catch (IOException i) {
            Log.d(TAG, i.getMessage(), i);
        }
    }

    public static File getCameraPicturesLocation(@NonNull Context context) throws IOException {
        File dir = tempImageDirectory(context);
        File file = new File(dir, "Temp.jpg");
        return File.createTempFile("Temp", ".jpg", dir);
    }

    public static File tempImageDirectory(@NonNull Context context) {
        File privateTempDir = new File(context.getExternalCacheDir(), "ImageTemp");
        if (!privateTempDir.exists()) {
            privateTempDir.mkdirs();
        }
        return privateTempDir;
    }

    public static Uri getUriToFile(@NonNull Context context, @NonNull File file) {
        String packageName = context.getApplicationContext().getPackageName();
        String authority = packageName;
        Log.d(TAG, authority);
        Log.d(TAG, file.getPath());
        try {
            return FileProvider.getUriForFile(context, authority, file);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }
}
