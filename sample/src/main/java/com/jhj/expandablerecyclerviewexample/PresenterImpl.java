package com.jhj.expandablerecyclerviewexample;

import android.graphics.Color;
import android.support.annotation.NonNull;

import com.jhj.expandablerecyclerview.util.Logger;
import com.jhj.expandablerecyclerview.widget.ExpandableAdapter;
import com.jhj.expandablerecyclerviewexample.model.MyChild;
import com.jhj.expandablerecyclerviewexample.model.MyParent;

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
    private List<MyParent> mData;

    public PresenterImpl(ExpandableAdapter adapter,List<MyParent> data) {
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
    private MyParent getParentItem() {
        MyParent myParent =new MyParent();
        myParent.setType(mRandom.nextInt(2));
        boolean hasChild=mRandom.nextBoolean();
        if (hasChild) {
            List<MyChild> myChildren =new ArrayList<>();
            final int childCount=mRandom.nextInt(6);
            for (int i = 0; i < childCount; i++) {
                MyChild myChild =new MyChild();
                myChild.setType(mRandom.nextInt(2));
                myChildren.add(myChild);
            }
            myParent.setMyChildren(myChildren);
        }
        return myParent;
    }

    @Override
    public void notifyParentItemInserted(int parentPosition) {
        notifyParentItemRangeInserted(parentPosition,1);
        Logger.e(TAG,"notifyParentItemInserted");
    }

    @Override
    public void notifyParentItemRangeInserted(int parentPositionStart, int parentItemCount) {
        for (int i = parentPositionStart; i < parentPositionStart + parentItemCount; i++) {
            MyParent myParent =getParentItem();
            mData.add(i, myParent);
        }
        mAdapter.notifyParentItemRangeInserted(parentPositionStart,parentItemCount);
        autoNotifyAllChanged();
        Logger.e(TAG,"notifyParentItemRangeInserted");
    }

    @Override
    public void notifyChildItemInserted(int parentPosition, int childPosition) {
        notifyChildItemRangeInserted(parentPosition,childPosition,1);
    }

    @Override
    public void notifyChildItemRangeInserted(int parentPosition, int childPositionStart,
            int childItemCount)
    {
        MyParent myParent =mData.get(parentPosition);
        List<MyChild> myChildren = checkChildItems(myParent);
        for (int i = childPositionStart; i <childPositionStart+childItemCount ; i++) {
            MyChild myChild =new MyChild();
            myChild.setType(mRandom.nextInt(2));
            myChildren.add(i, myChild);
        }

        Logger.e(TAG,"myChildren="+ myChildren.size());

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
        MyParent myParent =mData.get(parentPosition);
        List<MyChild> myChildren = myParent.getChildren();
        Logger.e(TAG,"myChildren="+ myChildren.size());
        for (int i = childPositionStart; i <childPositionStart+childItemCount ; i++) {
            //注意这里删除数据的 index 不是 i
            myChildren.remove(childPositionStart);
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
            MyParent myParent =mData.get(i);
            myParent.setDot(Color.argb(255,mRandom.nextInt(256),mRandom.nextInt(256),mRandom.nextInt(256)));
        }

        mAdapter.notifyParentItemRangeChanged(parentPositionStart,parentItemCount);
    }

    @Override
    public void notifyChildItemRangeChanged(int parentPosition, int childPositionStart,
            int childItemCount)
    {
        MyParent myParent =mData.get(parentPosition);
        List<MyChild> myChildren = myParent.getChildren();
        for (int i = childPositionStart; i < childPositionStart + childItemCount; i++) {
            MyChild myChild = myChildren.get(i);
            myChild.setDot(Color.argb(255, mRandom.nextInt(256), mRandom.nextInt(256),
                    mRandom.nextInt(256)));
        }
        mAdapter.notifyChildItemRangeChanged(parentPosition,childPositionStart,childItemCount);
    }

    @Override
    public void notifyParentItemMoved(int fromParentPosition, int toParentPosition) {
        MyParent fromMyParent = mData.get(fromParentPosition);
        Logger.e(TAG, "before" + mData);
        mData.remove(fromParentPosition);
        mData.add(toParentPosition, fromMyParent);
        Logger.e(TAG, "after" + mData);

        mAdapter.notifyParentItemMoved(fromParentPosition,toParentPosition);
    }

    @Override
    public void notifyChildItemMoved(int fromParentPosition, int fromChildPosition,
            int toParentPosition, int toChildPosition)
    {
        MyParent fromMyParent =mData.get(fromParentPosition);
        MyParent toMyParent =mData.get(toParentPosition);

        //from-> 先 get 再 remove
        MyChild fromMyChild = checkChildItems(fromMyParent).get(fromChildPosition);
        Logger.e(TAG, "before_fromParent.getChildItems()" + "\n" + fromMyParent.getChildren()
                +"\n"+"before_toParent.getChildItems()"+"\n"+ toMyParent.getChildren());
        fromMyParent.getChildren().remove(fromChildPosition);
        //to-> add fromMyChild
        checkChildItems(toMyParent).add(toChildPosition, fromMyChild);
        Logger.e(TAG,"notifyChildItemMoved"+"\n"+"after_fromParent.getChildItems()="+ fromMyParent
                .getChildren()+"\n"+"after_toMyParent.getChildItems()="+ toMyParent.getChildren());

        mAdapter.notifyChildItemMoved(fromParentPosition, fromChildPosition, toParentPosition,
                toChildPosition);
    }

    private List<MyChild> checkChildItems(MyParent myParent) {
        List<MyChild> myChildItems = myParent.getChildren();
        if (myChildItems == null) {
            myChildItems = new ArrayList<>();
            myParent.setMyChildren(myChildItems);
        }
        return myChildItems;
    }
}
