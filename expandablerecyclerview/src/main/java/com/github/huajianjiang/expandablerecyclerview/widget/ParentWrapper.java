package com.github.huajianjiang.expandablerecyclerview.widget;

import java.util.List;

/**
 * 客户端父列表包装类，包装客户端父列表项相关的数据，与客户端数据模型分离达到不影响客户端模型数据的前提下
 * 去实现本 Lib 所要实现的业务逻辑
 * Created by jhj_Plus on 2015/12/23.
 */
class ParentWrapper {
    private static final String TAG = "ParentWrapper";
    /**
     * 客户端的父列表项模型类
     */
    private Parent mParent;

    /**
     * 当前父列表项是否已展开
     */
    private boolean mExpanded = false;

    ParentWrapper(Parent parent) {
        mParent = parent;
    }

    /**
     * 获取包装前的父列表项
     * @return
     */
    public Parent getParent() {
        return mParent;
    }

    /**
     * 包装原始父列表项
     * @param parent
     */
    public void setParent(Parent parent)
    {
        mParent = parent;
    }


    /**
     * 当前父列表项是否已经展开
     * @return
     */
    boolean isExpanded() {
        return mExpanded;
    }

    /**
     * 设置当前父列表项是否已展开
     * @param expanded
     */
    void setExpanded(boolean expanded) {
        mExpanded = expanded;
    }


    boolean isExpandable() {
        return mParent.isExpandable() && hasChildren();
    }

    /**
     * 回调获取客户端父列表项初始化时是否展开
     * @return
     */
    boolean isInitiallyExpanded() {
        return mParent.isInitiallyExpanded();
    }

    /**
     * 回调获取属于该父列表项的所有子列表项
     * @return
     */
    List<?> getChildren() {
        return mParent.getChildren();
    }

    boolean hasChildren() {
        List<?> children = getChildren();
        return hasChildren(children);
    }

    boolean hasChildren(List<?> who) {
        return who != null && !who.isEmpty();
    }

    int getChildCount() {
        List<?> children = getChildren();
        return getChildCount(children);
    }

    int getChildCount(List<?> who) {
        return who != null ? who.size() : 0;
    }

}
