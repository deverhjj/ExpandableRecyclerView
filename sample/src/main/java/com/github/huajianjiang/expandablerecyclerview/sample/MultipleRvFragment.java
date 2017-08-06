package com.github.huajianjiang.expandablerecyclerview.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.huajianjiang.expandablerecyclerview.sample.adapter.MyAdapter;
import com.github.huajianjiang.expandablerecyclerview.sample.anim.CircularRevealItemAnimator;
import com.github.huajianjiang.expandablerecyclerview.sample.model.MyParent;
import com.github.huajianjiang.expandablerecyclerview.sample.util.AppUtil;
import com.github.huajianjiang.expandablerecyclerview.util.Logger;
import com.github.huajianjiang.expandablerecyclerview.widget.ExpandableAdapter;
import com.github.huajianjiang.expandablerecyclerview.widget.ExpandableRecyclerView;
import com.github.huajianjiang.expandablerecyclerview.widget.ParentViewHolder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.github.huajianjiang.expandablerecyclerview.sample.SingleRvFragment.REQUEST_RESULT;

/**
 * @author HuaJian Jiang.
 *         Date 2017/1/23.
 */
public class MultipleRvFragment extends Fragment {
    private static final String TAG = MultipleRvFragment.class.getSimpleName();
    private MyAdapter mAdapter;
    private RecyclerView.ItemAnimator mItemAnimator;
    private PresenterImpl mIPresenter;
    private List<MyParent> mData = AppUtil.getListData();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_multiple_rv, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 保存 ExpandableRecyclerView 状态
        mAdapter.onRestoreInstanceState(savedInstanceState);
    }

    private void init(View rootView) {
        mAdapter = new MyAdapter(getActivity(), mData);
        mAdapter.setExpandCollapseMode(ExpandableAdapter.ExpandCollapseMode.MODE_DEFAULT);
        mAdapter.addParentExpandableStateChangeListener(new ParentExpandableStateChangeListener());
        mAdapter.addParentExpandCollapseListener(new ParentExpandCollapseListener());

        RecyclerView rv_top= (RecyclerView) rootView.findViewById(R.id.rv_top);
        rv_top.setAdapter(mAdapter);
        rv_top.addItemDecoration(mAdapter.getItemDecoration());
        mItemAnimator =
                AppUtil.checkLollipop() ? new CircularRevealItemAnimator() : new DefaultItemAnimator();
        rv_top.setItemAnimator(mItemAnimator);

        RecyclerView rv_bottom = (RecyclerView) rootView.findViewById(R.id.rv_bottom);
        rv_bottom.setAdapter(mAdapter);
        rv_bottom.addItemDecoration(mAdapter.getItemDecoration());

        mIPresenter = new PresenterImpl(mAdapter, mData);

        registerForContextMenu(rv_top);
        registerForContextMenu(rv_bottom);
    }

    private class ParentExpandableStateChangeListener
            implements ExpandableAdapter.OnParentExpandableStateChangeListener
    {
        @Override
        public void onParentExpandableStateChanged(RecyclerView rv, ParentViewHolder pvh,
                int position, boolean expandable)
        {
            Logger.e(TAG, "onParentExpandableStateChanged=" + position + "," + rv.getTag());
            if (pvh == null) return;
            ImageView arrow = pvh.getView(R.id.arrow);
            if (expandable && arrow.getVisibility() != View.VISIBLE) {
                arrow.setVisibility(View.VISIBLE);
                arrow.setRotation(pvh.isExpanded() ? 180 : 0);
            } else if (!expandable && arrow.getVisibility() == View.VISIBLE) {
                arrow.setVisibility(View.GONE);
            }
        }
    }

    private class ParentExpandCollapseListener
            implements ExpandableAdapter.OnParentExpandCollapseListener
    {
        @Override
        public void onParentExpanded(RecyclerView rv, ParentViewHolder pvh, int position,
                boolean pendingCause, boolean byUser)
        {
            Logger.e(TAG, "onParentExpanded=" + position + "," + rv.getTag() + ",byUser=" + byUser);
            if (pvh == null) return;
            ImageView arrow = pvh.getView(R.id.arrow);
            if (arrow.getVisibility() != View.VISIBLE) return;
            float currRotate = arrow.getRotation();
            //重置为从0开始旋转
            if (currRotate == 360) {
                arrow.setRotation(0);
            }
            if (pendingCause) {
                arrow.setRotation(180);
            } else {
                arrow.animate().rotation(180).setDuration(mItemAnimator.getAddDuration() + 180)
                        .start();
            }
        }

        @Override
        public void onParentCollapsed(RecyclerView rv, ParentViewHolder pvh, int position,
                boolean pendingCause, boolean byUser)
        {
            Logger.e(TAG,
                    "onParentCollapsed=" + position + ",tag=" + rv.getTag() + ",byUser=" + byUser);

            if (pvh == null) return;
            final ImageView arrow = pvh.getView(R.id.arrow);
            if (arrow.getVisibility() != View.VISIBLE) return;
            final float currRotate = arrow.getRotation();
            float rotate = 360;
            //未展开完全并且当前旋转角度小于180，逆转回去
            if (currRotate < 180) {
                rotate = 0;
            }
            if (pendingCause) {
                arrow.setRotation(rotate);
            } else {
                arrow.animate().rotation(rotate)
                        .setDuration(mItemAnimator.getRemoveDuration() + 180).start();
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ExpandableRecyclerView.ExpandableRecyclerViewContextMenuInfo menuInfo = (ExpandableRecyclerView.ExpandableRecyclerViewContextMenuInfo) item
                .getMenuInfo();
        Logger.e(TAG, menuInfo.toString());
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case  R.id.action_test:
                DialogFragment dialog = (DialogFragment) getChildFragmentManager()
                        .findFragmentByTag("dialog");
                if (dialog == null) dialog = new MyDialog();
                dialog.setTargetFragment(this, REQUEST_RESULT);
                dialog.show(getChildFragmentManager(), "dialog");
                break;
            case R.id.action_refresh:
                mAdapter.notifyAllChanged();
                break;
            case R.id.action_toggle_expandable_1:
                mAdapter.toggleExpandable(1);
                break;
            case R.id.action_expand_all:
                mAdapter.expandAllParents();
                break;
            case R.id.action_collapse_all:
                mAdapter.collapseAllParents();
                break;
            case R.id.action_expand_1:
                mAdapter.expandParent(1);
                break;
            case R.id.action_collapse_1:
                mAdapter.collapseParent(1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_RESULT) {
            ArrayList<String> requests = data.getStringArrayListExtra(MyDialog.REQUEST);
            Log.e(TAG, "requests=" + requests.toString());
            final int requestCount = requests.size();
            for (int i = 0; i < requestCount; i++) {
                String request = requests.get(i);
                String[] requestSplit = request.split(",");
                String method = requestSplit[0];
                final int argsCount = requestSplit.length - 1;
                try {
                    Object[] args = new Object[argsCount];
                    Class<?>[] argTypes = new Class<?>[argsCount];
                    for (int k = 0; k < argsCount; k++) {
                        argTypes[k] = int.class;
                        try {
                            args[k] = Integer.valueOf(requestSplit[k + 1]);
                        } catch (NumberFormatException e) {
                            AppUtil.showToast(getContext(), "Test failed,please check input format");
                            return;
                        }
                    }
                    Method m = IPresenter.class.getDeclaredMethod(method, argTypes);
                    Log.e(TAG, "method=" + m.toString());
                    m.setAccessible(true);
                    m.invoke(mIPresenter,args);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    AppUtil.showToast(getContext(), "Test failed,please check input format");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    AppUtil.showToast(getContext(), "Test failed,please check input format");
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    AppUtil.showToast(getContext(), "Test failed,please check input format");
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAdapter.onSaveInstanceState(outState);
    }
}
