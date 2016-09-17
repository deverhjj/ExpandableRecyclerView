package com.jhj.expandablerecyclerviewexample.viewholder;

import android.view.View;
import android.widget.TextView;

import com.jhj.expandablerecyclerview.viewholder.ChildViewHolder;
import com.jhj.expandablerecyclerview.viewholder.Event;
import com.jhj.expandablerecyclerview.viewholder.ViewHolderCallback;
import com.jhj.expandablerecyclerviewexample.R;
import com.jhj.expandablerecyclerviewexample.model.Child;


/**
 * Created by jhj_Plus on 2016/9/2.
 */
public class BaseChildViewHolder extends ChildViewHolder<Child> implements ViewHolderCallback{
    private static final String TAG = "BaseParentViewHolder";
    public BaseChildViewHolder(View itemView) {
        super(itemView);
        setCallback(this);
    }

    @Override
    public void bind(Child data) {
        String info=data.getInfo();
        TextView tv_info=getView(R.id.info);
        tv_info.setText(info);
        getView(R.id.dot).setBackgroundColor(data.getDot());
    }

    @Override
    public Event.EventBefore[] eventBefore() {
        Event.EventBefore event=new Event.EventBefore();
        event.id= R.id.item_child;
        event.registerEventTypes= new Event.EventType[]{Event.EventType.CLICK};
        return new Event.EventBefore[]{event};
    }

    @Override
    public boolean eventAfter(ViewHolderEventAfter event) {
        return false;
    }
}
