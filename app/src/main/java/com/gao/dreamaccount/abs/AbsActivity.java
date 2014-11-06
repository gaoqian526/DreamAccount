package com.gao.dreamaccount.abs;

import android.content.Intent;
import android.support.v7.app.*;
import android.os.*;

public class AbsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    // =======================跳转activity======================

    public void launchActivity(Class<?> cls) {
        launchActivity(cls, null);
    }

    public void launchActivity(Class<?> cls, Bundle param) {
        Intent intent = new Intent(this, cls);
        if (param != null)
            intent.putExtras(param);
        startActivity(intent);
//        overridePendingTransition(R.anim.activity_right_in, R.anim.activity_left_out);
    }

    public void launchActivityForResult(Class<?> cls, int requestCode) {
        launchActivityForResult(cls, null, requestCode);
    }

    public void launchActivityForResult(Class<?> cls, Bundle param, int requestCode) {
        Intent intent = new Intent(this, cls);
        if (param != null)
            intent.putExtras(param);
        startActivityForResult(intent, requestCode);
//        overridePendingTransition(R.anim.activity_right_in, R.anim.activity_left_out);
    }

    // ===========================End===================================
}
