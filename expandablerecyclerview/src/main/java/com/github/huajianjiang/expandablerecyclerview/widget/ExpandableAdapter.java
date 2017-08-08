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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewGroupCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.github.huajianjiang.expandablerecyclerview.util.Logger;
import com.github.huajianjiang.expandablerecyclerview.util.Packager;
import com.github.huajianjiang.expandablerecyclerview.util.Preconditions;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 扩展 {@link RecyclerView.Adapter} 实现可展开折叠的 {@link RecyclerView}
 * <p>
 * <p>
 * 该适配器实现了以下接口:
 * <ul>
 * <li>{@link #notifyParentItemInserted(int)}</li>
 * <li>{@link #notifyParentItemRangeInserted(int, int)}</li>
 * <li>{@link #notifyParentItemRemoved(int)}</li>
 * <li>{@link #notifyParentItemRangeRemoved(int, int)}</li>
 * <li>{@link #notifyParentItemChanged(int)}</li>
 * <li>{@link #notifyParentItemRangeChanged(int, int)}</li>
 * <li>{@link #notifyParentItemMoved(int, int)} </li>
 * <p>
 * <li>{@link #notifyChildItemInserted(int, int, boolean)}</li>
 * <li>{@link #notifyChildItemRangeInserted(int, int, int, boolean)}</li>
 * <li>{@link #notifyChildItemRemoved(int, int)}</li>
 * <li>{@link #notifyChildItemRangeRemoved(int, int, int)}</li>
 * <li>{@link #notifyChildItemChanged(int, int)}</li>
 * <li>{@link #notifyChildItemRangeChanged(int, int, int)}</li>
 * <li>{@link #notifyChildItemMoved(int, int, int, int)} </li>
 * </ul>
 * </p>
 */
public abstract class ExpandableAdapter<PVH extends ParentViewHolder, CVH extends ChildViewHolder, P extends Parent, C>
        extends RecyclerView.Adapter<BaseViewHolder>
{
    private static final String TAG = ExpandableAdapter.class.getSimpleName();

    private static final String SAVED_EXPANSION_STATE = "savedExpansionState";

    /**
     * ExpandableRecyclerView 展开折叠模式处理类
     */
    private ExpandCollapseMode mExpandCollapseMode = null;

    /**
     * 父列表项集合
     */
    private List<P> mParents = null;
    /**
     * 当前显示的列表项(父列表项和所有展开的子列表项)集合
     */
    private List<ItemWrapper<P, C>> mItems = null;
    /**
     * 当前所有监听适配器的 RecyclerView 集合
     */
    private List<RecyclerView> mAttachedRecyclerViews = new ArrayList<>(2);

    /**
     * 所有监听父列表项展开折叠状态监听器集合
     */
    private List<OnParentExpandCollapseListener> mExpandCollapseListeners = null;

    /**
     * 父列表项是否可折叠展开监听器集合
     */
    private List<OnParentExpandableStateChangeListener> mExpandableStateChangeListeners = null;

    /**
     * RecyclerView 的 ItemView/ItemView 的 child View 的点击事件监听器
     */
    private OnParentClickListener mParentClickListener;
    /**
     * RecyclerView 的 ItemView/ItemView 的 child View 的长按事件监听器
     */
    private OnParentLongClickListener mParentLongClickListener;

    /**
     * RecyclerView 的 ItemView/ItemView 的 child View 的点击事件监听器
     */
    private OnChildClickListener mChildClickListener;
    /**
     * RecyclerView 的 ItemView/ItemView 的 child View 的长按事件监听器
     */
    private OnChildLongClickListener mChildLongClickListener;

    /**
     * 要监听点击事件的所有的 View (itemView/itemView's child view) 的 ids
     */
    private Integer[] mParentClickTargets;
    /**
     * 要监听长按事件的所有的 View (itemView/itemView's child view) 的 ids
     */
    private Integer[] mParentLongClickTargets;

    /**
     * 要监听点击事件的所有的 View (itemView/itemView's child view) 的 ids
     */
    private Integer[] mChildClickTargets;
    /**
     * 要监听长按事件的所有的 View (itemView/itemView's child view) 的 ids
     */
    private Integer[] mChildLongClickTargets;

    /**
     * 待处理展开折叠逻辑的所有 {@link ParentViewHolder}  的位置集合
     * <p>
     * 在调用 {@link #collapseAllParents()} {@link #expandAllParents()} 循环遍历展开折叠所有已折叠展开的
     * parent 时，在
     * {@link #collapseParent(int)} {@link #expandView(int, boolean, boolean)}
     * 方法中处理展开折叠逻辑时，因为某些特殊情况无法获取未布局到
     * {@link RecyclerView} 中的 {@link ParentViewHolder} 并且没有正确回调
     * {@link #onBindParentViewHolder(ParentViewHolder, int)} 方法进行同步导致设置
     * {@link ParentViewHolder} 所代表的 parentItem 展开状态彻底失败的 bug
     * ,所有这里收集待处理的所有
     * {@link ParentViewHolder} 的位置，以至于能够在 {@link #onViewAttachedToWindow(BaseViewHolder)}
     * 中处理所有待处理的展开折叠逻辑的 {@link ParentViewHolder}
     * <p>
     * </p>
     */
    private Map<Object, Set<Integer>> mPendingExpandablePositionMap = new HashMap<>();
    private Map<Object, Set<Integer>> mPendingExpandPositionMap = new HashMap<>();
    private Map<Object, Set<Integer>> mPendingCollapsePositionMap = new HashMap<>();
    /**
     * itemView 或者 itemView 的子 view 交互事件监听器
     */
    private ViewEventWatcher mViewEventWatcher;

    public ExpandableAdapter() {
        this(null);
    }

    public ExpandableAdapter(@Nullable List<P> parents) {
        init(parents);
    }

    private void init(List<P> parents) {
        mParents = parents == null ? Collections.<P>emptyList() : parents;
        mItems = ExpandableAdapters.generateItems(parents);
    }

    /**
     * 设置新的数据
     *
     * @param newParents 新数据
     */
    public void invalidate(@Nullable List<P> newParents) {//是否保存状态?
        init(newParents);
        notifyDataSetChanged();
    }

    private ExpandCollapseMode getMode() {
        if (mExpandCollapseMode == null) {
            mExpandCollapseMode = new ExpandCollapseMode();
        }
        return mExpandCollapseMode;
    }

    /**
     * 查询当前设置的展开折叠模式
     *
     * @return 当前设置的展开折叠模式
     * @see ExpandCollapseMode
     */
    public int getExpandCollapseMode() {
        return mExpandCollapseMode == null ? ExpandCollapseMode.MODE_DEFAULT :
                mExpandCollapseMode.mode;
    }

    /**
     * 设置当前的展开折叠模式
     *
     * @param mode 指定的模式
     * @see ExpandCollapseMode
     */
    public void setExpandCollapseMode(int mode) {
        getMode();
        if (mode == mExpandCollapseMode.mode) return;
        mExpandCollapseMode.mode = mode;
    }

    /**
     * 设置指定父列表项是否可以展开折叠
     * <p>
     * 设置成功后会接收到指定位置的父列表项的展开状态更改回调 {@link OnParentExpandableStateChangeListener}
     * </p>
     *
     * @param position   指定设置是否展开折叠的父列表项的在父列表中的位置
     * @param expandable 设置的是否可以展开
     * @see #getExpandable(int)
     */
    public void setExpandable(int position, boolean expandable) {
        ItemWrapper itemWrapper = getParentWrapper(getParentAdapterPosition(position));
        if (itemWrapper == null) return;
        if (itemWrapper.hasChildren() && itemWrapper.setExpandable(expandable)) {
            syncParentExpandableState(position);
        }
    }

    /**
     * 获取指定位置的父列表项当前是否可以展开折叠
     *
     * @param position 父列表项的位置
     * @return true 可展开，否则不可展开折叠
     * @see #setExpandable(int, boolean)
     */
    public boolean getExpandable(int position) {
        ItemWrapper itemWrapper = getParentWrapper(getParentAdapterPosition(position));
        return itemWrapper != null && itemWrapper.isExpandable();
    }

    /**
     * 切换设置指定位置父列表项的是否可展开折叠属性
     *
     * @param position 设置可展开折叠的父列表项在父列表里的位置
     */
    public void toggleExpandable(int position) {
        setExpandable(position, !getExpandable(position));
    }

    public RecyclerView[] getAttachedRecyclerViews() {
        return mAttachedRecyclerViews.toArray(new RecyclerView[mAttachedRecyclerViews.size()]);
    }

    private List<OnParentExpandableStateChangeListener> getExpandableStateChangeListeners() {
        if (mExpandableStateChangeListeners == null) {
            mExpandableStateChangeListeners = new ArrayList<>();
        }
        return mExpandableStateChangeListeners;
    }

    /**
     * 注册监听父列表项可展开状态改变监听器
     *
     * @param listener 监听器
     * @see OnParentExpandableStateChangeListener
     */
    public void addParentExpandableStateChangeListener(
            OnParentExpandableStateChangeListener listener)
    {
        if (listener == null || getExpandableStateChangeListeners().contains(listener)) return;
        mExpandableStateChangeListeners.add(listener);
    }

    /**
     * 注销之前注册的父列表项可展开状态更改监听器
     *
     * @param listener 要注销的监听器
     */
    public void removeParentExpandableStateChangeListener(
            OnParentExpandableStateChangeListener listener)
    {
        if (listener == null || mExpandableStateChangeListeners == null) return;
        mExpandableStateChangeListeners.remove(listener);
    }

    private List<ExpandableAdapter.OnParentExpandCollapseListener> getExpandCollapseListeners() {
        if (mExpandCollapseListeners == null) {
            mExpandCollapseListeners = new ArrayList<>();
        }
        return mExpandCollapseListeners;
    }

    /**
     * 注册一个监听父列表项展开或折叠状态改变监听器.
     * <p>use {@link #addParentExpandCollapseListener(OnParentExpandCollapseListener)}</p>
     *
     * @param listener 监听父列表项展开或折叠状态改变的监听器
     */
    @Deprecated
    public void setParentExpandCollapseListener(OnParentExpandCollapseListener listener)
    {
        addParentExpandCollapseListener(listener);
    }

    /**
     * 注册监听父列表项展开折叠状态监听器
     *
     * @param listener 监听器
     */
    public void addParentExpandCollapseListener(OnParentExpandCollapseListener listener) {
        if (listener == null || getExpandCollapseListeners().contains(listener)) return;
        mExpandCollapseListeners.add(listener);
    }

    /**
     * 取消注册监听父列表项折叠状态的改变
     *
     * @param listener 需要取消注册的监听器
     */
    public void removeParentExpandCollapseListener(OnParentExpandCollapseListener listener) {
        if (listener == null || mExpandCollapseListeners == null) return;
        mExpandCollapseListeners.remove(listener);
    }

    /**
     * <p>
     * 更具列表项类型判断并回调{@link #onCreateParentViewHolder(ViewGroup, int)}
     * 或者{@link #onCreateChildViewHolder(ViewGroup, int)}
     * </p>
     * 列表项类型包含 父列表项和子列表项类型,也可通过覆盖{@link #getParentType(int)}
     * 或者{@link #getChildType(int, int)}提供具体父列表项类型或子列表项类型
     *
     * @param parent   用于显示列表项视图{@link android.view.View}的 {@link ViewGroup}
     * @param viewType 列表项类型(父列表项类型或子列表项类型，包含具体的父列表项或子列表项类型)
     * @return 根据适配器 ExpandableRecycleViewAdapter 里的数据位置创建的该位置所代表的列表项视图
     */
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //本地 ItemView 的类型(parent 或者 child)
        final int localViewType = Packager.getLocalViewType(viewType);
        //外部返回的指定的具体的列表项类型(具体的父或子列表项类型)
        final int clientViewType = Packager.getClientViewType(viewType);
        if (localViewType == Packager.ITEM_VIEW_TYPE_PARENT) {
            //回调并返回父列表项视图 ParentViewHolder
            PVH pvh = onCreateParentViewHolder(parent, clientViewType);
            //注册 ParentItemView 点击回调监听器
            initViewHolder(pvh);
            pvh.associateRv = new WeakReference<>((RecyclerView) parent);
            return pvh;
        } else if (localViewType == Packager.ITEM_VIEW_TYPE_CHILD) {
            ////回调并返回子列表项视图 ChildViewHolder
            CVH cvh = onCreateChildViewHolder(parent, clientViewType);
            initViewHolder(cvh);
            cvh.associateRv = new WeakReference<>((RecyclerView) parent);
            return cvh;
        } else {
            throw new IllegalStateException("Incorrect ViewType found=>" + viewType);
        }
    }

    private void initViewHolder(BaseViewHolder holder) {
        if (holder instanceof ParentViewHolder) {
            if (!Preconditions.isNullOrEmpty(mParentClickTargets)) {
                for (int target : mParentClickTargets) {
                    View targetView = holder.getView(target);
                    if (targetView != null) {
                        targetView.setOnClickListener(getViewEventWatcher());
                    }
                }
            }

            if (!Preconditions.isNullOrEmpty(mParentLongClickTargets)) {
                for (int target : mParentLongClickTargets) {
                    View targetView = holder.getView(target);
                    if (targetView != null) {
                        targetView.setOnLongClickListener(getViewEventWatcher());
                    }
                }
            }

            // make sure parent can receive click callback
            holder.itemView.setOnClickListener(getViewEventWatcher());

        } else {
            if (!Preconditions.isNullOrEmpty(mChildClickTargets)) {
                for (int target : mChildClickTargets) {
                    View targetView = holder.getView(target);
                    if (targetView != null) {
                        targetView.setOnClickListener(getViewEventWatcher());
                    }
                }
            }

            if (!Preconditions.isNullOrEmpty(mChildLongClickTargets)) {
                for (int target : mChildLongClickTargets) {
                    View targetView = holder.getView(target);
                    if (targetView != null) {
                        targetView.setOnLongClickListener(getViewEventWatcher());
                    }
                }
            }
        }
        // make sure {@code itemView} can have chance to handle showContextMenu
        holder.itemView.setLongClickable(true);
    }

    /**
     * <p>
     * 根据指定的列表项位置在适配器{@link #ExpandableAdapter}里对应的数据集位置绑定数据到该列表项
     * 视图
     * <p>
     * </p>
     * 判断适配器位置的对应的列表项类型
     * (父或子列表项类型，或者具体的父或子列表项类型)
     * 并回调{@link #onBindParentViewHolder(ParentViewHolder, int)}
     * 或者{@link #onBindChildViewHolder(ChildViewHolder, int, int)}通知更新该列表项位置的视图内容
     *
     * @param holder   指定列表项位置的 ViewHolder，用于更新指定位置的列表项视图
     * @param position 该列表项在适配器数据集中代表的位置
     */
    @Override
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        ItemWrapper<P, C> item = getItem(position);
        int parentPos = getParentPosition(position);
        if (item.isParent()) {
            PVH pvh = (PVH) holder;
            P parent = item.getParent();
            //初始化初始化/重绑定 可展开状态
            pvh.setExpandable(item.isExpandable());
            //初始化/重绑定 展开折叠状态
            pvh.setExpanded(item.isExpanded());
            //bind associate parent entity
            pvh.setParent(parent);
            //position binding
            pvh.setParentPosition(parentPos);

            onBindParentViewHolder(pvh, parentPos);

            RecyclerView rv = findParent(holder);

            if (shouldNotifyExpandable(rv, parentPos))
                notifyParentExpandableStateChanged(pvh, pvh.isExpandable());
            if (shouldNotifyExpanded(rv, parentPos))
                notifyParentExpanded(pvh, true, false);
            else if (shouldNotifyCollapsed(rv, parentPos))
                notifyParentCollapsed(pvh, true, false);

        } else if (item.isChild()) {
            CVH cvh = (CVH) holder;
            int childPos = getChildPosition(position);
            C child = item.getChild();
            //bind associate child entity
            cvh.setChild(child);
            //position binding
            cvh.setParentPosition(parentPos);
            cvh.setChildPosition(childPos);

            onBindChildViewHolder(cvh, parentPos, childPos);
        }
    }

    private RecyclerView findParent(BaseViewHolder holder) {
        RecyclerView parent = null;
        if (holder.associateRv != null) {
            parent = holder.associateRv.get();
        }
        if (parent != null) {
            return parent;
        } else {
            for (RecyclerView rv : mAttachedRecyclerViews) {
                RecyclerView.ViewHolder vh = rv.getChildViewHolder(holder.itemView);
                if (holder == vh) {
                    return rv;
                }
            }
        }
        throw new RuntimeException("can not find parent for ViewHolder:" + holder);
    }

    private boolean shouldNotifyExpandable(ViewGroup parent, Integer parentPos) {
        return pendingExpandableRemove(parent, parentPos);
    }

    private boolean shouldNotifyExpanded(ViewGroup parent, Integer parentPos) {
        return pendingExpandRemove(parent, parentPos);
    }

    private boolean shouldNotifyCollapsed(ViewGroup parent, Integer parentPos) {
        return pendingCollapseRemove(parent, parentPos);
    }

    @Override
    public int getItemViewType(int position) {
        //获取指定列表项位置在适配器数据集合里所代表的列表项
        ItemWrapper<P, C> item = getItem(position);
        //获取指定列表项位置(父或子视图项的位置)的在父列表里位置
        int parentPosition = getParentPosition(position);
        //如果是父列表项类型就回调查询具体的父类型
        //返回父类型和具体的父类型的组合后的列表项类型
        if (item.isParent()) {
            int parentType = getParentType(parentPosition);
            checkViewType(parentType);
            return Packager.makeItemViewTypeSpec(parentType, Packager.ITEM_VIEW_TYPE_PARENT);
        } else if (item.isChild()) {
            //回调获取具体的子列表项类型
            //返回子类型和具体子类型的组合后的列表项类型
            int childType = getChildType(parentPosition, getChildPosition(position));
            checkViewType(childType);
            return Packager.makeItemViewTypeSpec(childType, Packager.ITEM_VIEW_TYPE_CHILD);
        }
        return Packager.ITEM_VIEW_TYPE_ILLEGAL;
    }

    /**
     * 返回指定父列表位置的父列表类型
     * 返回的 int 标识不能为负数
     *
     * @param parentPosition 要查询的父列表类型的位置
     * @return 指定父列表位置的父列表类型
     */
    public int getParentType(int parentPosition) {
        return Packager.ITEM_VIEW_TYPE_DEFAULT;
    }

    /**
     * 返回指定的父列表项位置下从属该父列表的子列表项的位置对应的子列表项类型
     * 返回的 int 标识不能为负数
     *
     * @param parentPosition 该子列表项的从属父列表项位置
     * @param childPosition  子列表项的位置
     * @return 子列表项的类型
     */
    public int getChildType(int parentPosition, int childPosition) {
        return Packager.ITEM_VIEW_TYPE_DEFAULT;
    }

    /**
     * 返回当前所有要显示的列表项(父列表和子列表)数据数量
     *
     * @return 初始化时显示的列表项数量
     */
    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /**
     * 来自{@link #onCreateViewHolder(ViewGroup, int)}的创建指定父列表项视图的回调
     * 创建父列表项用于显示的父列表项视图的 {@link ParentViewHolder}，通过回调返回客户端的所指定的父列表项视图
     *
     * @param parent     用于显示列表项视图的{@link ViewGroup}
     * @param parentType 父列表项的类型，用于提供不同的父列表项视图
     * @return 父列表项所需显示视图的 {@link ParentViewHolder}
     */
    public abstract PVH onCreateParentViewHolder(ViewGroup parent, int parentType);

    /**
     * 来自{@link #onCreateViewHolder(ViewGroup, int)}的创建指定子列表项视图的回调
     *
     * @param child     用于显示列表项视图的{@link ViewGroup}
     * @param childType 子列表项的类型
     * @return 子列表项所需显示视图的 {@link ChildViewHolder}
     */
    public abstract CVH onCreateChildViewHolder(ViewGroup child, int childType);

    /**
     * 来自 {@link #onBindViewHolder(RecyclerView.ViewHolder, int)}的用于绑定视图数据 到{@link PVH}
     * 的回调
     *
     * @param pvh            用于绑定数据的父列表项的 parentViewHolder
     * @param parentPosition 该父列表项所在父列表里的位置
     */
    public abstract void onBindParentViewHolder(PVH pvh, int parentPosition);

    /**
     * 来自 {@link #onBindViewHolder(RecyclerView.ViewHolder, int)}的用于绑定数据到{@link CVH}的回调
     *
     * @param cvh            用于显示或更新绑定到 CVH 里的数据
     * @param parentPosition 该子列表项所从属的父列表项在父列表里的位置
     * @param childPosition  该子列表项在子列表里的位置
     */
    public abstract void onBindChildViewHolder(CVH cvh, int parentPosition, int childPosition);

    /**
     * 当有 {@code RecyclerView} 监听该适配器时的回调
     * <p>
     * 注意: 同一个适配器可能被多个 {@code RecyclerView} 监听
     * </p>
     *
     * @param recyclerView 开始监听该适配器的 {@code RecyclerView} 实例
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mAttachedRecyclerViews.add(recyclerView);
        mPendingExpandablePositionMap.put(recyclerView, new HashSet<Integer>());
        mPendingExpandPositionMap.put(recyclerView, new HashSet<Integer>());
        mPendingCollapsePositionMap.put(recyclerView, new HashSet<Integer>());
    }

    /**
     * 当 {@code RecyclerView} 停止监听之前它所监听的适配器时的回调
     *
     * @param recyclerView 停止监听适配器的 {@code RecyclerView} 实例
     */
    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mAttachedRecyclerViews.remove(recyclerView);
        mPendingExpandablePositionMap.remove(recyclerView);
        mPendingExpandPositionMap.remove(recyclerView);
        mPendingCollapsePositionMap.remove(recyclerView);
    }

    private boolean pendingExpandableContains(ViewGroup parent, Integer pos) {
        Set<Integer> positions = mPendingExpandablePositionMap.get(parent);
        return !Preconditions.isNullOrEmpty(positions) && positions.contains(pos);
    }

    private boolean pendingExpandContains(ViewGroup parent, Integer pos) {
        Set<Integer> positions = mPendingExpandPositionMap.get(parent);
        return !Preconditions.isNullOrEmpty(positions) && positions.contains(pos);
    }

    private boolean pendingCollapseContains(ViewGroup parent, Integer pos) {
        Set<Integer> positions = mPendingCollapsePositionMap.get(parent);
        return !Preconditions.isNullOrEmpty(positions) && positions.contains(pos);
    }

    private void pendingExpandableAdd(ViewGroup parent, Integer pos) {
        Set<Integer> positions = mPendingExpandablePositionMap.get(parent);
        if (positions != null) {
            positions.add(pos);
        }
    }

    private void pendingExpandAdd(ViewGroup parent, Integer pos) {
        Set<Integer> positions = mPendingExpandPositionMap.get(parent);
        if (positions != null) {
            positions.add(pos);
        }
    }

    private void pendingCollapseAdd(ViewGroup parent, Integer pos) {
        Set<Integer> positions = mPendingCollapsePositionMap.get(parent);
        if (positions != null) {
            positions.add(pos);
        }
    }

    private boolean pendingExpandableRemove(ViewGroup parent, Integer pos) {
        Set<Integer> positions = mPendingExpandablePositionMap.get(parent);
        return !Preconditions.isNullOrEmpty(positions) && positions.contains(pos) &&
               positions.remove(pos);
    }

    private boolean pendingExpandRemove(ViewGroup parent, Integer pos) {
        Set<Integer> positions = mPendingExpandPositionMap.get(parent);
        return !Preconditions.isNullOrEmpty(positions) && positions.contains(pos) &&
               positions.remove(pos);
    }

    private boolean pendingCollapseRemove(ViewGroup parent, Integer pos) {
        Set<Integer> positions = mPendingCollapsePositionMap.get(parent);
        return !Preconditions.isNullOrEmpty(positions) && positions.contains(pos) &&
               positions.remove(pos);
    }

    /**
     * 获取指定适配器位置数据所代表的列表项
     *
     * @param position 该列表项在适配器数据集中对应的位置
     * @return 指定适配器位置数据所代表的列表项
     */
    private ItemWrapper<P, C> getItem(int position) {
        return mItems.get(position);
    }

    /**
     * 根据 parent 在 parent 列表里位置获取对应的 parent 数据实体
     *
     * @param position parent 实体数据在 parent 列表里的位置
     * @return 指定 parent 位置对应的 parent 实体数据
     * @see #getParentAdapterPosition(int)
     */
    public P getParent(int position) {
        return mParents != null && position >= 0 && position < mParents.size() ?
                mParents.get(position) : null;
    }

    /**
     * 根据 parent 在适配器里的位置获取对应的 parent 数据实体
     * <p>
     * 输入的适配器位置既可以是对应的 parent,也可以是 child 适配器位置
     * ,如果是 child 适配器位置,寻找并返回 child 所属的 parent 实体数据
     * </p>
     *
     * @param adapterPosition parent/child 在适配器数据集里的位置
     * @return parent 适配器位置对应的 parent 实体数据
     * @see #getParent(int)
     */
    public P getParentForAdapterPosition(int adapterPosition) {
        ItemWrapper<P, C> itemWrapper = mItems.get(adapterPosition);
        if (itemWrapper.isParent()) return itemWrapper.getParent();
        for (int i = adapterPosition - 1; i >= 0; i--) {
            if (mItems.get(i).isParent()) return mItems.get(i).getParent();
        }
        return null;
    }

    /**
     * 根据 child 在 child 列表里的位置和其所属的 parent 的位置获取对应的 child 数据实体
     *
     * @param parentPosition child 所属的 parent 列表项位置
     * @param childPosition  child 列表项位置
     * @return child 数据实体
     */
    public C getChild(int parentPosition, int childPosition) {
        C child = null;
        if (mParents != null && parentPosition >= 0 && parentPosition < mParents.size()) {
            List<C> children = mParents.get(parentPosition).getChildren();
            if (children != null && childPosition >= 0 && childPosition < children.size()) {
                child = children.get(childPosition);
            }
        }
        return child;
    }

    /**
     * 根据 child 在适配器数据集里的位置和其所属的 parent 的适配器位置获取对应的 child 数据实体
     *
     * @param childAdapterPosition child 在适配器数据集里对应的位置
     * @return child 数据实体
     */
    public C getChildForAdapterPosition(int childAdapterPosition) {
        return mItems.get(childAdapterPosition).getChild();
    }

    /**
     * 根据指定的列表项在适配器数据集中所对应的位置返回该列表项在父列表里的位置
     * 如果该列表项为子列表项就返回该子列表项所属的父列表项在父列表里位置
     *
     * @param adapterPosition 要查询的列表项在适配器里所对应的位置
     * @return 指定列表项在父列表里的位置
     */
    public int getParentPosition(int adapterPosition) {
        if (adapterPosition == RecyclerView.NO_POSITION) return RecyclerView.NO_POSITION;
        if (adapterPosition == 0) return 0;
        if (getItem(adapterPosition).isParent()) {
            return adapterPosition - getBeforeExpandedChildCount(adapterPosition);
        } else {
            for (int i = adapterPosition - 1; i >= 0; i--) {
                if (getItem(i).isParent()) {
                    return i - getBeforeExpandedChildCount(i);
                }
            }
        }
        return RecyclerView.NO_POSITION;
    }

    /**
     * 根据指定的子列表项在适配器数据集中所对应的位置返回该子列表项在子列表里的位置
     *
     * @param childAdapterPosition 子列表项在适配器里对应的位置
     * @return 指定子列表项在子列表里的位置
     */
    public int getChildPosition(int childAdapterPosition) {
        if (childAdapterPosition == RecyclerView.NO_POSITION) return RecyclerView.NO_POSITION;
        int childPos = RecyclerView.NO_POSITION;
        ItemWrapper<P, C> itemWrapper = getParentWrapper(childAdapterPosition);
        if (itemWrapper != null) {
            C child = getItem(childAdapterPosition).getChild();
            List<C> children = itemWrapper.getChildren();
            if (children != null) {
                childPos = children.indexOf(child);
            }
        }
        return childPos;
    }

    /**
     * 添加对应 {@code parentWrapperPosition} 位置的父列表项
     *
     * @param parentWrapperPosition 要添加新父列表项所指定的位置
     * @param newParentPosition     新添加的父列表项的位置
     * @return 插入要适配器数据集里的列表项的个数(父列表项和其展开的子列表项)
     */
    private int addParentWrapper(int parentWrapperPosition, int newParentPosition) {
        if (parentWrapperPosition == RecyclerView.NO_POSITION ||
            newParentPosition == RecyclerView.NO_POSITION) return 0;
        P newParent = mParents.get(newParentPosition);
        ItemWrapper<P, C> newItemWrapper = new ItemWrapper<>(newParent);
        List<C> children = newItemWrapper.getChildren();
        final boolean hasChildren = newItemWrapper.hasChildren(children);
        mItems.add(parentWrapperPosition, newItemWrapper);
        int insertedItemCount = 1;
        //初始化该父列表项可展开折叠状态
        newItemWrapper.setExpandable(newItemWrapper.isInitiallyExpandable() && hasChildren);
        //parent 初始化展开但是当前展开折叠模式是单项展开模式时强制不展开该 parent
        //parent 当前展开折叠模式是单项折叠模式时不管初始化是否展开强制进行对有 child 的 parent 展开
        if ((newItemWrapper.isInitiallyExpanded() &&
             getExpandCollapseMode() != ExpandCollapseMode.MODE_SINGLE_EXPAND) ||
            getExpandCollapseMode() == ExpandCollapseMode.MODE_SINGLE_COLLAPSE)
        {
            newItemWrapper.setExpanded(hasChildren);
            if (hasChildren) {
                insertedItemCount += addChildWrapper(getParentPosition(parentWrapperPosition), 0,
                        children);
            }
        }
        return insertedItemCount;
    }

    /**
     * 将一批外部子列表项数据包装成内部可使用的子列表包装类
     *
     * @param parentPosition     父列表项的位置
     * @param childPositionStart 子项的起始位置
     * @param children           外部子列表项数据集合
     * @return 成功添加子列表项的个数
     */
    private int addChildWrapper(int parentPosition, int childPositionStart, List<C> children) {
        if (children == null) return 0;
        int parentAdapterPos = getParentAdapterPosition(parentPosition);
        if (parentAdapterPos == RecyclerView.NO_POSITION) return 0;
        int insertedItemCount = 0;
        for (C child : children) {
            if (child == null) continue;
            insertedItemCount++;
            mItems.add(parentAdapterPos + childPositionStart + insertedItemCount,
                    new ItemWrapper<P, C>(child));
        }
        return insertedItemCount;
    }

    /**
     * 根据指定的列表项在适配器数据集中所对应的位置返回该对应的本地父列表项的包装类
     *
     * @param adapterPosition 列表项在适配器里对应的位置
     * @return 本地父列表项包装类
     */
    private ItemWrapper<P, C> getParentWrapper(int adapterPosition) {
        if (adapterPosition == RecyclerView.NO_POSITION) return null;
        ItemWrapper<P, C> itemWrapper = getItem(adapterPosition);
        if (itemWrapper.isParent()) {
            return itemWrapper;
        } else if (itemWrapper.isChild()) {
            for (int i = adapterPosition - 1; i >= 0; i--) {
                ItemWrapper<P, C> item = getItem(i);
                if (item.isParent()) {
                    return item;
                }
            }
        }
        return null;
    }

    /**
     * 获取指定的父列表项位置返回该列表项在适配器里对应的位置
     *
     * @param parentPosition 指定的父列表项在父列表里的位置
     * @return 指定的父列表项位置在适配器里对应的位置
     */
    private int getParentAdapterPosition(int parentPosition) {
        if (parentPosition < 0) return RecyclerView.NO_POSITION;
        int parentIndex = -1;
        int itemCount = mItems.size();
        for (int i = 0; i < itemCount; i++) {
            if (mItems.get(i).isParent()) {
                parentIndex++;
                if (parentIndex == parentPosition) {
                    return i;
                }
            }
        }
        return RecyclerView.NO_POSITION;
    }

    /**
     * 获取指定的子列表项位置在适配器里对应的位置
     *
     * @param parentPosition 要查询的子列表项所属的父列表项的位置
     * @param childPosition  子列表项的位置
     * @return 子列表项位置在适配器里对应的位置
     */
    private int getChildAdapterPosition(int parentPosition, int childPosition) {
        if (parentPosition < 0 || childPosition < 0) return RecyclerView.NO_POSITION;
        int parentAdapterPosition = getParentAdapterPosition(parentPosition);
        return parentAdapterPosition + childPosition + 1;
    }

    /**
     * 获取父列表项之前展开的所有子列表项数量
     *
     * @param parentAdapterPosition 父列表项在适配器中所对应的位置
     * @return 父列表项之前展开的所有子列表项数量
     */
    private int getBeforeExpandedChildCount(int parentAdapterPosition) {
        if (parentAdapterPosition == 0 || parentAdapterPosition == RecyclerView.NO_POSITION)
            return 0;
        int beforeExpandedChildCount = 0;
        for (int i = 0; i < parentAdapterPosition; i++) {
            if (!(getItem(i).isParent())) {
                beforeExpandedChildCount++;
            }
        }
        return beforeExpandedChildCount;
    }

    /**
     * Test 方法
     * 通知更新所有 ItemView
     */
    public void notifyAllChanged() {
        notifyItemRangeChanged(0, mItems.size());
    }

    /**
     * 同步 parent 的展开折叠状态
     * <p>
     * 如果当前要通知的 parent 未 laid out , 后期当该 parent attach 到 {@link RecyclerView} 时,
     * 会完成同步并回调相关接口
     * </p>
     *
     * @param parentPosition parent 列表项位置
     */
    private void syncParentExpandableState(int parentPosition) {
        final int parentAdapterPos = getParentAdapterPosition(parentPosition);
        if (parentAdapterPos == RecyclerView.NO_POSITION) return;
        ItemWrapper itemWrapper = getItem(parentAdapterPos);
        final boolean expandable = itemWrapper.isExpandable();
        Logger.e(TAG, "syncParentExpandableState=>" + parentPosition + "," + expandable);
        for (RecyclerView rv : mAttachedRecyclerViews) {
            ParentViewHolder pvh = (ParentViewHolder) rv
                    .findViewHolderForAdapterPosition(parentAdapterPos);
            if (pvh != null && pvh.isExpandable() != expandable) {
                pvh.setExpandable(expandable);
                notifyParentExpandableStateChanged(pvh, expandable);
            } else if (pvh == null) {
                pendingExpandableAdd(rv, parentPosition);
            }
        }
    }

    /**
     * 通知客户端相关 parent 可展开折叠状态改变
     *
     * @param pvh        {@link ParentViewHolder} 所代表的 parent
     * @param expandable 当前该 parent 是否可展开折叠
     */
    private void notifyParentExpandableStateChanged(ParentViewHolder pvh, boolean expandable)
    {
        if (Preconditions.isNullOrEmpty(mExpandableStateChangeListeners)) return;
        int parentAdapterPos = pvh.getAdapterPosition();
        for (OnParentExpandableStateChangeListener listener : mExpandableStateChangeListeners) {
            int parentPos = parentAdapterPos - getBeforeExpandedChildCount(parentAdapterPos);
            listener.onParentExpandableStateChanged(findParent(pvh), pvh, parentPos, expandable);
        }
    }

    /**
     * 展开指定适配器位置所对应的父列表项
     *
     * @param parentPosition 父列表项在父列表里所对应的位置
     * @return 是否展开成功, 如果当前已经展开了或者该 Parent 没有 Child，亦或是内部异常发生时返回 false
     */
    private boolean expandParent(int parentPosition, boolean force)
    {
        if (parentPosition == RecyclerView.NO_POSITION) return false;
        final int parentAdapterPosition = getParentAdapterPosition(parentPosition);
        ItemWrapper<P, C> item = getItem(parentAdapterPosition);
        if (!item.isParent()) return false;
        //如果非强制展开 Parent 并且当前 parent 无法展开或者当前 Parent 已展开情况下调用无效
        if (!force && (!item.isExpandable() || item.isExpanded())) return false;
        //如果强制展开 Parent 并且当前 parent 已展开时调用也无效，例如:程序调用展开同一 parent 方法多次
        if (force && item.isExpanded()) return false;

        List<C> children = item.getChildren();
        if (!item.hasChildren(children)) return false;

        //保存该父列表项当前为展开状态
        item.setExpanded(true);

        final int insertPosStart = parentAdapterPosition + 1;
        //按照顺序依次将子列表项插入到该父列表项下
        final int childCount = addChildWrapper(parentPosition, 0, children);
        //通知 RecyclerView 指定位置有新的列表项插入，刷新界面
        notifyItemRangeInserted(insertPosStart, childCount);
        //检查当前的展开模式，如果为单项展开模式处理单项 Parent 展开逻辑
        checkSingleExpandMode(parentPosition);

        return true;
    }

    /**
     * 检查并处理当前是否为单项 Parent 展开模式逻辑
     * <p>
     * 单项 Parent 模式下，记录上次展开的 parent 的 position 和 adapterPosition 供下次展开其他 Parent
     * 时自动折叠该位置记录下的 parentItem
     * </p>
     *
     * @param currExpandedParentPosition 当前已展开的 parent 的父列表项位置
     */
    private void checkSingleExpandMode(int currExpandedParentPosition) {
        if (mExpandCollapseMode == null ||
            mExpandCollapseMode.mode != ExpandCollapseMode.MODE_SINGLE_EXPAND) return;
        final int lastExpandedPosition = mExpandCollapseMode.lastExpandedPosition;
        if (lastExpandedPosition == currExpandedParentPosition) return;
        Logger.e(TAG,
                "checkSingleExpandMode " + "---->lastExpandedPosition=*" + lastExpandedPosition);
        //折叠上次记录的展开位置对应的 parentItem，注意这里需转换的是上次的 position 而非参数 currExpandedAdapterPosition!
        collapseView(lastExpandedPosition, true, false);
        //将当前展开的 parentItem 相关位置记录为下次自动折叠位置
        mExpandCollapseMode.lastExpandedPosition = currExpandedParentPosition;
    }

    /**
     * 通知所有外部已注册监听 Parent 展开折叠状态的监听器当前 Parent
     * <p><b>注意:通知的前提必须已经调用 {@link RecyclerView Adapter} 通知 {@link RecyclerView} 数据改变</b></p>
     *
     * @param pvh    被展开的 Parent
     * @param byUser 是否是被用户手动展开的
     */
    private void notifyParentExpanded(ParentViewHolder pvh, boolean pendingCause, boolean byUser)
    {
        if (Preconditions.isNullOrEmpty(mExpandCollapseListeners)) return;
        int parentAdapterPos = pvh.getAdapterPosition();
        for (OnParentExpandCollapseListener listener : mExpandCollapseListeners) {
            int parentPos = parentAdapterPos - getBeforeExpandedChildCount(parentAdapterPos);
            listener.onParentExpanded(findParent(pvh), pvh, parentPos, pendingCause, byUser);
        }
    }

    /**
     * 折叠指定适配器位置所对应的父列表项
     *
     * @param parentPosition 父列表项在父列表里所对应的位置
     */
    private boolean collapseParent(int parentPosition, boolean force)
    {
        if (parentPosition == RecyclerView.NO_POSITION) return false;
        final int parentAdapterPosition = getParentAdapterPosition(parentPosition);
        ItemWrapper<P, C> item = getItem(parentAdapterPosition);
        if (!item.isParent()) return false;
        if (!force && (!item.isExpandable() || !item.isExpanded())) return false;
        if (force && !item.isExpanded()) return false;
        List<C> children = item.getChildren();
        if (!item.hasChildren(children)) return false;

        //保存该父列表项当前为折叠状态
        item.setExpanded(false);

        final int collapsePosStart = parentAdapterPosition + 1;
        final int childCount = children.size();
        //按照顺序依次将该父列表项下的子列表项移除
        for (int i = collapsePosStart; i < collapsePosStart + childCount; i++) {
            mItems.remove(collapsePosStart);
        }
        //通知 RecyclerView 指定位置有列表项已移除，刷新界面
        notifyItemRangeRemoved(collapsePosStart, childCount);
        //检查当前的折叠模式，如果为单项折叠模式处理单项 parentItem 折叠逻辑
        checkSingleCollapseMode(parentPosition);

        return true;
    }

    /**
     * 检查并处理当前是否为单项 Parent 折叠模式逻辑
     * <p>
     * 单项 Parent 模式下，记录上次折叠的 parentItem 的 position 和 adapterPosition 供下次折叠其他 Parent
     * 时自动展开该位置记录下的 parentItem
     * </p>
     *
     * @param currCollapsedParentPosition 当前已折叠的 parent 的父列表项位置
     */
    private void checkSingleCollapseMode(int currCollapsedParentPosition) {
        if (mExpandCollapseMode == null ||
            mExpandCollapseMode.mode != ExpandCollapseMode.MODE_SINGLE_COLLAPSE) return;
        final int lastCollapsedPosition = mExpandCollapseMode.lastCollapsedPosition;
        if (currCollapsedParentPosition == lastCollapsedPosition) return;
        Logger.e(TAG, "checkSingleCollapseMode " + "---->lastCollapsedPosition=*" +
                      lastCollapsedPosition);
        //展开上次记录的折叠位置对应的 parentItem，注意这里需转换的是上次的 position 而非参数 currCollapsedAdapterPosition!
        expandView(lastCollapsedPosition, true, false);
        //将当前折叠的 parentItem 相关位置记录为下次自动展开位置
        mExpandCollapseMode.lastCollapsedPosition = currCollapsedParentPosition;
    }

    /**
     * 通知所有外部已注册监听 Parent 展开折叠状态的监听器当前 Parent 已折叠
     * <p><b>注意:通知的前提必须已经调用 {@link RecyclerView Adapter} 通知 {@link RecyclerView} 数据改变</b></p>
     *
     * @param pvh    被折叠的 Parent
     * @param byUser 是否是被用户手动折叠的
     */
    private void notifyParentCollapsed(ParentViewHolder pvh, boolean pendingCause, boolean byUser) {
        if (Preconditions.isNullOrEmpty(mExpandCollapseListeners)) return;
        int parentAdapterPos = pvh.getAdapterPosition();
        for (OnParentExpandCollapseListener listener : mExpandCollapseListeners) {
            int parentPos = parentAdapterPos - getBeforeExpandedChildCount(parentAdapterPos);
            listener.onParentCollapsed(findParent(pvh), pvh, parentPos, pendingCause, byUser);
        }
    }

    /**
     * 同步 attach 到当前 adapter 的所有 RecyclerView 指定的适配器位置的 parentItem 的展开状态
     * <p>
     * <b>注意:</b>可能由于 ParentItem 未 laid out 到 RecyclerView 并且 RecyclerView 没有正确回调
     * {@link #onBindParentViewHolder(ParentViewHolder, int)} 方法导致同步
     * 展开状态失败，这里存储待处理的 parentItem 的位置，以至于能够在 {@link #onViewAttachedToWindow(BaseViewHolder)}
     * 时处理状态同步逻辑
     * </p>
     *
     * @param parentPosition 指定的同步展开状态的 parent 的在父列表项里的位置
     */
    @SuppressWarnings("unchecked")
    private void syncViewExpansionState(Integer parentPosition, boolean byUser)
    {
        Logger.e(TAG, "syncViewExpansionState=>" + parentPosition);
        int parentAdapterPos = getParentAdapterPosition(parentPosition);
        if (parentAdapterPos == RecyclerView.NO_POSITION) return;
        ItemWrapper itemWrapper = getItem(parentAdapterPos);
        boolean expanded = itemWrapper.isExpanded();

        for (RecyclerView rv : mAttachedRecyclerViews) {
            PVH pvh = (PVH) rv.findViewHolderForAdapterPosition(parentAdapterPos);
            if (pvh != null && !pvh.isExpanded() && expanded) {
                //这里需要额外判断当该 parent 可以 expandable 时才应用同步设置和通知展开逻辑
                //防止不可展开的 parent 的不必要的同步设置和通知处理
                pvh.setExpanded(true);
                notifyParentExpanded(pvh, false, byUser);
            } else if (pvh == null && expanded) {
                Logger.e(TAG, "expandViews pvh is null---->parentPos=" + parentPosition +
                              ",parentAdapterPos=" + parentAdapterPos);
                //未 laid out 的 Parent ,虽然无法获取并设置展开折叠标识，
                // 这里添加待处理展开逻辑的所有 parentItem 的 position
                // 如果先前已经记录待折叠位置记录，移除该记录并以最新的待展开记录为准!
                pendingCollapseRemove(rv, parentPosition);

                pendingExpandAdd(rv, parentPosition);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void syncViewCollapseState(Integer parentPosition, boolean byUser)
    {
        Logger.e(TAG, "syncViewCollapseState=>" + parentPosition);
        int parentAdapterPos = getParentAdapterPosition(parentPosition);
        if (parentAdapterPos == RecyclerView.NO_POSITION) return;
        ItemWrapper itemWrapper = getItem(parentAdapterPos);
        boolean collapsed = !itemWrapper.isExpanded();

        for (RecyclerView rv : mAttachedRecyclerViews) {
            PVH pvh = (PVH) rv.findViewHolderForAdapterPosition(parentAdapterPos);
            if (pvh != null && pvh.isExpanded() && collapsed) {
                pvh.setExpanded(false);
                // 通知所有监听 Parent 展开折叠状态监听器当前 Parent 已折叠
                notifyParentCollapsed(pvh, false, byUser);
            } else if (pvh == null && collapsed) {
                // 判断当前的 parent 是否 expandable ,防止记录不可 expandable 的 parent 的待折叠位置信息
                // 当该后期应用该位置的 parent 折叠操作时导致本该应用该位置的 parent 展开操作时被该折叠操作所覆盖问题
                Logger.e(TAG, "collapseViews pvh is null---->parentPos=" + parentPosition +
                              ",parentAdapterPos=" + parentAdapterPos);
                // 未 laid out 的 Parent ,虽然无法获取并设置展开折叠标识，
                // 这里添加待处理折叠逻辑的所有 parentItem 的 position
                // 如果先前已经记录待折叠位置记录，移除该记录并以最新的待展开记录为准!
                pendingExpandRemove(rv, parentPosition);
                pendingCollapseAdd(rv, parentPosition);
            }
        }
    }

    /**
     * 展开指定适配器位置所对应的父列表项
     * <p>循环遍历展开所有 attach 到此 adapter 的 {@link RecyclerView} 中指定的{@code parentAdapterPosition}
     * 所对应的 Parent ,并且同步改变对应 View 层(ParentViewHolder) 中的展开折叠状态
     * </p>
     *
     * @param parentPosition 父列表项在父列表项里所对应的位置
     */
    @SuppressWarnings("unchecked")
    private boolean expandView(int parentPosition, boolean force, boolean byUser) {
        boolean success = expandParent(parentPosition, force);
        //同步 parentItem 展开状态
        //注意:这里需要再次通过 parentPosition 转换为 parentAdapterPosition，而不是直接使用参数 parentAdapterPosition!
        //因为{@link #expandParentItem()可能会因为当前模式为单项展开模式导致内部逻辑会自动折叠先前记录的展开位置的 Parent}
        if (success) syncViewExpansionState(parentPosition, byUser);

        return success;
    }

    /**
     * 根据指定的父列表项在父列表里的位置展开该位置在适配器里对应的父列表项
     *
     * @param parentPosition 该父列表项在父列表里的位置
     */
    public boolean expandParent(int parentPosition) {
        return expandView(parentPosition, true, false);
    }

    /**
     * 展开与{@link Parent}相关的父列表项
     *
     * @param parent 与父列表项相关的 Parent
     */
    public boolean expandParent(P parent) {
        if (parent == null) return false;
        int parentPosition = mParents.indexOf(parent);
        return expandParent(parentPosition);
    }

    /**
     * 展开所有的父列表项
     * <p>
     * <b>注意:如果当前设置的展开折叠 {@link #setExpandCollapseMode(int)} 为
     * {@link ExpandCollapseMode MODE_SINGLE_EXPAND} ,调用该方法不会展开所有的 parentItem，只会展开最后一个
     * parentItem，如果可以展开的话</b>
     * </p>
     */
    public void expandAllParents() {
        for (int i = 0; i < mParents.size(); i++) {
            expandParent(i);
        }
    }

    /**
     * 折叠指定适配器位置所对应的父列表项.
     * <p>循环遍历展开所有 attach 到此 adapter 的 {@link RecyclerView} 中指定的{@code parentAdapterPosition}
     * 所对应的 Parent ,并且同步改变对应 View 层(ParentViewHolder) 中的展开折叠状态
     * </p>
     *
     * @param parentPosition 父列表项在父列表项里所对应的位置
     */
    @SuppressWarnings("unchecked")
    private boolean collapseView(int parentPosition, boolean force, boolean byUser) {
        boolean success = collapseParent(parentPosition, force);
        //同步 parentItem 折叠状态
        //注意:这里需要再次通过 parentPosition 转换为 parentAdapterPosition，而不是直接使用参数 parentAdapterPosition!
        //因为{@link #collapseParentItem()可能会因为当前模式为单项折叠模式导致内部逻辑会自动展开先前记录的折叠位置的 Parent}
        if (success) syncViewCollapseState(parentPosition, byUser);
        return success;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onViewAttachedToWindow(BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (!(holder instanceof ParentViewHolder)) return;
        PVH pvh = (PVH) holder;

        int adapterPos = pvh.getAdapterPosition();
        Integer pos = getParentPosition(adapterPos);

        RecyclerView rv = findParent(pvh);

        if (pendingExpandRemove(rv, pos)) {
            Logger.e(TAG, "onViewAttachedToWindow==PendingExpandPosition=>" + pos + ",tag=" +
                          rv.getTag() + ",," + pvh.isExpanded());
            if (!pvh.isExpanded()) {
                Logger.e(TAG, "PendingExpand==notifyParentExpanded");
                pvh.setExpanded(true);
                notifyParentExpanded(pvh, true, false);
            }
        }

        if (pendingCollapseRemove(rv, pos)) {
            Logger.e(TAG, "onViewAttachedToWindow==PendingCollapsePosition=>" + pos);
            if (pvh.isExpanded()) {
                Logger.e(TAG, "PendingCollapse=notifyParentCollapsed");
                pvh.setExpanded(false);
                notifyParentCollapsed(pvh, true, false);
            }
        }

        //同步之前记录的位置下的 parent 的是否可展开属性
        if (pendingExpandableRemove(rv, pos)) {
            Logger.e(TAG, "onViewAttachedToWindow==PendingExpandablePosition=>" + pos);
            boolean expandable = getItem(adapterPos).isExpandable();
            if (pvh.isExpandable() != expandable) {
                Logger.e(TAG, "PendingExpandable==notifyParentExpandableStateChanged");
                pvh.setExpandable(expandable);
                notifyParentExpandableStateChanged(pvh, expandable);
            }
        }
    }

    /**
     * 根据指定的父列表项在父列表里的位置折叠该位置在适配器里对应的父列表项
     *
     * @param parentPosition 该父列表项在父列表里的位置
     */
    public boolean collapseParent(int parentPosition) {
        return collapseView(parentPosition, true, false);
    }

    /**
     * 折叠与{@link Parent}相关的父列表项
     *
     * @param parent 与父列表项相关的 Parent
     */
    public boolean collapseParent(P parent) {
        if (parent == null) return false;
        int parentPosition = mParents.indexOf(parent);
        return collapseParent(parentPosition);
    }

    /**
     * 折叠所有的父列表项
     * <p>
     * <b>注意:如果当前设置的展开折叠 {@link #setExpandCollapseMode(int)} 为
     * {@link ExpandCollapseMode MODE_SINGLE_COLLAPSE} ,调用该方法不会折叠所有的 parentItem，只会折叠最后一个
     * parentItem，如果可以折叠的话</b>
     * </p>
     */
    public void collapseAllParents() {
        for (int i = 0; i < mParents.size(); i++) {
            collapseParent(i);
        }
    }

    /**
     * 通知任何注册的监视器当前在 {@code parentPosition} 位置有新的父列表项插入，
     * 之前在该位置存在的父列表项将被移动到 {@code parentPosition + 1} 位置。
     * <p>
     * 这是数据结构上的变化，尽管位置变化了，但是之前存在的所有列表项在数据集里的数据都会被认为最新，
     * 因此这些列表项不会被重新绑定数据
     * </p>
     *
     * @param parentPosition 要插入新的父列表项的位置
     * @see #notifyParentItemRangeInserted(int, int)
     */
    public void notifyParentItemInserted(int parentPosition) {
        notifyParentItemRangeInserted(parentPosition, 1);
    }

    /**
     * 通知任何注册的监视器当前在 {@code parentPositionStart} 位置有 {@code parentItemCount} 个父列表项插入，
     * 之前在该位置存在的父列表项将被移动到 {@code parentPosition + parentItemCount} 位置。
     * <p>
     * 这是数据结构上的变化，尽管位置变化了，但是之前存在的所有列表项在数据集里的数据都会被认为最新，
     * 因此这些列表项不会被重新绑定数据
     * </p>
     *
     * @param parentPositionStart 要插入多个新父列表项的位置
     * @param parentItemCount     要插入新父列表项的个数
     * @see #notifyParentItemInserted(int)
     */
    public void notifyParentItemRangeInserted(int parentPositionStart, int parentItemCount) {
        final int endInsertPos = mParents.size() - parentItemCount;
        int parentAdapterPos = RecyclerView.NO_POSITION;
        if (parentPositionStart >= 0 && parentPositionStart < endInsertPos) {
            parentAdapterPos = getParentAdapterPosition(parentPositionStart);
        } else if (parentPositionStart == endInsertPos) {
            parentAdapterPos = mItems.size();
        }
        if (parentAdapterPos == RecyclerView.NO_POSITION) return;
        int index = 0;
        int[] insertedItemCounts = new int[parentItemCount];
        int totalInsertedItemCount = 0;
        for (int i = parentPositionStart, c = parentPositionStart + parentItemCount; i < c; i++) {
            int insertItemCount = addParentWrapper(parentAdapterPos + totalInsertedItemCount, i);
            totalInsertedItemCount += insertItemCount;
            insertedItemCounts[index++] = insertItemCount;
        }
        notifyItemRangeInserted(parentAdapterPos, totalInsertedItemCount);
        //这里如果有 parent 初始化是展开的 默认 notifyParentExpanded 通知客户端，以防万一 parent 展开状态没有同步到 view 层
        //注意：通知客户端通知的前提是当前所有的 Parent 已经 notifyItemRangeInserted 通知 recyclerView 了
        index = 0;
        for (int j = parentPositionStart, c = parentPositionStart + parentItemCount; j < c; j++) {
            syncParentExpandableState(j);
            if (insertedItemCounts[index++] > 1) syncViewExpansionState(j, false);
        }
    }

    /**
     * 通知任何注册的监视器当前在 {@code parentPosition,childPosition} 位置有新的子列表项插入，
     * 之前在该位置存在的子列表项将被移动到 {@code childPosition+1} 位置。
     * <p>
     * 这是数据结构上的变化，尽管位置变化了，但是之前存在的所有列表项在数据集里的数据都会被认为最新，
     * 因此这些列表项不会被重新绑定数据
     * </p>
     *
     * @param parentPosition 要插入的子列表项所属的父列表项位置
     * @param childPosition  要插入的子列表项的位置
     * @see #notifyChildItemRangeInserted(int, int, int, boolean)
     */
    public void notifyChildItemInserted(int parentPosition, int childPosition)
    {
        notifyChildItemInserted(parentPosition, childPosition, true);
    }

    /**
     * 通知任何注册的监视器当前在 {@code parentPosition,childPosition} 位置有新的子列表项插入，
     * 之前在该位置存在的子列表项将被移动到 {@code childPosition+1} 位置。
     * <p>
     * 这是数据结构上的变化，尽管位置变化了，但是之前存在的所有列表项在数据集里的数据都会被认为最新，
     * 因此这些列表项不会被重新绑定数据
     * </p>
     *
     * @param parentPosition    要插入的子列表项所属的父列表项位置
     * @param childPosition     要插入的子列表项的位置
     * @param forceExpandParent 是否强制展开已插入的子列表项所属的父列表项，如果当前父列表项没有展开
     * @see #notifyChildItemRangeInserted(int, int, int, boolean)
     */
    public void notifyChildItemInserted(int parentPosition, int childPosition,
            boolean forceExpandParent)
    {
        notifyChildItemRangeInserted(parentPosition, childPosition, 1, forceExpandParent);
    }

    /**
     * 通知任何注册的监视器当前在 {@code parentPosition,childPositionStart} 位置有 {@code childItemCount} 个子列表项插入，
     * 之前在该位置存在的子列表项将被移动到 {@code childPositionStart + childItemCount} 位置。
     * <p>
     * 这是数据结构上的变化，尽管位置变化了，但是之前存在的所有列表项在数据集里的数据都会被认为最新，
     * 因此这些列表项不会被重新绑定数据
     * </p>
     *
     * @param parentPosition     要插入多个新子列表项所属的父列表项位置
     * @param childPositionStart 要插入多个新子列表项的位置
     * @param childItemCount     要插入的子列表项的个数
     * @see #notifyChildItemInserted(int, int, boolean)
     */
    public void notifyChildItemRangeInserted(int parentPosition, int childPositionStart,
            int childItemCount)
    {
        notifyChildItemRangeInserted(parentPosition, childPositionStart, childItemCount, true);
    }

    /**
     * 通知任何注册的监视器当前在 {@code parentPosition,childPositionStart} 位置有 {@code childItemCount} 个子列表项插入，
     * 之前在该位置存在的子列表项将被移动到 {@code childPositionStart + childItemCount} 位置。
     * <p>
     * 这是数据结构上的变化，尽管位置变化了，但是之前存在的所有列表项在数据集里的数据都会被认为最新，
     * 因此这些列表项不会被重新绑定数据
     * </p>
     *
     * @param parentPosition     要插入多个新子列表项所属的父列表项位置
     * @param childPositionStart 要插入多个新子列表项的位置
     * @param childItemCount     要插入的子列表项的个数
     * @param forceExpandParent  是否强制展开已插入的子列表项所属的父列表项，如果当前父列表项没有展开
     * @see #notifyChildItemInserted(int, int, boolean)
     */
    public void notifyChildItemRangeInserted(int parentPosition, int childPositionStart,
            int childItemCount, boolean forceExpandParent)
    {
        int parentAdapterPos = getParentAdapterPosition(parentPosition);
        if (parentAdapterPos == RecyclerView.NO_POSITION) return;
        ItemWrapper<P, C> itemWrapper = getItem(parentAdapterPos);
        //这里判断如果父列表项已展开才添加数据并通知刷新列表界面
        //注意：这里的数据添加和 {@code expandParentItem } 数据添加有冲突，因为 expandParentItem
        //获取子列表数据然后再添加到模型数据层再刷新界面的，这里如果判断没展开就不能添加数据，否则有重复数据显示
        if (!itemWrapper.isExpanded()) {
            if (itemWrapper.isInitiallyExpandable() &&
                itemWrapper.setExpandable(itemWrapper.hasChildren()))
            {
                syncParentExpandableState(parentPosition);
            }
            if (forceExpandParent) {
                expandView(parentPosition, true, false);
            }
            return;
        }

        List<C> children = itemWrapper.getChildren();
        if (!itemWrapper.hasChildren(children)) return;
        List<C> insertedChildren = children
                .subList(childPositionStart, childItemCount + childPositionStart);

        int childAdapterPos = RecyclerView.NO_POSITION;
        if (childPositionStart >= 0 && childPositionStart < children.size() - childItemCount) {
            childAdapterPos = getChildAdapterPosition(parentPosition, childPositionStart);
        } else if (childPositionStart == children.size() - childItemCount) {
            childAdapterPos = parentAdapterPos + children.size() - childItemCount + 1;
        }
        if (childAdapterPos == RecyclerView.NO_POSITION) return;

        childItemCount = addChildWrapper(parentPosition, childPositionStart, insertedChildren);
        notifyItemRangeInserted(childAdapterPos, childItemCount);
    }

    /**
     * 通知任何注册的监视器移除当前在 {@code parentPosition} 的父列表项，
     * 之前在该位置之下并存在的所有父列表项将被移动到 {@code oldPosition - 1} 位置。
     * <p>
     * 这是数据结构上的变化，尽管位置变化了，但是之前存在的所有列表项在数据集里的数据都会被认为最新，
     * 因此这些列表项不会被重新绑定数据
     * </p>
     *
     * @param parentPosition 要移除的父列表项在父列表里的位置
     * @see #notifyParentItemRangeRemoved(int, int)
     */
    public void notifyParentItemRemoved(int parentPosition) {
        notifyParentItemRangeRemoved(parentPosition, 1);
    }

    /**
     * 通知任何注册的监视器移除当前从 {@code parentPositionStart} 位置开始 {@code parentItemCount} 个 的父列表项，
     * 之前在该位置之下并存在的所有父列表项将被移动到 {@code oldPosition - parentItemCount} 位置。
     * <p>
     * 这是数据结构上的变化，尽管位置变化了，但是之前存在的所有列表项在数据集里的数据都会被认为最新，
     * 因此这些列表项不会被重新绑定数据
     * </p>
     *
     * @param parentPositionStart 移除多个父列表项开始的位置
     * @param parentItemCount     移除父列表项的个数
     * @see #notifyParentItemRemoved(int)
     */
    public void notifyParentItemRangeRemoved(int parentPositionStart, int parentItemCount) {
        int parentAdapterPosStart = getParentAdapterPosition(parentPositionStart);
        if (parentAdapterPosStart == RecyclerView.NO_POSITION) return;
        //计算移除的 ItemView 个数
        int removedItemCount = 0;
        for (int i = parentAdapterPosStart, c = parentAdapterPosStart + parentItemCount; i < c; i++)
        {
            ItemWrapper<P, C> itemWrapper = getItem(parentAdapterPosStart);
            mItems.remove(parentAdapterPosStart);
            removedItemCount++;
            //判断当前 Parent 是否为展开状态
            if (!itemWrapper.isExpanded()) continue;
            List<C> children = itemWrapper.getChildren();
            if (!itemWrapper.hasChildren(children)) continue;
            int removedChildCount = children.size();
            for (int j = parentAdapterPosStart, cc = parentAdapterPosStart + removedChildCount; j < cc; j++) {
                mItems.remove(parentAdapterPosStart);
            }
            removedItemCount += removedChildCount;
        }
        notifyItemRangeRemoved(parentAdapterPosStart, removedItemCount);
    }

    /**
     * 通知任何注册的监视器移除当前在 {@code parentPosition,childPosition} 位置的子列表项，
     * 之前在该位置之下并存在的所有子列表项将被移动到 {@code oldPosition - 1} 位置。
     * <p>
     * 这是数据结构上的变化，尽管位置变化了，但是之前存在的所有列表项在数据集里的数据都会被认为最新，
     * 因此这些列表项不会被重新绑定数据
     * </p>
     *
     * @param parentPosition 要移除的子列表项所属的父列表项的位置
     * @param childPosition  要移除的子列表项的位置
     * @see #notifyChildItemRangeRemoved(int, int, int)
     */
    public void notifyChildItemRemoved(int parentPosition, int childPosition) {
        notifyChildItemRemoved(parentPosition, childPosition, false);
    }

    public void notifyChildItemRemoved(int parentPosition, int childPosition,
            boolean forceCollapseParent)
    {
        notifyChildItemRangeRemoved(parentPosition, childPosition, 1, forceCollapseParent);
    }

    public void notifyChildItemRangeRemoved(int parentPosition, int childPositionStart,
            int childItemCount)
    {
        notifyChildItemRangeRemoved(parentPosition, childPositionStart, childItemCount, false);
    }

    /**
     * 通知任何注册的监视器移除当前从 {@code parentPosition,childPositionStart} 位置 {@code childItemCount} 个子列表项，
     * 之前在该位置之下并存在的所有子列表项将被移动到 {@code oldPosition - childItemCount} 位置。
     * <p>
     * 这是数据结构上的变化，尽管位置变化了，但是之前存在的所有列表项在数据集里的数据都会被认为最新，
     * 因此这些列表项不会被重新绑定数据
     * </p>
     *
     * @param parentPosition     要移除的多个子列表项所属的父列表项的位置
     * @param childPositionStart 移除多个子列表项所开始的位置
     * @param childItemCount     移除的子列表项的个数
     * @see #notifyChildItemRemoved(int, int)
     */
    public void notifyChildItemRangeRemoved(int parentPosition, int childPositionStart,
            int childItemCount, boolean forceCollapseParent)
    {
        int parentAdapterPos = getParentAdapterPosition(parentPosition);
        if (parentAdapterPos == RecyclerView.NO_POSITION) return;

        ItemWrapper<P, C> itemWrapper = getItem(parentAdapterPos);
        //判断是否所有的 child 都移除了,
        boolean allChildRemoved = !itemWrapper.hasChildren();
        //注意：这里判断当前父列表项是否已经打开，只有打开更改本地数据结构并通知刷新，否则会出现数据混乱异常
        if (!itemWrapper.isExpanded()) {
            //如果子列表项都删除了，默认通知这些删除的子列表项所属的父列表项已变为折叠状态
            if (allChildRemoved && itemWrapper.setExpandable(false)) {
                syncParentExpandableState(parentPosition);
            }
            return;
        }

        int childAdapterPosStart = getChildAdapterPosition(parentPosition, childPositionStart);
        if (childAdapterPosStart == RecyclerView.NO_POSITION) return;

        for (int i = childAdapterPosStart, c = childAdapterPosStart + childItemCount; i < c; i++) {
            mItems.remove(childAdapterPosStart);
        }
        notifyItemRangeRemoved(childAdapterPosStart, childItemCount);

        //强制在移除 child 时自动折叠 parent
        if (forceCollapseParent && !allChildRemoved) {
            collapseView(parentPosition, true, false);
        } else {
            //如果子列表项都删除了，默认通知这些删除的子列表项所属的父列表项已变为折叠状态
            if (allChildRemoved) {
                if (itemWrapper.setExpandable(false)) {
                    syncParentExpandableState(parentPosition);
                }
                itemWrapper.setExpanded(false);
                //同步 ParentViewHolder 和 ItemWrapper 的 Parent 展开状态
                syncViewCollapseState(parentPosition, false);
                //检查单项折叠情况
                checkSingleCollapseMode(parentPosition);
            }
        }
    }

    /**
     * 通知任何注册的监视器更新当前在 {@code parentPosition} 位置的父列表项，
     * <p>
     * <p>
     * 这是列表项相关的数据的改变，不是数据结构上的变化，因此，该列表项在适配器数据集里对应位置的数据已经过期，
     * 需要更新该列表项所绑定的数据
     * </p>
     *
     * @param parentPosition 要更新的父列表项在父列表里的位置
     */
    public void notifyParentItemChanged(int parentPosition) {
        P changedParent = mParents.get(parentPosition);
        if (changedParent == null) return;
        int parentAdapterPos = getParentAdapterPosition(parentPosition);
        if (parentAdapterPos == RecyclerView.NO_POSITION) return;
        ItemWrapper<P, C> itemWrapper = getItem(parentAdapterPos);
        final boolean beforeExpanded = itemWrapper.isExpanded();
        if (beforeExpanded) {
            notifyChildItemRangeRemoved(parentPosition, 0, itemWrapper.getChildCount(), false);
        }
        itemWrapper.setParent(changedParent);
        if (itemWrapper.isInitiallyExpandable() &&
            itemWrapper.setExpandable(itemWrapper.hasChildren()))
        {
            syncParentExpandableState(parentPosition);
        }
        if (itemWrapper.isInitiallyExpanded() && itemWrapper.hasChildren()) {
            expandView(parentPosition, true, false);
        }
        notifyItemChanged(getParentAdapterPosition(parentPosition));
    }

    /**
     * 通知任何注册的件事情在位置 {@code parentPositionStart} 上有 {@code parentItemCount} 个数据发生改变
     * <p>相当于调用 {@code notifyParentItemRangeChanged(parentPositionStart,parentItemCount,null)}</p>
     * <p>这是列表项相关的数据的改变，不是数据结构上的变化，因此，该列表项在适配器数据集里对应位置的数据已经过期，
     * 需要更新该列表项所绑定的数据,在该范围里的列表保留相同的 Id</p>
     *
     * @param parentPositionStart 父列表项数据更改的起始位置
     * @param parentItemCount     父列表项数据更改的数量
     */
    public void notifyParentItemRangeChanged(int parentPositionStart, int parentItemCount) {
        //判断当前是否所有的 Parent 都是折叠状态
        //1.如果都为折叠状态，直接调用 {@link notifyItemRangeChanged(positionStart,itemCount)}
        //2.只要有一个是打开状态，循环遍历调用 {@link notifyParentItemChanged(parentPosition)}
        boolean allCollapsed = true;
        for (int i = parentPositionStart, c = parentPositionStart + parentItemCount; i < c; i++) {
            int parentAdapterPos = getParentAdapterPosition(i);
            if (parentAdapterPos == RecyclerView.NO_POSITION) {
                allCollapsed = false;
                break;
            }
            ItemWrapper<P, C> itemWrapper = getItem(parentAdapterPos);
            if (itemWrapper.isExpanded()) {
                allCollapsed = false;
                break;
            }
        }
        if (allCollapsed) {
            final int parentAdapterPosStart = getParentAdapterPosition(parentPositionStart);
            notifyItemRangeChanged(parentAdapterPosStart, parentItemCount);
        } else {
            for (int i = parentPositionStart, c = parentPositionStart + parentItemCount; i < c; i++)
            {
                notifyParentItemChanged(i);
            }
        }
    }

    /**
     * 通知任何注册的监视器更新当前在 {@code parentPosition，childPosition} 位置的子列表项，
     *
     * @param parentPosition 该子列表项所属的父列表项位置
     * @param childPosition  子列表项的位置
     */
    public void notifyChildItemChanged(int parentPosition, int childPosition) {
        notifyChildItemRangeChanged(parentPosition, childPosition, 1);
    }

    /**
     * 通知任何注册的件事情在位置 {@code parentPosition,childPositionStart} 上有 {@code childItemCount} 个数据发生改变
     * <p>相当于调用 {@code notifyParentItemRangeChanged(parentPositionStart,parentItemCount,null)}</p>
     * <p>这是列表项相关的数据的改变，不是数据结构上的变化，因此，该列表项在适配器数据集里对应位置的数据已经过期，
     * 需要更新该列表项所绑定的数据,在该范围里的列表保留相同的 Id</p>
     *
     * @param parentPosition     子列表数据更改从属的父列表项位置
     * @param childPositionStart 子列表数据更改的起始子列表项位置
     * @param childItemCount     子列表数据更改的数量
     */
    public void notifyChildItemRangeChanged(int parentPosition, int childPositionStart,
            int childItemCount)
    {
        int parentAdapterPos = getParentAdapterPosition(parentPosition);
        if (parentAdapterPos == RecyclerView.NO_POSITION) return;

        ItemWrapper<P, C> itemWrapper = getItem(parentAdapterPos);
        //父列表项没有展开，不执行更新子列表相关事务
        if (!itemWrapper.isExpanded()) return;
        List<C> children = itemWrapper.getChildren();
        if (!itemWrapper.hasChildren(children)) return;

        int changedItemCount = 0;
        for (int i = childPositionStart, c = childPositionStart + childItemCount; i < c; i++) {
            C changedChild = children.get(i);
            if (changedChild == null) continue;
            int childAdapterPos = getChildAdapterPosition(parentPosition, i);
            if (childAdapterPos == RecyclerView.NO_POSITION) continue;
            mItems.set(childAdapterPos, new ItemWrapper<P, C>(changedChild));
            changedItemCount++;
        }
        final int childAdapterPosStart = getChildAdapterPosition(parentPosition,
                childPositionStart);
        notifyItemRangeChanged(childAdapterPosStart, changedItemCount);
    }

    /**
     * 通知任何注册的监视器在 {@code fromParentPosition} 位置的父列表项已经移动到 {@code toParentPosition}位置，
     * <p>
     * 这是数据结构上的变化，尽管位置变化了，但是之前存在的所有列表项在数据集里的数据都会被认为最新，
     * 因此这些列表项不会被重新绑定数据
     * </p>
     *
     * @param fromParentPosition 该父列表项项之前的位置
     * @param toParentPosition   移动后的位置
     */
    public void notifyParentItemMoved(int fromParentPosition, int toParentPosition)
    {
        if (fromParentPosition == toParentPosition) return;

        int fromParentAdapterPos = getParentAdapterPosition(fromParentPosition);
        int toParentAdapterPos = getParentAdapterPosition(toParentPosition);
        if (fromParentAdapterPos == RecyclerView.NO_POSITION ||
            toParentAdapterPos == RecyclerView.NO_POSITION) return;

        ItemWrapper<P, C> fromItemWrapper = getItem(fromParentAdapterPos);
        ItemWrapper<P, C> toItemWrapper = getItem(toParentAdapterPos);
        final boolean isFromExpanded = fromItemWrapper.isExpanded();
        final boolean isToExpanded = toItemWrapper.isExpanded();
        //Parent 或 Child 往下 Move 需要特殊处理
        boolean moveToBottom = fromParentPosition < toParentAdapterPos;

        mItems.remove(fromParentAdapterPos);
        int moveToParentAdapterPos;
        //这里需要判断 toParentItem 展开状态来计算出 fromParentItem 的 moveTo 位置
        if (moveToBottom && isToExpanded && toItemWrapper.getChildCount() > 0) {
            moveToParentAdapterPos = toParentAdapterPos + toItemWrapper.getChildCount();
        } else {
            moveToParentAdapterPos = toParentAdapterPos;
        }
        mItems.add(moveToParentAdapterPos, fromItemWrapper);
        notifyItemMoved(fromParentAdapterPos, moveToParentAdapterPos);

        //根据 fromParentItem 的展开状态判断是否需要移动其 ChildItem(s)
        if (isFromExpanded) {
            List<C> children = fromItemWrapper.getChildren();
            if (!fromItemWrapper.hasChildren(children)) return;

            final int childCount = children.size();
            for (int i = 0; i < childCount; i++) {
                C fromChild = children.get(i);
                if (fromChild == null) continue;
                int fromChildAdapterPos =
                        moveToBottom ? fromParentAdapterPos : fromParentAdapterPos + i + 1;
                int toChildAdapterPos =
                        moveToBottom ? moveToParentAdapterPos : moveToParentAdapterPos + i + 1;
                mItems.remove(fromChildAdapterPos);
                mItems.add(toChildAdapterPos, new ItemWrapper<P, C>(fromChild));
                notifyItemMoved(fromChildAdapterPos, toChildAdapterPos);
            }
        }
    }

    /**
     * 通知任何注册的监视器在 {@code fromParentPosition,fromChildPosition} 位置的子列表项已经移动到 {@code
     * toParentPosition,toChildPosition}位置，
     * <p>
     * 这是数据结构上的变化，尽管位置变化了，但是之前存在的所有列表项在数据集里的数据都会被认为最新，
     * 因此这些列表项不会被重新绑定数据
     * </p>
     * <b>注意:1.如果移动后的 parent 有 child 并且只有一个(第一次插入),那么会自动展开并同步通知该 parent 展开状态
     * 2.如果移动后的 parent 没有 child 并且 当前被移动的 child 所属的 parent 处于折叠状态,也会同步通知 parent 折叠状态
     * </b>
     *
     * @param fromParentPosition 子列表项移动所在的父列表项的起始位置
     * @param fromChildPosition  子列表项移动的起始位置
     * @param toParentPosition   子列表项移动所在的父列表项的目标位置
     * @param toChildPosition    子列表项移动的目标位置
     */
    public void notifyChildItemMoved(int fromParentPosition, int fromChildPosition,
            int toParentPosition, int toChildPosition)
    {
        if (fromParentPosition == toParentPosition && fromChildPosition == toChildPosition) return;
        int fromParentAdapterPos = getParentAdapterPosition(fromParentPosition);
        int fromChildAdapterPos = getChildAdapterPosition(fromParentPosition, fromChildPosition);
        int toParentAdapterPos = getParentAdapterPosition(toParentPosition);
        int toChildAdapterPos = getChildAdapterPosition(toParentPosition, toChildPosition);
        if (fromParentAdapterPos == RecyclerView.NO_POSITION ||
            fromChildAdapterPos == RecyclerView.NO_POSITION ||
            toParentAdapterPos == RecyclerView.NO_POSITION ||
            toChildAdapterPos == RecyclerView.NO_POSITION) return;

        ItemWrapper fromItemWrapper = (ItemWrapper) getItem(fromParentAdapterPos);
        ItemWrapper toItemWrapper = (ItemWrapper) getItem(toParentAdapterPos);
        if (fromItemWrapper.isExpanded() && toItemWrapper.isExpanded()) {
            C fromChild = getItem(fromChildAdapterPos).getChild();
            mItems.remove(fromChildAdapterPos);
            toChildAdapterPos = getChildAdapterPosition(toParentPosition, toChildPosition);
            mItems.add(toChildAdapterPos, new ItemWrapper<P, C>(fromChild));
            notifyItemMoved(fromChildAdapterPos, toChildAdapterPos);
            if (!fromItemWrapper.hasChildren()) {
                if (fromItemWrapper.setExpandable(false)) {
                    syncParentExpandableState(fromParentPosition);
                }
                fromItemWrapper.setExpanded(false);
                syncViewCollapseState(fromParentPosition, false);
                checkSingleCollapseMode(fromParentPosition);
            }
        } else {
            notifyChildItemRemoved(fromParentPosition, fromChildPosition, false);//是否强制折叠?
            notifyChildItemInserted(toParentPosition, toChildPosition, true);//是否强制展开?
        }
    }

    private void checkViewType(int viewType) {
        if (viewType < 0) {
            throw new IllegalArgumentException("viewType should not be negative");
        }
    }

    /**
     * 保存 Parent 的展开状态
     * <p>在屏幕旋转或者退出应用时保存当前所有 Parent 的展开状态</p>
     *
     * @param outState 存储需要保存的数据的 Bundle
     */
    //TODO 保存并恢复当前 expandCollapseMode 为非 default 时的先前 折叠/展开 的 parent位置信息
    public void onSaveInstanceState(Bundle outState) {
        if (outState == null) return;
        outState.putParcelable(SAVED_EXPANSION_STATE, getSavedState());
    }

    //———————————————————————————保存 Parent 的展开状态———————————————————————————————————————\\

    private SavedState getSavedState() {
        final int count = getParentCount();
        boolean[] expandableState = new boolean[count];
        boolean[] expansionState = new boolean[count];
        SavedState savedState = new SavedState(expandableState, expansionState);
        final int itemCount = mItems.size();
        int index = 0;
        for (int i = 0; i < itemCount; i++) {
            ItemWrapper item = getItem(i);
            if (item.isParent()) {
                expandableState[index] = item.isExpandable();
                expansionState[index] = item.isExpanded();
                index++;
            }
        }
        return savedState;
    }

    private int getParentCount() {
        int count = 0;
        for (ItemWrapper<P, C> item : mItems) {
            if (item.isParent()) {
                count++;
            }
        }
        return count;
    }

    /**
     * 恢复 Parent 的展开状态
     * <p>如果恢复时 parent 数据与之前保存数据的不对应，那么默认按照原先保存的状态去恢复</p>
     *
     * @param savedInstanceState 之前保存过的数据的 Bundle
     */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) return;
        SavedState savedState = savedInstanceState.getParcelable(SAVED_EXPANSION_STATE);
        if (savedState == null) return;
        boolean[] savedExpandableState = savedState.getExpandableState();
        boolean[] savedExpansionState = savedState.getExpansionState();
        if (savedExpandableState == null || savedExpansionState == null) return;

        List<ItemWrapper<P, C>> savedItems = new ArrayList<>();
        final int savedCount = savedExpansionState.length;
        final int parentCount = mParents.size();
        for (int i = 0; i < parentCount; i++) {
            P parent = mParents.get(i);
            if (parent == null) continue;
            ItemWrapper<P, C> itemWrapper = new ItemWrapper<>(parent);
            savedItems.add(itemWrapper);
            if (i < savedCount) {
                final boolean hasChildren = itemWrapper.hasChildren();
                itemWrapper.setExpandable(savedExpandableState[i] && hasChildren);
                final boolean isExpanded = savedExpansionState[i];
                if (isExpanded) {
                    List<C> children = itemWrapper.getChildren();
                    if (!hasChildren) continue;
                    itemWrapper.setExpanded(true);
                    final int childCount = children.size();
                    for (int j = 0; j < childCount; j++) {
                        C child = children.get(j);
                        if (child == null) continue;
                        savedItems.add(new ItemWrapper<P, C>(child));
                    }
                }
            }
        }
        mItems = savedItems;
        notifyDataSetChanged();
    }

    /**
     * 设置需要监听点击事件的 View (itemView/itemView's child view)
     *
     * @param ids 所有需要监听点击事件的 View 的 id
     * @return 链式对象，配置其他设置
     */
    public ExpandableAdapter parentClickTargets(Integer... ids) {
        mParentClickTargets = ids;
        return this;
    }

    /**
     * 设置需要监长按击事件的 View (itemView/itemView's child view)
     *
     * @param ids 所有需要监听长按事件的 View 的 id
     * @return 链式对象，配置其他设置
     */
    public ExpandableAdapter parentLongClickTargets(Integer... ids) {
        mParentLongClickTargets = ids;
        return this;
    }

    /**
     * 设置需要监听点击事件的 View (itemView/itemView's child view)
     *
     * @param ids 所有需要监听点击事件的 View 的 id
     * @return 链式对象，配置其他设置
     */
    public ExpandableAdapter childClickTargets(Integer... ids) {
        mChildClickTargets = ids;
        return this;
    }

    /**
     * 设置需要监长按击事件的 View (itemView/itemView's child view)
     *
     * @param ids 所有需要监听长按事件的 View 的 id
     * @return 链式对象，配置其他设置
     */
    public ExpandableAdapter childLongClickTargets(Integer... ids) {
        mChildLongClickTargets = ids;
        return this;
    }

    /**
     * 配置一个监听 View (itemView/itemView's child view) 的点击事件监听器
     *
     * @param listener 点击事件监听器
     * @return 链式对象，配置其他设置
     */
    public ExpandableAdapter listenParentClick(OnParentClickListener listener) {
        mParentClickListener = listener;
        return this;
    }

    /**
     * 配置一个监听 View (itemView/itemView's child view) 的长按事件监听器
     *
     * @param listener 长按事件监听器
     * @return 链式对象，配置其他设置
     */
    public ExpandableAdapter listenParentLongClick(OnParentLongClickListener listener) {
        mParentLongClickListener = listener;
        return this;
    }


    /**
     * 配置一个监听 View (itemView/itemView's child view) 的点击事件监听器
     *
     * @param listener 点击事件监听器
     * @return 链式对象，配置其他设置
     */
    public ExpandableAdapter listenChildClick(OnChildClickListener listener) {
        mChildClickListener = listener;
        return this;
    }

    /**
     * 配置一个监听 View (itemView/itemView's child view) 的长按事件监听器
     *
     * @param listener 长按事件监听器
     * @return 链式对象，配置其他设置
     */
    public ExpandableAdapter listenChildLongClick(OnChildLongClickListener listener) {
        mChildLongClickListener = listener;
        return this;
    }

    private ViewEventWatcher getViewEventWatcher() {
        if (mViewEventWatcher == null) {
            mViewEventWatcher = new ViewEventWatcher();
        }
        return mViewEventWatcher;
    }

    /**
     * 父列表项的展开折叠状态监听器
     * <p>
     * 任何导致父列表项的是否可展开折叠状态更改的操作都会获得相关回调
     * 例如: 当父列表项第一次插入子项 或 父列表项中的子项全部删除完时(不管当前父列表项是否处于折叠状态)
     * </p>
     *
     * @see #addParentExpandableStateChangeListener(OnParentExpandableStateChangeListener)
     */
    public interface OnParentExpandableStateChangeListener {
        /**
         * 父列表项可展开状态更改时的回调
         *
         * @param pvh        更改可展开状态的 {@link ParentViewHolder} 所代表的父列表项
         * @param position   状态更新所对应的父列表项的位置
         * @param expandable 当前该父列表项是否可展开折叠
         */
        void onParentExpandableStateChanged(RecyclerView rv, ParentViewHolder pvh, int position,
                boolean expandable);
    }


    /**
     * 父列表项展开或折叠状态监听接口
     */
    public interface OnParentExpandCollapseListener {

        /**
         * 父列表项展开后的回调
         * <p>
         * <b>注意：当通过调用以下方法操作来改变 parent 中的 child 时，如果强制展开 parent 成功，该方法会被回调</b>
         * <ul>
         * <li>{@link #notifyChildItemInserted(int, int, boolean)}</li>
         * <li>{@link #notifyChildItemRangeInserted(int, int, int, boolean)}</li>
         * <li>{@link #expandParent(int)}</li>
         * <li>{@link #expandParent(Parent)}</li>
         * <li>{@link #expandAllParents()}  </li>
         * </ul>
         * </p>
         *
         * @param position     该父列表项在父列表里的位置
         * @param pendingCause 是否是后期待展开操作造成的回调，eg: 如果调用 {@link #collapseAllParents()} 导致不在当前中的 parent 获得不到回调，但是后期该 parent attach 到屏幕时会回调先前未完成的回调
         * @param byUser       是否属于用户点击父列表项之后产生的展开事件，用于区分用户手动还是程序触发的
         */
        void onParentExpanded(RecyclerView rv, ParentViewHolder pvh, int position,
                boolean pendingCause, boolean byUser);

        /**
         * 父列表项折叠后的回调
         * <p>
         * <b>注意：如果调用以下方法时发现 parent 中返回的 children List 已为 null 或者为空，该方法也会被回调</b>
         * <ul>
         * <li>{@link #notifyChildItemRemoved(int, int)}</li>
         * <li>{@link #notifyChildItemRangeRemoved(int, int, int)}</li>
         * </ul>
         * </p>
         *
         * @param position     该父列表项在父列表里的位置
         * @param pendingCause 是否是后期待折叠操作造成的回调，eg: 如果调用 {@link #collapseAllParents()} 导致不在当前中的 parent 获得不到回调，但是后期该 parent attach 到屏幕时会回调先前未完成的回调
         * @param byUser       是否属于用户点击父列表项之后产生的折叠事件，用于区分用户手动还是程序触发的
         */
        void onParentCollapsed(RecyclerView rv, ParentViewHolder pvh, int position,
                boolean pendingCause, boolean byUser);
    }

    /**
     * RecyclerView's itemView/itemView's child view 的点击事件监听器
     */
    public interface OnParentClickListener {
        /**
         * RecyclerView's itemView/itemView's child view 的点击事件回调
         *
         * @param parent 产生该事件的 view 的 parent RecyclerView
         * @param view   itemView/itemView's child view
         */
        void onParentClick(RecyclerView parent, View view);
    }

    public interface OnParentLongClickListener {
        boolean onParentLongClick(RecyclerView parent, View view);
    }

    /**
     * RecyclerView's itemView/itemView's child view 的点击事件监听器
     */
    public interface OnChildClickListener {
        /**
         * RecyclerView's itemView/itemView's child view 的点击事件回调
         *
         * @param parent 产生该事件的 view 的 parent RecyclerView
         * @param view   itemView/itemView's child view
         */
        void onChildClick(RecyclerView parent, View view);
    }


    public interface OnChildLongClickListener {
        boolean onChildLongClick(RecyclerView parent, View view);
    }

    /**
     * ExpandableRecyclerView 展开折叠模式
     * <p>
     * 支持以下模式:
     * <ul>
     * <li>MODE_DEFAULT:默认模式，可以展开折叠任意数量 parent,不受约束</li>
     * <li>MODE_SINGLE_EXPAND:该模式下只能有一个 parent 处于展开状态，展开最新的 parent 会自动折叠先前展开的
     * parent</li>
     * <li>MODE_SINGLE_COLLAPSE:该模式下只能有一个 parent 处于折叠状态，折叠最新的 parent 会自动展开先前折叠的
     * parent</li>
     * </ul>
     * </p>
     */
    public class ExpandCollapseMode {
        /**
         * 默认模式，可以展开折叠任意数量 parent,不受约束
         */
        public static final int MODE_DEFAULT = 0;
        /**
         * 该模式下只能有一个 parent 处于展开状态，展开最新的 parent 会自动折叠先前展开的 parent
         */
        public static final int MODE_SINGLE_EXPAND = 1;
        /**
         * 该模式下只能有一个 parent 处于折叠状态，折叠最新的 parent 会自动展开先前折叠的 parent
         */
        public static final int MODE_SINGLE_COLLAPSE = 2;
        /**
         * 当前设置的模式
         */
        private int mode = MODE_DEFAULT;

        /**
         * 先前展开的 parent 在模型数据集里对应的位置
         */
        private int lastExpandedPosition = RecyclerView.NO_POSITION;

        /**
         * 先前折叠的 parent 在模型数据集里对应的位置
         */
        private int lastCollapsedPosition = RecyclerView.NO_POSITION;
    }

    private class ViewEventWatcher implements View.OnClickListener, View.OnLongClickListener {
        @Override
        public void onClick(View v) {
            for (RecyclerView parent : mAttachedRecyclerViews) {
                BaseViewHolder vh = (BaseViewHolder) parent.findContainingViewHolder(v);
                if (vh != null) {
                    if (vh instanceof ParentViewHolder) {
                        //内部 parent 展开折叠逻辑
                        if (v == vh.itemView) {
                            ParentViewHolder pvh = (ParentViewHolder) vh;
                            // 需要再次查询位置而不是缓存中的位置
                            final int pos = getParentPosition(pvh.getAdapterPosition());
                            final boolean collapsed = !pvh.isExpanded();
                            if (collapsed) {
                                pvh.setExpanded(expandView(pos, false, true));
                            } else {
                                pvh.setExpanded(!collapseView(pos, false, true));
                            }
                        }
                        if (mParentClickListener != null) {
                            mParentClickListener.onParentClick(parent, v);
                        }
                    } else if (vh instanceof ChildViewHolder && mChildClickListener != null) {
                        mChildClickListener.onChildClick(parent, v);
                    }
                    break;
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            for (RecyclerView parent : mAttachedRecyclerViews) {
                BaseViewHolder vh = (BaseViewHolder) parent.findContainingViewHolder(v);
                if (vh != null) {
                    boolean handled = false;
                    if (vh instanceof ParentViewHolder && mParentLongClickListener != null) {
                        handled = mParentLongClickListener.onParentLongClick(parent, v);
                    } else if (vh instanceof ChildViewHolder && mChildLongClickListener != null) {
                        handled = mChildLongClickListener.onChildLongClick(parent, v);
                    }
                    return handled;
                }
            }
            return false;
        }
    }

}

