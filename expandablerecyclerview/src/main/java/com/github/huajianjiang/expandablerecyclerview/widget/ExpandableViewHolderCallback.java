package com.github.huajianjiang.expandablerecyclerview.widget;

import android.view.View;

import com.github.huajianjiang.baserecyclerview.viewholder.BaseViewHolder;

/**
 * @author HuaJian Jiang.
 *         Date 2016/12/29.
 */

interface ExpandableViewHolderCallback<VH extends BaseViewHolder, P extends Parent<C>, C> {

    void onExpandableItemClick(VH vh, View v, P parent, C child, int parentPosition,
                               int childPosition);

    boolean onExpandableItemLongClick(VH vh, View v, P parent, C child, int parentPosition,
                                      int childPosition);
}
