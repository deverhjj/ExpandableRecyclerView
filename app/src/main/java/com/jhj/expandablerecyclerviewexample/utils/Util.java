package com.jhj.expandablerecyclerviewexample.utils;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jhj.expandablerecyclerviewexample.model.ChildItem;
import com.jhj.expandablerecyclerviewexample.model.ParentItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by jhj_Plus on 2016/9/2.
 */
public class Util {
    private static final String TAG = "Util";

    public static List<ParentItem> getListData() {
        Random random=new Random();
        List<ParentItem> parentItems=new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            ParentItem parentItem=new ParentItem();
            parentItem.setType(random.nextInt(2));
            if (random.nextBoolean()) {
                List<ChildItem> childItems=new ArrayList<>();
                for (int j = 0; j < random.nextInt(6); j++) {
                    ChildItem childItem=new ChildItem();
                    childItem.setType(random.nextInt(2));
                    childItems.add(childItem);
                }
                parentItem.setChildItems(childItems);
            }
            parentItems.add(parentItem);
        }
        return parentItems;
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
