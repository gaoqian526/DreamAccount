package com.gao.dreamaccount.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class PreferencesUtils {

    private Context mContext;
    private Editor editor;
    private SharedPreferences sp;

    private static PreferencesUtils utils;

    public static PreferencesUtils getInstance(Context context, String preferenceName) {
        if (TextUtils.isEmpty(preferenceName)) {
            utils = new PreferencesUtils(context);
        } else {
            utils = new PreferencesUtils(context, preferenceName);
        }
        return utils;
    }

    // 根据文件名，取配置文件
    public PreferencesUtils(Context context, String preferenceName) {
        mContext = context;
        sp = mContext.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
    }

    // 不包含文件名，取默认配置文件
    public PreferencesUtils(Context context) {
        mContext = context;
        sp = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    /**
     * 获取指定文件中的int值
     */
    public static int getInt(Context context, String name, String key) {
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sp.getInt(key, 0);
    }

    /**
     * 获取指定文件中的string
     */
    public static String getString(Context context, String name, String key) {
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    /**
     * 获取指定文件的boolean值
     */
    public static boolean getBoolean(Context context, String name, String key) {
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }
    /**
     * 获取指定文件的boolean值
     */
    public static boolean getBoolean(Context context, String name, String key,boolean defValue) {
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defValue);
    }

    /**
     * 清空配置文件
     */
    public boolean clear() {
        editor = sp.edit();
        editor.clear();
        return editor.commit();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return sp.getBoolean(key, defValue);
    }

    public void putBoolean(String key, boolean state) {
        editor = sp.edit();
        editor.putBoolean(key, state);
        editor.commit();
    }

    public String getString(String key, String defValue) {
        return sp.getString(key, defValue);
    }

    public void putString(String key, String value) {
        editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public int getInt(String key, int defValue) {
        return sp.getInt(key, defValue);
    }

    public void putInt(String key, int value) {
        editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void putLong(String key, long value) {
        editor = sp.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public long getLong(String key, Long defValue) {
        return sp.getLong(key, defValue);
    }

    public boolean contains(String key) {
        return sp.contains(key);
    }
}
