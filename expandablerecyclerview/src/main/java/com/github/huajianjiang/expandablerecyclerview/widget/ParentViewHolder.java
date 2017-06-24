package com.github.huajianjiang.expandablerecyclerview.widget;

import android.view.View;


/**
 * <p>父列表项 ViewHolder，监听父列表项的点击事件并根据当前展开或收缩状态触发父列表项展开或折叠事件
 * 客户端父列表项 ViewHolder 应该继承它实现可展开的 {@code RecyclerView}
 * </p>
 * Created by jhj_Plus on 2015/12/23.
 */
public class ParentViewHolder<P extends Parent> extends BaseViewHolder {
    private static final String TAG = "ParentViewHolder";

    private int parentPosition;

    private P parent;

    /**
     * 是否该可展开折叠
     */
    private boolean mExpandable = true;

    /**
     * 设置当前父列表项是否已展开
     */
    private boolean mExpanded = false;

    public ParentViewHolder(View itemView) {
        super(itemView);
    }

    public boolean isExpandable() {
        return mExpandable;
    }

    boolean setExpandable(boolean expandable) {
        if (expandable == mExpandable) return false;
        mExpandable = expandable;
        return true;
    }

    /**
     * 返回当前父列表项是否已展开
     *
     * @return 父列表项是否已经展开
     */
    public boolean isExpanded() {
        return mExpanded;
    }

    /**
     * 设置父列表项是否已展开
     *
     * @param expanded 父列表项是否已展开
     */
    boolean setExpanded(boolean expanded) {
        if (expanded == mExpanded) return false;
        mExpanded = expanded;
        return true;
    }

    /**
     * 返回该 parent 的位置，不一定是同步过的,例如在 parent move 之后该值并没有同步更新，因此需要重新查询
     *
     * @return parent 的折叠位置
     */
    public int getParentPosition() {
        return parentPosition;
    }

    void setParentPosition(int parentPosition) {
        this.parentPosition = parentPosition;
    }

    public P getParent() {
        return parent;
    }

    void setParent(P parent) {
        this.parent = parent;
    }
}
