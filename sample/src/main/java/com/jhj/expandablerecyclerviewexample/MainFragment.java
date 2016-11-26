package com.jhj.expandablerecyclerviewexample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jhj.expandablerecyclerview.util.Logger;
import com.jhj.expandablerecyclerview.widget.ExpandableAdapter;
import com.jhj.expandablerecyclerview.widget.ParentViewHolder;
import com.jhj.expandablerecyclerviewexample.adapter.MyAdapter;
import com.jhj.expandablerecyclerviewexample.model.MyChild;
import com.jhj.expandablerecyclerviewexample.model.MyParent;
import com.jhj.expandablerecyclerviewexample.model.Test;
import com.jhj.expandablerecyclerviewexample.utils.Util;
import com.jhj.expandablerecyclerviewexample.viewholder.MyParentViewHolder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jhj_Plus on 2016/9/2.
 */
public class MainFragment extends Fragment {
    public static final int REQUEST_RESULT = 1;
    private static final String TAG = "MainFragment";
    private RecyclerView mRecyclerView;
    private PresenterImpl mIPresenter;
    private List<MyParent> mData = Util.getListData();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Logger.e(TAG,"***********onCreate*********");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState)
    {
        Logger.e(TAG,"***********onCreateView*********");
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Logger.e(TAG,"***********onViewCreated*********");
        super.onViewCreated(view, savedInstanceState);
        init(getView());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Logger.e(TAG,"***********onActivityCreated*********");
        super.onActivityCreated(savedInstanceState);

        // 保存 ExpandableRecyclerView 状态
        MyAdapter adapter= (MyAdapter) mRecyclerView.getAdapter();
        adapter.onRestoreInstanceState(savedInstanceState);

    }

    private void init(View rootView) {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final MyAdapter adapter = new MyAdapter(getActivity(), mData);
        adapter.setExpandCollapseMode(ExpandableAdapter.ExpandCollapseMode.MODE_DEFAULT);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addItemDecoration(adapter.getItemDecoration());
        adapter.setParentExpandCollapseListener(new ExpandableAdapter
                .OnParentExpandCollapseListener() {

            @Override
            public void onParentExpanded(ParentViewHolder pvh, int parentPosition,
                    boolean byUser)
            {
                Logger.e(TAG,"onParentExpanded="+parentPosition);

                MyParentViewHolder vh =
                        (MyParentViewHolder) mRecyclerView.findViewHolderForAdapterPosition(
                                pvh.getAdapterPosition());
                if (vh == null) return;
                final ImageView arrow = vh.getView(R.id.arrow);
                if (vh.isExpandable() && arrow.getVisibility() != View.VISIBLE) {
                    arrow.setVisibility(View.VISIBLE);
                }
                final float currRotate=arrow.getRotation();
                Logger.e(TAG, "currRotate=" + currRotate);
                //重置为从0开始旋转
                if (currRotate == 360) {
                    arrow.setRotation(0);
                }
                arrow.animate().rotation(180).setDuration(300).start();
            }

            @Override
            public void onParentCollapsed(ParentViewHolder pvh, int parentPosition,
                    boolean byUser)
            {
                Logger.e(TAG,"onParentCollapsed="+parentPosition);

                MyParentViewHolder vh =
                        (MyParentViewHolder) mRecyclerView.findViewHolderForAdapterPosition(
                                pvh.getAdapterPosition());
                if (vh == null) return;
                final ImageView arrow = vh.getView(R.id.arrow);
                if (!vh.isExpandable() && arrow.getVisibility() == View.VISIBLE) {
                    arrow.setVisibility(View.GONE);
                }
                final float currRotate=arrow.getRotation();
                Logger.e(TAG,"currRotate="+currRotate);
                float rotate = 360;
                //未展开完全并且当前旋转角度小于180，逆转回去
                if (currRotate < 180) {
                    rotate = 0;
                }
                arrow.animate().rotation(rotate).setDuration(300).start();
            }
        });


        adapter.addParentExpandCollapseListener(new ExpandableAdapter
                .OnParentExpandCollapseListener() {

            @Override
            public void onParentExpanded(ParentViewHolder pvh, int parentPosition,
                    boolean byUser)
            {

            }

            @Override
            public void onParentCollapsed(ParentViewHolder pvh, int parentPosition,
                    boolean byUser)
            {

            }
        });

        mIPresenter = new PresenterImpl(adapter, mData);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MyAdapter adapter= (MyAdapter) mRecyclerView.getAdapter();
        int id = item.getItemId();
        switch (id) {
            case  R.id.action_test:
                DialogFragment dialog= (DialogFragment) getActivity().getSupportFragmentManager().findFragmentByTag
                        ("dialog");
                if (dialog == null) {
                    Logger.e(TAG,"create new Dialog");
                    dialog = new MyDialog();
                }
                dialog.setTargetFragment(this, REQUEST_RESULT);
                dialog.show(getActivity().getSupportFragmentManager(), "dialog");
                break;
            case R.id.action_refresh:
                adapter.notifyAllChanged();
                break;
            case R.id.action_toggle_expandable_1:
                MyParent parent = adapter.getData().get(1);
                parent.setExpandable(!parent.isExpandable());
                adapter.notifyParentItemChanged(1);
                break;
            case R.id.action_expand_all:
                adapter.expandAllParent();
                break;
            case R.id.action_collapse_all:
                adapter.collapseAllParent();
                break;
            case R.id.action_expand_1:
                adapter.expandParent(1);
                break;
            case R.id.action_collapse_1:
                adapter.collapseParent(1);
                break;
            case R.id.action_settings:
                Intent intent=new Intent(getActivity(),SecondActivity.class);
                Test test=new Test();
                MyParent myParent =test.getMyParent();
                List<MyChild> myChildren = myParent.getChildren();
                test.setString("pppppppppppp");
                myParent.setInitiallyExpanded(false);
                if (myChildren != null && !myChildren.isEmpty()) {
                    myChildren.get(0).setDot(1);
                    Logger.e(TAG, "change child 0 dot");
                }
                intent.putExtra("test", test);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode==REQUEST_RESULT) {
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
                        args[k] = Integer.valueOf(requestSplit[k + 1]);
                    }
                    Method m = IPresenter.class.getDeclaredMethod(method, argTypes);
                    Log.e(TAG, "method=" + m.toString());
                    m.setAccessible(true);
                    m.invoke(mIPresenter,args);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    Util.showToast(getContext(), "Test failed,please check input format");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    Util.showToast(getContext(), "Test failed,please check input format");
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    Util.showToast(getContext(), "Test failed,please check input format");
                }
            }
        }
    }

    @Override
    public void onStart() {
        Logger.e(TAG,"***********onStart*********");
        super.onStart();
    }

    @Override
    public void onResume() {
        Logger.e(TAG,"***********onResume*********");
        super.onResume();
    }

    @Override
    public void onPause() {
        Logger.e(TAG,"***********onPause*********");
        super.onPause();
    }

    @Override
    public void onStop() {
        Logger.e(TAG,"***********onStop*********");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Logger.e(TAG,"***********onDestroyView*********");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Logger.e(TAG,"***********onDestroy*********");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Logger.e(TAG,"***********onDetach*********");
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Logger.e(TAG,"***********onSaveInstanceState*********");
        super.onSaveInstanceState(outState);
        MyAdapter adapter= (MyAdapter) mRecyclerView.getAdapter();
        adapter.onSaveInstanceState(outState);
    }

}
