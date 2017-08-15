package com.github.huajianjiang.expandablerecyclerview.sample;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.support.v7.widget.FitWindowsFrameLayout;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.FrameLayout;

import com.github.huajianjiang.expandablerecyclerview.util.Logger;

/**
 * <p>Author: Huajian Jiang
 * <br>Date: 2017/8/15
 * <br>Email: developer.huajianjiang@gmail.com
 */
public class MyFrameLayout extends FrameLayout {

    private static final String TAG = MyFrameLayout.class.getSimpleName();

    public MyFrameLayout(Context context) {
        super(context);
    }

    public MyFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MyFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs,
            @AttrRes int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MyFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs,
            @AttrRes int defStyleAttr, @StyleRes int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        for (int i = 0, c = getChildCount(); i < c; i++) {
            Logger.e(TAG, "onApplyWindowInsets>>" + getChildAt(i));
            getChildAt(i).dispatchApplyWindowInsets(insets);
        }
        return insets;
    }
}
