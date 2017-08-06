package com.github.huajianjiang.expandablerecyclerview.widget;

import com.github.huajianjiang.expandablerecyclerview.util.Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@link ExpandableAdapter} 业务助手
 * Created by jhj_Plus on 2015/12/23.
 */
final class ExpandableAdapters {
    private static final String TAG = "ExpandableAdapters";

    private ExpandableAdapters(){}

    /**
     * 按照数据源的数据顺序构建并返回本地数据模型
     * <p><b>注意：构建本地数据模型结合时会忽略客户端为 null 的数据模型</b></p>
     * @param parents 客户端所有的父列表项数据集合
     * @return 本地数据模型集合
     */
    static <P extends Parent, C> List<ItemWrapper<P, C>> generateItems(List<P> parents)
    {
        if (Preconditions.isNullOrEmpty(parents)) return Collections.emptyList();
        List<ItemWrapper<P, C>> items = new ArrayList<>();
        int parentCount = parents.size();
        for (int i = 0; i < parentCount; i++) {
            P parent = parents.get(i);
            if (parent == null) continue;
            ItemWrapper<P, C> itemWrapper = new ItemWrapper<>(parent);
            items.add(itemWrapper);
            if (itemWrapper.isInitiallyExpanded()) {
                List<C> children = itemWrapper.getChildren();
                if (!itemWrapper.hasChildren()) continue;
                final int childCount = children.size();
                for (int j = 0; j < childCount; j++) {
                    C child = children.get(j);
                    if (child == null) continue;
                    items.add(new ItemWrapper<P, C>(child));
                }
            }
        }
        return items;
    }
}
