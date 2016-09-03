package com.jhj.expandablerecyclerviewexample;

import android.graphics.Color;
import android.support.annotation.NonNull;

import com.jhj.expandablerecyclerview.adapter.ExpandableRecyclerViewAdapter;
import com.jhj.expandablerecyclerviewexample.model.ChildItem;
import com.jhj.expandablerecyclerviewexample.model.ParentItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by jhj_Plus on 2016/9/2.
 */
public class PresenterImpl implements IPresenter {
    private static final String TAG = "PresenterImpl";

    private Random mRandom=new Random();

    private ExpandableRecyclerViewAdapter mAdapter;
    private List<ParentItem> mData;

    public PresenterImpl(ExpandableRecyclerViewAdapter adapter,List<ParentItem> data) {
        mAdapter=adapter;
        mData = data;
    }

    /**
     * 因为数据结构发生改变，所有更新所有的列表项数据
     */
    public void autoNotifyAllChanged() {
//        mAdapter.notifyAllChanged();
    }

    @NonNull
    private ParentItem getParentItem() {
        ParentItem parentItem=new ParentItem();
        parentItem.setType(mRandom.nextInt(2));
        boolean hasChild=mRandom.nextBoolean();
        if (hasChild) {
            List<ChildItem> childItems=new ArrayList<>();
            final int childCount=mRandom.nextInt(6);
            for (int i = 0; i < childCount; i++) {
                ChildItem childItem=new ChildItem();
                childItem.setType(mRandom.nextInt(2));
                childItems.add(childItem);
            }
            parentItem.setChildItems(childItems);
        }
        return parentItem;
    }

    @Override
    public void notifyParentItemInserted(int parentPosition) {
        notifyParentItemRangeInserted(parentPosition,1);
    }

    @Override
    public void notifyParentItemRangeInserted(int parentPositionStart, int parentItemCount) {
        for (int i = parentPositionStart; i < parentPositionStart + parentItemCount; i++) {
            ParentItem parentItem=getParentItem();
            mData.add(i,parentItem);
        }
        mAdapter.notifyParentItemRangeInserted(parentPositionStart,parentItemCount);
        autoNotifyAllChanged();
    }

    @Override
    public void notifyChildItemInserted(int parentPosition, int childPosition) {
        notifyChildItemRangeInserted(parentPosition,childPosition,1);
    }

    @Override
    public void notifyChildItemRangeInserted(int parentPosition, int childPositionStart,
            int childItemCount)
    {
        ParentItem parentItem=mData.get(parentPosition);
        List<ChildItem> childItems=parentItem.getChildItemList();
        if (childItems==null) {
            childItems=new ArrayList<>();
            parentItem.setChildItems(childItems);
        }
        for (int i = childPositionStart; i <childPositionStart+childItemCount ; i++) {
            ChildItem childItem=new ChildItem();
            childItem.setType(mRandom.nextInt(2));
            childItems.add(i,childItem);
        }
        mAdapter.notifyChildItemRangeInserted(parentPosition,childPositionStart,childItemCount);
        autoNotifyAllChanged();
    }

    @Override
    public void notifyParentItemRemoved(int parentPosition) {
        notifyParentItemRangeRemoved(parentPosition,1);
    }

    @Override
    public void notifyParentItemRangeRemoved(int parentPositionStart, int parentItemCount) {
        for (int i = parentPositionStart; i < parentPositionStart + parentItemCount; i++) {
            mData.remove(i);
        }
        mAdapter.notifyParentItemRangeRemoved(parentPositionStart,parentItemCount);
        autoNotifyAllChanged();
    }

    @Override
    public void notifyChildItemRemoved(int parentPosition, int childPosition) {
        notifyChildItemRangeRemoved(parentPosition,childPosition,1);
    }

    @Override
    public void notifyChildItemRangeRemoved(int parentPosition, int childPositionStart,
            int childItemCount)
    {
        ParentItem parentItem=mData.get(parentPosition);
        List<ChildItem> childItems=parentItem.getChildItemList();
        for (int i = childPositionStart; i <childPositionStart+childItemCount ; i++) {
            childItems.remove(i);
        }
        mAdapter.notifyChildItemRangeRemoved(parentPosition,childPositionStart,childItemCount);
        autoNotifyAllChanged();
    }

    @Override
    public void notifyParentItemChanged(int parentPosition) {
        ParentItem parentItem=mData.get(parentPosition);
        parentItem.setDot(Color.argb(255,mRandom.nextInt(256),mRandom.nextInt(256),mRandom.nextInt(256)));
        mAdapter.notifyParentItemChanged(parentPosition);
    }

    @Override
    public void notifyChildItemChanged(int parentPosition, int childPosition) {
        ParentItem parentItem=mData.get(parentPosition);
        List<ChildItem> childItems=parentItem.getChildItemList();
        ChildItem childItem=childItems.get(childPosition);
        childItem.setDot(Color.argb(255,mRandom.nextInt(256),mRandom.nextInt(256),mRandom.nextInt(256)));
        mAdapter.notifyChildItemChanged(parentPosition,childPosition);
    }
}
