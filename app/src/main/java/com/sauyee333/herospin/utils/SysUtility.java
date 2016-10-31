package com.sauyee333.herospin.utils;

import android.text.TextUtils;

/**
 * Created by sauyee on 30/10/16.
 */

public class SysUtility {

    public static String generateImageUrl(String path, String filename, String extension) {
        String imageUrl = "";
        if (!TextUtils.isEmpty(path) && !TextUtils.isEmpty(extension) && !TextUtils.isEmpty(filename)) {
            imageUrl = path + "/" + filename + "." + extension;
        }
        return imageUrl;
    }
}
