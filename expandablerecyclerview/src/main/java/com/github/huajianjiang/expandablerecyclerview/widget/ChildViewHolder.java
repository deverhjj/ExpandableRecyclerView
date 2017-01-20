/*
 * Copyright (c) 2015 Huajian Jiang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.github.huajianjiang.expandablerecyclerview.widget;

import android.view.View;

import com.github.huajianjiang.baserecyclerview.viewholder.BaseViewHolder;


/**
 * <p>
 * 子列表项 ChildViewHolder ，用于实现子列表项视图的创建
 * 客户端 ChildViewHolder 应该实现该类实现可扩展的 {@code RecyclerView}
 * </p>
 * <p>
 * Created by jhj_Plus on 2015/12/23.
 */
public class ChildViewHolder<P extends Parent, C> extends BaseExpandableViewHolder
        implements ExpandableViewHolderCallback<ChildViewHolder, P, C>
{
    private static final String TAG = "ChildViewHolder";

    public ChildViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onItemClick(BaseViewHolder vh, View v, int adapterPosition) {
        if (isReceiveExpandableItemEvent() && getAssociateAdapter() != null)
            onExpandableItemClick(ChildViewHolder.this, v,
                    (P) getAssociateAdapter().getParentForAdapterPosition(adapterPosition),
                    (C) getAssociateAdapter().getChildForAdapterPosition(adapterPosition),
                    getAssociateAdapter().getParentPosition(adapterPosition),
                    getAssociateAdapter().getChildPosition(adapterPosition));
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean onItemLongClick(BaseViewHolder vh, View v, int adapterPosition) {
        return isReceiveExpandableItemEvent() && getAssociateAdapter() != null &&
               onExpandableItemLongClick(ChildViewHolder.this, v,
                       (P) getAssociateAdapter().getParentForAdapterPosition(adapterPosition),
                       (C) getAssociateAdapter().getChildForAdapterPosition(adapterPosition),
                       getAssociateAdapter().getParentPosition(adapterPosition),
                       getAssociateAdapter().getChildPosition(adapterPosition));
    }

    @Override
    public void onExpandableItemClick(ChildViewHolder childViewHolder, View v, P parent, C child,
                                      int parentPosition, int childPosition)
    {
        // do nothing, let client implement it
    }

    @Override
    public boolean onExpandableItemLongClick(ChildViewHolder childViewHolder, View v, P parent,
                                             C child, int parentPosition, int childPosition)
    {
        // do nothing, let client implement it
        return false;
    }
}
