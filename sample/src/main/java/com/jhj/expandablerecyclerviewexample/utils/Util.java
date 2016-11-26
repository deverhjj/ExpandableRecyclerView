package com.jhj.expandablerecyclerviewexample.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jhj.expandablerecyclerviewexample.model.MyChild;
import com.jhj.expandablerecyclerviewexample.model.MyParent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by jhj_Plus on 2016/9/2.
 */
public class Util {
    private static final String TAG = "Util";

    private static Random sRandom=new Random();

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
        MyParent myParent =new MyParent();
        myParent.setType(sRandom.nextInt(2));
        if (sRandom.nextBoolean()) {
            List<MyChild> myChildren =new ArrayList<>();
            for (int j = 0; j < sRandom.nextInt(6); j++) {
                MyChild myChild =new MyChild();
                myChild.setType(sRandom.nextInt(2));
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
