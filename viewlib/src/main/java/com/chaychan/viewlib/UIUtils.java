package com.chaychan.viewlib;

import android.content.Context;

/**
 * @author chaychan
 * @description: TODO
 * @date 2017/3/7  17:19
 * Copyright 2017 Youjiang
 * Version 1.0
 */
public class UIUtils {
    /**
     * dip-->px
     */
    public static int dip2Px(Context context,int dip) {
        // px/dip = density;
        // density = dpi/160
        // 320*480 density = 1 1px = 1dp
        // 1280*720 density = 2 2px = 1dp

        float density = context.getResources().getDisplayMetrics().density;
        int px = (int) (dip * density + 0.5f);
        return px;
    }
}
