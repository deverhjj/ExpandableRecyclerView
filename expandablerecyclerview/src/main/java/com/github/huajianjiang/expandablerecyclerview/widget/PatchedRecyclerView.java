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

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Author: Huajian Jiang
 * <br>Date: 2017/8/6
 * <br>Email: developer.huajianjiang@gmail.com
 */
public class PatchedRecyclerView extends RecyclerView {
    private static final String TAG = PatchedRecyclerView.class.getSimpleName();

    protected ContextMenu.ContextMenuInfo mContextMenuInfo;

    private Adapter mAdapter;
    private EmptyDataObserver mObserver;
    private View mEmptyView;

    public PatchedRecyclerView(Context context) {
        this(context,null);
    }

    public PatchedRecyclerView(Context context, @Nullable AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public PatchedRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (mAdapter != null && mObserver != null) {
            mAdapter.unregisterAdapterDataObserver(mObserver);
        }

        mAdapter = adapter;

        if (mAdapter != null && mEmptyView != null) {
            mAdapter.registerAdapterDataObserver(getObserver());
        }

        super.setAdapter(adapter);
    }

    public void setEmptyView(View emptyView) {
        if (mEmptyView != null) {
            mEmptyView.setVisibility(GONE);
            if (mAdapter != null && mObserver != null) {
                mAdapter.unregisterAdapterDataObserver(mObserver);
            }
        }

        mEmptyView = emptyView;

        if (mAdapter != null && mEmptyView != null) {
            mAdapter.registerAdapterDataObserver(getObserver());
        }

        updateEmptyStatus(shouldShowEmptyView());
    }

    private boolean shouldShowEmptyView() {
        return mAdapter == null || mAdapter.getItemCount() == 0;
    }

    private void updateEmptyStatus(boolean empty) {
        if (empty) {
            if (mEmptyView == null) {
                setVisibility(View.VISIBLE);
            } else {
                setVisibility(View.GONE);
                mEmptyView.setVisibility(VISIBLE);
            }
        } else {
            if (mEmptyView != null) mEmptyView.setVisibility(GONE);
            setVisibility(View.VISIBLE);
        }
    }

    private EmptyDataObserver getObserver() {
        if (mObserver == null) {
            mObserver = new EmptyDataObserver();
        }
        return mObserver;
    }


    private class EmptyDataObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            updateEmptyStatus(shouldShowEmptyView());
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {onChanged();}

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {onChanged();}

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {onChanged();}

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {onChanged();}

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {onChanged();}
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
         * 上下文菜单锚点 View，也就是 RecyclerView 的直接 childView
         */
        public View targetView;
        /**
         * 构建上下文菜单的 RecyclerView 的直接 childView 的位置
         */
        public int position;
        /**
         *  RecyclerView 的直接 childView 的 row id
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
