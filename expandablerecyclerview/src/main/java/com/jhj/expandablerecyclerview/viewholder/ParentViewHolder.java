package com.jhj.expandablerecyclerview.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * <p>父列表项 ViewHolder，监听父列表项的点击事件并根据当前展开或收缩状态触发父列表项展开或折叠事件
 * 客户端父列表项 ViewHolder 应该继承它实现可展开的 {@code RecyclerView}
 * </p>
 * Created by jhj_Plus on 2015/12/23.
 */
public class ParentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = "ParentViewHolder";

    /**
     * 父列表项展开折叠监听器
     */
    private OnParentListItemExpandCollapseListener mParentListItemExpandCollapseListener;
    /**
     * 当前父列表项是否已展开
     */
    private boolean mExpanded;

    private boolean mExpandable=true;

    public ParentViewHolder(View itemView) {
        super(itemView);
        mExpanded=false;
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
    public void setExpanded(boolean expanded) {
        mExpanded = expanded;
    }

    /**
     * 父列表项点击展开收缩事件监听器
     */
    public interface OnParentListItemExpandCollapseListener {
        /**
         * 父列表项展开后的回调
         * @param parentAdapterPosition 父列表项在适配器里对应的位置
         */
        void onParentListItemExpanded(int parentAdapterPosition);

        /**
         * 父列表项折叠后的回调
         * @param parentAdapterPosition 父列表项在适配器里对应的位置
         */
        void onParentListItemCollapsed(int parentAdapterPosition);
    }

    /**
     * 获取已注册的监听父列表项点击展开监听器
     * @return
     */
    public OnParentListItemExpandCollapseListener getParentListItemExpandCollapseListener() {
        return mParentListItemExpandCollapseListener;
    }

    /**
     * 注册一个监听父列表项点击展开的监听器
     * @param parentListItemExpandCollapseListener 父列表项点击展开收缩监听器
     */
    public void setParentListItemExpandCollapseListener(
            OnParentListItemExpandCollapseListener parentListItemExpandCollapseListener)
    {
        mParentListItemExpandCollapseListener = parentListItemExpandCollapseListener;
    }

    /**
     * 注册一个父列表项的点击事件监听器
     */
    public void setParentListItemOnClickListener() {

        itemView.setOnClickListener(this);
    }

    /**
     * 父列表项点击回调，根据当前父列表项的展开折叠状态触发父列表项的展开折叠事件
     * @param v 被点击的父列表项视图 {@link View}
     */
    @Override
    public void onClick(View v) {
        if (mExpanded) {
            collapseParentListItem();
        } else {
            expandParentListItem();
        }
    }

    /**
     * 展开父列表项
     */
    private void expandParentListItem() {
        setExpanded(true);
        if (mParentListItemExpandCollapseListener != null) {
            mParentListItemExpandCollapseListener.onParentListItemExpanded(getAdapterPosition());
        }
    }

    /**
     * 折叠父列表项
     */
    private void collapseParentListItem() {
        setExpanded(false);
        if (mParentListItemExpandCollapseListener != null) {
            mParentListItemExpandCollapseListener.onParentListItemCollapsed(getAdapterPosition());
        }
    }
}
