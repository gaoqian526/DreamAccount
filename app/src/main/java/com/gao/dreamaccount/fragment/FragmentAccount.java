package com.gao.dreamaccount.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.gao.dreamaccount.R;
import com.gao.dreamaccount.abs.AbsActivity;
import com.gao.dreamaccount.abs.AbsFragment;
import com.gao.dreamaccount.adapter.AdapterFragmentAccount;
import com.gao.dreamaccount.bean.AccountBean;
import com.gao.dreamaccount.bean.AccountTotalBean;
import com.gao.dreamaccount.event.UpdateEvent;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by Gao on 2014/11/21.
 */
public class FragmentAccount extends AbsFragment {

    @InjectView(R.id.fragment_account_listview)
    ListView listView;

    private AdapterFragmentAccount adapterFragmentAccount;
    private Dao<AccountBean, Integer> accountBeanDao;
    private int sumType;//聚合时间 按累计，日，月，年 0,1,2,3
    private int dataType;//数据类型 收入还是支出
    private String dateStr;//时间字符串，当不为空时 查询相等时间

    public static FragmentAccount newInstance(int sumType, int dataType, String dateStr) {
        FragmentAccount fragmentAccount = new FragmentAccount();
        Bundle args = new Bundle();
        args.putInt("stype", sumType);
        args.putInt("dtype", dataType);
        args.putString("date", dateStr);
        fragmentAccount.setArguments(args);
        return fragmentAccount;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sumType = getArguments().getInt("stype", 0);
        dataType = getArguments().getInt("dtype", AccountBean.INCOME);
        dateStr = getArguments().getString("date");
        accountBeanDao = ((AbsActivity) getActivity()).getDataBaseHelper().getAccountBeanDao();
        adapterFragmentAccount = new AdapterFragmentAccount(getActivity());
        EventBus.getDefault().register(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (listView != null) {
            listView.setAdapter(adapterFragmentAccount);
            getData();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    public void onEvent(UpdateEvent event) {
        getData();
    }

    private void getData() {
        if (sumType == 0) {//累计
            getTotalAccountData(dataType);
        } else if (sumType == 1) {//日
            getDayAccountData(dataType);
        } else if (sumType == 2) {//月
            getMonthAccountData(dataType);
        } else {//年
            getYearAccountData(dataType);
        }
    }

    /**
     * 获取金额汇总
     *
     * @param type 收入还是支出
     */
    private double getDayAccountData(int type) {
        double amountStr = 0.0f;
        try {
            String incomeSql = "";
//            if (TextUtils.isEmpty(dateStr)) {
//                incomeSql = "select uuid,name,sum(amount),strftime('%Y-%m-%d',datetime((setTime/1000),'unixepoch','localtime')) from table_account where  strftime('%Y-%m-%d',datetime((setTime/1000),'unixepoch','localtime'))=strftime('%Y-%m-%d','now') and type=" + type + " group by strftime('%Y-%m-%d',datetime((setTime/1000),'unixepoch','localtime'))";
//            } else {
//                incomeSql = "select uuid,name,sum(amount),strftime('%Y-%m-%d',datetime((setTime/1000),'unixepoch','localtime')) from table_account where  strftime('%Y-%m-%d',datetime((setTime/1000),'unixepoch','localtime'))='" + dateStr + "' and type=" + type + " group by strftime('%Y-%m-%d',datetime((setTime/1000),'unixepoch','localtime'))";
//            }
            if (TextUtils.isEmpty(dateStr)) {
                incomeSql = "select uuid,name,amount,strftime('%Y-%m-%d',datetime((setTime/1000),'unixepoch','localtime')) from table_account where  strftime('%Y-%m-%d',datetime((setTime/1000),'unixepoch','localtime'))=strftime('%Y-%m-%d','now') and type=" + type;
            } else {
                incomeSql = "select uuid,name,amount,strftime('%Y-%m-%d',datetime((setTime/1000),'unixepoch','localtime')) from table_account where  strftime('%Y-%m-%d',datetime((setTime/1000),'unixepoch','localtime'))='" + dateStr + "' and type=" + type;
            }
            GenericRawResults<String[]> rawResults = accountBeanDao.queryRaw(incomeSql);
            List<String[]> values = rawResults.getResults();
            List<AccountTotalBean> accountTotalBeans = new ArrayList<AccountTotalBean>();
            if (values != null && values.size() > 0) {
                for (String[] itemArray : values) {
                    AccountTotalBean accountTotalBean = new AccountTotalBean();
                    accountTotalBean.setUuid(itemArray[0]);
                    accountTotalBean.setTitle(itemArray[1]);
                    accountTotalBean.setDateStr(itemArray[3]);
                    accountTotalBean.setIncomeAmount(Double.valueOf(itemArray[2]));
                    accountTotalBean.setType(sumType);
                    accountTotalBean.setsType(sumType);
                    accountTotalBeans.add(accountTotalBean);
                }
                adapterFragmentAccount.setAccountTotalBeans(accountTotalBeans);
            }
        } catch (Exception e) {
            e.printStackTrace();
            amountStr = 0.0f;
        }
        return amountStr;
    }

    /**
     * 获取金额汇总
     *
     * @param type 收入还是支出
     */
    private double getMonthAccountData(int type) {
        double amountStr = 0.0f;
        try {
            String incomeSql = "";
            if (TextUtils.isEmpty(dateStr)) {
                incomeSql = "select uuid,name,sum(amount),strftime('%Y-%m-%d',datetime((setTime/1000),'unixepoch','localtime')) from table_account where  strftime('%Y-%m',datetime((setTime/1000),'unixepoch','localtime'))=strftime('%Y-%m','now') and type=" + type + " group by strftime('%Y-%m-%d',datetime((setTime/1000),'unixepoch','localtime'))";
            } else {
                incomeSql = "select uuid,name,sum(amount),strftime('%Y-%m-%d',datetime((setTime/1000),'unixepoch','localtime')) from table_account where  strftime('%Y-%m',datetime((setTime/1000),'unixepoch','localtime'))='" + dateStr + "' and type=" + type + " group by strftime('%Y-%m-%d',datetime((setTime/1000),'unixepoch','localtime'))";
            }
            GenericRawResults<String[]> rawResults = accountBeanDao.queryRaw(incomeSql);
            List<String[]> values = rawResults.getResults();
            List<AccountTotalBean> accountTotalBeans = new ArrayList<AccountTotalBean>();
            if (values != null && values.size() > 0) {
                for (String[] itemArray : values) {
                    AccountTotalBean accountTotalBean = new AccountTotalBean();
                    accountTotalBean.setUuid(itemArray[0]);
                    accountTotalBean.setTitle(itemArray[1]);
                    accountTotalBean.setDateStr(itemArray[3]);
                    accountTotalBean.setIncomeAmount(Double.valueOf(itemArray[2]));
                    accountTotalBean.setType(sumType);
                    accountTotalBean.setsType(1);
                    accountTotalBeans.add(accountTotalBean);
                }
                adapterFragmentAccount.setAccountTotalBeans(accountTotalBeans);
            }
        } catch (Exception e) {
            e.printStackTrace();
            amountStr = 0.0f;
        }
        return amountStr;
    }

    /**
     * 获取金额汇总
     *
     * @param type 收入还是支出
     */
    private double getYearAccountData(int type) {
        double amountStr = 0.0f;
        try {
            String incomeSql = "";
            if (TextUtils.isEmpty(dateStr)) {
                incomeSql = "select uuid,name, sum(amount),strftime('%Y-%m',datetime((setTime/1000),'unixepoch','localtime')) from table_account where  strftime('%Y',datetime((setTime/1000),'unixepoch','localtime'))=strftime('%Y','now') and type=" + type + " group by strftime('%Y-%m',datetime((setTime/1000),'unixepoch','localtime'))";
            } else {
                incomeSql = "select uuid,name, sum(amount),strftime('%Y-%m',datetime((setTime/1000),'unixepoch','localtime')) from table_account where  strftime('%Y',datetime((setTime/1000),'unixepoch','localtime'))='" + dateStr + "' and type=" + type + " group by strftime('%Y-%m',datetime((setTime/1000),'unixepoch','localtime'))";
            }
            GenericRawResults<String[]> rawResults = accountBeanDao.queryRaw(incomeSql);
            List<String[]> values = rawResults.getResults();
            List<AccountTotalBean> accountTotalBeans = new ArrayList<AccountTotalBean>();
            if (values != null && values.size() > 0) {
                for (String[] itemArray : values) {
                    AccountTotalBean accountTotalBean = new AccountTotalBean();
                    accountTotalBean.setUuid(itemArray[0]);
                    accountTotalBean.setTitle(itemArray[1]);
                    accountTotalBean.setDateStr(itemArray[3]);
                    accountTotalBean.setIncomeAmount(Double.valueOf(itemArray[2]));
                    accountTotalBean.setType(sumType);
                    accountTotalBean.setsType(2);
                    accountTotalBeans.add(accountTotalBean);
                }
                adapterFragmentAccount.setAccountTotalBeans(accountTotalBeans);
            }
        } catch (Exception e) {
            e.printStackTrace();
            amountStr = 0.0f;
        }
        return amountStr;
    }

    /**
     * 获取金额汇总
     *
     * @param type 收入还是支出
     */
    private double getTotalAccountData(int type) {
        double amountStr = 0.0f;
        try {
            String incomeSql = "";
            incomeSql = "select uuid,name,sum(amount),strftime('%Y',datetime((setTime/1000),'unixepoch','localtime')) from table_account where  type=" + type + " group by strftime('%Y',datetime((setTime/1000),'unixepoch','localtime'))";
            GenericRawResults<String[]> rawResults = accountBeanDao.queryRaw(incomeSql);
            List<String[]> values = rawResults.getResults();
            List<AccountTotalBean> accountTotalBeans = new ArrayList<AccountTotalBean>();
            if (values != null && values.size() > 0) {
                for (String[] itemArray : values) {
                    AccountTotalBean accountTotalBean = new AccountTotalBean();
                    accountTotalBean.setUuid(itemArray[0]);
                    accountTotalBean.setTitle(itemArray[1]);
                    accountTotalBean.setDateStr(itemArray[3]);
                    accountTotalBean.setIncomeAmount(Double.valueOf(itemArray[2]));
                    accountTotalBean.setType(sumType);
                    accountTotalBean.setsType(3);
                    accountTotalBeans.add(accountTotalBean);
                }
                adapterFragmentAccount.setAccountTotalBeans(accountTotalBeans);
            }
        } catch (Exception e) {
            e.printStackTrace();
            amountStr = 0.0f;
        }
        return amountStr;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
        EventBus.getDefault().unregister(this);
    }
}
