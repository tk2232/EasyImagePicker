package com.devtk.tk2232.EasyImagePicker.ImagePicker;

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
     * Erstellt ein im internal storrage ein Tempfile
     *
     * @param config
     * @return
     * @throws IOException
     */
    public static File getCameraTempFile(@NonNull FileConfiguration config) throws IOException {
        File dir = getTempImageDirectory(config);
        return new File(dir + File.separator + config.getInternalImageFilename() + config.getSuffix());
    }

    /**
     * Gibt den Ordner vom private internal storrage zurück
     *
     * @param config
     * @return
     */
    public static File getTempImageDirectory(@NonNull FileConfiguration config) {
        File privateTempDir = new File(config.getContext().getFilesDir(), config.getPrivateTempFilePathChild());
        if (!privateTempDir.exists()) {
            privateTempDir.mkdirs();
        }
        return privateTempDir;
    }

    /**
     * Gibt die Uri vom fileprovider wieder. Fileprovider muss in der Manifest angemeldet werden
     *
     * @param config
     * @param file
     * @return
     */
    public static Uri getUriToFile(@NonNull FileConfiguration config, @NonNull File file) {
        String packageName = config.getContext().getApplicationContext().getPackageName();
        String authority = packageName + ".fileprovider";

        return FileProvider.getUriForFile(config.getContext(), authority, file);
    }

    /**
     * Copiert das Foto auf den external storrage
     *
     * @param config
     * @param photoUri
     * @return
     * @throws IOException
     */
    public static File pickedExistingPicture(@NonNull FileConfiguration config, Uri photoUri) throws IOException {
        InputStream pictureInputStream = config.getContext().getContentResolver().openInputStream(photoUri);
        File directory = getTempImageDirectory(config);
        File photoFile = new File(directory, UUID.randomUUID().toString() + "." + getMimeType(config.getContext(), photoUri));
        photoFile.createNewFile();
        writeToFile(pictureInputStream, photoFile);
        return photoFile;
    }

    @Deprecated
    public static File getCameraPicturesLocation(@NonNull FileConfiguration config) throws IOException {
        File dir = getTempImageDirectory(config);
        return File.createTempFile(UUID.randomUUID().toString(), ".jpg", dir);
    }

//    public static File tempCacheImageDirectory(@NonNull Context context) {
//        configuration(context);
//        File privateTempDir = new File(context.getCacheDir(), PRIVATE_TEMP_FILE_CHILD_DEFAULT);
//        if (!privateTempDir.exists()) {
//            privateTempDir.mkdirs();
//        }
//        return privateTempDir;
//    }

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

    public static void copyFilesInSeparateThread(final FileConfiguration config, final List<File> filesToCopy) {
        (new Thread(new Runnable() {
            public void run() {
                List<File> copiedFiles = new ArrayList();
                int i = 1;

                for (Iterator iterator = filesToCopy.iterator(); iterator.hasNext(); ++i) {
                    File fileToCopy = (File) iterator.next();
                    File dstDir = new File(Environment.getExternalStoragePublicDirectory(config.getEnvironment()), config.getFolderPath());
                    if (!dstDir.exists()) {
                        dstDir.mkdirs();
                    }

                    String[] filenameSplit = fileToCopy.getName().split("\\.");
                    String extension = "." + filenameSplit[filenameSplit.length - 1];
//                    String filename = String.format("IMG_%s_%d.%s", (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(Calendar.getInstance().getTime()), i, extension);
                    String filename = getImageFileName(config) + config.getSuffix();
                    File dstFile = new File(dstDir, filename);

                    try {
                        dstFile.createNewFile();
                        copyFile(fileToCopy, dstFile);
                        copiedFiles.add(dstFile);
                    } catch (IOException var11) {
                        var11.printStackTrace();
                    }
                }
                scanCopiedImages(config.getContext(), copiedFiles);
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

    @Deprecated
    public static File getTempImageFile(@NonNull FileConfiguration config) throws IOException {
        if (config.isWriteToExternalStorrage()) {
            if (isExternalStorageReadable() && isExternalStorageWritable()) {
                File file = new File(config.getContext().getFilesDir(), config.getInternalImageFilename() + config.getSuffix());
                return file;
            } else {
                log(config, TAG, "getTempImageFile read/write error");
            }
        } else {

        }
        return null;
    }

    @Deprecated
    public static File getImageFile(FileConfiguration config) throws IOException {
        // Create an image file name
        if (config.isWriteToExternalStorrage()) {
            if (isExternalStorageReadable() && isExternalStorageWritable()) {
                return createImageFile(config);
            } else {
                log(config, TAG, "getTempImageFile read/write error");
            }
        } else {

        }
        return null;
    }

    @Deprecated
    public static File createImageFile(FileConfiguration config) throws IOException {
        File folderPath = createExternalPublicFolder(config);
        if (folderPath == null) {
            return null;
        }
        if (config.isCreateTempFile()) {
            File image = File.createTempFile(getImageFileName(config), config.getSuffix(), createExternalPublicFolder(config));
            return image;
        } else {
            File image = new File(createExternalPublicFolder(config), getImageFileName(config) + config.getSuffix());
            return image;
        }
    }

    /**
     * Erstellt einen Ordner auf dem externen Speicher falls dieser noch nicht exestiert
     *
     * @return FolderPath
     */
    private static File createExternalPublicFolder(@NonNull FileConfiguration config) {
        //Ref. https://stackoverflow.com/questions/22366217/cant-create-folder-on-external-storage-on-android
//        File file = new File(Environment.getExternalStorageDirectory() + File.separator + FOLDER_PATH);
        File file = new File(Environment.getExternalStoragePublicDirectory(config.getEnvironment()) + File.separator + config.getFolderPath());

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

    public static boolean removeFile(@NonNull FileConfiguration config, @NonNull File file) {
        try {
            if (file.exists()) {
                if (file.delete()) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (NullPointerException e) {
            log(config, config.getContext().getClass().getSimpleName(), e.getMessage(), e);
        }
        return false;
    }

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

    public static String getImageFileName(@NonNull FileConfiguration config) {
        return config.isAutoImageFileName() ? getDefaultImageFileName(config) : config.getImageFileName();
    }

    private static String getDefaultImageFileName(@NonNull FileConfiguration config) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSS").format(new Date());
        return config.getSuffix().toUpperCase() + "_" + timeStamp + "_";
    }

    private static void log(FileConfiguration conf, String tag, String msg, Throwable tr) {
        if (conf.getImageLogCallback() == null) {
            Log.e(tag, msg, tr);
        } else {
            conf.getImageLogCallback().log(tag, msg, tr);
        }
    }

    private static void log(FileConfiguration conf, String tag, String msg) {
        if (conf.getImageLogCallback() == null) {
            Log.e(tag, msg);
        } else {
            conf.getImageLogCallback().log(tag, msg);
        }
    }

}
