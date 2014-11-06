package com.gao.dreamaccount.abs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class AbsFragment extends Fragment {
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
