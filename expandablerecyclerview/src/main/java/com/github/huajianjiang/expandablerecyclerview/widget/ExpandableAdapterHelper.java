package com.github.huajianjiang.expandablerecyclerview.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@link ExpandableAdapter} 业务助手
 * Created by jhj_Plus on 2015/12/23.
 */
class ExpandableAdapterHelper {
    private static final String TAG = "ExpandableAdapterHelper";

    /**
     * 按照数据源的数据顺序构建并返回本地数据模型
     * <p><b>注意：构建本地数据模型结合时会忽略客户端为 null 的数据模型</b></p>
     * @param parents 客户端所有的父列表项数据集合
     * @return 本地数据模型集合
     */
    public static <P extends Parent, C> List<ParentWrapper<P, C>> generateItems(List<P> parents)
    {
        if (parents == null) return Collections.EMPTY_LIST;
        List<ParentWrapper<P, C>> items = new ArrayList<>();
        final int parentCount = parents.size();
        for (int i = 0; i < parentCount; i++) {
            P parent = parents.get(i);
            if (parent == null) continue;
            ParentWrapper<P, C> parentWrapper = new ParentWrapper<>(parent);
            items.add(parentWrapper);
            final boolean hasChildren = parentWrapper.hasChildren();
            if (parent.isInitiallyExpandable()) parentWrapper.setExpandable(hasChildren);
            if (parentWrapper.isInitiallyExpanded()) {
                List<C> children = parentWrapper.getChildren();
                //父列表项返回的 ChildItems 为 null 或者 childCount 为0 设置为折叠状态
                parentWrapper.setExpanded(hasChildren);
                if (!hasChildren) continue;
                final int childCount = children.size();
                for (int j = 0; j < childCount; j++) {
                    C child = children.get(j);
                    if (child == null) continue;
                    items.add(new ParentWrapper<P, C>(child));
                }
            }
        }
        return items;
    }
}
