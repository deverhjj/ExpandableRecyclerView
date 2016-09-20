package com.jhj.expandablerecyclerviewexample.model;


import com.jhj.expandablerecyclerview.model.ParentItem;

import java.util.List;

/**
 * Created by jhj_Plus on 2016/9/2.
 */
public class Parent implements ParentItem<Child> {
    private static final String TAG = "Parent";

    private boolean isExpandable=false;

    private boolean isInitiallyExpanded=true;

    private int dot;

    private int type;

    private String info;

    private List<Child> mChildren;

    public void setChildren(List<Child> children)
    {
        mChildren = children;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDot() {
        return dot;
    }

    public void setDot(int dot) {
        this.dot = dot;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public List<Child> getChildItems() {
        return mChildren;
    }

    public void setExpandable(boolean expandable) {
        isExpandable = expandable;
    }

    public void setInitiallyExpanded(boolean initiallyExpanded) {
        isInitiallyExpanded = initiallyExpanded;
    }

    @Override
    public boolean isExpandable() {
        return isExpandable;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return isInitiallyExpanded;
    }

}
