package com.jhj.expandablerecyclerview.viewholder;

/**
 * Created by jhj_Plus on 2016/9/12.
 */
public interface ViewHolderCallback {
    Event.EventBefore[] eventBefore();
    boolean eventAfter(BaseViewHolder.ViewHolderEventAfter event);
}
