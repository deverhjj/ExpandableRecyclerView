package com.github.huajianjiang.expandablerecyclerview.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * @author HuaJian Jiang.
 *         Date 2017/1/23.
 */
public class SingleRvActivity extends BaseActivity {
    private static final String TAG = SingleRvActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBackNaviAction();
        setTitle(R.string.single_rv_title);
    }

    @Override
    public Fragment getFragment() {
        return new SingleRvFragment();
    }
}
