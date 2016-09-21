package com.jhj.expandablerecyclerview.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.jhj.expandablerecyclerview.model.ParentItem;
import com.jhj.expandablerecyclerview.model.ParentItemWrapper;
import com.jhj.expandablerecyclerview.viewholder.BaseViewHolder;
import com.jhj.expandablerecyclerview.viewholder.ChildViewHolder;
import com.jhj.expandablerecyclerview.viewholder.OnParentItemExpandCollapseListener;
import com.jhj.expandablerecyclerview.viewholder.ParentViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 扩展 RecyclerView.Adapter 实现可展开和折叠列表项.
 */
public abstract class ExpandableRecyclerViewAdapter<PVH extends ParentViewHolder, CVH extends
        ChildViewHolder>
        extends RecyclerView.Adapter<BaseViewHolder> implements OnParentItemExpandCollapseListener
{
    private static final String TAG = "ExpandableRVAdapter";

    private static final String TYPE_FORMAT = "%1$d%2$d";

    /**
     * 父列表项标识
     */
    private static final int TYPE_PARENT = 1;
    /**
     * 子列表项标识
     */
    private static final int TYPE_CHILD = 2;
    /**
     * 默认的没有指定父列表项类型的标识
     */
    private static final int TYPE_PARENT_NO_TYPE = 0;
    /**
     * 默认的没有指定子列表项类型的标识
     */
    private static final int TYPE_CHILD_NO_TYPE = 0;
    /**
     * 父列表项集合
     */
    private List<? extends ParentItem> mParentItems = null;
    /**
     * 当前显示的列表项(父列表项和所有展开的子列表项)集合
     */
    private List<Object> mItems;
    /**
     * 当前所有监听适配器的 RecyclerView 集合
     */
    private List<RecyclerView> mAttachedRecyclerViews = new ArrayList<>(1);

    /**
     * 所有监听父列表项展开折叠状态监听器集合
     */
    private List<OnParentExpandCollapseListener> mExpandCollapseListeners = new ArrayList<>(1);


    public ExpandableRecyclerViewAdapter(@NonNull List<? extends ParentItem> parentItems) {

        if (parentItems==null) throw new IllegalArgumentException("parentItems should not be " +
                "null");

        mParentItems = parentItems;

        mItems = ExpandableRecyclerViewAdapterHelper.generateParentChildItemList(
                parentItems);

    }

    /**
     * 父列表项展开或折叠状态监听接口
     */
    public interface OnParentExpandCollapseListener {

        /**
         * 父列表项展开后的回调
         * @param parentPosition 该父列表项在父列表里的位置
         * @param byUser 是否属于用户点击父列表项之后产生的展开事件，用于区分用户手动还是程序触发的
         */
        void onParentExpanded(int parentPosition, int parentAdapterPosition, boolean byUser);

        /**
         * 父列表项折叠后的回调
         * @param parentPosition 该父列表项在父列表里的位置
         * @param byUser 是否属于用户点击父列表项之后产生的折叠事件，用于区分用户手动还是程序触发的
         */
        void onParentCollapsed(int parentPosition, int parentAdapterPosition, boolean byUser);
    }


    /**
     * 注册一个监听父列表项展开或折叠状态改变监听器.
     * <p>use {@link #addParentExpandCollapseListener(OnParentExpandCollapseListener)}</p>
     * @param listener 监听父列表项展开或折叠状态改变的监听器
     *
     */
    @Deprecated
    public void setParentExpandCollapseListener(
            OnParentExpandCollapseListener listener)
    {
        addParentExpandCollapseListener(listener);
    }

    /**
     * 注册监听父列表项展开折叠状态监听器
     * @param listener 监听器
     */
    public void addParentExpandCollapseListener(OnParentExpandCollapseListener listener) {
        if (listener==null || mExpandCollapseListeners.contains(listener)) return;
        mExpandCollapseListeners.add(listener);
    }

    /**
     * 取消注册监听父列表项折叠状态的改变
     * @param listener 需要取消注册的监听器
     */
    public void unRegisterParentExpandCollapseListener(OnParentExpandCollapseListener listener){
        if (listener==null) return;
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

        //父或子类型和具体的不同父或子类型的类型组合
        //例如 11，第一个1代表父列表项，第二个1代表外部传进来的具体的父列表项类型
        String packedViewType = String.valueOf(viewType);

        char[] chars = packedViewType.toCharArray();
        String[] splitPackedViewType = {String.valueOf(chars[0]), String.valueOf(chars[1])};

        //获得列表项类型，父列表项或子列表项类型，用于预先判断该列表项是父列表项还是子列表项
        viewType = Integer.valueOf(splitPackedViewType[0]);

        //外部返回的指定的具体的列表项类型(具体的父或子列表项类型)
        int specifiedViewType = Integer.valueOf(splitPackedViewType[1]);

        if (viewType == TYPE_PARENT) {
            //回调并返回父列表项视图 ParentViewHolder
            PVH pvh = onCreateParentViewHolder(parent, specifiedViewType);
            //注册父列表项视图点击事件监听器,用于监听列表项视图的点击并根据列表项的展开状态触发列表项的展开或折叠回调
            pvh.setClickEvent();
            //注册 ParentItemView 点击回调监听器
            pvh.setParentItemExpandCollapseListener(this);
            return pvh;
        } else if (viewType == TYPE_CHILD) {
            ////回调并返回子列表项视图 ChildViewHolder
            return onCreateChildViewHolder(parent, specifiedViewType);
        } else {
            throw new IllegalStateException("Incorrect ViewType found");
        }
    }

    /**
     * <p>
     *  根据指定的列表项位置在适配器{@link #ExpandableRecyclerViewAdapter}里对应的数据集位置绑定数据到该列表项
     * 视图
     *
     *</p>
     * 判断适配器位置的对应的列表项类型
     * (父或子列表项类型，或者具体的父或子列表项类型)
     * 并回调{@link #onBindParentViewHolder(ParentViewHolder, int, int, ParentItem)}
     * 或者{@link #onBindChildViewHolder(ChildViewHolder, int, int, int, int,Object)}通知更新该列表项位置的视图内容
     * 
     *
     * @param holder 指定列表项位置的 ViewHolder，用于更新指定位置的列表项视图
     * @param position 该列表项在适配器数据集中代表的位置
     */
    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        //
        Object listItem = getItem(position);
        int parentPosition = getParentPosition(position);

        if (listItem instanceof ParentItemWrapper) {
            PVH pvh = (PVH) holder;
            ParentItemWrapper parentItemWrapper = (ParentItemWrapper) listItem;
            //初始化展开折叠状态
            pvh.setExpanded(parentItemWrapper.isExpanded());
            onBindParentViewHolder(pvh, position, parentPosition,
                    parentItemWrapper.getParentItem());
        } else if (listItem == null) {
            throw new IllegalStateException("Incorrect ViewHolder found");
        } else {
            CVH cvh = (CVH) holder;
            onBindChildViewHolder(cvh, position, parentPosition,
                    getChildPosition(position), getParentAdapterPosition(parentPosition),listItem);
        }
    }

    @Override
    public int getItemViewType(int position) {
        //获取指定列表项位置在适配器数据集合里所代表的列表项
        Object listItem = getItem(position);
        //获取指定列表项位置(父或子视图项的位置)的在父列表里位置
        int parentPosition = getParentPosition(position);
        //如果是父列表项类型就回调查询具体的父类型
        //返回父类型和具体的父类型的组合后的列表项类型
        if (listItem instanceof ParentItemWrapper) {
            int parentType = getParentType(parentPosition);
            String packedParentType = String.format(Locale.getDefault(), TYPE_FORMAT, TYPE_PARENT,
                    parentType);
            return Integer.valueOf(packedParentType);
        } else if (listItem == null) {
            throw new IllegalStateException("Null object added");
        } else {
            //回调获取具体的子列表项类型
            //返回子类型和具体子类型的组合后的列表项类型
            int childType = getChildType(parentPosition, getChildPosition(position));
            String packedChildType = String.format(Locale.getDefault(), TYPE_FORMAT, TYPE_CHILD,
                    childType);
            return Integer.valueOf(packedChildType);
        }
    }

    /**
     * 获取指定父列表位置的父列表类型
     * @param parentPosition 要查询的父列表类型的位置
     * @return 指定父列表位置的父列表类型
     */
    public int getParentType(int parentPosition) {
        return TYPE_PARENT_NO_TYPE;
    }

    /**
     * 获取指定的父列表项位置下从属该父列表的子列表项的位置对应的子列表项类型
     * @param parentPosition 该子列表项的从属父列表项位置
     * @param childPosition 子列表项的位置
     * @return 子列表项的类型
     */
    public int getChildType(int parentPosition, int childPosition) {
        return TYPE_CHILD_NO_TYPE;
    }

    /**
     * 返回当前所有要显示的列表项(父列表和子列表)数据数量
     * @return 初始化时显示的列表项数量
     */
    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /**
     * 来自{@link #onCreateViewHolder(ViewGroup, int)}的创建指定父列表项视图的回调
     * 创建父列表项用于显示的父列表项视图的 {@link ParentViewHolder}，通过回调返回客户端的所指定的父列表项视图
     * @param parent 用于显示列表项视图的{@link ViewGroup}
     * @param parentType 父列表项的类型，用于提供不同的父列表项视图
     * @return 父列表项所需显示视图的 {@link ParentViewHolder}
     */
    public abstract PVH onCreateParentViewHolder(ViewGroup parent, int parentType);

    /**
     * 来自{@link #onCreateViewHolder(ViewGroup, int)}的创建指定子列表项视图的回调
     * @param child 用于显示列表项视图的{@link ViewGroup}
     * @param childType 子列表项的类型
     * @return 子列表项所需显示视图的 {@link ChildViewHolder}
     */
    public abstract CVH onCreateChildViewHolder(ViewGroup child, int childType);

    /**
     * 来自 {@link #onBindViewHolder(RecyclerView.ViewHolder, int)}的用于绑定视图数据 到{@link PVH}
     * 的回调
     * @param parentViewHolder 用于绑定数据的父列表项的 parentViewHolder
     * @param parentAdapterPosition 该父列表项在适配器数据集里所对应的位置
     * @param parentPosition 该父列表项所在父列表里的位置
     * @param parentItem 和该父列表项绑定的数据源 {@link ParentItem}
     */
    public abstract void onBindParentViewHolder(PVH parentViewHolder, int parentAdapterPosition,
            int parentPosition, ParentItem parentItem);

    /**
     * 来自 {@link #onBindViewHolder(RecyclerView.ViewHolder, int)}的用于绑定数据到{@link CVH}的回调
     * @param childViewHolder 用于显示或更新绑定到 CVH 里的数据
     * @param childAdapterPosition 该子列表项在适配器数据集里对应的位置
     * @param parentPosition 该子列表项所从属的父列表项在父列表里的位置
     * @param childPosition 该子列表项在子列表里的位置
     * @param childListItem 用于绑定到 CVH 里的数据源
     */
    public abstract void onBindChildViewHolder(CVH childViewHolder, int childAdapterPosition,
            int parentPosition, int childPosition,int parentAdapterPosition, Object childListItem);

    /**
     * {@link ParentViewHolder}里父列表项展开回调，用于监听父列表项展开事件并触发相关的展开操作
     * @param parentAdapterPosition 该父列表项在适配器数据集里对应的位置
     */
    @Override
    public boolean onParentItemExpand(int parentAdapterPosition) {
        return expandParentItem(parentAdapterPosition, true, false);
    }
    /**
     * {@link ParentViewHolder} 里父列表项折叠回调，用于监听父列表项折叠事件并触发相关的折叠操作
     * @param parentAdapterPosition 该父列表项在适配器数据集里对应的位置
     */
    @Override
    public boolean onParentItemCollapse(int parentAdapterPosition) {
        return collapseParentItem(parentAdapterPosition, true);
    }


    /**
     * 当有 {@code RecyclerView} 监听该适配器时的回调
     * <p>
     *     注意: 同一个适配器可能被多个 {@code RecyclerView} 监听
     * </p>
     * @param recyclerView 开始监听该适配器的 {@code RecyclerView} 实例
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mAttachedRecyclerViews.add(recyclerView);
    }

    /**
     * 当 {@code RecyclerView} 停止监听之前它所监听的适配器时的回调
     * @param recyclerView 停止监听适配器的 {@code RecyclerView} 实例
     */
    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mAttachedRecyclerViews.remove(recyclerView);
    }

    /**
     * 获取指定适配器位置数据所代表的列表项
     *
     * @param adapterPosition 该列表项在适配器数据集中对应的位置
     * @return 指定适配器位置数据所代表的列表项
     */
    private Object getItem(int adapterPosition) {
        return mItems.get(adapterPosition);
    }

    /**
     * 根据指定的列表项在适配器数据集中所对应的位置返回该列表项在父列表里的位置
     * 如果该列表项为子列表项就返回该子列表项所属的父列表项在父列表里位置
     * @param adapterPosition 要查询的列表项在适配器里所对应的位置
     * @return 指定列表项在父列表里的位置
     */
    private int getParentPosition(int adapterPosition) {
        if (adapterPosition == 0) return 0;
        Object item = getItem(adapterPosition);
        if (item instanceof ParentItemWrapper) {
            int beforeExpandedChildItemCount = getBeforeExpandedChildCount(adapterPosition);
            return adapterPosition - beforeExpandedChildItemCount;
        } else {
            for (int i = adapterPosition - 1; i >= 0; i--) {
                if (getItem(i) instanceof ParentItemWrapper) {
                    return i - getBeforeExpandedChildCount(i);
                }
            }
        }
        return RecyclerView.NO_POSITION;
    }
    
    
    /**
     * 根据指定的子列表项在适配器数据集中所对应的位置返回该子列表项在子列表里的位置
     * @param childAdapterPosition 子列表项在适配器里对应的位置
     * @return 指定子列表项在子列表里的位置
     */
    private int getChildPosition(int childAdapterPosition) {
        int childAdapterPos = RecyclerView.NO_POSITION;
        ParentItemWrapper parentItemWrapper = getParentWrapper(childAdapterPosition);
        if (parentItemWrapper != null) {
            Object childItem = getItem(childAdapterPosition);
            List<?> childItems = parentItemWrapper.getChildItems();
            if (childItems != null) {
                childAdapterPos = childItems.indexOf(childItem);
            }
        }
        return childAdapterPos;
    }


    /**
     * 添加对应 {@code parentWrapperPosition} 位置的父列表项
     *
     * @param parentWrapperPosition 要添加新父列表项所指定的位置
     * @param newParentItemPosition     新添加的父列表项的位置
     * @return 插入要适配器数据集里的列表项的个数(父列表项和其展开的子列表项)
     */
    private int addParentWrapper(int parentWrapperPosition, int newParentItemPosition) {
        if (parentWrapperPosition==RecyclerView.NO_POSITION || newParentItemPosition==-1) return 0;
        ParentItem newParentItem=mParentItems.get(newParentItemPosition);
        ParentItemWrapper newParentItemWrapper = new ParentItemWrapper(newParentItem);
        mItems.add(parentWrapperPosition, newParentItemWrapper);
        int insertedItemCount = 1;
        if (newParentItemWrapper.isInitiallyExpanded()) {
            newParentItemWrapper.setExpanded(true);
            List<?> childItems = newParentItemWrapper.getChildItems();
            if (childItems!=null) {
                mItems.addAll(parentWrapperPosition + insertedItemCount, childItems);
                insertedItemCount += childItems.size();
            }
        }
        return insertedItemCount;
    }

    /**
     * 根据指定的子列表项在适配器数据集中所对应的位置返回该子列表项所属的父列表项
     * @param childAdapterPosition 子列表项在适配器里对应的位置
     * @return 指定子列表项所属的父列表项
     */
    private ParentItemWrapper getParentWrapper(int childAdapterPosition) {
        for (int i = childAdapterPosition - 1; i >= 0; i--) {
            Object listItem = getItem(i);
            if (listItem instanceof ParentItemWrapper) {
                return (ParentItemWrapper) listItem;
            }
        }
        return null;
    }

//    /**
//     * 根据指定的在适配器里对应位置的列表项返回是否是父列表项
//     * @param adapterPosition 列表项在适配器里对应的位置
//     * @return 指定的在适配器里对应位置的列表项返回是否是父列表项
//     */
//    public boolean isParent(int adapterPosition) {
//        return !(adapterPosition >= mItems.size() || adapterPosition < 0) && mItems.get(
//                adapterPosition) instanceof ParentItemWrapper;
//    }

    /**
     * 获取指定的父列表项位置返回该列表项在适配器里对应的位置
     * @param parentPosition 指定的父列表项在父列表里的位置
     * @return 指定的父列表项位置在适配器里对应的位置
     */
    private int getParentAdapterPosition(int parentPosition) {
        if (parentPosition == -1) return RecyclerView.NO_POSITION;
        int parentIndex = -1;
        int listItemCount = mItems.size();
        for (int i = 0; i < listItemCount; i++) {
            Object listItem = mItems.get(i);
            if (listItem instanceof ParentItemWrapper) {
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
     * @param parentPosition 要查询的子列表项所属的父列表项的位置
     * @param childPosition 子列表项的位置
     * @return 子列表项位置在适配器里对应的位置
     */
    private int getChildAdapterPosition(int parentPosition, int childPosition) {
        if (parentPosition == -1 || childPosition == -1) return RecyclerView.NO_POSITION;
        int parentAdapterPosition=getParentAdapterPosition(parentPosition);
        return parentAdapterPosition + childPosition + 1;
    }

    /**
     * 获取父列表项之前展开的所有子列表项数量
     * @param parentAdapterPosition 父列表项在适配器中所对应的位置
     * @return 父列表项之前展开的所有子列表项数量
     */
    private int getBeforeExpandedChildCount(int parentAdapterPosition) {
        if (parentAdapterPosition == 0) return 0;
        int beforeExpandedChildCount = 0;
        for (int i = 0; i < parentAdapterPosition; i++) {
            if (!(getItem(i) instanceof ParentItemWrapper)) {
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
        notifyItemRangeChanged(0,mItems.size());
    }

    /**
     * 展开指定适配器位置所对应的父列表项
     * @param parentAdapterPosition 父列表项在适配器里所对应的位置
     * @param byUser 是否是用户通过点击展开父列表项的
     * @return 是否展开成功,如果当前已经展开了或者该 Parent 没有 Child，亦或是内部异常发生时返回 false              
     */
    private boolean expandParentItem(int parentAdapterPosition, boolean byUser, boolean forceExpandParent)
    {
        if (parentAdapterPosition==RecyclerView.NO_POSITION) return false;
        Object item = getItem(parentAdapterPosition);
        if (!(item instanceof ParentItemWrapper)) return false;
        ParentItemWrapper parentItemWrapper = (ParentItemWrapper) item;

        if (!forceExpandParent && (!parentItemWrapper.isExpandable() ||
                parentItemWrapper.isExpanded()))
            return false;

        List<?> childItems = parentItemWrapper.getChildItems();
        if (childItems == null || childItems.isEmpty()) return false;

        //保存该父列表项当前为展开状态
        parentItemWrapper.setExpanded(true);

        final int insertPosStart = parentAdapterPosition + 1;
        final int childCount = childItems.size();
        //按照顺序依次将子列表项插入到该父列表项下
        mItems.addAll(insertPosStart, childItems);
        //通知 RecyclerView 指定位置有新的列表项插入，刷新界面
        notifyItemRangeInserted(insertPosStart, childCount);

        for (OnParentExpandCollapseListener listener : mExpandCollapseListeners) {
            int beforeExpandedChildCount = getBeforeExpandedChildCount(parentAdapterPosition);
            listener.onParentExpanded(parentAdapterPosition - beforeExpandedChildCount,
                    parentAdapterPosition, byUser);
        }

        return true;
    }

    /**
     * 折叠指定适配器位置所对应的父列表项
     * @param parentAdapterPosition 父列表项在适配器里所对应的位置
     * @param byUser 是否是用户通过点击展开父列表项的
     */
    private boolean collapseParentItem(int parentAdapterPosition, boolean byUser)
    {
        if (parentAdapterPosition==RecyclerView.NO_POSITION) return false;
        Object item = getItem(parentAdapterPosition);
        if (!(item instanceof ParentItemWrapper)) return false;
        ParentItemWrapper parentItemWrapper = (ParentItemWrapper) item;
        if (!parentItemWrapper.isExpandable() || !parentItemWrapper.isExpanded()) return false;
        List<?> childItems = parentItemWrapper.getChildItems();
        if (childItems == null || childItems.isEmpty()) return false;

        //保存该父列表项当前为折叠状态
        parentItemWrapper.setExpanded(false);

        final int collapsePosStart = parentAdapterPosition + 1;
        final int childItemCount = childItems.size();
        //按照顺序依次将该父列表项下的子列表项移除
        mItems.removeAll(childItems);
        //通知 RecyclerView 指定位置有列表项已移除，刷新界面
        notifyItemRangeRemoved(collapsePosStart, childItemCount);

        for (OnParentExpandCollapseListener listener : mExpandCollapseListeners) {
            int beforeExpandedChildCount = getBeforeExpandedChildCount(parentAdapterPosition);
            listener.onParentCollapsed(parentAdapterPosition - beforeExpandedChildCount,
                    parentAdapterPosition, byUser);
        }

        return true;
    }

    /**
     * 展开指定适配器位置所对应的父列表项。
     * @param parentAdapterPosition 父列表项在适配器里所对应的位置
     */
    private boolean expandViews(int parentAdapterPosition, boolean forceExpandParent) {
        boolean success = true;
        for (RecyclerView recyclerView : mAttachedRecyclerViews) {
            if (!expandParentItem(parentAdapterPosition, false, forceExpandParent)) {
                success = false;
            }
            PVH pvh = (PVH) recyclerView.findViewHolderForAdapterPosition(parentAdapterPosition);
            if (pvh != null && !pvh.isExpanded()) {
                pvh.setExpanded(success);
            }
        }
        return success;
    }

    /**
     * 根据指定的父列表项在父列表里的位置展开该位置在适配器里对应的父列表项
     * @param parentPosition 该父列表项在父列表里的位置
     */
    public boolean expandParent(int parentPosition) {
        int parentAdapterPosition = getParentAdapterPosition(parentPosition);
        return parentAdapterPosition != RecyclerView.NO_POSITION && expandViews(
                parentAdapterPosition,true);
    }

    /**
     * 展开与{@link ParentItem}相关的父列表项
     * @param parentItem 与父列表项相关的 ParentItem
     */
    public boolean expandParent(ParentItem parentItem) {
        if (parentItem == null) return false;
        int parentPosition = mParentItems.indexOf(parentItem);
        return expandParent(parentPosition);
    }

    /**
     * 展开所有的父列表项
     */
    public void expandAllParent() {
        for (int i = 0; i < mParentItems.size(); i++) {
            expandParent(i);
        }
    }

    /**
     * 折叠指定适配器位置所对应的父列表项。
     * @param parentAdapterPosition 父列表项在适配器里所对应的位置
     */
    private boolean collapseViews(int parentAdapterPosition) {
        boolean success = true;
        for (RecyclerView recyclerView : mAttachedRecyclerViews) {
            if (!collapseParentItem(parentAdapterPosition, false)) {
                success = false;
            }
            PVH pvh = (PVH) recyclerView.findViewHolderForAdapterPosition(parentAdapterPosition);
            if (pvh != null && pvh.isExpanded()) {
                pvh.setExpanded(success);
            }
        }
        return success;
    }

    /**
     * 根据指定的父列表项在父列表里的位置折叠该位置在适配器里对应的父列表项
     * @param parentPosition 该父列表项在父列表里的位置
     */
    public boolean collapseParent(int parentPosition) {
        int parentAdapterPosition = getParentAdapterPosition(parentPosition);
        return parentAdapterPosition != RecyclerView.NO_POSITION && collapseViews(
                parentAdapterPosition);
    }

    /**
     * 折叠与{@link ParentItem}相关的父列表项
     * @param parentItem 与父列表项相关的 ParentItem
     */
    public boolean collapseParent(ParentItem parentItem) {
        if (parentItem == null) return false;
        int parentPosition = mParentItems.indexOf(parentItem);
        return collapseParent(parentPosition);
    }

    /**
     * 折叠所有的父列表项
     */
    public void collapseAllParent() {
        for (int i = 0; i < mParentItems.size(); i++) {
            collapseParent(i);
        }
    }

    /**
     *  通知任何注册的监视器当前在 {@code parentPosition} 位置有新的父列表项插入，
     * 之前在该位置存在的父列表项将被移动到 {@code parentPosition + 1} 位置。
     * <p>
     * 这是数据结构上的变化，尽管位置变化了，但是之前存在的所有列表项在数据集里的数据都会被认为最新，
     * 因此这些列表项不会被重新绑定数据
     * </p>
     * @param parentPosition
     *                      要插入新的父列表项的位置
     * @see #notifyParentItemRangeInserted(int, int)
     */
    public final void notifyParentItemInserted(int parentPosition) {
        notifyParentItemRangeInserted(parentPosition,1);
    }

    /**
     * 通知任何注册的监视器当前在 {@code parentPosition,childPosition} 位置有新的子列表项插入，
     * 之前在该位置存在的子列表项将被移动到 {@code childPosition+1} 位置。
     * <p>
     * 这是数据结构上的变化，尽管位置变化了，但是之前存在的所有列表项在数据集里的数据都会被认为最新，
     * 因此这些列表项不会被重新绑定数据
     * </p>
     * @param parentPosition 要插入的子列表项所属的父列表项位置
     * @param childPosition 要插入的子列表项的位置
     * @param forceExpandParent 是否强制展开已插入的子列表项所属的父列表项
     * @see #notifyChildItemRangeInserted(int, int, int, boolean)
     */
    public final void notifyChildItemInserted(int parentPosition, int childPosition,
            boolean forceExpandParent)
    {
        notifyChildItemRangeInserted(parentPosition, childPosition, 1, forceExpandParent);
    }

    /**
     * 通知任何注册的监视器当前在 {@code parentPositionStart} 位置有 {@code parentItemCount} 个父列表项插入，
     * 之前在该位置存在的父列表项将被移动到 {@code parentPosition + parentItemCount} 位置。
     * <p>
     * 这是数据结构上的变化，尽管位置变化了，但是之前存在的所有列表项在数据集里的数据都会被认为最新，
     * 因此这些列表项不会被重新绑定数据
     * </p>
     * @param parentPositionStart 要插入多个新父列表项的位置
     * @param parentItemCount 要插入新父列表项的个数
     * @see #notifyParentItemInserted(int)
     */
    public final void notifyParentItemRangeInserted(int parentPositionStart, int parentItemCount) {
        final int endInsertPos = mParentItems.size() - parentItemCount;
        int parentAdapterPos = RecyclerView.NO_POSITION;
        if (parentPositionStart >= 0 && parentPositionStart < endInsertPos) {
            parentAdapterPos = getParentAdapterPosition(parentPositionStart);
        } else if (parentPositionStart == endInsertPos) {
            parentAdapterPos = mItems.size();
        }
        if (parentAdapterPos == RecyclerView.NO_POSITION) return;
        int insertedItemCount = 0;
        for (int i = parentPositionStart; i < parentPositionStart + parentItemCount; i++) {
            insertedItemCount += addParentWrapper(parentAdapterPos + insertedItemCount, i);
        }
        notifyItemRangeInserted(parentAdapterPos, insertedItemCount);
    }

    /**
     * 通知任何注册的监视器当前在 {@code parentPosition,childPositionStart} 位置有 {@code childItemCount} 个子列表项插入，
     * 之前在该位置存在的子列表项将被移动到 {@code childPositionStart + childItemCount} 位置。
     * <p>
     * 这是数据结构上的变化，尽管位置变化了，但是之前存在的所有列表项在数据集里的数据都会被认为最新，
     * 因此这些列表项不会被重新绑定数据
     * </p>
     *
     * @param parentPosition 要插入多个新子列表项所属的父列表项位置
     * @param childPositionStart 要插入多个新子列表项的位置
     * @param childItemCount 要插入的子列表项的个数
     * @param forceExpandParent 是否强制展开已插入的子列表项所属的父列表项
     * @see #notifyChildItemInserted(int, int, boolean)
     */
    public final void notifyChildItemRangeInserted(int parentPosition, int childPositionStart,
            int childItemCount, boolean forceExpandParent)
    {
        int parentAdapterPos = getParentAdapterPosition(parentPosition);
        if (parentAdapterPos == RecyclerView.NO_POSITION) return;
        ParentItemWrapper parentItemWrapper = (ParentItemWrapper) getItem(parentAdapterPos);
        //这里判断如果父列表项已展开才添加数据并通知刷新列表界面
        //注意：这里的数据添加和 {@code expandParentItem } 数据添加有冲突，因为 expandParentItem
        //获取子列表数据然后再添加到模型数据层再刷新界面的，这里如果判断没展开就不能添加数据，否则有重复数据显示
        if (!forceExpandParent && !parentItemWrapper.isExpanded()) return;

        //如果强制展开父列表项并且当前的父列表项没有展开，直接调用{@link #expandViews},发送父列表项展开通知
        if (forceExpandParent && !parentItemWrapper.isExpanded()) {
            //直接展开 ParentItem,注意：同时更新对应的 ParentViewHolder 的展开状态
            expandViews(parentAdapterPos, true);
            return;
        }

        List<?> childItems = parentItemWrapper.getChildItems();
        if (childItems==null || childItems.isEmpty()) return;
        List<?> insertedChildItemList = childItems.subList(childPositionStart,
                childItemCount + childPositionStart);

        int childAdapterPos=RecyclerView.NO_POSITION;
        if (childPositionStart >= 0 && childPositionStart < childItems.size() - childItemCount) {
            childAdapterPos = getChildAdapterPosition(parentPosition, childPositionStart);
        } else if(childPositionStart == childItems.size() - childItemCount){
            childAdapterPos = parentAdapterPos + childItems.size() - childItemCount + 1;
        }
        if (childAdapterPos==RecyclerView.NO_POSITION) return;

        mItems.addAll(childAdapterPos, insertedChildItemList);
        notifyItemRangeInserted(childAdapterPos, childItemCount);
    }

    /**
     * 通知任何注册的监视器移除当前在 {@code parentPosition} 的父列表项，
     * 之前在该位置之下并存在的所有父列表项将被移动到 {@code oldPosition - 1} 位置。
     * <p>
     * 这是数据结构上的变化，尽管位置变化了，但是之前存在的所有列表项在数据集里的数据都会被认为最新，
     * 因此这些列表项不会被重新绑定数据
     * </p>
     * @param parentPosition 要移除的父列表项在父列表里的位置
     *  @see #notifyParentItemRangeRemoved(int, int)
     */
    public final void notifyParentItemRemoved(int parentPosition) {
        notifyParentItemRangeRemoved(parentPosition,1);
    }

    /**
     * 通知任何注册的监视器移除当前在 {@code parentPosition,childPosition} 位置的子列表项，
     * 之前在该位置之下并存在的所有子列表项将被移动到 {@code oldPosition - 1} 位置。
     * <p>
     * 这是数据结构上的变化，尽管位置变化了，但是之前存在的所有列表项在数据集里的数据都会被认为最新，
     * 因此这些列表项不会被重新绑定数据
     * </p>
     * @param parentPosition 要移除的子列表项所属的父列表项的位置
     * @param childPosition 要移除的子列表项的位置
     * @see #notifyChildItemRangeRemoved(int, int, int)
     */
    public final void notifyChildItemRemoved(int parentPosition, int childPosition) {
        notifyChildItemRangeRemoved(parentPosition, childPosition, 1);
    }

    /**
     * 通知任何注册的监视器移除当前从 {@code parentPositionStart} 位置开始 {@code parentItemCount} 个 的父列表项，
     * 之前在该位置之下并存在的所有父列表项将被移动到 {@code oldPosition - parentItemCount} 位置。
     * <p>
     * 这是数据结构上的变化，尽管位置变化了，但是之前存在的所有列表项在数据集里的数据都会被认为最新，
     * 因此这些列表项不会被重新绑定数据
     * </p>
     * @param parentPositionStart 移除多个父列表项开始的位置
     * @param parentItemCount 移除父列表项的个数
     * @see #notifyParentItemRemoved(int)
     */
    public final void notifyParentItemRangeRemoved(int parentPositionStart, int parentItemCount) {
        int parentAdapterPosStart = getParentAdapterPosition(parentPositionStart);
        if (parentAdapterPosStart == RecyclerView.NO_POSITION) return;
        //计算移除的 ItemView 个数
        int removedItemCount = 0;
        for (int i = parentPositionStart; i < parentPositionStart + parentItemCount; i++) {
            ParentItemWrapper parentItemWrapper = (ParentItemWrapper) getItem(
                    parentAdapterPosStart);
            //判断当前 ParentItem 是否为展开状态
            if (parentItemWrapper.isExpanded()) {
                List<?> childItems = parentItemWrapper.getChildItems();
                if (childItems != null) {
                    int childItemCount = childItems.size();
                    mItems.removeAll(childItems);
                    removedItemCount += childItemCount;
                }
            }
            mItems.remove(parentItemWrapper);
            removedItemCount++;
        }
        notifyItemRangeRemoved(parentAdapterPosStart, removedItemCount);
    }

    /**
     * 通知任何注册的监视器移除当前从 {@code parentPosition,childPositionStart} 位置 {@code childItemCount} 个子列表项，
     * 之前在该位置之下并存在的所有子列表项将被移动到 {@code oldPosition - childItemCount} 位置。
     * <p>
     * 这是数据结构上的变化，尽管位置变化了，但是之前存在的所有列表项在数据集里的数据都会被认为最新，
     * 因此这些列表项不会被重新绑定数据
     * </p>
     * @param parentPosition 要移除的多个子列表项所属的父列表项的位置
     * @param childPositionStart 移除多个子列表项所开始的位置
     * @param childItemCount 移除的子列表项的个数}
     * @see #notifyChildItemRemoved(int, int)
     */
    public final void notifyChildItemRangeRemoved(int parentPosition, int childPositionStart,
            int childItemCount)
    {
        int parentAdapterPos = getParentAdapterPosition(parentPosition);
        if (parentAdapterPos == RecyclerView.NO_POSITION) return;

        ParentItemWrapper parentItemWrapper = (ParentItemWrapper) getItem(parentAdapterPos);
        List<?> childItems = parentItemWrapper.getChildItems();
        //如果子列表项都删除了，默认通知这些删除的子列表项所属的父列表项已变为折叠状态
        if (childItems == null || childItems.isEmpty()) {
            for (OnParentExpandCollapseListener listener : mExpandCollapseListeners) {
                int beforeExpandedChildCount = getBeforeExpandedChildCount(parentAdapterPos);
                listener.onParentCollapsed(parentAdapterPos - beforeExpandedChildCount,
                        parentAdapterPos, false);
            }
        }
        //注意：这里判断当前父列表项是否已经打开，只有打开更改本地数据结构并通知刷新，否则会出现数据混乱异常
        if (!parentItemWrapper.isExpanded()) return; 

        int childAdapterPosStart = getChildAdapterPosition(parentPosition, childPositionStart);
        if (childAdapterPosStart==RecyclerView.NO_POSITION) return;

        mItems.removeAll(mItems.subList(childAdapterPosStart,childAdapterPosStart+childItemCount));
        notifyItemRangeRemoved(childAdapterPosStart, childItemCount);
    }

    /**
     * 通知任何注册的监视器更新当前在 {@code parentPosition} 位置的父列表项，
     *
     * <p>
     * 这是列表项相关的数据的改变，不是数据结构上的变化，因此，该列表项在适配器数据集里对应位置的数据已经过期，
     * 需要更新该列表项所绑定的数据
     * </p>
     * @param parentPosition 要更新的父列表项在父列表里的位置
     *
     */
    public final void notifyParentItemChanged(int parentPosition) {
        ParentItem changedParentItem = mParentItems.get(parentPosition);
        if (changedParentItem == null) return;
        int parentAdapterPos = getParentAdapterPosition(parentPosition);
        if (parentAdapterPos == RecyclerView.NO_POSITION) return;
        ParentItemWrapper parentItemWrapper = (ParentItemWrapper) getItem(parentAdapterPos);
        parentItemWrapper.setParentItem(changedParentItem);
        notifyItemChanged(parentPosition);
    }

    /**
     * 通知任何注册的件事情在位置 {@code parentPositionStart} 上有 {@code parentItemCount} 个数据发生改变
     * <p>相当于调用 {@code notifyParentItemRangeChanged(parentPositionStart,parentItemCount,null)}</p>
     * <p>这是列表项相关的数据的改变，不是数据结构上的变化，因此，该列表项在适配器数据集里对应位置的数据已经过期，
     * 需要更新该列表项所绑定的数据,在该范围里的列表保留相同的 Id</p>
     * @param parentPositionStart 父列表项数据更改的起始位置
     * @param parentItemCount 父列表项数据更改的数量
     */
    public final void notifyParentItemRangeChanged(int parentPositionStart, int parentItemCount) {
        //判断当前是否所有的 ParentItem 都是折叠状态
        //1.如果都为折叠状态，直接调用 {@link notifyItemRangeChanged(positionStart,itemCount)}
        //2.只要有一个是打开状态，循环遍历调用 {@link notifyParentItemChanged(parentPosition)}
        boolean allCollapsed = true;
        for (int i = parentPositionStart; i < parentPositionStart + parentItemCount; i++) {
            int parentAdapterPos = getParentAdapterPosition(i);
            if (parentAdapterPos == RecyclerView.NO_POSITION) {
                allCollapsed = false;
                break;
            }
            ParentItemWrapper parentItemWrapper = (ParentItemWrapper) getItem(parentAdapterPos);
            if (parentItemWrapper.isExpanded()) {
                allCollapsed = false;
                break;
            }
        }
        if (allCollapsed) {
            final int parentAdapterPosStart = getParentAdapterPosition(parentPositionStart);
            notifyItemRangeChanged(parentAdapterPosStart, parentItemCount);
        } else {
            for (int i = parentPositionStart; i < parentPositionStart + parentItemCount; i++) {
                notifyParentItemChanged(i);
            }
        }
    }
    
    /**
     * 通知任何注册的监视器更新当前在 {@code parentPosition，childPosition} 位置的子列表项，
     * @param parentPosition 该子列表项所属的父列表项位置
     * @param childPosition 子列表项的位置
     */
    public final void notifyChildItemChanged(int parentPosition, int childPosition) {
        notifyChildItemRangeChanged(parentPosition,childPosition,1);
    }

    /**
     *  通知任何注册的件事情在位置 {@code parentPosition,childPositionStart} 上有 {@code childItemCount} 个数据发生改变
     * <p>相当于调用 {@code notifyParentItemRangeChanged(parentPositionStart,parentItemCount,null)}</p>
     * <p>这是列表项相关的数据的改变，不是数据结构上的变化，因此，该列表项在适配器数据集里对应位置的数据已经过期，
     * 需要更新该列表项所绑定的数据,在该范围里的列表保留相同的 Id</p>
     * @param parentPosition 子列表数据更改从属的父列表项位置
     * @param childPositionStart 子列表数据更改的起始子列表项位置
     * @param childItemCount 子列表数据更改的数量
     */
    public final void notifyChildItemRangeChanged(int parentPosition, int childPositionStart,
            int childItemCount)
    {
        int parentAdapterPos = getParentAdapterPosition(parentPosition);
        if (parentAdapterPos == RecyclerView.NO_POSITION) return;

        ParentItemWrapper parentItemWrapper = (ParentItemWrapper) getItem(parentAdapterPos);
        //父列表项没有展开，不执行更新子列表相关事务
        if (!parentItemWrapper.isExpanded()) return;
        List<?> childItems = parentItemWrapper.getChildItems();
        if (childItems==null || childItems.isEmpty()) return;

        int changedItemCount=0;
        for (int i = childPositionStart; i < childPositionStart + childItemCount; i++) {
            Object changedChildItem = childItems.get(i);
            if (changedChildItem == null) continue;
            int childAdapterPos = getChildAdapterPosition(parentPosition, i);
            if (childAdapterPos == RecyclerView.NO_POSITION) continue;
            mItems.set(childAdapterPos, changedChildItem);
            changedItemCount++;
        }
        final int childAdapterPosStart=getChildAdapterPosition(parentPosition, childPositionStart);
        notifyItemRangeChanged(childAdapterPosStart,changedItemCount);
    }

    /**
     * 通知任何注册的监视器在 {@code fromParentPosition} 位置的父列表项已经移动到 {@code toParentPosition}位置，
     * <p>
     * 这是数据结构上的变化，尽管位置变化了，但是之前存在的所有列表项在数据集里的数据都会被认为最新，
     * 因此这些列表项不会被重新绑定数据
     * </p>
     * @param fromParentPosition 该父列表项项之前的位置
     * @param toParentPosition 移动后的位置
     */
    public final void notifyParentItemMoved(int fromParentPosition, int toParentPosition)
    {
        if (fromParentPosition == toParentPosition) return;

        int fromParentAdapterPos = getParentAdapterPosition(fromParentPosition);
        int toParentAdapterPos = getParentAdapterPosition(toParentPosition);
        if (fromParentAdapterPos == RecyclerView.NO_POSITION ||
                toParentAdapterPos == RecyclerView.NO_POSITION) return;

        ParentItemWrapper fromParentItemWrapper= (ParentItemWrapper) getItem(fromParentAdapterPos);
        ParentItemWrapper toParentItemWrapper= (ParentItemWrapper) getItem(toParentAdapterPos);
        final boolean isFromExpanded=fromParentItemWrapper.isExpanded();
        final boolean isToExpanded=toParentItemWrapper.isExpanded();
        //Parent 或 Child 往下 Move 需要特殊处理
        boolean moveToBottom = fromParentPosition < toParentAdapterPos;

        mItems.remove(fromParentAdapterPos);
        int moveToParentAdapterPos;
        //这里需要判断 toParentItem 展开状态来计算出 fromParentItem 的 moveTo 位置
        if (moveToBottom && isToExpanded && toParentItemWrapper.getChildItemCount() > 0) {
            moveToParentAdapterPos=toParentAdapterPos + toParentItemWrapper.getChildItemCount();
        } else {
            moveToParentAdapterPos=toParentAdapterPos;
        }
        mItems.add(moveToParentAdapterPos, fromParentItemWrapper);
        notifyItemMoved(fromParentAdapterPos,moveToParentAdapterPos);

        //根据 fromParentItem 的展开状态判断是否需要移动其 ChildItem(s)
        if (isFromExpanded) {
            List<?> childItems = fromParentItemWrapper.getChildItems();
            if (childItems==null || childItems.isEmpty()) return;

            final int childCount=childItems.size();
            for (int i = 0; i < childCount; i++) {
                Object fromChildItem=childItems.get(i);
                if (fromChildItem==null) continue;
                int fromChildAdapterPos =
                        moveToBottom ? fromParentAdapterPos  : fromParentAdapterPos + i + 1;
                int toChildAdapterPos = moveToBottom ? moveToParentAdapterPos
                        : moveToParentAdapterPos + i + 1;
                mItems.remove(fromChildAdapterPos);
                mItems.add(toChildAdapterPos, fromChildItem);
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
     * @param fromParentPosition 子列表项移动所在的父列表项的起始位置
     * @param fromChildPosition 子列表项移动的起始位置
     * @param toParentPosition 子列表项移动所在的父列表项的目标位置
     * @param toChildPosition 子列表项移动的目标位置
     */
    public final void notifyChildItemMoved(int fromParentPosition, int fromChildPosition,
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
                toChildAdapterPos == RecyclerView.NO_POSITION)
            return;

        ParentItemWrapper fromParentItemWrapper = (ParentItemWrapper) getItem(fromParentAdapterPos);
        ParentItemWrapper toParentItemWrapper = (ParentItemWrapper) getItem(toParentAdapterPos);
        if (fromParentItemWrapper.isExpanded()) {
            if (toParentItemWrapper.isExpanded()) {
                Object fromChildItem=getItem(fromChildAdapterPos);
                mItems.remove(fromChildAdapterPos);
                mItems.add(toChildAdapterPos,fromChildItem);
                notifyItemMoved(fromChildAdapterPos,toChildAdapterPos);
            } else {
                notifyChildItemRemoved(fromParentPosition, fromChildPosition);
            }
        } else if (toParentItemWrapper.isExpanded()) {
            notifyChildItemInserted(toParentPosition, toChildPosition,false);
        } else {
            // from 和 to parentItem 都没有展开,do nothing
        }
    }
}

