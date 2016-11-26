package com.jhj.expandablerecyclerviewexample.viewholder;

import android.view.View;
import android.widget.TextView;

import com.jhj.expandablerecyclerview.util.Logger;
import com.jhj.expandablerecyclerview.widget.ChildViewHolder;
import com.jhj.expandablerecyclerviewexample.R;
import com.jhj.expandablerecyclerviewexample.model.MyChild;


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
        Logger.e(TAG, "***********bind**************>>" + getAdapterPosition());
    }

}
