package com.github.huajianjiang.expandablerecyclerview.widget;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

import com.github.huajianjiang.expandablerecyclerview.util.Packager;

/**
 * @author HuaJian Jiang.
 *         Date 2017/1/20.
 */
public class BaseExpandableViewHolder extends RecyclerView.ViewHolder
        implements ExpandableViewHolderCallback
{
    private static final String TAG = BaseExpandableViewHolder.class.getSimpleName();
    /**
     * ItemView 的 childView 缓存
     * 便于根据 id 查找对应的 View
     * 如果该缓存里没有查找到该 childView 就先 findViewById 再缓存下来
     */
    private SparseArray<View> mCachedViews = new SparseArray<>();
    /**
     * the {@link RecyclerView} this {@code ViewHolder} associate with
     */
    private RecyclerView mRv;
    /**
     * this {@code ViewHolder} associate with the adapter
     */
    private ExpandableAdapter mAdapter;

    public BaseExpandableViewHolder(View itemView) {
        super(itemView);
    }

    private void initView() {
        if (mAdapter == null) return;
        final View iv = itemView;
        if (iv.isEnabled() && iv.isClickable())
            iv.setOnClickListener(mAdapter.getViewEventWatcher());
        if (iv.isEnabled() && iv.isLongClickable())
            iv.setOnLongClickListener(mAdapter.getViewEventWatcher());
        //注册 子 view 点击监听器
        int[] clickViewIds = onRegisterClickEvent(mRv);
        if (clickViewIds != null)
            for (int id : clickViewIds) {
                View v = getView(id);
                if (v != null) v.setOnClickListener(mAdapter.getViewEventWatcher());
            }
        //注册 子 view 长按监听器
        int[] longClickViewIds = onRegisterLongClickEvent(mRv);
        if (longClickViewIds != null)
            for (int id : longClickViewIds) {
                View v = getView(id);
                if (v != null) v.setOnLongClickListener(mAdapter.getViewEventWatcher());
            }
    }

    /**
     * 根据 id 查找 ItemView 里 childView
     * @param id ItemView 里 childView 的 id
     * @return ItemView 里的 childView
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int id) {
        if (id == View.NO_ID) return null;
        final View iv = itemView;
        View v = mCachedViews.get(id);
        if (v == null) {
            if (id == iv.getId()) {
                v = iv;
            } else {
                v = iv.findViewById(id);
            }
            if (v != null) mCachedViews.put(id, v);
        }
        return (T) v;
    }

    void connectAdapter(RecyclerView rv, ExpandableAdapter adapter) {
        this.mRv = rv;
        this.mAdapter = adapter;
        initView();
    }

    /**
     * get the {@link RecyclerView} this {@code ViewHolder} associate with
     * @return associate {@link RecyclerView}
     */
    RecyclerView getAssociateRecyclerView() {
        return mRv;
    }

    /**
     * get the {@link ExpandableAdapter} this {@link android.support.v7.widget.RecyclerView.ViewHolder}
     * associate with
     * @return associate {@link ExpandableAdapter}
     */
    public ExpandableAdapter getAssociateAdapter() {
        return mAdapter;
    }

    /**
     * you should use this method to get {@link android.support.v7.widget.RecyclerView.ViewHolder} associate view type
     *
     * @return The view type of this {@link android.support.v7.widget.RecyclerView.ViewHolder}.
     */
    public int getType() {
        return Packager.getClientViewType(getItemViewType());
    }

    @Override
    public int[] onRegisterClickEvent(RecyclerView rv) {return null;}

    @Override
    public void onItemClick(RecyclerView rv, View v) {}

    @Override
    public int[] onRegisterLongClickEvent(RecyclerView rv) {return null;}

    @Override
    public boolean onItemLongClick(RecyclerView rv, View v) {return false;}
}
