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
import com.gao.dreamaccount.activity.ActivityAccountMonthPager;
import com.gao.dreamaccount.activity.ActivityAccountYearPager;
import com.gao.dreamaccount.adapter.AdapterFragmentYearAccount;
import com.gao.dreamaccount.bean.AccountBean;
import com.gao.dreamaccount.bean.AccountTotalBean;
import com.gao.dreamaccount.event.UpdateEvent;
import com.gao.dreamaccount.util.Constant;
import com.gao.dreamaccount.util.LogUtil;
import com.gao.dreamaccount.util.Utils;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import de.greenrobot.event.EventBus;

/**
 * Created by Gao on 2014/11/21.
 */
public class FragmentYearAccount extends AbsFragment {

    @InjectView(R.id.fragment_account_listview)
    ListView listView;

    private AdapterFragmentYearAccount adapterFragmentAccount;
    private Dao<AccountBean, Integer> accountBeanDao;
    private int dataType;//数据类型 收入还是支出
    private String yearStr;

    public static FragmentYearAccount newInstance(int dataType, String yearStr) {
        FragmentYearAccount fragmentAccount = new FragmentYearAccount();
        Bundle args = new Bundle();
        args.putInt("dtype", dataType);
        args.putString("year", yearStr);
        fragmentAccount.setArguments(args);
        return fragmentAccount;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataType = getArguments().getInt("dtype", AccountBean.INCOME);
        yearStr = getArguments().getString("year");
        accountBeanDao = ((AbsActivity) getActivity()).getDataBaseHelper().getAccountBeanDao();
        adapterFragmentAccount = new AdapterFragmentYearAccount(getActivity());
        EventBus.getDefault().register(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (listView != null) {
            listView.setAdapter(adapterFragmentAccount);
            ((ActivityAccountYearPager) getActivity()).attchListView(listView);
            getData();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @OnItemClick(R.id.fragment_account_listview)
    void itemClick(int position) {
        AccountTotalBean accountTotalBean = adapterFragmentAccount.getItem(position);
        Bundle bundle = new Bundle();
        bundle.putString("date", accountTotalBean.getDateStr());
        launchActivity(ActivityAccountMonthPager.class, bundle);
    }

    public void onEvent(UpdateEvent event) {
        getData();
    }

    //    private void getData() {
//        List<AccountTotalBean> accountTotalBeans = new ArrayList<AccountTotalBean>();
//        for (int i = 1; i < 13; i++) {
//            AccountTotalBean accountTotalBean = getYearAccountData(i);
//            accountTotalBeans.add(accountTotalBean);
//        }
//        adapterFragmentAccount.setAccountTotalBeans(accountTotalBeans);
//    }
    private void getData() {
        List<AccountTotalBean> accountTotalBeans = new ArrayList<AccountTotalBean>();
        for (int i = 1; i < 13; i++) {
            AccountTotalBean accountTotalBean = new AccountTotalBean();
            double expenseAmount = getYearExpenseAccountData(Utils.formateInteger(i));
            double incomeAmount = getYearIncomeAccountData(Utils.formateInteger(i));
            LogUtil.e("收入：" + incomeAmount);
            LogUtil.e("支出：" + expenseAmount);
            if (expenseAmount == 0 && incomeAmount == 0) {
                continue;
            } else {
                accountTotalBean.setExpenseAmount(expenseAmount);
                accountTotalBean.setIncomeAmount(incomeAmount);
                accountTotalBean.setTitle(Utils.formateInteger(i));
                accountTotalBeans.add(accountTotalBean);
            }
        }
        adapterFragmentAccount.setAccountTotalBeans(accountTotalBeans);
    }


    /**
     * 获取金额汇总
     *
     * @param month 月份
     */
    private double getYearExpenseAccountData(String month) {
        try {
            String incomeSql = "";
            if (TextUtils.isEmpty(yearStr)) {
                incomeSql = "select uuid,name, sum(amount),strftime('%Y-%m',datetime((setTime/1000),'unixepoch','localtime')),setTime from table_account where  strftime('%Y',datetime((setTime/1000),'unixepoch','localtime'))=strftime('%Y','now') and strftime('%m',datetime((setTime/1000),'unixepoch','localtime'))='" + month + "' and type=" + AccountBean.EXPENSE + " group by strftime('%Y-%m',datetime((setTime/1000),'unixepoch','localtime'))";
            } else {
                incomeSql = "select uuid,name, sum(amount),strftime('%Y-%m',datetime((setTime/1000),'unixepoch','localtime')),setTime from table_account where  strftime('%Y',datetime((setTime/1000),'unixepoch','localtime'))='" + yearStr + "' and strftime('%m',datetime((setTime/1000),'unixepoch','localtime'))='" + month + "' and type=" + AccountBean.EXPENSE + " group by strftime('%Y-%m',datetime((setTime/1000),'unixepoch','localtime'))";
            }
            GenericRawResults<String[]> rawResults = accountBeanDao.queryRaw(incomeSql);
            List<String[]> values = rawResults.getResults();
            if (values != null && values.size() > 0) {
                String[] itemArray = values.get(0);
                LogUtil.e("支出：" + itemArray[0] + "==" + itemArray[1] + "==" + itemArray[2]);
                return Double.valueOf(itemArray[2]);
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * 获取金额汇总
     *
     * @param month 月份
     */
    private double getYearIncomeAccountData(String month) {
        try {
            String incomeSql = "";
            if (TextUtils.isEmpty(yearStr)) {
                incomeSql = "select uuid,name, sum(amount),strftime('%Y-%m',datetime((setTime/1000),'unixepoch','localtime')),setTime from table_account where  strftime('%Y',datetime((setTime/1000),'unixepoch','localtime'))=strftime('%Y','now') and strftime('%m',datetime((setTime/1000),'unixepoch','localtime'))='" + month + "' and type=" + AccountBean.INCOME + " group by strftime('%Y-%m',datetime((setTime/1000),'unixepoch','localtime'))";
            } else {
                incomeSql = "select uuid,name, sum(amount),strftime('%Y-%m',datetime((setTime/1000),'unixepoch','localtime')),setTime from table_account where  strftime('%Y',datetime((setTime/1000),'unixepoch','localtime'))='" + yearStr + "' and strftime('%m',datetime((setTime/1000),'unixepoch','localtime'))='" + month + "' and type=" + AccountBean.INCOME + " group by strftime('%Y-%m',datetime((setTime/1000),'unixepoch','localtime'))";
            }
            GenericRawResults<String[]> rawResults = accountBeanDao.queryRaw(incomeSql);
            List<String[]> values = rawResults.getResults();
            if (values != null && values.size() > 0) {
                String[] itemArray = values.get(0);
                LogUtil.e("支出：" + itemArray[0] + "==" + itemArray[1] + "==" + itemArray[2]);
                return Double.valueOf(itemArray[2]);
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取金额汇总
     *
     * @param month 月份
     */
    private AccountTotalBean getYearAccountData(int month) {
        AccountTotalBean accountTotalBean = new AccountTotalBean();
        accountTotalBean.setTitle(Constant.STRING_MONTH[month - 1]);
        accountTotalBean.setColorId(Constant.COLOR_MONTH[month - 1]);
        try {
            String incomeSql = "";
            if (TextUtils.isEmpty(yearStr)) {
                incomeSql = "select uuid,name, sum(amount),strftime('%Y-%m',datetime((setTime/1000),'unixepoch','localtime')),setTime from table_account where  strftime('%Y',datetime((setTime/1000),'unixepoch','localtime'))=strftime('%Y','now') and strftime('%m',datetime((setTime/1000),'unixepoch','localtime'))='" + month + "' and type=" + AccountBean.INCOME + " group by strftime('%Y-%m',datetime((setTime/1000),'unixepoch','localtime'))";
            } else {
                incomeSql = "select uuid,name, sum(amount),strftime('%Y-%m',datetime((setTime/1000),'unixepoch','localtime')),setTime from table_account where  strftime('%Y',datetime((setTime/1000),'unixepoch','localtime'))='" + yearStr + "' and strftime('%m',datetime((setTime/1000),'unixepoch','localtime'))='" + month + "' and type=" + AccountBean.INCOME + " group by strftime('%Y-%m',datetime((setTime/1000),'unixepoch','localtime'))";
            }
            GenericRawResults<String[]> rawResults = accountBeanDao.queryRaw(incomeSql);
            List<String[]> values = rawResults.getResults();
            if (values != null && values.size() > 0) {
                String[] itemArray = values.get(0);
                accountTotalBean.setUuid(itemArray[0]);
                accountTotalBean.setDateStr(itemArray[3]);
                accountTotalBean.setIncomeAmount(Double.valueOf(itemArray[2]));
            } else {
                accountTotalBean.setIncomeAmount(Double.valueOf(0.0f));
                accountTotalBean.setDateStr(Utils.getCurrentYear() + "-" + Utils.formateInteger(month));
            }
        } catch (Exception e) {
            e.printStackTrace();
            accountTotalBean.setIncomeAmount(Double.valueOf(0.0f));
        }
        return accountTotalBean;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
        EventBus.getDefault().unregister(this);
    }
}
