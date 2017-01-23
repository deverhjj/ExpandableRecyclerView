package com.github.huajianjiang.expandablerecyclerview.sample.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.huajianjiang.expandablerecyclerview.sample.R;
import com.github.huajianjiang.expandablerecyclerview.sample.model.MyChild;
import com.github.huajianjiang.expandablerecyclerview.sample.model.MyParent;
import com.github.huajianjiang.expandablerecyclerview.sample.util.AppUtil;
import com.github.huajianjiang.expandablerecyclerview.widget.ChildViewHolder;

/**
 * Created by jhj_Plus on 2016/9/2.
 */
public class MyChildViewHolder extends ChildViewHolder {
    private static final String TAG = "MyChildViewHolder";

    public MyChildViewHolder(View itemView) {
        super(itemView);
    }

    public void bind(MyChild data) {
        String info = data.getInfo();
        TextView tv_info = getView(R.id.info);
        tv_info.setText(info);
        getView(R.id.dot).setBackgroundColor(data.getDot());
    }

    @Override
    public int[] onRegisterLongClickEvent(RecyclerView rv) {
        return new int[]{itemView.getId()};
    }

    @Override
    public boolean onItemLongClick(RecyclerView rv, View v) {
        AppUtil.showToast(v.getContext(), "Child LongClick==>" + getAssociateAdapter()
                .getParentPosition(getAdapterPosition()) + " , " + getAssociateAdapter()
                                                  .getChildPosition(getAdapterPosition()));
        MyParent myParent = (MyParent) getAssociateAdapter()
                .getParentForAdapterPosition(getAdapterPosition());
        MyChild myChild = (MyChild) getAssociateAdapter()
                .getChildForAdapterPosition(getAdapterPosition());
        return true;
    }

}
