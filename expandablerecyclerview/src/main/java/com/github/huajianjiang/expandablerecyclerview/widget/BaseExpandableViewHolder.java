package com.github.huajianjiang.expandablerecyclerview.widget;

import android.view.View;

import com.github.huajianjiang.baserecyclerview.viewholder.BaseViewHolder;

/**
 * @author HuaJian Jiang.
 *         Date 2017/1/20.
 */
public class BaseExpandableViewHolder extends BaseViewHolder {
    private static final String TAG = BaseExpandableViewHolder.class.getSimpleName();

    private ExpandableAdapter adapter;

    private boolean mReceiveExpandableItemEvent;

    public BaseExpandableViewHolder(View itemView) {
        super(itemView);
    }

    void connectAdapter(ExpandableAdapter adapter) {
        this.adapter = adapter;
    }

    public ExpandableAdapter getAssociateAdapter() {
        return adapter;
    }

    public boolean isReceiveExpandableItemEvent() {
        return mReceiveExpandableItemEvent;
    }

    public void setReceiveExpandableItemEvent(boolean receiveExpandableItemEvent) {
        mReceiveExpandableItemEvent = receiveExpandableItemEvent;
    }
}
