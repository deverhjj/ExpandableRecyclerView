package com.github.huajianjiang.expandablerecyclerview.sample.viewholder;

import android.view.View;
import android.widget.TextView;

import com.github.huajianjiang.baserecyclerview.viewholder.BaseViewHolder;
import com.github.huajianjiang.expandablerecyclerview.sample.R;
import com.github.huajianjiang.expandablerecyclerview.sample.model.MyParent;
import com.github.huajianjiang.expandablerecyclerview.sample.utils.Util;
import com.github.huajianjiang.expandablerecyclerview.widget.ParentViewHolder;



/**
 * Created by jhj_Plus on 2016/9/2.
 */
public class MyParentViewHolder extends ParentViewHolder {
    private static final String TAG = "MyParentViewHolder";

    public MyParentViewHolder(View itemView) {
        super(itemView);
    }

    public void bind(MyParent data) {
        String info = data.getInfo();
        TextView tv_info = getView(R.id.info);
        tv_info.setText(info);
        getView(R.id.dot).setBackgroundColor(data.getDot());
        View arrow = getView(R.id.arrow);
        arrow.setVisibility(isExpandable() ? View.VISIBLE : View.GONE);
        if (isExpandable()) {
            arrow.setRotation(isExpanded() ? 180 : 0);
        }
    }

    @Override
    public int[] onRegisterLongClickEvent() {
        return new int[]{itemView.getId()};
    }

    @Override
    public boolean onItemLongClick(BaseViewHolder vh, View v, int adapterPosition) {
        Util.showToast(v.getContext(), "Parent LongClick=" + adapterPosition);
        return true;
    }
}
