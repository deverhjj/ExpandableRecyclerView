package com.jhj.expandablerecyclerviewexample.viewholder;

import android.view.View;
import android.widget.TextView;

import com.jhj.expandablerecyclerview.viewholder.ParentViewHolder;



/**
 * Created by jhj_Plus on 2016/9/2.
 */
public class BaseParentViewHolder extends ParentViewHolder {
    private static final String TAG = "BaseParentViewHolder";

    private TextView mTextView;
    private View mDotView;

    public BaseParentViewHolder(View itemView,int tvId,int dotViewId) {
        super(itemView);
        mTextView= (TextView) itemView.findViewById(tvId);
        mDotView=itemView.findViewById(dotViewId);
    }

    public void bind(String data,int dotColor) {
        mTextView.setText(data);
        mDotView.setBackgroundColor(dotColor);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        //Util.showSnackbar(v,v.getContext().getString(R.string.snack,getAdapterPosition()));
    }
}
