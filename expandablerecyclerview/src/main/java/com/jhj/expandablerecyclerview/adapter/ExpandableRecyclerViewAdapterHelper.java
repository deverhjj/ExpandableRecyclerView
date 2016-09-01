package com.jhj.expandablerecyclerview.adapter;

import com.jhj.expandablerecyclerview.model.ParentListItem;
import com.jhj.expandablerecyclerview.model.ParentWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link ExpandableRecyclerViewAdapter} 业务助手
 * Created by jhj_Plus on 2015/12/23.
 */
public final class ExpandableRecyclerViewAdapterHelper {
    private static final String TAG = "ExpandableRecyclerViewAdapterHelper";

    /**
     * 按照数据源的数据顺序构建并返回当前所有在初始化时要显示的列表项(父列表项和子列表项)集合
     * @param parentListItems 所有的父列表项集合
     * @return 初始化时要显示的所有父列表项和子列表项集合
     */
    public static List<Object> generateParentChildItemList(
            List<? extends ParentListItem> parentListItems)
    {
        List<Object> itemList = new ArrayList<Object>();

        int parentCount = parentListItems.size();

        for (int i = 0; i < parentCount; i++) {

            ParentListItem parentListItem = parentListItems.get(i);

            if (parentListItem != null) {
                ParentWrapper parentWrapper = new ParentWrapper(parentListItem);

                itemList.add(parentWrapper);

                if (parentWrapper.isInitiallyExpanded()) {

                    parentWrapper.setExpanded(true);

                    List<?> childItemList = parentWrapper.getChildItemList();

                    if (childItemList != null) {
                        int childCount = childItemList.size();

                        for (int j = 0; j < childCount; j++) {
                            Object childListItem=childItemList.get(j);
                            if (childListItem!=null) {
                                itemList.add(childListItem);
                            } else {
                                throw new IllegalStateException("Null Object added");
                            }
                        }
                    }
                }
            } else {
                 throw new IllegalStateException("Null ParentListItem added");
            }
        }
        return itemList;
    }
}
