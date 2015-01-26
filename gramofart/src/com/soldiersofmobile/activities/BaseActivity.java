package com.soldiersofmobile.activities;

import android.os.Bundle;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.soldiersofmobile.R;

public abstract class BaseActivity extends SherlockFragmentActivity {

    private TextView mTitleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    public void setActionBarTitle(int resId){
        mTitleTv.setText(resId);
    }



    protected void initActionBar() {
        ActionBar actionbar = getSupportActionBar();

        actionbar.setCustomView(R.layout.abs_custom_layout);
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setDisplayUseLogoEnabled(false);
        actionbar.setDisplayShowHomeEnabled(false);

        // set defaults for logo & home up
        actionbar.setDisplayUseLogoEnabled(true);
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayHomeAsUpEnabled(false);
        mTitleTv = (TextView) findViewById(R.id.actionbar_title);
    }
}
