package com.jhj.expandablerecyclerviewexample;

import android.graphics.Color;
import android.support.annotation.NonNull;

import com.jhj.expandablerecyclerview.adapter.ExpandableAdapter;
import com.jhj.expandablerecyclerview.utils.Logger;
import com.jhj.expandablerecyclerviewexample.model.Child;
import com.jhj.expandablerecyclerviewexample.model.Parent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by jhj_Plus on 2016/9/2.
 */
public class PresenterImpl implements IPresenter {
    private static final String TAG = "PresenterImpl";

    private Random mRandom=new Random();

    private ExpandableAdapter mAdapter;
    private List<Parent> mData;

    public PresenterImpl(ExpandableAdapter adapter,List<Parent> data) {
        mAdapter=adapter;
        mData = data;
    }

    /**
     * 因为数据结构发生改变，所有更新所有的列表项数据
     */
    public void autoNotifyAllChanged() {
        //mAdapter.notifyAllChanged();
    }

    @NonNull
    private Parent getParentItem() {
        Parent parent =new Parent();
        parent.setType(mRandom.nextInt(2));
        boolean hasChild=mRandom.nextBoolean();
        if (hasChild) {
            List<Child> children =new ArrayList<>();
            final int childCount=mRandom.nextInt(6);
            for (int i = 0; i < childCount; i++) {
                Child child =new Child();
                child.setType(mRandom.nextInt(2));
                children.add(child);
            }
            parent.setChildren(children);
        }
        return parent;
    }

    @Override
    public void notifyParentItemInserted(int parentPosition) {
        notifyParentItemRangeInserted(parentPosition,1);
        Logger.e(TAG,"notifyParentItemInserted");
    }

    @Override
    public void notifyParentItemRangeInserted(int parentPositionStart, int parentItemCount) {
        for (int i = parentPositionStart; i < parentPositionStart + parentItemCount; i++) {
            Parent parent =getParentItem();
            mData.add(i, parent);
        }
        mAdapter.notifyParentItemRangeInserted(parentPositionStart,parentItemCount);
        autoNotifyAllChanged();
        Logger.e(TAG,"notifyParentItemInserted");
    }

    @Override
    public void notifyChildItemInserted(int parentPosition, int childPosition) {
        notifyChildItemRangeInserted(parentPosition,childPosition,1);
    }

    @Override
    public void notifyChildItemRangeInserted(int parentPosition, int childPositionStart,
            int childItemCount)
    {
        Parent parent =mData.get(parentPosition);
        List<Child> children = checkChildItems(parent);
        for (int i = childPositionStart; i <childPositionStart+childItemCount ; i++) {
            Child child =new Child();
            child.setType(mRandom.nextInt(2));
            children.add(i, child);
        }

        Logger.e(TAG,"children="+ children.size());

        mAdapter.notifyChildItemRangeInserted(parentPosition,childPositionStart,childItemCount);
        autoNotifyAllChanged();
    }

    @Override
    public void notifyParentItemRemoved(int parentPosition) {
        notifyParentItemRangeRemoved(parentPosition,1);
        Logger.e(TAG,"notifyParentItemRemoved");
    }

    @Override
    public void notifyParentItemRangeRemoved(int parentPositionStart, int parentItemCount) {

        for (int i = parentPositionStart; i < parentPositionStart + parentItemCount; i++) {
            //注意这里删除数据的 index 不是 i
            mData.remove(parentPositionStart);
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
        Parent parent =mData.get(parentPosition);
        List<Child> children = parent.getChildItems();
        Logger.e(TAG,"children="+ children.size());
        for (int i = childPositionStart; i <childPositionStart+childItemCount ; i++) {
            //注意这里删除数据的 index 不是 i
            children.remove(childPositionStart);
        }

        mAdapter.notifyChildItemRangeRemoved(parentPosition,childPositionStart,childItemCount,false);

        autoNotifyAllChanged();
    }

    @Override
    public void notifyParentItemChanged(int parentPosition) {
        notifyParentItemRangeChanged(parentPosition,1);
    }

    @Override
    public void notifyChildItemChanged(int parentPosition, int childPosition) {
        notifyChildItemRangeChanged(parentPosition,childPosition,1);
    }

    @Override
    public void notifyParentItemRangeChanged(int parentPositionStart, int parentItemCount) {
        for (int i = parentPositionStart; i < parentPositionStart+parentItemCount; i++) {
            Parent parent =mData.get(i);
            parent.setDot(Color.argb(255,mRandom.nextInt(256),mRandom.nextInt(256),mRandom.nextInt(256)));
        }

        mAdapter.notifyParentItemRangeChanged(parentPositionStart,parentItemCount);
    }

    @Override
    public void notifyChildItemRangeChanged(int parentPosition, int childPositionStart,
            int childItemCount)
    {
        Parent parent =mData.get(parentPosition);
        List<Child> children = parent.getChildItems();
        for (int i = childPositionStart; i < childPositionStart + childItemCount; i++) {
            Child child = children.get(i);
            child.setDot(Color.argb(255, mRandom.nextInt(256), mRandom.nextInt(256),
                    mRandom.nextInt(256)));
        }
        mAdapter.notifyChildItemRangeChanged(parentPosition,childPositionStart,childItemCount);
    }

    @Override
    public void notifyParentItemMoved(int fromParentPosition, int toParentPosition) {
        Parent fromParent =mData.get(fromParentPosition);
        Logger.e(TAG, "before" + mData);
        mData.remove(fromParentPosition);
        mData.add(toParentPosition, fromParent);
        Logger.e(TAG, "after" + mData);

        mAdapter.notifyParentItemMoved(fromParentPosition,toParentPosition);
    }

    @Override
    public void notifyChildItemMoved(int fromParentPosition, int fromChildPosition,
            int toParentPosition, int toChildPosition)
    {
        Parent fromParent =mData.get(fromParentPosition);
        Parent toParent =mData.get(toParentPosition);

        //from-> 先 get 再 remove
        Child fromChild = checkChildItems(fromParent).get(fromChildPosition);
        Logger.e(TAG, "before_fromParent.getChildItems()" + "\n" + fromParent.getChildItems()
                +"\n"+"before_toParent.getChildItems()"+"\n"+toParent.getChildItems());
        fromParent.getChildItems().remove(fromChildPosition);
        //to-> add fromChild
        checkChildItems(toParent).add(toChildPosition, fromChild);

        Logger.e(TAG,"notifyChildItemMoved"+"\n"+"after_fromParent.getChildItems()="+fromParent
                .getChildItems()+"\n"+"toParent.getChildItems()="+toParent.getChildItems());

        mAdapter.notifyChildItemMoved(fromParentPosition, fromChildPosition, toParentPosition,
                toChildPosition);
    }

    private List<Child> checkChildItems(Parent parent) {
        List<Child> childItems = parent.getChildItems();
        if (childItems == null) {
            childItems = new ArrayList<>();
            parent.setChildren(childItems);
        }
        return childItems;
    }
}
