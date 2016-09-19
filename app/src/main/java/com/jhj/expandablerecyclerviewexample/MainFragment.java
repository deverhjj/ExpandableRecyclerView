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

import com.jhj.expandablerecyclerview.adapter.ExpandableRecyclerViewAdapter;
import com.jhj.expandablerecyclerview.utils.Logger;
import com.jhj.expandablerecyclerviewexample.adapter.MyAdapter;
import com.jhj.expandablerecyclerviewexample.model.Parent;
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
    private static final String TAG = "MainFragment";
    public static final int REQUEST_RESULT = 1;
    private RecyclerView mRecyclerView;

    private PresenterImpl mIPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState)
    {
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init(getView());
    }

    private void init(View rootView) {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<Parent> data=Util.getListData();
        final MyAdapter adapter = new MyAdapter(getActivity(), data);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addItemDecoration(adapter.getItemDecoration());
        adapter.setParentExpandCollapseListener(new ExpandableRecyclerViewAdapter.OnParentExpandCollapseListener() {

            @Override
            public void onParentExpanded(int parentPosition, int parentAdapterPosition,
                    boolean byUser)
            {
                Logger.e(TAG,"onParentExpanded="+parentPosition);

                BaseParentViewHolder vh= (BaseParentViewHolder) mRecyclerView
                        .findViewHolderForAdapterPosition(parentAdapterPosition);
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
        mIPresenter=new PresenterImpl(adapter,data);
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
            case R.id.action_settings:
                adapter.notifyParentItemInserted(-1);
                //startActivity(new Intent(getActivity(),SecondActivity.class));
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



}
