# Camera


Install:

```
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

Example:

First Check read/write permissions in activity and manifest

Manifest:
```
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```
    
Activity:

        PickImage
                .activity(this)
                .includeCamera(true)
                .includeDocuments(true)
                .includeMultipleSelect(false)
                .logCallback(imageLogCallback)
                .start();


