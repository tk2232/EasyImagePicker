# Camera


## Install:

```java
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	};
```

```
dependencies {
	        implementation 'com.github.tk2232:EasyImagePicker:v1.0'
	}
```

### Example:

First Check read/write permissions in activity and manifest<br/>

Manifest:
```java
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```
    
### Activity:
```java
        PickImage
                .activity(this)
                .includeCamera(true)
                .includeDocuments(true)
                .includeMultipleSelect(false)
                .logCallback(imageLogCallback)
                .start();
```
#### onActivityResult
```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        PickImage.handleActivityResult(requestCode, resultCode, data, this, new Callbacks() {
            @Override
            public void onImagesPicked(@NonNull List<File> files, ImageSource imageSource, int type) {
                Uri uri = Uri.fromFile(files.get(0));
                mImageView.setImageURI(uri);
            }

            @Override
            public void onCanceled(ImageSource imageSource, int type) {

            }

            @Override
            public void onImagePickerError(Exception e, ImageSource imageSource, int type) {

            }
        });
    }
```
#### ImageLogCallback
```java
   ImageLogCallback imageLogCallback = new ImageLogCallback() {
        @Override
        public void log(String tag, String msg, Throwable tr) {

        }

        @Override
        public void log(String tag, String msg) {
            Log.d(tag, msg);
        }
    };
```

