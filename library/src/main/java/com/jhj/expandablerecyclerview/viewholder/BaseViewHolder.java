package com.jhj.expandablerecyclerview.viewholder;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

/**
 * Created by jhj_Plus on 2016/9/12.
 */
 public class BaseViewHolder<D> extends RecyclerView.ViewHolder {
    private static final String TAG = "BaseViewHolder";

    /**
     * View 触发 Event 回调时的 Event 信息携带者
     */
    private ViewHolderEventAfter mEventAfter = new ViewHolderEventAfter();
    
    /**
     * ItemView 的 childView 缓存
     * 为了防止频繁调用 findViewById 查找 childView 时的额外性能消耗问题
     * 如果该缓存里没有查找到该 childView 就先 findViewById 再缓存下来
     */
    private SparseArray<View> mCachedViews=new SparseArray<>();

    /**
     * View 的 Event 缓存
     */
    private SparseArray<Event.EventBefore> mCachedEvents = new SparseArray<>();

    /**
     * ItemView 或 ItemView 的 ChildView 事件监听器，应包含基本的 View 的 Event 注册
     */
    private ViewEvent mViewEvents=new ViewEvent();

    /**
     * ViewHolder 事件回调接口
     */
    private ViewHolderCallback mCallback;

    public BaseViewHolder(View itemView) {
       this(itemView,null);
    }

    public BaseViewHolder(View itemView, ViewHolderCallback callback) {
        super(itemView);
        mCallback = callback;
    }

    /**
     * ViewHolder 数据绑定
     * @param data 要绑定的数据
     */
    @SuppressWarnings("unused")
    public void bind(D data) {}

    //TODO 反射绑定数据?
    @SuppressWarnings("unused")
    public void autoBind(Class<? extends D> data,int... ids){}

    /**
     * 设置 ViewHolderCallback 回调接口
     * @param callback
     */
    public void setCallback(ViewHolderCallback callback) {
        mCallback = callback;
        initCallback();
    }

    /**
     * 初始化 View 事件注册
     */
    private void initCallback() {
        if (mCallback == null) return;
        Event.EventBefore[] events = mCallback.eventBefore();
        if (events==null) return;
        for (Event.EventBefore event : events) {
             setEvent(event);
        }
     }

    /**
     * 注册 {@code ItemView} 或者 {@code ItemView} 的 Child View 事件
     * <p>同一个 {@link View} 可同时注册多个 Event</p>
     * <p><b>注意:</b>需要注册事件的 {@link View} 必须提供其对应 <b>Id</b> 元数据,否则该 {@link View} 注册失效</p>
     * @param event 注册事件的元数据
     */
    public void setEvent(Event.EventBefore event) {
        if (event == null || event.id == View.NO_ID) return;
        View v = getView(event.id);
        if (v == null) return;
        if (event.registerEventTypes==null) return;
        for (Event.EventType eventType : event.registerEventTypes) {
            switch (eventType) {
                case CLICK:
                    v.setOnClickListener(mViewEvents);
                    break;
                case LONGCLICK:
                    v.setOnLongClickListener(mViewEvents);
                    break;
            }
        }
        //缓存 View 事件
        mCachedEvents.put(event.id, event);
    }


    /**
     * 根据 View Id 查询返回其对应注册的 Event
     * <p>如果该 Id 对应的 View 之前没有注册 Event 就返回 null</p>
     * @param id 要查询的 View 的 Id
     * @return View 注册的 Event
     */
    public Event.EventBefore getEventBefore(int id) {
        return  mCachedEvents.get(id);
    }
    
    /**
     * 根据 id 查找 ItemView 里 childView
     * @param id ItemView 里 childView 的 id
     * @return ItemView 里的 childView
     */
    public <T extends View> T getView(int id) {
        if (id == View.NO_ID) return null;
        View v = mCachedViews.get(id);
        if (v == null) {
            if (id == itemView.getId()) {
                v = itemView;
            } else {
                v = itemView.findViewById(id);
            }
            if (v!=null) mCachedViews.put(id, v);
        }
        return (T) v;
    }


    /**
     * 内部实现 View 触发事件的相关回调
     */
    private class ViewEvent implements Event.EventCallback {

        @Override
        public void onClick(View v) {
            dispatchEvent(v, Event.EventType.CLICK);
        }

        @Override
        public boolean onLongClick(View v) {
            return dispatchEvent(v, Event.EventType.LONGCLICK);
        }
    }

    /**
     * 回调之前对指定 View Event 触发感兴趣的相关事件回调
     * @param v 触发 Event 的 View
     * @param eventType 触发的 Event 类型
     * {@link com.jhj.expandablerecyclerview.viewholder.Event.EventType}
     * @return 是否消费此 Event
     */
    private boolean dispatchEvent(View v,Event.EventType eventType) {
        final int id=v.getId();
        if (mCallback!=null) {
            ViewHolderEventAfter event = mEventAfter;
            event.id=id;
            event.v=v;
            event.triggeredEventType=eventType;
            return mCallback.eventAfter(event);
        }
        return false;
    }


    /**
     * View 触发事件后包含相关触发信息的类
     *
     */
    public static class ViewHolderEventAfter extends Event.EventAfter {
        /**
         * 触发 Event 的 View 在一级列表里的 parentPosition
         */
        public int parentPosition = RecyclerView.NO_POSITION;

        /**
         * 触发 Event 的 View 在二级列表里的 childPosition.
         * <p><b>注意：</b>当触发的 View 属于 Parent 类型，该值为 {@code RecyclerView.NO_POSITION}</p>
         */
        public int childPosition = RecyclerView.NO_POSITION;

        @Override
        public String toString() {
            return super.toString()+"ViewHolderEventAfter{" +
                    "parentPosition=" + parentPosition +
                    ", childPosition=" + childPosition +
                    '}';
        }
    }
    
}
