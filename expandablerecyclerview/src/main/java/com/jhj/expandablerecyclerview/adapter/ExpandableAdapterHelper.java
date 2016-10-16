package com.jhj.expandablerecyclerview.adapter;

import com.jhj.expandablerecyclerview.model.ParentItem;
import com.jhj.expandablerecyclerview.model.ParentItemWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link ExpandableAdapter} 业务助手
 * Created by jhj_Plus on 2015/12/23.
 */
final class ExpandableAdapterHelper {
    private static final String TAG = "ExpandableAdapterHelper";

    /**
     * 按照数据源的数据顺序构建并返回本地数据模型
     * <p><b>注意：构建本地数据模型结合时会忽略客户端为 null 的数据模型</b></p>
     * @param parentItems 客户端所有的父列表项数据集合
     * @return 本地数据模型集合
     */
    static List<Object> generateItems(List<? extends ParentItem> parentItems)
    {
        List<Object> items = new ArrayList<>();
        final int parentCount = parentItems.size();
        for (int i = 0; i < parentCount; i++) {
            ParentItem parentItem = parentItems.get(i);
            if (parentItem == null) continue;
            ParentItemWrapper parentItemWrapper = new ParentItemWrapper(parentItem);
            items.add(parentItemWrapper);

            if (parentItemWrapper.isInitiallyExpanded()) {
                List<?> childItems = parentItemWrapper.getChildItems();
                final boolean hasChildren = childItems != null && !childItems.isEmpty();
                //父列表项返回的 ChildItems 为 null 或者 childCount 为0 设置为折叠状态
                parentItemWrapper.setExpanded(hasChildren);
                if (!hasChildren) continue;
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
