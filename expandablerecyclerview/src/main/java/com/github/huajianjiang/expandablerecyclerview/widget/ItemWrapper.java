package com.github.huajianjiang.expandablerecyclerview.widget;

import java.util.List;

/**
 * 客户端列表项模型层类的包装类，包装客户端父列表/子列表项项相关的数据，与客户端数据模型分离达到不影响客户端模型数据的前提下
 * 去实现本 lib 所要实现的业务逻辑
 * Created by jhj_Plus on 2015/12/23.
 */
class ItemWrapper<P extends Parent, C> {
    private static final String TAG = "ItemWrapper";
    /**
     * 客户端的父列表项模型类
     */
    private P mParent;
    /**
     * 客户端的子列表项模型类
     */
    private C mChild;

    private boolean mExpandable;

    /**
     * 当前父列表项是否已展开
     */
    private boolean mExpanded;

    ItemWrapper(P parent) {
        mParent = parent;
        init();
    }

    ItemWrapper(C child) {
        mChild = child;
    }

    private void init() {
        mExpandable = mParent.isInitiallyExpandable() && hasChildren();
        mExpanded = mParent.isInitiallyExpanded() && hasChildren();
    }

    public boolean isParent() {
        return mParent != null && mChild == null;
    }

    public boolean isChild() {
        return mChild != null && mParent == null;
    }

    /**
     * 获取包装前的父列表项
     * @return
     */
    public P getParent() {
        checkParentType();
        return mParent;
    }

    /**
     * 包装原始父列表项
     * @param parent
     */
    public void setParent(P parent)
    {
        mParent = parent;
        init();
    }

    public C getChild() {
        return mChild;
    }

    public void setChild(C child) {
        mChild = child;
    }

    /**
     * 当前父列表项是否已经展开
     * @return
     */
    boolean isExpanded() {
        checkParentType();
        return mExpanded;
    }

    /**
     * 设置当前父列表项是否已展开
     * @param expanded
     */
    boolean setExpanded(boolean expanded) {
        checkParentType();
        if (mExpanded == expanded) return false;
        mExpanded = expanded;
        return true;
    }

    boolean isExpandable() {
        checkParentType();
        return mExpandable;
    }

    boolean setExpandable(boolean expandable) {
        checkParentType();
        if (mExpandable == expandable) return false;
        mExpandable = expandable;
        return true;
    }

    boolean isInitiallyExpandable() {
        checkParentType();
        return mParent.isInitiallyExpandable();
    }

    /**
     * 回调获取客户端父列表项初始化时是否展开
     * @return
     */
    boolean isInitiallyExpanded() {
        checkParentType();
        return mParent.isInitiallyExpanded();
    }

    /**
     * 回调获取属于该父列表项的所有子列表项
     * @return
     */
    List<C> getChildren() {
        checkParentType();
        return mParent.getChildren();
    }

    boolean hasChildren() {
        List<C> children = getChildren();
        return hasChildren(children);
    }

    boolean hasChildren(List<C> who) {
        return who != null && !who.isEmpty();
    }

    int getChildCount() {
        List<C> children = getChildren();
        return getChildCount(children);
    }

    int getChildCount(List<C> who) {
        return who != null ? who.size() : 0;
    }

    private void checkParentType() {
        if (mParent == null || mChild != null) {
            throw new IllegalStateException(
                    "Illegal type, attempt access parent from no parent " + "type");
        }
    }

}
