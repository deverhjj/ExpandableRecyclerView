package com.jhj.expandablerecyclerviewexample.viewholder;

import android.view.View;
import android.widget.TextView;

import com.jhj.expandablerecyclerview.utils.Logger;
import com.jhj.expandablerecyclerview.viewholder.Event;
import com.jhj.expandablerecyclerview.viewholder.ParentViewHolder;
import com.jhj.expandablerecyclerview.viewholder.ViewHolderCallback;
import com.jhj.expandablerecyclerviewexample.R;
import com.jhj.expandablerecyclerviewexample.model.Parent;
import com.jhj.expandablerecyclerviewexample.utils.Util;


/**
 * Created by jhj_Plus on 2016/9/2.
 */
public class BaseParentViewHolder extends ParentViewHolder<Parent> {
    private static final String TAG = "BaseParentViewHolder";

    private TextView mTextView;
    private View mDotView;

    public BaseParentViewHolder(final View itemView,int tvId,int dotViewId) {
        super(itemView);
        mTextView= (TextView) itemView.findViewById(tvId);
        mDotView=itemView.findViewById(dotViewId);
        setCallback(mViewHolderCallback);
    }

    public void bind(String data,int dotColor) {
        mTextView.setText(data);
        mDotView.setBackgroundColor(dotColor);
    }

    @Override
    protected void bind(Parent data) {
        super.bind(data);
    }

    @Override
    protected Event.EventType[] getItemViewEventTypes() {
        return new Event.EventType[]{Event.EventType.CLICK, Event.EventType.LONGCLICK};
    }

    private ViewHolderCallback mViewHolderCallback= new ViewHolderCallback() {
        @Override
        public Event.EventBefore[] eventBefore() {
            Event.EventBefore[] events=new Event.EventBefore[1];
            Event.EventBefore event=new Event.EventBefore();
            event.id= R.id.image;
            event.registerEventTypes= new Event.EventType[]{Event.EventType.CLICK, Event.EventType.LONGCLICK};
            events[0]=event;
            return events;
        }

        @Override
        public boolean eventAfter(ViewHolderEventAfter event) {
            Logger.e(TAG,"onEvent="+event.toString());
            if (event.triggeredEventType==Event.EventType.LONGCLICK) {
                Util.showToast(itemView.getContext(),event.id==R.id.image?"image long " +
                        "click":"long click");
            }
            return true;
        }
    };
}
