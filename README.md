# Camera


Installation:

Example:

First Check read/write permissions in activity and manifest

        PickImage
                .activity(this)
                .includeCamera(true)
                .includeDocuments(true)
                .includeMultipleSelect(false)
                .logCallback(imageLogCallback)
                .start();


