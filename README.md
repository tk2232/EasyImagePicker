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

        PickImage
                .activity(this)
                .includeCamera(true)
                .includeDocuments(true)
                .includeMultipleSelect(false)
                .logCallback(imageLogCallback)
                .start();


