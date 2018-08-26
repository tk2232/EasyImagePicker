package com.example.sebastianesau.camera;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class FileHelper {

    private static final String TAG = FileHelper.class.getSimpleName();

    /**
     * Coniguration
     */
    private static String EXTERNAL_FOLDER_PATH_DEFAULT;
    /**
     * DIRECTORY_MUSIC
     * DIRECTORY_PODCASTS
     * DIRECTORY_RINGTONES
     * DIRECTORY_ALARMS
     * DIRECTORY_NOTIFICATIONS
     * DIRECTORY_PICTURES damit gibt es probleme beim auslesen onActivityResult
     * DIRECTORY_MOVIES
     * DIRECTORY_DOWNLOADS
     * DIRECTORY_DCIM
     * DIRECTORY_DOCUMENTS
     */
    private static String ENVIRONMENT_DEFAULT = Environment.DIRECTORY_DCIM;
    private static final boolean CREATE_TEMP_FILE_DEFAULT = true;
    private static final String SUFFIX_DEFAULT = ".jpg";
    private static final boolean AUTO_IMAGE_FILE_NAME_DEFAULT = true;
    private static String INERNAL_IMAGE_TEMP_FILE;
    private static final String PRIVATE_TEMP_FILE_CHILD_DEFAULT = "ImageTemp";

    private static final String CAPTURE_IMAGE_FILE_PROVIDER = "com.example.sebastianesau.fileprovider";

    private static String folderPath;
    private static String imageFileName;
    private static boolean autoImageFileName;
    private static String environment;
    private static boolean createTempFile;
    private static String suffix;

    private static boolean writeToExternalStorrage = true;
    private static Context context;

    private static Configuration configuration;
    private static boolean hasConfig;

    private static Configuration getConfiguration(Context context) {
        return new Configuration(context);
    }

    public static File pickedExistingPicture(@NonNull Context context, Uri photoUri) throws IOException {
        InputStream pictureInputStream = context.getContentResolver().openInputStream(photoUri);
        File directory = tempCacheImageDirectory(context);
        File photoFile = new File(directory, UUID.randomUUID().toString() + "." + getMimeType(context, photoUri));
        photoFile.createNewFile();
        writeToFile(pictureInputStream, photoFile);
        return photoFile;
    }

    private final static String DEFAULT_FOLDER_NAME = "EasyImage";

    public static File getTempFileTest(Context context) {
        File privateTempDir = new File(context.getCacheDir(), PRIVATE_TEMP_FILE_CHILD_DEFAULT);
        if (!privateTempDir.exists()) privateTempDir.mkdirs();
        return privateTempDir;
    }

    public static Uri getUriToFile(@NonNull Context context, @NonNull File file) {
        String packageName = context.getApplicationContext().getPackageName();
        String authority = packageName + ".fileprovider";
        return FileProvider.getUriForFile(context, authority, file);
    }

    public static Uri getUriToFileTempFile(@NonNull Context context) throws IOException {
        String packageName = context.getApplicationContext().getPackageName();
        String authority = packageName + ".fileprovider";
        File file = getCameraTempFile(context);
        return FileProvider.getUriForFile(context, authority, file);
    }

    public static File getCameraPicturesLocation(@NonNull Context context) throws IOException {
        File dir = tempImageDirectory(context);
        return File.createTempFile(UUID.randomUUID().toString(), ".jpg", dir);
    }

    public static File getCameraTempFile(@NonNull Context context) throws IOException {
        File dir = tempImageDirectory(context);
        return new File(dir + File.separator + INERNAL_IMAGE_TEMP_FILE + suffix);
    }


    public static File getFileFromProvider(Context context) {
        String packageName = context.getApplicationContext().getPackageName();
        String authority = packageName + ".fileprovider";
        File path = new File(context.getFilesDir(), PRIVATE_TEMP_FILE_CHILD_DEFAULT);
        if (!path.exists()) path.mkdirs();
        return new File(path, "image.jpg");
    }

    public static File tempCacheImageDirectory(@NonNull Context context) {
        configuration(context);
        File privateTempDir = new File(context.getCacheDir(), PRIVATE_TEMP_FILE_CHILD_DEFAULT);
        if (!privateTempDir.exists()) {
            privateTempDir.mkdirs();
        }
        return privateTempDir;
    }

    public static File tempImageDirectory(@NonNull Context context) {
        configuration(context);
        //TODO nach erfolgreichen tests auf getFilesDir
        File privateTempDir = new File(context.getExternalFilesDir(null), PRIVATE_TEMP_FILE_CHILD_DEFAULT);
        if (!privateTempDir.exists()) {
            privateTempDir.mkdirs();
        }
        return privateTempDir;
    }

    private static void writeToFile(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];

            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            out.close();
            in.close();
        } catch (Exception var5) {
            var5.printStackTrace();
        }

    }

    private static String getMimeType(@NonNull Context context, @NonNull Uri uri) {
        String extension;
        if (uri.getScheme().equals("content")) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
        }

        return extension;
    }

    static void copyFilesInSeparateThread(final Context context, final List<File> filesToCopy) {
        (new Thread(new Runnable() {
            public void run() {
                List<File> copiedFiles = new ArrayList();
                int i = 1;

                for (Iterator var3 = filesToCopy.iterator(); var3.hasNext(); ++i) {
                    File fileToCopy = (File) var3.next();
                    File dstDir = new File(Environment.getExternalStoragePublicDirectory(environment), getFolderPath());
                    if (!dstDir.exists()) {
                        dstDir.mkdirs();
                    }

                    String[] filenameSplit = fileToCopy.getName().split("\\.");
                    String extension = "." + filenameSplit[filenameSplit.length - 1];
//                    String filename = String.format("IMG_%s_%d.%s", (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(Calendar.getInstance().getTime()), i, extension);
                    String filename = getImageFileName() + suffix;
                    File dstFile = new File(dstDir, filename);

                    try {
                        dstFile.createNewFile();
                        copyFile(fileToCopy, dstFile);
                        copiedFiles.add(dstFile);
                    } catch (IOException var11) {
                        var11.printStackTrace();
                    }
                }

                scanCopiedImages(context, copiedFiles);
            }
        })).run();
    }

    private static void copyFile(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        writeToFile(in, dst);
    }

    static void scanCopiedImages(Context context, List<File> copiedImages) {
        String[] paths = new String[copiedImages.size()];

        for (int i = 0; i < copiedImages.size(); ++i) {
            paths[i] = ((File) copiedImages.get(i)).toString();
        }

        MediaScannerConnection.scanFile(context, paths, (String[]) null, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
                Log.d(this.getClass().getSimpleName(), "Scanned " + path + ":");
                Log.d(this.getClass().getSimpleName(), "-> uri=" + uri);
            }
        });
    }

    public static File getTempImageFile(@NonNull Context context) throws IOException {
        configuration(context);
        if (writeToExternalStorrage) {
            if (isExternalStorageReadable() && isExternalStorageWritable()) {
                File file = new File(context.getFilesDir(), INERNAL_IMAGE_TEMP_FILE + suffix);
                return file;
            } else {
                //TODO read/write error
            }
        } else {

        }
        return null;
    }

    public static File getImageFile(Context context) throws IOException {
        configuration(context);
        // Create an image file name
        if (writeToExternalStorrage) {
            if (isExternalStorageReadable() && isExternalStorageWritable()) {
                return createImageFile();
            } else {
                //TODO read/write error
            }
        } else {

        }
        return null;
    }

    public static File createImageFile() throws IOException {
        File folderPath = createExternalPublicFolder();
        if (folderPath == null) {
            return null;
        }
        if (createTempFile) {
            File image = File.createTempFile(getImageFileName(), suffix, createExternalPublicFolder());
            return image;
        } else {
            File image = new File(createExternalPublicFolder(), getImageFileName() + suffix);
            return image;
        }
    }


    /**
     * Erstellt einen Ordner auf dem externen Speicher falls dieser noch nicht exestiert
     *
     * @return FolderPath
     */
    private static File createExternalPublicFolder() {
        //Ref. https://stackoverflow.com/questions/22366217/cant-create-folder-on-external-storage-on-android
//        File file = new File(Environment.getExternalStorageDirectory() + File.separator + FOLDER_PATH);
        File file = new File(Environment.getExternalStoragePublicDirectory(environment) + File.separator + getFolderPath());

        if (!file.exists()) {
            Log.d(TAG, "Folder doesn't exist, creating it...");
            boolean rv = file.mkdir();
            Log.d(TAG, "Folder creation " + (rv ? "success" : "failed"));
//            if (!rv) {
//                return null;
//            }
        } else {
            Log.d(TAG, "Folder already exists.");
        }
        return file;
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean removeFile(@NonNull File file) {
        try {
            if (file.exists()) {
                if (file.delete()) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (NullPointerException e) {
            //TODO
            Log.e(context.getClass().getSimpleName(), e.getMessage(), e);
        }
        return false;
    }
    //TODO einbauen

    /**
     * Test if we can open the given Android URI to test if permission required error is thrown.<br>
     * Only relevant for API version 23 and above.
     *
     * @param context used to access Android APIs, like content resolve, it is your
     *                activity/fragment/widget.
     * @param uri     the result URI of image pick.
     */
    public static boolean isUriRequiresPermissions(@NonNull Context context, @NonNull Uri uri) {
        try {
            ContentResolver resolver = context.getContentResolver();
            InputStream stream = resolver.openInputStream(uri);
            if (stream != null) {
                stream.close();
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public static String getImageFileName() {
        return autoImageFileName ? getDefaultImageFileName() : imageFileName;
    }

    private static String getDefaultImageFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSS").format(new Date());
        return suffix.toUpperCase() + "_" + timeStamp + "_";
    }

    public static String getFolderPath() {
        return (folderPath == null || folderPath.isEmpty()) ? EXTERNAL_FOLDER_PATH_DEFAULT : folderPath;
    }

    public boolean isWriteToExternalStorrage() {
        return writeToExternalStorrage;
    }

    public static boolean isCreateTempFile() {
        return createTempFile;
    }

    public static final class Configuration {

        private Configuration(Context context) {
            FileHelper.context = context;
            if (!hasConfig) {
                //TODO den anhang bei FolderPath ändern
                EXTERNAL_FOLDER_PATH_DEFAULT = ((Activity) context).getText(R.string.app_name).toString() + "_Test";
                INERNAL_IMAGE_TEMP_FILE = "temp_image";
                folderPath = EXTERNAL_FOLDER_PATH_DEFAULT;
                environment = ENVIRONMENT_DEFAULT;
                createTempFile = CREATE_TEMP_FILE_DEFAULT;
                suffix = SUFFIX_DEFAULT;
                autoImageFileName = AUTO_IMAGE_FILE_NAME_DEFAULT;
                hasConfig = true;
            }
        }

        public Configuration folderPath(String folderPath) {
            FileHelper.folderPath = folderPath;
            return this;
        }

        public Configuration imageFileName(String imageFileName) {
            FileHelper.imageFileName = imageFileName;
            FileHelper.autoImageFileName = true;
            return this;
        }

        public Configuration setAutoImageFileName(boolean autoImageFileName) {
            FileHelper.autoImageFileName = autoImageFileName;
            return this;
        }

        public Configuration writeToExternalStorrage(boolean writeToExternalStorrage) {
            FileHelper.writeToExternalStorrage = writeToExternalStorrage;
            return this;
        }


        public Configuration environment(String environment) {
            FileHelper.environment = environment;
            return this;
        }

        public Configuration createTempFile(boolean createTempFile) {
            FileHelper.createTempFile = createTempFile;
            return this;
        }

        public Configuration suffix(String suffix) {
            FileHelper.suffix = suffix;
            return this;
        }
    }

    public static Configuration configuration(Context context) {
        return configuration == null ? getConfiguration(context) : configuration;
    }
}
