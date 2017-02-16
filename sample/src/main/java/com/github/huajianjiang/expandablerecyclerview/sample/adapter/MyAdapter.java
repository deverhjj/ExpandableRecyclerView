package com.github.huajianjiang.expandablerecyclerview.sample.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.huajianjiang.expandablerecyclerview.sample.R;
import com.github.huajianjiang.expandablerecyclerview.sample.model.MyChild;
import com.github.huajianjiang.expandablerecyclerview.sample.model.MyParent;
import com.github.huajianjiang.expandablerecyclerview.sample.viewholder.MyChildViewHolder;
import com.github.huajianjiang.expandablerecyclerview.sample.viewholder.MyParentViewHolder;
import com.github.huajianjiang.expandablerecyclerview.widget.ExpandableAdapter;

import java.util.List;

/**
 * Created by jhj_Plus on 2016/9/2.
 */
public class MyAdapter
        extends ExpandableAdapter<MyParentViewHolder, MyChildViewHolder, MyParent, MyChild>
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
        View itemView = mInflater
                .inflate(childType == CHILD_1_TYPE ? R.layout.item_child_1 : R.layout.item_child_2,
                        child, false);
        return new MyChildViewHolder(itemView);
    }

    @Override
    public void onBindParentViewHolder(MyParentViewHolder pvh, int parentPosition,
            MyParent parent)
    {
        final int parentType = pvh.getType();
        String info = mContext.getString(R.string.parent_type, parentType, parentPosition,
                pvh.getAdapterPosition());
        parent.setInfo(info);
        pvh.bind(parent);
    }

    @Override
    public void onBindChildViewHolder(MyChildViewHolder cvh, int parentPosition,
            int childPosition, MyChild child)
    {
        final int childType = cvh.getType();
        String info = mContext
                .getString(R.string.child_type, childType, childPosition, cvh.getAdapterPosition());
        child.setInfo(info);
        cvh.bind(child);
    }

    @Override
    public int getParentType(int parentPosition) {
        MyParent myParent = mData.get(parentPosition);
        return myParent.getType();
    }

    @Override
    public int getChildType(int parentPosition, int childPosition) {
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
