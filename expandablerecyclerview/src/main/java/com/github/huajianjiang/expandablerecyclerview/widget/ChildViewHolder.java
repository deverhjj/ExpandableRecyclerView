package com.github.huajianjiang.expandablerecyclerview.widget;

import android.view.View;

import com.github.huajianjiang.baserecyclerview.viewholder.BaseViewHolder;


/**
 * <p>
 * 子列表项 ChildViewHolder ，用于实现子列表项视图的创建
 * 客户端 ChildViewHolder 应该实现该类实现可扩展的 {@code RecyclerView}
 * </p>
 * <p>
 * Created by jhj_Plus on 2015/12/23.
 */
public class ChildViewHolder<P extends Parent, C> extends BaseViewHolder
        implements ExpandableViewHolderCallback<ChildViewHolder, P, C>
{
    private static final String TAG = "ChildViewHolder";

    private ExpandableAdapter mAdapter;

    public ChildViewHolder(View itemView) {
        super(itemView);
    }

    void connectAdapter(ExpandableAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onItemClick(BaseViewHolder vh, View v, int adapterPosition) {
        onExpandableItemClick(ChildViewHolder.this, v,
                (P) mAdapter.getParentForAdapterPosition(adapterPosition),
                (C) mAdapter.getChildForAdapterPosition(adapterPosition),
                mAdapter.getParentPosition(adapterPosition),
                mAdapter.getChildPosition(adapterPosition));
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean onItemLongClick(BaseViewHolder vh, View v, int adapterPosition) {
        return onExpandableItemLongClick(ChildViewHolder.this, v,
                (P) mAdapter.getParentForAdapterPosition(adapterPosition),
                (C) mAdapter.getChildForAdapterPosition(adapterPosition),
                mAdapter.getParentPosition(adapterPosition),
                mAdapter.getChildPosition(adapterPosition));
    }

    @Override
    public void onExpandableItemClick(ChildViewHolder childViewHolder, View v, P parent, C child,
                                      int parentPosition, int childPosition)
    {
        // do nothing, let client implement it
    }

    @Override
    public boolean onExpandableItemLongClick(ChildViewHolder childViewHolder, View v, P parent,
                                             C child, int parentPosition, int childPosition)
    {
        // do nothing, let client implement it
        return false;
    }
}
