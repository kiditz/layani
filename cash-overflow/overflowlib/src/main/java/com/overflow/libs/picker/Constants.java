package com.overflow.libs.picker;

/**
 * Created by Jacek Kwiecień on 03.11.2015.
 */
public interface Constants {

    String DEFAULT_FOLDER_NAME = "TeachMe";

    interface RequestCodes {
        int PICKER_IDENTIFICATOR = 0b1101101100; //876
        int SOURCE_CHOOSER = 1 << 14;

        int PICK_PICTURE_FROM_DOCUMENTS = PICKER_IDENTIFICATOR + (1 << 11);
        int PICK_PICTURE_FROM_GALLERY = PICKER_IDENTIFICATOR + (1 << 12);
        int PICK_PDF_FROM_DOCUMENTS = PICKER_IDENTIFICATOR + (1 << 13);
        int TAKE_PICTURE = PICKER_IDENTIFICATOR + (1 << 13);
    }

    interface BundleKeys {
        String FOLDER_NAME = "com.overflow.libs.picker.folder_name";
        String ALLOW_MULTIPLE = "com.overflow.libs.picker.allow_multiple";
        String COPY_TAKEN_PHOTOS = "com.overflow.libs.picker.copy_taken_photos";
        String COPY_PICKED_IMAGES = "com.overflow.libs.picker.copy_picked_images";
    }
}