package com.github.huajianjiang.expandablerecyclerview.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.View;
import android.view.ViewParent;

import com.github.huajianjiang.expandablerecyclerview.util.Logger;

/**
 * <p>Author: Huajian Jiang
 * <br>Date: 2017/8/6
 * <br>Email: developer.huajianjiang@gmail.com
 */
public class PatchedRecyclerView extends RecyclerView {
    private static final String TAG = PatchedRecyclerView.class.getSimpleName();
    protected ContextMenu.ContextMenuInfo mContextMenuInfo;

    public PatchedRecyclerView(Context context) {
        super(context);
    }

    public PatchedRecyclerView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PatchedRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected ContextMenu.ContextMenuInfo createContextMenuInfo(View targetView, int position,
            long id)
    {
        Logger.e(TAG, "createContextMenuInfo");
        return new RecyclerViewContextMenuInfo(targetView, position, id);
    }

    @Override
    public boolean showContextMenu() {
        return showContextMenuInternal(Float.NaN, Float.NaN, false);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(Build.VERSION_CODES.N)
    public boolean showContextMenu(float x, float y) {
        return showContextMenuInternal(x, y, true);
    }

    private boolean showContextMenuInternal(float x, float y, boolean useOffsets) {
        View child = findChildViewUnder(x, y);
        if (child != null) {
            mContextMenuInfo = createContextMenuInfo(child, getChildAdapterPosition(child),
                    getChildItemId(child));
            if (useOffsets && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return super.showContextMenuForChild(child, x, y);
            } else {
                return super.showContextMenuForChild(child);
            }
        }

        if (useOffsets && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return super.showContextMenu(x, y);
        } else {
            return super.showContextMenu();
        }
    }

    @Override
    public boolean showContextMenuForChild(View originalView) {
        return showContextMenuForChildInternal(originalView, Float.NaN, Float.NaN, false);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(Build.VERSION_CODES.N)
    public boolean showContextMenuForChild(View originalView, float x, float y) {
        return showContextMenuForChildInternal(originalView, x, y, true);
    }

    private boolean showContextMenuForChildInternal(View originalView, float x, float y,
            boolean useOffsets)
    {
        View child = findContainingItemView(originalView);
        final int childAdapterPos = getChildAdapterPosition(child);
        if (childAdapterPos == RecyclerView.NO_POSITION) return false;
        ViewParent parent = getParent();
        if (parent == null) return false;
        mContextMenuInfo = createContextMenuInfo(child, childAdapterPos, getChildItemId(child));
        if (useOffsets && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return parent.showContextMenuForChild(originalView, x, y);
        } else {
            return parent.showContextMenuForChild(originalView);
        }
    }

    @Override
    protected ContextMenu.ContextMenuInfo getContextMenuInfo() {
        return mContextMenuInfo;
    }


    public static class RecyclerViewContextMenuInfo implements ContextMenu.ContextMenuInfo {
        /**
         *
         */
        public View targetView;
        /**
         *
         */
        public int position;
        /**
         *
         */
        public long id;

        RecyclerViewContextMenuInfo(View targetView, int position, long id) {
            this.targetView = targetView;
            this.position = position;
            this.id = id;
        }

        @Override
        public String toString() {
            String vId = "0x" + Integer.toHexString(targetView.getId());
            return "RecyclerViewContextMenuInfo{" + "targetView=" + String.format("%1$s", vId) +
                   ", position=" + position + ", id=" + id + '}';
        }
    }

}
