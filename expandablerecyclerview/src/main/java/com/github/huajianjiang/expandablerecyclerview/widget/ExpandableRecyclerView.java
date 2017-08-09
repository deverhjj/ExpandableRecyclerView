package com.github.huajianjiang.expandablerecyclerview.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
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

    private ExpandableAdapter mAdapter;

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

    /**
     * <b>do not use this method,please instead using {@link #setAdapter(ExpandableAdapter)}</b>
     * <p>
     * {@inheritDoc}
     */
    @Override
    public void setAdapter(Adapter adapter) {
        throw new RuntimeException(
                "Do not use setAdapter(Adapter),instead using setAdapter(ExpandableAdapter)");
    }

    /**
     * <b>do not use this method,please instead using {@link #getExpandableAdapter()}</b>
     */
    @Override
    public Adapter getAdapter() {
        return super.getAdapter();
    }

    public void setAdapter(ExpandableAdapter adapter) {
        mAdapter = adapter;
        super.setAdapter(mAdapter);
    }

    public ExpandableAdapter getExpandableAdapter() {
        return mAdapter;
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
         * 上下文菜单锚点 View，也就是 RecyclerView 的直接 childView
         */
        public View targetView;

        /**
         * 一个编码过的位置信息，包含 item 的 type，parentPosition 和 childPosition(如果该 item 为 child 的话)信息
         */
        //TODO performance
        public long packedPosition;

        /**
         * RecyclerView 的直接 childView 的 row id
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
