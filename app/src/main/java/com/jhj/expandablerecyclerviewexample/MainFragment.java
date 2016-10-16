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

import com.jhj.expandablerecyclerview.adapter.ExpandableAdapter;
import com.jhj.expandablerecyclerview.utils.Logger;
import com.jhj.expandablerecyclerviewexample.adapter.MyAdapter;
import com.jhj.expandablerecyclerviewexample.model.Child;
import com.jhj.expandablerecyclerviewexample.model.Parent;
import com.jhj.expandablerecyclerviewexample.model.Test;
import com.jhj.expandablerecyclerviewexample.utils.Util;
import com.jhj.expandablerecyclerviewexample.viewholder.BaseParentViewHolder;

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

    private List<Parent> mData=Util.getListData();

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
        MyAdapter adapter=new MyAdapter(getActivity(), mData);
        adapter.setExpandCollapseMode(ExpandableAdapter.ExpandCollapseMode.MODE_SINGLE_COLLAPSE);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addItemDecoration(adapter.getItemDecoration());
        adapter.setParentExpandCollapseListener(new ExpandableAdapter.OnParentExpandCollapseListener() {

            @Override
            public void onParentExpanded(int parentPosition, int parentAdapterPosition,
                    boolean byUser)
            {
                Logger.e(TAG,"onParentExpanded="+parentPosition);

                BaseParentViewHolder vh= (BaseParentViewHolder) mRecyclerView
                        .findViewHolderForAdapterPosition(parentAdapterPosition);
                if (vh==null) return;
                final ImageView arrow = vh.getView(R.id.arrow);
                final float currRotate=arrow.getRotation();
                Logger.e(TAG, "currRotate=" + currRotate);
                //重置为从0开始旋转
                if (currRotate == 360) {
                    arrow.setRotation(0);
                }
                arrow.animate().rotation(180).setDuration(300).start();
            }

            @Override
            public void onParentCollapsed(int parentPosition, int parentAdapterPosition,
                    boolean byUser)
            {
                Logger.e(TAG,"onParentCollapsed="+parentPosition);

                BaseParentViewHolder vh= (BaseParentViewHolder) mRecyclerView
                        .findViewHolderForAdapterPosition(parentAdapterPosition);
                if (vh==null) return;
                final ImageView arrow = vh.getView(R.id.arrow);
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
        mIPresenter=new PresenterImpl(adapter,mData);
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
                Parent parent = adapter.getData().get(1);
                parent.setExpandable(!parent.isExpandable());
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
                Parent p=test.getParent();
                List<Child> children=p.getChildItems();
                test.setString("pppppppppppp");
                p.setInitiallyExpanded(false);
                if (children != null && !children.isEmpty()) {
                    children.get(0).setDot(1);
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
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode==REQUEST_RESULT) {

            ArrayList<String> requests=data.getStringArrayListExtra(MyDialog.REQUEST);
            Log.e(TAG,"requests="+requests.toString());

            final int requestCount=requests.size();

            for (int i = requestCount-1; i >=0; i--) {
                String request = requests.get(i);
                String[] requestSplit = request.split(",");
                String method=requestSplit[0];
                final int argsCount=requestSplit.length-1;

                try {
                    Object[] args=new Object[argsCount];
                    Class<?>[] argTypes=new Class<?>[argsCount];
                    for (int k = 0; k < argsCount; k++) {
                        argTypes[k]=int.class;
                        args[k]=Integer.valueOf(requestSplit[k+1]);
                    }

                    Method m = IPresenter.class.getDeclaredMethod(method, argTypes);
                    Log.e(TAG, "method=" + m.toString());
                    m.setAccessible(true);
                    m.invoke(mIPresenter,args);

                } catch (NoSuchMethodException | IllegalAccessException |
                        InvocationTargetException e) {
                    e.printStackTrace();
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
