package com.gao.dreamaccount.abs;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.gao.dreamaccount.R;
import com.gao.dreamaccount.dao.DataBaseHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.umeng.analytics.MobclickAgent;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AbsActivity extends ActionBarActivity {

    protected DataBaseHelper dataBaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CalligraphyConfig.initDefault("fonts/Roboto-Light.ttf", R.attr.fontPath);
        if (dataBaseHelper == null) {
            dataBaseHelper = OpenHelperManager.getHelper(this, DataBaseHelper.class);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public DataBaseHelper getDataBaseHelper() {
        return dataBaseHelper;
    }

    protected Toolbar getActionBarToolbar() {
        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        return mActionBarToolbar;
    }

    protected void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataBaseHelper != null) {
            OpenHelperManager.releaseHelper();
            dataBaseHelper = null;
        }
    }

    public DisplayImageOptions getDisplayImageOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.color.blue_500)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();
        return options;
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
