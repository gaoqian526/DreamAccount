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
import com.gao.dreamaccount.activity.ActivityAccountTotalPager;
import com.gao.dreamaccount.activity.ActivityAccountYearPager;
import com.gao.dreamaccount.adapter.AdapterFragmentYearAccount;
import com.gao.dreamaccount.bean.AccountBean;
import com.gao.dreamaccount.bean.AccountTotalBean;
import com.gao.dreamaccount.event.UpdateEvent;
import com.gao.dreamaccount.util.Constant;
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
public class FragmentTotalAccount extends AbsFragment {

    @InjectView(R.id.fragment_account_listview)
    ListView listView;

    private AdapterFragmentYearAccount adapterFragmentAccount;
    private Dao<AccountBean, Integer> accountBeanDao;
    private int dataType;//数据类型 收入还是支出

    public static FragmentTotalAccount newInstance(int dataType) {
        FragmentTotalAccount fragmentAccount = new FragmentTotalAccount();
        Bundle args = new Bundle();
        args.putInt("dtype", dataType);
        fragmentAccount.setArguments(args);
        return fragmentAccount;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataType = getArguments().getInt("dtype", AccountBean.INCOME);
        accountBeanDao = ((AbsActivity) getActivity()).getDataBaseHelper().getAccountBeanDao();
        adapterFragmentAccount = new AdapterFragmentYearAccount(getActivity());
        EventBus.getDefault().register(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (listView != null) {
            listView.setAdapter(adapterFragmentAccount);
            ((ActivityAccountTotalPager) getActivity()).attchListView(listView);
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
        bundle.putString("year", accountTotalBean.getDateStr());
        launchActivity(ActivityAccountYearPager.class, bundle);
    }

    public void onEvent(UpdateEvent event) {
        getData();
    }

    private void getData() {
        List<AccountTotalBean> accountTotalBeans = getTotalAccountData();
        adapterFragmentAccount.setAccountTotalBeans(accountTotalBeans);
    }


    /**
     * 获取金额汇总
     *
     * @param month 月份
     */
    private List<AccountTotalBean> getTotalAccountData() {
        try {
            String incomeSql = "";
            incomeSql = "select uuid,name, sum(amount),strftime('%Y',datetime((setTime/1000),'unixepoch','localtime')),setTime from table_account where   type=" + dataType + " group by strftime('%Y',datetime((setTime/1000),'unixepoch','localtime'))";
            GenericRawResults<String[]> rawResults = accountBeanDao.queryRaw(incomeSql);
            List<String[]> values = rawResults.getResults();
            if (values != null && values.size() > 0) {
                List<AccountTotalBean> accountTotalBeans = new ArrayList<AccountTotalBean>();
                for (String[] itemArray : values) {
                    AccountTotalBean accountTotalBean = new AccountTotalBean();
                    accountTotalBean.setColorId(Constant.GET_COLOR_RADOME());
                    accountTotalBean.setTitle(itemArray[3]);
                    accountTotalBean.setUuid(itemArray[0]);
                    accountTotalBean.setDateStr(itemArray[3]);
                    accountTotalBean.setIncomeAmount(Double.valueOf(itemArray[2]));
                    accountTotalBeans.add(accountTotalBean);
                }
                return accountTotalBeans;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
        EventBus.getDefault().unregister(this);
    }
}
