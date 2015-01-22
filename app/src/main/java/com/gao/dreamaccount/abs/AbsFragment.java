package com.gao.dreamaccount.abs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umeng.analytics.MobclickAgent;

public class AbsFragment extends Fragment {
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getName()); //统计页面
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getName());
    }

    // =======================跳转activity======================

    public void launchActivity(Class<?> cls) {
        launchActivity(cls, null);
    }

    public void launchActivity(Class<?> cls, Bundle param) {
        Intent intent = new Intent(getActivity(), cls);
        if (param != null)
            intent.putExtras(param);
        startActivity(intent);
//        getActivity().overridePendingTransition(R.anim.activity_right_in,
//                R.anim.activity_left_out);
    }

    public void launchActivityForResult(Class<?> cls, int requestCode) {
        launchActivityForResult(cls, null, requestCode);
    }

    public void launchActivityForResult(Class<?> cls, Bundle param, int requestCode) {
        Intent intent = new Intent(getActivity(), cls);
        if (param != null)
            intent.putExtras(param);
        startActivityForResult(intent, requestCode);
//        getActivity().overridePendingTransition(R.anim.activity_right_in,
//                R.anim.activity_left_out);
    }

    // ===========================End===================================
}
