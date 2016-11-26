package com.jhj.expandablerecyclerviewexample.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jhj.expandablerecyclerview.util.Logger;
import com.jhj.expandablerecyclerview.widget.ExpandableAdapter;
import com.jhj.expandablerecyclerview.widget.Parent;
import com.jhj.expandablerecyclerviewexample.R;
import com.jhj.expandablerecyclerviewexample.model.MyChild;
import com.jhj.expandablerecyclerviewexample.model.MyParent;
import com.jhj.expandablerecyclerviewexample.viewholder.MyChildViewHolder;
import com.jhj.expandablerecyclerviewexample.viewholder.MyParentViewHolder;

import java.util.List;

/**
 * Created by jhj_Plus on 2016/9/2.
 */
public class MyAdapter extends ExpandableAdapter<MyParentViewHolder,MyChildViewHolder>
{
    private static final String TAG = "MyAdapter";

    public static final int PARENT_1_TYPE = 0;
    public static final int PARENT_2_TYPE = 1;
    public static final int CHILD_1_TYPE = 0;
    public static final int CHILD_2_TYPE = 1;

    private List<MyParent> mData;
    private Context mContext;
    private LayoutInflater mInflater;

    public MyAdapter(Context context, List<MyParent> data) {
        super(data);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mData = data;
    }

    public List<MyParent> getData() {
        return mData;
    }

    @Override
    public MyParentViewHolder onCreateParentViewHolder(ViewGroup parent, int parentType) {
        View itemView = mInflater.inflate(
                parentType == PARENT_1_TYPE ? R.layout.item_parent_1 : R.layout.item_parent_2,
                parent, false);
        return new MyParentViewHolder(itemView);
    }

    @Override
    public MyChildViewHolder onCreateChildViewHolder(ViewGroup child, int childType) {
        View itemView=mInflater.inflate(childType==CHILD_1_TYPE?R.layout
                .item_child_1:R.layout.item_child_2,child,false);
        return new MyChildViewHolder(itemView);
    }

    @Override
    public void onBindParentViewHolder(MyParentViewHolder parentViewHolder, int parentPosition,
            Parent parentListItem)
    {
        MyParent parent = (MyParent) parentListItem;
        int parentType = getParentType(parentPosition);
        String info = mContext.getString(R.string.parent_type, parentType, parentPosition,
                parentViewHolder.getAdapterPosition());
        parent.setInfo(info);
        parentViewHolder.bind(parent);
    }

    @Override
    public void onBindChildViewHolder(MyChildViewHolder childViewHolder, int parentPosition,
            int childPosition, Object childListItem)
    {
        MyChild myChild = (MyChild) childListItem;
        int childType = getChildType(parentPosition, childPosition);
        String info = mContext.getString(R.string.child_type, childType, childPosition,
                childViewHolder.getAdapterPosition());
        myChild.setInfo(info);
        childViewHolder.bind(myChild);
    }

    @Override
    public int getParentType(int parentPosition) {
        Logger.i(TAG,"getParentType="+parentPosition);
        MyParent myParent = (MyParent) mData.get(parentPosition);
        return myParent.getType();
    }

    @Override
    public int getChildType(int parentPosition, int childPosition) {
        Logger.i(TAG,"getChildType="+parentPosition+","+childPosition);
        MyChild myChild =  mData.get(parentPosition).getChildren().get(childPosition);
        return myChild.getType();
    }

    public ItemDecoration getItemDecoration() {
        return new ItemDecoration();
    }

    private class ItemDecoration extends RecyclerView.ItemDecoration {

        int itemOffset = mContext.getResources().getDimensionPixelSize(R.dimen.itemOffset);

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                RecyclerView.State state)
        {
            final int childAdapterPos = parent.getChildAdapterPosition(view);
            outRect.set(0, itemOffset, 0, childAdapterPos == getItemCount() - 1 ? itemOffset : 0);
        }
    }

}
