package com.gao.dreamaccount.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.gao.dreamaccount.R;
import com.gao.dreamaccount.abs.AbsActivity;
import com.gao.dreamaccount.abs.AbsFragment;
import com.gao.dreamaccount.activity.ActivityAccountDayPager;
import com.gao.dreamaccount.activity.ActivityAccountMonthPager;
import com.gao.dreamaccount.adapter.AdapterFragmentYearAccount;
import com.gao.dreamaccount.bean.AccountBean;
import com.gao.dreamaccount.bean.AccountTotalBean;
import com.gao.dreamaccount.event.UpdateEvent;
import com.gao.dreamaccount.util.Constant;
import com.gao.dreamaccount.util.Utils;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import de.greenrobot.event.EventBus;

/**
 * Created by Gao on 2014/11/21.
 */
public class FragmentMonthAccount extends AbsFragment {

    @InjectView(R.id.fragment_account_listview)
    ListView listView;

    private AdapterFragmentYearAccount adapterFragmentAccount;
    private Dao<AccountBean, Integer> accountBeanDao;
    private int dataType;//数据类型 收入还是支出
    private String dateStr;//日期

    public static FragmentMonthAccount newInstance(int dataType, String dateStr) {
        FragmentMonthAccount fragmentAccount = new FragmentMonthAccount();
        Bundle args = new Bundle();
        args.putInt("dtype", dataType);
        args.putString("date", dateStr);
        fragmentAccount.setArguments(args);
        return fragmentAccount;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataType = getArguments().getInt("dtype", AccountBean.INCOME);
        dateStr = getArguments().getString("date");
        accountBeanDao = ((AbsActivity) getActivity()).getDataBaseHelper().getAccountBeanDao();
        adapterFragmentAccount = new AdapterFragmentYearAccount(getActivity());
        EventBus.getDefault().register(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (listView != null) {
            listView.setAdapter(adapterFragmentAccount);
            ((ActivityAccountMonthPager) getActivity()).attchListView(listView);
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

    @OnItemClick(R.id.fragment_account_listview)
    void itemClick(int position) {
        AccountTotalBean accountTotalBean = adapterFragmentAccount.getItem(position);
        if (accountTotalBean.getIncomeAmount() > 0.0d) {
            Bundle bundle = new Bundle();
            bundle.putString("date", accountTotalBean.getDateStr());
            launchActivity(ActivityAccountDayPager.class, bundle);
        }
    }

    private void getData() {
        long time = Utils.getTimeTemp(dateStr, "yyyy-MM");
        int mdc = Utils.countDay(new Date(time));
        List<AccountTotalBean> accountTotalBeans = new ArrayList<AccountTotalBean>();
        for (int i = 1; i < mdc + 1; i++) {
            AccountTotalBean accountTotalBean = getMonthAccountData(i);
            accountTotalBeans.add(accountTotalBean);
        }
        adapterFragmentAccount.setAccountTotalBeans(accountTotalBeans);
    }


    /**
     * 获取金额汇总
     *
     * @param month 月份
     */
    private AccountTotalBean getMonthAccountData(int month) {
        AccountTotalBean accountTotalBean = new AccountTotalBean();
        accountTotalBean.setTitle(Constant.STRING_DAY[month - 1]);
        accountTotalBean.setColorId(Constant.GET_COLOR_RADOME());
        try {
            String dateTme = dateStr + "-" + Utils.formateInteger(month);
            String incomeSql = "";
            incomeSql = "select uuid,name, sum(amount),strftime('%Y-%m-%d',datetime((setTime/1000),'unixepoch','localtime')) from table_account where  strftime('%Y-%m-%d',datetime((setTime/1000),'unixepoch','localtime'))='" + dateTme + "' and type=" + dataType + " group by strftime('%Y-%m-%d',datetime((setTime/1000),'unixepoch','localtime'))";
            GenericRawResults<String[]> rawResults = accountBeanDao.queryRaw(incomeSql);
            List<String[]> values = rawResults.getResults();
            if (values != null && values.size() > 0) {
                String[] itemArray = values.get(0);
                accountTotalBean.setUuid(itemArray[0]);
                accountTotalBean.setDateStr(itemArray[3]);
                accountTotalBean.setIncomeAmount(Double.valueOf(itemArray[2]));
            } else {
                accountTotalBean.setIncomeAmount(Double.valueOf(0.0f));
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
