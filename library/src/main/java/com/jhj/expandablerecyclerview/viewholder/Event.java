package com.jhj.expandablerecyclerview.viewholder;

import android.view.View;

import java.util.Arrays;

/**
 * Created by jhj_Plus on 2016/9/12.
 */
public class Event {

    public enum EventType {
        CLICK,
        LONGCLICK
    }

    public static class EventBefore {
        /**
         * 注册监听事件的 View id
         */
        public int id = View.NO_ID;
        /**
         * View 注册的事件类型
         */
        public EventType[] registerEventTypes;

        @Override
        public String toString() {
            return "EventBefore{" +
                    "id=" + id +
                    ", registerEventTypes=" + Arrays.toString(registerEventTypes) +
                    '}';
        }
    }


    public static class EventAfter {
        /**
         * 触发事件的 View
         */
        public View v;

        /**
         * 注册监听事件的 View id
         */
        public int id = View.NO_ID;

        /**
         * View 触发事件的类型
         */
        public EventType triggeredEventType;


        @Override
        public String toString() {
            return "EventAfter{" +
                    "v=" + v +
                    ", id=" + id +
                    ", triggeredEventType=" + triggeredEventType +
                    '}';
        }
    }

    public interface EventCallback  extends View.OnClickListener, View.OnLongClickListener{}

    public static class SimpleEventCallback implements EventCallback {
        @Override
        public void onClick(View v) {
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }

}
