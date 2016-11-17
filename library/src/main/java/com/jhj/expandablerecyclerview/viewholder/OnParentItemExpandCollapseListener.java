package com.jhj.expandablerecyclerview.viewholder;

/**
 * Created by jhj_Plus on 2016/9/13.
 */
public interface OnParentItemExpandCollapseListener {
    /**
     * 父列表项展开后的回调
     *
     * @param parentAdapterPosition 父列表项在适配器里对应的位置
     */
    boolean onParentItemExpand(int parentAdapterPosition);

    /**
     * 父列表项折叠后的回调
     *
     * @param parentAdapterPosition 父列表项在适配器里对应的位置
     */
    boolean onParentItemCollapse(int parentAdapterPosition);
}
