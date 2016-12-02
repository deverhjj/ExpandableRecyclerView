package com.github.huajianjiang.expandablerecyclerview.widget;

import android.view.View;

import com.github.huajianjiang.baserecyclerview.viewholder.BaseViewHolder;


/**
 * <p>
 *     子列表项 ChildViewHolder ，用于实现子列表项视图的创建
 *     客户端 ChildViewHolder 应该实现该类实现可扩展的 {@code RecyclerView}
 * </p>
 *
 * Created by jhj_Plus on 2015/12/23.
 */
public class ChildViewHolder extends BaseViewHolder {
    private static final String TAG = "ChildViewHolder";
    public ChildViewHolder(View itemView) {
        super(itemView);
    }
}
