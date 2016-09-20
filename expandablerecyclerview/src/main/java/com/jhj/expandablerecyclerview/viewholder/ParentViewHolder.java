package com.jhj.expandablerecyclerview.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jhj.expandablerecyclerview.R;
import com.jhj.expandablerecyclerview.adapter.ExpandableRecyclerViewAdapter;
import com.jhj.expandablerecyclerview.utils.Logger;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>父列表项 ViewHolder，监听父列表项的点击事件并根据当前展开或收缩状态触发父列表项展开或折叠事件
 * 客户端父列表项 ViewHolder 应该继承它实现可展开的 {@code RecyclerView}
 * </p>
 * Created by jhj_Plus on 2015/12/23.
 */
public class ParentViewHolder<D> extends BaseViewHolder<D> {
    private static final String TAG = "ParentViewHolder";

    private OnParentItemExpandCollapseListener mExpandCollapseListener;

    private ViewHolderCallbackWrapper mCallbackWrapper;

    /**
     * 设置当前父列表项是否已展开
     */
    private boolean mExpanded=false;


    public ParentViewHolder(View itemView) {
        this(itemView ,null);
    }

    public ParentViewHolder(View itemView,ViewHolderCallback callback) {
        super(itemView);
        //设置ViewHolder回调接口
        setCallback(callback);
    }

    @Override
    public void setCallback(ViewHolderCallback callback) {
        if (mCallbackWrapper == null) {
            mCallbackWrapper = new ViewHolderCallbackWrapper(callback);
        } else {
            mCallbackWrapper.mOuterCallback = callback;
        }
        super.setCallback(mCallbackWrapper);
    }


    /**
     * 返回当前父列表项是否已展开
     * @return 父列表项是否已经展开
     */
    public boolean isExpanded() {
        return mExpanded;
    }

    /**设置父列表项是否已展开
     * @param expanded 父列表项是否已展开
     */
    public void setExpanded(boolean expanded) {
        mExpanded = expanded;
    }

    /**
     * 设置 该 ItemView 点击展开折叠状态
     * 子类不能调用该方法
     * @param listener
     */
    public void setParentItemExpandCollapseListener(OnParentItemExpandCollapseListener listener) {
        if (!(listener instanceof ExpandableRecyclerViewAdapter)) throw new RuntimeException
                ("subClass should not invoke this method,you should use ExpandableRecyclerView" +
                        ".OnParentExpandCollapseListener to be notified");
        mExpandCollapseListener = listener;
    }

    /**
     * 注册一个父列表项的点击事件监听器
     *
     */
    public void setClickEvent() {
        Event.EventBefore event = new Event.EventBefore();
        event.registerEventTypes = new Event.EventType[]{Event.EventType.CLICK};
        Event.EventType[] outerEventTypes=getItemViewEventTypes();

        Event.EventType[] mergedEventTypes=mergeEvent(event.registerEventTypes,outerEventTypes);
        if (mergedEventTypes!=null) event.registerEventTypes=mergedEventTypes;

        //此处判断 ItemView 是否设置 ID，防止注册不了点击事件回调
        if (itemView.getId() == View.NO_ID) itemView.setId(R.id.itemView);
        event.id=itemView.getId();
        setEvent(event);
    }

    /**
     * 合内部和外部注册的同一个 View 事件，ItemVew 的 Event 注册内外部同时比较常见
     * @param innerEventTypes 内部已经注册过的 Event
     * @param outerEventTypes 外部需要注册的同一个 View Event
     * @return 合并后新注册的 View Event
     */
    private Event.EventType[] mergeEvent(@NonNull Event.EventType[] innerEventTypes,
            Event.EventType[] outerEventTypes)
    {
        if (outerEventTypes == null || outerEventTypes.length == 0) return null;
        Set<Event.EventType> mergedEventTypes = new HashSet<>(Arrays.asList(innerEventTypes));
        Collections.addAll(mergedEventTypes, outerEventTypes);
        return mergedEventTypes.toArray(new Event.EventType[mergedEventTypes.size()]);
    }

    /**
     * 如果子类需要注册该 Parent ItemView 相关 View Event，应该重写该方法并返回相关 Event，而不是通过
     * {@link ViewHolderCallback} 返回对应的 View Event
     * @return Caller 返回的需要注册私有的 Parent ItemView 相关的 View Event
     */
    protected Event.EventType[] getItemViewEventTypes() {return null;}

    /**
     * 注册 View 相关的 Event
     * @param event 注册 Event 的元数据
     */
    @Override
    public void setEvent(Event.EventBefore event) {
        if (event == null || event.id == View.NO_ID) return;
        Event.EventBefore cachedEvent=getEventBefore(event.id);
        //该 View 之前已经注册过事件，合并注册的 Event
        if (cachedEvent!=null) {
            final Event.EventType[] cachedEventTypes=cachedEvent.registerEventTypes;
            final  Event.EventType[] newEventTypes=event.registerEventTypes;
            Event.EventType[] mergedEventTypes=mergeEvent(cachedEventTypes,newEventTypes);
            if (mergedEventTypes==null) return;
            event.registerEventTypes=mergedEventTypes;
        }
        super.setEvent(event);
    }

    /**
     * 展开父列表项
     */
    private void expandParent() {
        if (mExpandCollapseListener != null) {
            mExpanded=mExpandCollapseListener.onParentItemExpand(getAdapterPosition());
        }
    }

    /**
     * 折叠父列表项
     */
    private void collapseParent() {
        if (mExpandCollapseListener != null) {
            mExpanded=!mExpandCollapseListener.onParentItemCollapse(getAdapterPosition());
        }
    }


    private class ViewHolderCallbackWrapper implements ViewHolderCallback {
        private ViewHolderCallback mOuterCallback;

        public ViewHolderCallbackWrapper(ViewHolderCallback outerCallback) {
            mOuterCallback = outerCallback;
        }

        @Override
        public Event.EventBefore[] eventBefore() {
            Event.EventBefore[] events=null;
            if (mOuterCallback != null) {
                events = mOuterCallback.eventBefore();
            }
            return events;
        }

        @Override
        public boolean eventAfter(BaseViewHolder.ViewHolderEventAfter event) {
            Logger.i(TAG, "onEvent=" + event.toString());
            //处理内部 ItemView 事件，需要监听ParentItemView点击事件来处理展开折叠
            handleInnerEvent(event);

            event.parentPosition = RecyclerView.NO_POSITION;//FIXME wrong position
            return mOuterCallback != null && mOuterCallback.eventAfter(event);
        }
    }

    private void handleInnerEvent(BaseViewHolder.ViewHolderEventAfter event) {
        if (event.v == itemView && event.triggeredEventType == Event.EventType.CLICK) {
            if (mExpanded) {
                collapseParent();
            } else {
                expandParent();
            }
        }
    }

}
