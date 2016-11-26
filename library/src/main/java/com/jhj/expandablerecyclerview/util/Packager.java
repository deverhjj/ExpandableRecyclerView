package com.jhj.expandablerecyclerview.util;

import android.view.ViewGroup;

import com.jhj.expandablerecyclerview.widget.ExpandableAdapter;

/**
 * 本地 ItemViewType(parent、child)和客户端返回的多种类型的 parent 或/和 child ItemViewType 打包器
 * <p>#方便在{@link ExpandableAdapter#getItemViewType(int)} 方法中返回本地 parent 和 child 与客户端可能返回的多种类型的 parent 或/和
 * child 类型的打包后的
 * ItemViewType
 * ，在{@link ExpandableAdapter#onCreateViewHolder(ViewGroup, int)} 方法中将传递过来的先前打包过的
 * ItemViewType 解包为具体的本地类型和客户端返回的类型(parent、child)来进行判断和回调</p>
 */
public class Packager {
    private static final String TAG = "Packager";
    private static final int TYPE_SHIFT = 30;
    private static final int TYPE_MASK = 0x3 << TYPE_SHIFT;

    public static final int ITEM_VIEW_TYPE_DEFAULT = 0 << TYPE_SHIFT;
    public static final int ITEM_VIEW_TYPE_PARENT = 1 << TYPE_SHIFT;
    public static final int ITEM_VIEW_TYPE_CHILD = 2 << TYPE_SHIFT;

    //------------打包(客户端的 ItemView 类型和本地类型)----------------
    public static int makeItemViewTypeSpec(int clientViewType, int localViewType) {
        return (clientViewType & ~TYPE_MASK) | (localViewType & TYPE_MASK);
    }

    //解包(本地ItemViewType)
    public static int getLocalViewType(int itemViewTypeSpec) {
        return (itemViewTypeSpec & TYPE_MASK);
    }

    //解包(客户端ItemViewType)
    public static int getClientViewType(int itemViewTypeSpec) {
        return (itemViewTypeSpec & ~TYPE_MASK);
    }
}
