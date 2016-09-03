package com.jhj.expandablerecyclerviewexample;

/**
 * Created by jhj_Plus on 2016/9/2.
 */
public interface IPresenter {
    void notifyParentItemInserted(int parentPosition);

    void notifyChildItemInserted(int parentPosition, int childPosition);

    void notifyParentItemRangeInserted(int parentPositionStart, int parentItemCount);

    void notifyChildItemRangeInserted(int parentPosition, int childPositionStart,
            int childItemCount);

    void notifyParentItemRemoved(int parentPosition);

    void notifyChildItemRemoved(int parentPosition, int childPosition);

    void notifyParentItemRangeRemoved(int parentPositionStart, int parentItemCount);

    void notifyChildItemRangeRemoved(int parentPosition, int childPositionStart,
            int childItemCount);

    void notifyParentItemChanged(int parentPosition);

    void notifyChildItemChanged(int parentPosition, int childPosition);
}
