package com.github.huajianjiang.expandablerecyclerview.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.View;

import com.github.huajianjiang.expandablerecyclerview.util.Logger;

/**
 * <p>Author: Huajian Jiang
 * <br>Date: 2017/8/5
 * <br>Email: developer.huajianjiang@gmail.com
 */
public class ExpandableRecyclerView extends PatchedRecyclerView {
    private static final String TAG = ExpandableRecyclerView.class.getSimpleName();

    public ExpandableRecyclerView(Context context) {
        super(context);
    }

    public ExpandableRecyclerView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ExpandableRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected ContextMenu.ContextMenuInfo createContextMenuInfo(View targetView, int position,
            long id)
    {
        Logger.e(TAG, "createExpandableContextMenuInfo");
        return new ExpandableRecyclerViewContextMenuInfo(targetView, position, id);
    }

    public static class ExpandableRecyclerViewContextMenuInfo
            implements ContextMenu.ContextMenuInfo
    {
        /**
         *
         */
        public View targetView;

        /**
         *
         */
        public long packedPosition;

        /**
         *
         */
        public long id;

        ExpandableRecyclerViewContextMenuInfo(View targetView, long packedPosition, long id) {
            this.targetView = targetView;
            this.packedPosition = packedPosition;
            this.id = id;
        }

        @Override
        public String toString() {
            String vId = "0x" + Integer.toHexString(targetView.getId());
            return "ExpandableRecyclerViewContextMenuInfo{" + "targetView=" +
                   String.format("%1$s", vId) + ", packedPosition=" + packedPosition + ", id=" +
                   id + '}';
        }
    }

}
