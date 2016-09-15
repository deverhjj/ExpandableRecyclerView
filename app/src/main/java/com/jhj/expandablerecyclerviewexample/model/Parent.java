package com.jhj.expandablerecyclerviewexample.model;


import com.jhj.expandablerecyclerview.model.ParentItem;

import java.util.List;

/**
 * Created by jhj_Plus on 2016/9/2.
 */
public class Parent implements ParentItem<Child> {
    private static final String TAG = "Parent";

    private int dot;

    private int type;
    private int pos;
    private int adapterPos;

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

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getAdapterPos() {
        return adapterPos;
    }

    public void setAdapterPos(int adapterPos) {
        this.adapterPos = adapterPos;
    }


    @Override
    public List<Child> getChildItems() {
        return mChildren;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
