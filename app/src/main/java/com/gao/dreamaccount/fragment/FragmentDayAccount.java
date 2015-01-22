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
import com.gao.dreamaccount.activity.ActivityAccountDayPager;
import com.gao.dreamaccount.activity.ActivityNewAccount;
import com.gao.dreamaccount.adapter.AdapterFragmentDayAccount;
import com.gao.dreamaccount.bean.AccountBean;
import com.gao.dreamaccount.bean.AccountTotalBean;
import com.gao.dreamaccount.event.UpdateEvent;
import com.gao.dreamaccount.util.Constant;
import com.gao.dreamaccount.util.LogUtil;
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
public class FragmentDayAccount extends AbsFragment {

    @InjectView(R.id.fragment_account_listview)
    ListView listView;

    private AdapterFragmentDayAccount adapterFragmentAccount;
    private Dao<AccountBean, Integer> accountBeanDao;
    private int dataType;//数据类型 收入还是支出
    private String dateStr;//日期

    public static FragmentDayAccount newInstance(int dataType, String dateStr) {
        FragmentDayAccount fragmentAccount = new FragmentDayAccount();
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
        adapterFragmentAccount = new AdapterFragmentDayAccount(getActivity());
        EventBus.getDefault().register(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (listView != null) {
            listView.setAdapter(adapterFragmentAccount);
            ((ActivityAccountDayPager) getActivity()).attchListView(listView);
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
        String uuid = accountTotalBean.getUuid();
        if (!TextUtils.isEmpty(uuid)) {
            Bundle bundle = new Bundle();
            bundle.putString("uuid", uuid);
            launchActivity(ActivityNewAccount.class, bundle);
        }
    }

    private void getData() {
        long time = Utils.getTimeTemp(dateStr, "yyyy-MM-dd");
        int mdc = Utils.countDay(new Date(time));
        List<AccountTotalBean> accountTotalBeans = getDayAccountData();
        adapterFragmentAccount.setAccountTotalBeans(accountTotalBeans);
    }


    /**
     * 获取金额汇总
     */
    private List<AccountTotalBean> getDayAccountData() {
        try {
            String incomeSql = "";
            incomeSql = "select uuid,name, amount,strftime('%Y-%m-%d',datetime((setTime/1000),'unixepoch','localtime')) from table_account where  strftime('%Y-%m-%d',datetime((setTime/1000),'unixepoch','localtime'))='" + dateStr + "' and type=" + dataType;
            GenericRawResults<String[]> rawResults = accountBeanDao.queryRaw(incomeSql);
            List<String[]> values = rawResults.getResults();
            List<AccountTotalBean> accountTotalBeans = new ArrayList<AccountTotalBean>();
            if (values != null && values.size() > 0) {
                LogUtil.e(values.size() + "个");
                for (String[] itemArray : values) {
                    AccountTotalBean accountTotalBean = new AccountTotalBean();
                    LogUtil.e(itemArray[1]);
                    accountTotalBean.setUuid(itemArray[0]);
                    accountTotalBean.setDateStr(itemArray[3]);
                    accountTotalBean.setIncomeAmount(Double.valueOf(itemArray[2]));
                    String title = itemArray[1].substring(itemArray[1].length() - 1, itemArray[1].length());
                    accountTotalBean.setTitle(title);
                    accountTotalBean.setName(itemArray[1]);
                    accountTotalBean.setColorId(Constant.GET_COLOR_RADOME());
                    accountTotalBeans.add(accountTotalBean);
                }
            }
            return accountTotalBeans;
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
