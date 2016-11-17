package com.jhj.expandablerecyclerviewexample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.jhj.expandablerecyclerview.utils.Logger;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.e(TAG,"***********onCreate*********");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);
        if (fragment == null) {
            fragmentManager.beginTransaction().replace(R.id.fragmentContainer, new MainFragment())
                    .commit();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Logger.e(TAG,"***********onSaveInstanceState*********");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Logger.e(TAG,"***********onRestoreInstanceState*********");
        super.onRestoreInstanceState(savedInstanceState);
    }
}
