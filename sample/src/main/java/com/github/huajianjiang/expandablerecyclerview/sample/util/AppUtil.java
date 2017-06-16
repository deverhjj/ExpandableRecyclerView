package com.github.huajianjiang.expandablerecyclerview.sample.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.huajianjiang.expandablerecyclerview.sample.R;
import com.github.huajianjiang.expandablerecyclerview.sample.model.MyChild;
import com.github.huajianjiang.expandablerecyclerview.sample.model.MyParent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by jhj_Plus on 2016/9/2.
 */
public class AppUtil {
    private static final String TAG = "AppUtil";

    private static Random sRandom = new Random();

    public static final int[] TYPE_PARENT = {R.layout.item_parent_1, R.layout.item_parent_2};
    public static final int[] TYPE_CHILD = {R.layout.item_child_1, R.layout.item_child_2};

    public static boolean checkLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    // sRandom.nextInt(16) + 5
    public static List<MyParent> getListData() {
        List<MyParent> myParents = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            MyParent myParent = getParent();
            myParents.add(myParent);
        }
        return myParents;
    }

    @NonNull
    public static MyParent getParent() {
        MyParent myParent = new MyParent();
        myParent.setType(TYPE_PARENT[sRandom.nextInt(2)]);
        if (sRandom.nextBoolean()) {
            List<MyChild> myChildren =new ArrayList<>();
            for (int j = 0; j < sRandom.nextInt(6); j++) {
                MyChild myChild = new MyChild();
                myChild.setType(TYPE_CHILD[sRandom.nextInt(2)]);
                myChildren.add(myChild);
            }
            myParent.setMyChildren(myChildren);
        }
        return myParent;
    }


    public static void showSnackbar(View view, String text) {
        final Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_SHORT);
        snackbar.setAction("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        }).show();
    }


    public static void showSnackbar(Activity activity, String text) {
        final Snackbar snackbar = Snackbar.make(((ViewGroup) activity.getWindow().getDecorView().getRootView().findViewById(
                android.R.id.content)).getChildAt(0), text, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        }).show();
    }

    public static void showToast(Context context,String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

}
