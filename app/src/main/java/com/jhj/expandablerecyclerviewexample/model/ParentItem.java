package com.jhj.expandablerecyclerviewexample.model;


import com.jhj.expandablerecyclerview.model.ParentListItem;

import java.util.List;

/**
 * Created by jhj_Plus on 2016/9/2.
 */
public class ParentItem implements ParentListItem {
    private static final String TAG = "ParentItem";

    private int dot;

    private int type;
    private int pos;
    private int adapterPos;

    private List<ChildItem> mChildItems;

    public void setChildItems(List<ChildItem> childItems)
    {
        mChildItems = childItems;
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
    public List<ChildItem> getChildItemList() {
        return mChildItems;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
