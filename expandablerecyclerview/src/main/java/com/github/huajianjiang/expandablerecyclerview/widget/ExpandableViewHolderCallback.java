package com.github.huajianjiang.expandablerecyclerview.widget;

import android.view.View;

import com.github.huajianjiang.baserecyclerview.viewholder.BaseViewHolder;

/**
 * @author HuaJian Jiang.
 *         Date 2016/12/29.
 */

interface ExpandableViewHolderCallback<VH extends BaseViewHolder, T> {

    void onExpandableItemClick(VH vh, View v, T item, int parentPosition, int childPosition);

    boolean onExpandableItemLongClick(VH vh, View v, T item, int parentPosition, int childPosition);
}
