package com.jhj.expandablerecyclerview.model;

import java.util.List;

/**
 * 父列表项接口，客户端父列表项数据需要实现该基类实现自定义的父列表项数据模型
 * Created by jhj_Plus on 2015/12/23.
 */
public interface ParentItem<CT> {
    /**
     * 获取属于该父列表项的子项列表
     *
     * @return 所属该父列表项的子项列表
     */
    List<CT> getChildItems();

    /**
     * 父列表项初始化时是否展开回调
     *
     * @return 父列表项初始化时是否展开
     */
    boolean isInitiallyExpanded();
}
