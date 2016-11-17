package com.jhj.expandablerecyclerview.viewholder;

import android.view.View;

/**
 * <p>
 *     子列表项 ChildViewHolder ，用于实现子列表项视图的创建
 *     客户端 ChildViewHolder 应该实现该类实现可扩展的 {@code RecyclerView}
 * </p>
 *
 * Created by jhj_Plus on 2015/12/23.
 */
public class ChildViewHolder<D> extends BaseViewHolder<D> {
    private static final String TAG = "ChildViewHolder";
    public ChildViewHolder(View itemView) {
        this(itemView,null);
    }

    public ChildViewHolder(View itemView,ViewHolderCallback callback) {
        super(itemView,callback);
    }
}
