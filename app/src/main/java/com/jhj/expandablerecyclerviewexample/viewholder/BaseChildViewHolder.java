package com.jhj.expandablerecyclerviewexample.viewholder;

import android.view.View;
import android.widget.TextView;

import com.jhj.expandablerecyclerview.viewholder.ChildViewHolder;


/**
 * Created by jhj_Plus on 2016/9/2.
 */
public class BaseChildViewHolder extends ChildViewHolder implements View.OnClickListener{
    private static final String TAG = "BaseParentViewHolder";

    private TextView mTextView;
    private View mDotView;

    public BaseChildViewHolder(View itemView,int tvId,int dotViewId) {
        super(itemView);
        mTextView= (TextView) itemView.findViewById(tvId);
        itemView.setOnClickListener(this);
        mDotView=itemView.findViewById(dotViewId);
    }

    public void bind(String data,int dotColor){
        mTextView.setText(data);
        mDotView.setBackgroundColor(dotColor);
    }

    @Override
    public void onClick(View v) {
        //Util.showSnackbar(v,v.getContext().getString(R.string.snack,getAdapterPosition()));
    }
}
