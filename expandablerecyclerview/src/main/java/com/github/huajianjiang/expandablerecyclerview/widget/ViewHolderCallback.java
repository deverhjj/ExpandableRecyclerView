package com.github.huajianjiang.expandablerecyclerview.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * {@link android.support.v7.widget.RecyclerView.ViewHolder} 所代表的 {@code itemView} 的 {@link View}
 * ,包含 {@code itemView} 中的子 {@link View} 的{@code View} 的交互事件回调
 * @author HuaJian Jiang.
 *         Date 2017/1/22.
 */
interface ViewHolderCallback {

    int[] onRegisterClickEvent(RecyclerView parent);

    void onItemClick(RecyclerView parent, View v);

    int[] onRegisterLongClickEvent(RecyclerView parent);

    boolean onItemLongClick(RecyclerView parent, View v);
}
