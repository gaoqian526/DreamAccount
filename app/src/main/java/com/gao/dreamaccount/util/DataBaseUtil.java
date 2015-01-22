package com.gao.dreamaccount.util;

import android.util.Log;

import com.gao.dreamaccount.bean.AccountBean;
import com.gao.dreamaccount.bean.DreamBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gao on 2014/11/20.
 */
public class DataBaseUtil {

    public static String exportAccountData(List<AccountBean> accountBeans) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for (AccountBean accountBean : accountBeans) {
                JSONObject object = new JSONObject();
                object.put("uuid", accountBean.getUuid());
                object.put("name", accountBean.getName());
                object.put("des", accountBean.getDes());
                object.put("type", accountBean.getType());
                object.put("timeStr", accountBean.getTimeStr());
                object.put("setTime", accountBean.getSetTime());
                object.put("insetTime", accountBean.getInsetTime());
                object.put("amount", accountBean.getAmount());
                object.put("temp1", accountBean.getTemp1());
                object.put("temp2", accountBean.getTemp2());
                object.put("temp3", accountBean.getTemp3());
                jsonArray.put(object);
            }
            jsonObject.put("array", jsonArray);
            Log.e("account", jsonObject.toString());
            return jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<AccountBean> importAccountData(String json) {
        List<AccountBean> accountBeans = new ArrayList<AccountBean>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("array");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                AccountBean accountBean = new AccountBean();
                accountBean.setUuid(object.getString("uuid"));
                accountBean.setName(object.getString("name"));
                accountBean.setDes(object.getString("des"));
                accountBean.setType(object.getInt("type"));
                accountBean.setTimeStr(object.getString("timeStr"));
                accountBean.setSetTime(object.getLong("setTime"));
                accountBean.setInsetTime(object.getLong("insetTime"));
                accountBean.setAmount(object.getDouble("amount"));
                if (object.has("temp1")) {
                    accountBean.setTemp1(object.getString("temp1"));
                }
                if (object.has("temp2")) {
                    accountBean.setTemp2(object.getString("temp2"));
                }
                if (object.has("temp3")) {
                    accountBean.setTemp3(object.getString("temp3"));
                }
                accountBeans.add(accountBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
            accountBeans = null;
        }
        return accountBeans;
    }

    public static String exportDreamData(List<DreamBean> dreamBeans) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for (DreamBean dreamBean : dreamBeans) {
                JSONObject object = new JSONObject();
                object.put("uuid", dreamBean.getUuid());
                object.put("setTime", dreamBean.getSetTime());
                object.put("insertTime", dreamBean.getInsertTime());
                object.put("name", dreamBean.getName());
                object.put("des", dreamBean.getDes());
                object.put("budget", dreamBean.getBudget());
                object.put("status", dreamBean.getStatus());
                object.put("priority", dreamBean.getPriority());
                object.put("temp1", dreamBean.getTemp1());
                object.put("temp2", dreamBean.getTemp2());
                object.put("temp3", dreamBean.getTemp3());
                jsonArray.put(object);
            }
            jsonObject.put("array", jsonArray);
            return jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<DreamBean> importDreamData(String json) {
        List<DreamBean> dreamBeans = new ArrayList<DreamBean>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("array");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                DreamBean dreamBean = new DreamBean();
                dreamBean.setUuid(object.getString("uuid"));
                dreamBean.setSetTime(object.getLong("setTime"));
                dreamBean.setInsertTime(object.getLong("insertTime"));
                dreamBean.setName(object.getString("name"));
                dreamBean.setDes(object.getString("des"));
                dreamBean.setBudget(object.getDouble("budget"));
                dreamBean.setStatus(object.getInt("status"));
                dreamBean.setPriority(object.getInt("priority"));
                if (object.has("temp1")) {
                    dreamBean.setTemp1(object.getString("temp1"));
                }
                if (object.has("temp2")) {
                    dreamBean.setTemp2(object.getString("temp2"));
                }
                if (object.has("temp3")) {
                    dreamBean.setTemp3(object.getString("temp3"));
                }
                dreamBeans.add(dreamBean);
            }
        } catch (Exception e) {
            dreamBeans = null;
            e.printStackTrace();
        }
        return dreamBeans;
    }
}
