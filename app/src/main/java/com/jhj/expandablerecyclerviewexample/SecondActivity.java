package com.jhj.expandablerecyclerviewexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.jhj.expandablerecyclerview.utils.Logger;
import com.jhj.expandablerecyclerviewexample.model.Child;
import com.jhj.expandablerecyclerviewexample.model.Parent;
import com.jhj.expandablerecyclerviewexample.model.Test;

import java.util.List;

/**
 * Created by jhj_Plus on 2016/9/15.
 */
public class SecondActivity extends AppCompatActivity {
    private static final String TAG = "SecondActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Test test=getIntent().getParcelableExtra("test");
        Parent parent = test.getParent();
        List<Child> children = parent.getChildItems();
        boolean hasOneChild = children != null && !children.isEmpty();
        Logger.e(TAG, "string=" + test.getString() + ",parent=" + parent.isInitiallyExpanded() +
                ",child-dot=" + (hasOneChild ? children.get(0).getDot() : "none"));
    }
}
