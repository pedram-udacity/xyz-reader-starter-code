package com.example.xyzreader.utilities;

import android.content.Context;
import android.os.Build;

public class MiscUtils {

    public final static boolean LOLLIPOP_AND_HIGHER = android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

    public static int getStatusBarHeight(Context aContext) {
        int result = 0;
        int resourceId = aContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = aContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
