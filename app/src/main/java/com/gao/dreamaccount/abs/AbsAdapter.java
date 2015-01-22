package com.gao.dreamaccount.abs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.BaseAdapter;


public abstract class AbsAdapter extends BaseAdapter {
    protected Context mContext;

    public AbsAdapter(Context context) {
        mContext = context;
    }

    // =======================跳转activity======================

    public void launchActivity(Class<?> cls) {
        launchActivity(cls, null);
    }

    public void launchActivity(Class<?> cls, Bundle param) {
        Intent intent = new Intent(mContext, cls);
        if (param != null)
            intent.putExtras(param);
        mContext.startActivity(intent);
    }
}
