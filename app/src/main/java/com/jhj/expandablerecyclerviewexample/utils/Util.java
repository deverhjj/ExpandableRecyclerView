package com.jhj.expandablerecyclerviewexample.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jhj.expandablerecyclerviewexample.model.Child;
import com.jhj.expandablerecyclerviewexample.model.Parent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by jhj_Plus on 2016/9/2.
 */
public class Util {
    private static final String TAG = "Util";
    private static Random sRandom=new Random();
    public static List<Parent> getListData() {

        List<Parent> parents =new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            Parent parent = getParent();
            parents.add(parent);
        }
        return parents;
    }

    @NonNull
    public static Parent getParent() {
        Parent parent =new Parent();
        parent.setType(sRandom.nextInt(2));
        if (sRandom.nextBoolean()) {
            List<Child> children =new ArrayList<>();
            for (int j = 0; j < sRandom.nextInt(6); j++) {
                Child child =new Child();
                child.setType(sRandom.nextInt(2));
                children.add(child);
            }
            parent.setChildren(children);
        }
        return parent;
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
