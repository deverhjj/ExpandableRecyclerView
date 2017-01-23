package com.github.huajianjiang.expandablerecyclerview.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.huajianjiang.expandablerecyclerview.util.Logger;


/**
 * <p>父列表项 ViewHolder，监听父列表项的点击事件并根据当前展开或收缩状态触发父列表项展开或折叠事件
 * 客户端父列表项 ViewHolder 应该继承它实现可展开的 {@code RecyclerView}
 * </p>
 * Created by jhj_Plus on 2015/12/23.
 */
public class ParentViewHolder extends BaseExpandableViewHolder
{
    private static final String TAG = "ParentViewHolder";

    private InnerOnParentExpandCollapseListener mExpandCollapseListener;

    /**
     * 是否该可展开折叠
     */
    private boolean mExpandable = false;

    /**
     * 设置当前父列表项是否已展开
     */
    private boolean mExpanded = false;

    public ParentViewHolder(View itemView) {
        super(itemView);
        // make sure we can receive parent item click callback
        itemView.setEnabled(true);
        itemView.setClickable(true);
    }

    public boolean isExpandable() {
        return mExpandable;
    }

    void setExpandable(boolean expandable) {
        if (expandable == mExpandable) return;
        mExpandable = expandable;
    }

    /**
     * 返回当前父列表项是否已展开
     * @return 父列表项是否已经展开
     */
    public boolean isExpanded() {
        return mExpanded;
    }

    /**设置父列表项是否已展开
     * @param expanded 父列表项是否已展开
     */
    void setExpanded(boolean expanded) {
        if (expanded == mExpanded) return;
        mExpanded = expanded;
    }

    /**
     * 设置 该 ItemView 点击展开折叠状态
     * @param listener
     */
    void setOnParentExpandCollapseListener(InnerOnParentExpandCollapseListener listener) {
        mExpandCollapseListener = listener;
    }

    /**
     * 当父列表项点击时的回调,包括之前注册的在父列表项里的子 view 的点击监听
     * <p>
     *     make sure call super {@link #onItemClick(RecyclerView rv,BaseExpandableViewHolder, View)} method
     * </p>
     * @param v
     */
    @Override
    @SuppressWarnings("unchecked")
    public void onItemClick(RecyclerView rv, BaseExpandableViewHolder vh, View v) {
        if (v != itemView) return;
        if (mExpanded) {
            collapseParent();
        } else {
            expandParent();
        }
    }

    /**
     * 展开父列表项
     */
    private void expandParent() {
        if (mExpandCollapseListener != null) {
            setExpanded(mExpandCollapseListener.onParentExpand(ParentViewHolder.this));
            Logger.e(TAG, "*******expandParent*******>expanded=" + mExpanded + ",expandable=" +
                          mExpandable);
        }
    }

    /**
     * 折叠父列表项
     */
    private void collapseParent() {
        if (mExpandCollapseListener != null) {
            setExpanded(!mExpandCollapseListener.onParentCollapse(ParentViewHolder.this));
            Logger.e(TAG, "*******collapseParent*******>expanded=" + mExpanded + ",expandable=" +
                          mExpandable);
        }
    }
}
