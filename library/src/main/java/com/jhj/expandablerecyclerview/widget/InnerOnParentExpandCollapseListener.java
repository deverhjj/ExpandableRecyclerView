package com.jhj.expandablerecyclerview.widget;

/**
 * Created by jhj_Plus on 2016/9/13.
 */
interface InnerOnParentExpandCollapseListener {
    /**
     * 父列表项展开后的回调
     *
     * @param pvh 被展开的父列表
     */
    boolean onParentExpand(ParentViewHolder pvh);

    /**
     * 父列表项折叠后的回调
     *
     * @param pvh 被折叠的父列表
     */
    boolean onParentCollapse(ParentViewHolder pvh);
}
