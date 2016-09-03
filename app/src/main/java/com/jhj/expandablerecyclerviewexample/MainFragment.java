package com.jhj.expandablerecyclerviewexample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.jhj.expandablerecyclerview.adapter.ExpandableRecyclerViewAdapter;
import com.jhj.expandablerecyclerviewexample.adapter.MyAdapter;
import com.jhj.expandablerecyclerviewexample.model.ParentItem;
import com.jhj.expandablerecyclerviewexample.utils.Util;

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        init(view);
    }

    private void init(View rootView) {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<ParentItem> data=Util.getListData();
        final MyAdapter adapter = new MyAdapter(getActivity(), data);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addItemDecoration(adapter.getItemDecoration());
        adapter.setParentExpandCollapseListener(new ExpandableRecyclerViewAdapter.OnParentExpandCollapseListener() {

            @Override
            public void onParentExpanded(int parentPosition, boolean byUser) {
                mIPresenter.autoNotifyAllChanged();
            }

            @Override
            public void onParentCollapsed(int parentPosition, boolean byUser) {
                mIPresenter.autoNotifyAllChanged();
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
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_test) {
            MyDialog dialog = new MyDialog();
            dialog.setTargetFragment(this, REQUEST_RESULT);
            dialog.show(getActivity().getSupportFragmentManager(), "dialog");
            return true;
        } else if (id==R.id.action_1) {
           mIPresenter.notifyParentItemRemoved(1);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode==REQUEST_RESULT) {

            MyAdapter adapter= (MyAdapter) mRecyclerView.getAdapter();

            ArrayList<String> methods=data.getStringArrayListExtra(MyDialog.METHODS);

            final int methodsCount=methods.size();

            for (int i = 0; i < methodsCount; i++) {
                String info = methods.get(i);
                String[] infos = info.split(",");
                String method=infos[0];

                List<Integer> args=new ArrayList<>(infos.length-1);

                for (int j = 1; j < infos.length; j++) {
                    String at = infos[j].trim();
                    if (!at.equals("null")) {
                        args.add(Integer.valueOf(at));
                    }
                }

                try {
                    Method m=null;
                    switch (args.size()) {
                        case 1:
                            m=IPresenter.class.getDeclaredMethod(method,int.class);
                            break;
                        case 2:
                            m=IPresenter.class.getDeclaredMethod(method,int.class,int.class);
                            break;
                        case 3:
                            m=IPresenter.class.getDeclaredMethod(method,int.class,int
                                    .class,int.class);
                            break;
                    }

                    if (m!=null) {
                        Log.e(TAG,"args="+args.toString());
                        m.setAccessible(true);
                        m.invoke(mIPresenter,args.toArray());
                    }

                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            Log.e(TAG,methods.toString());
        }
    }



}
