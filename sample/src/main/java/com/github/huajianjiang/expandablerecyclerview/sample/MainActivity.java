package com.github.huajianjiang.expandablerecyclerview.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import com.github.huajianjiang.expandablerecyclerview.util.Logger;

/**
 * @author HuaJian Jiang.
 *         Date 2017/1/23.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View mainView =
                LayoutInflater.from(getApplicationContext()).inflate(R.layout.fragment_main, null);
        getFragmentContainer().addView(mainView);
        findViewById(R.id.jump_single_rv).setOnClickListener(this);
        findViewById(R.id.jump_multiple_rv).setOnClickListener(this);
    }

    @Override
    public Fragment getFragment() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.jump_single_rv:
                startActivity(new Intent(this, SingleRvActivity.class));
                break;
            case R.id.jump_multiple_rv:
                startActivity(new Intent(this, MultipleRvActivity.class));
                break;
        }
    }
}
