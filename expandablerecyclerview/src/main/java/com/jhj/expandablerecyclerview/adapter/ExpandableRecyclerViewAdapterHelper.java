package com.jhj.expandablerecyclerview.adapter;

import com.jhj.expandablerecyclerview.model.ParentItem;
import com.jhj.expandablerecyclerview.model.ParentItemWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link ExpandableRecyclerViewAdapter} 业务助手
 * Created by jhj_Plus on 2015/12/23.
 */
public final class ExpandableRecyclerViewAdapterHelper {
    private static final String TAG = "ExpandableRecyclerViewAdapterHelper";

    /**
     * 按照数据源的数据顺序构建并返回本地数据模型
     * <p><b>注意：构建本地数据模型结合时会忽略客户端为 null 的数据模型</b></p>
     * @param parentItems 客户端所有的父列表项数据集合
     * @return 本地数据模型集合
     */
    public static List<Object> generateParentChildItemList(
            List<? extends ParentItem> parentItems)
    {
        List<Object> items = new ArrayList<>();
        final int parentCount = parentItems.size();
        for (int i = 0; i < parentCount; i++) {
            ParentItem parentItem = parentItems.get(i);
            if (parentItem == null) continue;
            ParentItemWrapper parentItemWrapper = new ParentItemWrapper(parentItem);
            items.add(parentItemWrapper);

            if (parentItemWrapper.isInitiallyExpanded()) {
                parentItemWrapper.setExpanded(true);
                List<?> childItems = parentItemWrapper.getChildItems();
                if (childItems == null) continue;
                final int childCount = childItems.size();
                for (int j = 0; j < childCount; j++) {
                    Object childListItem = childItems.get(j);
                    if (childListItem == null) continue;
                    items.add(childListItem);
                }
            }
        }
        return items;
    }
}
