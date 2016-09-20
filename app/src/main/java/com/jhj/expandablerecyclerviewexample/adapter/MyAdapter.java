package com.jhj.expandablerecyclerviewexample.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jhj.expandablerecyclerview.adapter.ExpandableRecyclerViewAdapter;
import com.jhj.expandablerecyclerview.model.ParentItem;
import com.jhj.expandablerecyclerview.utils.Logger;
import com.jhj.expandablerecyclerviewexample.R;
import com.jhj.expandablerecyclerviewexample.model.Child;
import com.jhj.expandablerecyclerviewexample.model.Parent;
import com.jhj.expandablerecyclerviewexample.viewholder.BaseChildViewHolder;
import com.jhj.expandablerecyclerviewexample.viewholder.BaseParentViewHolder;
import com.jhj.expandablerecyclerviewexample.viewholder.Child1ViewHolder;
import com.jhj.expandablerecyclerviewexample.viewholder.Child2ViewHolder;
import com.jhj.expandablerecyclerviewexample.viewholder.Parent1ViewHolder;
import com.jhj.expandablerecyclerviewexample.viewholder.Parent2ViewHolder;

import java.util.List;

/**
 * Created by jhj_Plus on 2016/9/2.
 */
public class MyAdapter extends ExpandableRecyclerViewAdapter<BaseParentViewHolder,BaseChildViewHolder>
{
    private static final String TAG = "MyAdapter";

    public static final int PARENT_1_TYPE=0;
    public static final int PARENT_2_TYPE=1;
    public static final int CHILD_1_TYPE=0;
    public static final int CHILD_2_TYPE=1;

    private List<Parent> mData;

    private Context mContext;

    public MyAdapter(Context context,List<Parent> data) {
        super(data);
        mContext=context;
        mData=data;
    }

    public List<Parent> getData() {
        return mData;
    }

    @Override
    public BaseParentViewHolder onCreateParentViewHolder(ViewGroup parent, int parentType) {
        View itemView=LayoutInflater.from(mContext).inflate(parentType==PARENT_1_TYPE?R.layout
                .item_parent_1:R.layout.item_parent_2,parent,false);
        return parentType==PARENT_1_TYPE?new Parent1ViewHolder(itemView):new Parent2ViewHolder(itemView);
    }

    @Override
    public BaseChildViewHolder onCreateChildViewHolder(ViewGroup child, int childType) {
        View itemView=LayoutInflater.from(mContext).inflate(childType==CHILD_1_TYPE?R.layout
                .item_child_1:R.layout.item_child_2,child,false);
        return childType==CHILD_1_TYPE?new Child1ViewHolder(itemView):new Child2ViewHolder(itemView);
    }

    @Override
    public void onBindParentViewHolder(BaseParentViewHolder parentViewHolder, int parentAdapterPosition,
            int parentPosition, ParentItem parentListItem)
    {
        Parent parent = (Parent) parentListItem;
        int parentType = getParentType(parentPosition);
        String info = mContext.getString(R.string.parent_type, parentType, parentPosition,
                parentAdapterPosition);
        parent.setInfo(info);

        parentViewHolder.bind(parent);
    }

    @Override
    public void onBindChildViewHolder(BaseChildViewHolder childViewHolder, int childAdapterPosition,
            int parentPosition, int childPosition, int parentAdapterPosition,Object childListItem)
    {
        Child child = (Child) childListItem;
        int childType = getChildType(parentPosition, childPosition);
        String info = mContext.getString(R.string.child_type, childType, childPosition,
                childAdapterPosition);
        child.setInfo(info);
        childViewHolder.bind(child);
    }

    @Override
    public int getParentType(int parentPosition) {
        Logger.i(TAG,"getParentType="+parentPosition);
        Parent parent =mData.get(parentPosition);
        return parent.getType();
    }

    @Override
    public int getChildType(int parentPosition, int childPosition) {
        Logger.i(TAG,"getChildType="+parentPosition+","+childPosition);
        Child child =mData.get(parentPosition).getChildItems().get(childPosition);
        return child.getType();
    }

    public ItemDecoration getItemDecoration() {
        return new ItemDecoration();
    }

    private class ItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                RecyclerView.State state)
        {
            outRect.set(0,mContext.getResources().getDimensionPixelSize(R.dimen.itemOffset),0,0);
        }
    }


}
