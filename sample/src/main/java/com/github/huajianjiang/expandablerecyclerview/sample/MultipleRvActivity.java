package com.github.huajianjiang.expandablerecyclerview.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * @author HuaJian Jiang.
 *         Date 2017/1/23.
 */
public class MultipleRvActivity extends BaseActivity {
    private static final String TAG = MultipleRvActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBackNaviAction();
        setTitle(R.string.multiple_rv_title);
    }

    @Override
    public Fragment getFragment() {
        return new MultipleRvFragment();
    }
}
