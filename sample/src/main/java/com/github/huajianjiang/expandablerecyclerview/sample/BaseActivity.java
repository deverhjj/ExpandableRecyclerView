package com.github.huajianjiang.expandablerecyclerview.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.github.huajianjiang.expandablerecyclerview.sample.util.Res;
import com.github.huajianjiang.expandablerecyclerview.util.Logger;

public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    private Toolbar mToolbar;

    public abstract Fragment getFragment();

    public ViewGroup getFragmentContainer() {
        return (ViewGroup) findViewById(R.id.fragmentContainer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        Logger.e(TAG,"Res.getStatusBarHeight(this)>>>"+Res.getStatusBarHeight(this));

        ViewCompat.setOnApplyWindowInsetsListener(mToolbar, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                Logger.e(TAG, "onApplyWindowInsets" + insets.getSystemWindowInsetTop() + ",," +
                              "consumed = " + insets.isConsumed());
                ((ViewGroup.MarginLayoutParams) v.getLayoutParams()).topMargin =
                        insets.getSystemWindowInsetTop();
                return insets.consumeSystemWindowInsets();
            }
        });


        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);
        if (fragment == null) {
            fragment = getFragment();
            if (fragment == null) return;
            fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
        }


    }

    public void setBackNaviAction() {
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
