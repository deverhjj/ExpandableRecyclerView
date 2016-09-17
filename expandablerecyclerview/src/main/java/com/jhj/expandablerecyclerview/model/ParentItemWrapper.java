package com.jhj.expandablerecyclerview.model;

import java.util.List;

/**
 * 客户端父列表包装类，包装客户端父列表项相关的数据，与客户端数据模型分离达到不影响客户端模型数据的前提下
 * 去实现本 Lib 所要实现的业务逻辑
 * Created by jhj_Plus on 2015/12/23.
 */
public class ParentItemWrapper {
    private static final String TAG = "ParentItemWrapper";
    /**
     * 客户端的父列表项集合
     */
    private ParentItem mParentItem;
    /**
     * 当前父列表项是否已展开
     */
    private boolean mExpanded;

    public ParentItemWrapper(ParentItem parentItem) {
        mParentItem = parentItem;
    }

    /**
     * 获取包装前的父列表项
     * @return
     */
    public ParentItem getParentItem() {
        return mParentItem;
    }

    /**
     * 包装原始父列表项
     * @param parentItem
     */
    public void setParentItem(ParentItem parentItem)
    {
        mParentItem = parentItem;
    }

    /**
     * 当前父列表项是否已经展开
     * @return
     */
    public boolean isExpanded() {
        return mExpanded;
    }

    /**
     * 设置当前父列表项是否已展开
     * @param expanded
     */
    public void setExpanded(boolean expanded) {
        mExpanded = expanded;
    }

    /**
     * 回调获取客户端父列表项初始化时是否展开
     * @return
     */
    public boolean isInitiallyExpanded() {
        return mParentItem.isInitiallyExpanded();
    }

    /**
     * 回调获取属于该父列表项的所有子列表项
     * @return
     */
    public List<?> getChildItems() {
        return mParentItem.getChildItems();
    }
}
