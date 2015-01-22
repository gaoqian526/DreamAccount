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
import com.gao.dreamaccount.activity.ActivityDreamPager;
import com.gao.dreamaccount.adapter.AdapterFragmentDream;
import com.gao.dreamaccount.bean.AccountBean;
import com.gao.dreamaccount.bean.DreamBean;
import com.gao.dreamaccount.event.UpdateEvent;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by Gao on 2014/11/4.
 */
public class FragmentDream extends AbsFragment {

    @InjectView(R.id.fragment_dream_listview)
    ListView listView;

    private Dao<DreamBean, Integer> dreamBeanDao;
    private Dao<AccountBean, Integer> accountBeanDao;
    private int type;
    private AdapterFragmentDream adapterFragmentMain;
    private List<DreamBean> dreamBeanList;

    public static FragmentDream newInstance(int type) {
        FragmentDream fragmentDream = new FragmentDream();
        Bundle args = new Bundle();
        args.putInt("type", type);
        fragmentDream.setArguments(args);
        return fragmentDream;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int tempType = getArguments().getInt("type", 0);//完成状态 0未完成 1已完成
        if (tempType == 0) {
            type = DreamBean.STATUS_UNDO;
        } else {
            type = DreamBean.STATUS_DONE;
        }
        dreamBeanDao = ((AbsActivity) getActivity()).getDataBaseHelper().getDreamBeanDao();
        accountBeanDao = ((AbsActivity) getActivity()).getDataBaseHelper().getAccountBeanDao();
        double balance = getBalance();
        adapterFragmentMain = new AdapterFragmentDream(getActivity(), balance);
        setRetainInstance(true);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (listView != null) {
            listView.setAdapter(adapterFragmentMain);
            ((ActivityDreamPager) getActivity()).attchListView(listView);
            getData();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dream, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    public void onEvent(UpdateEvent event) {
        getData();
    }

    /**
     * 获取数据
     */
    private void getData() {
        try {
            dreamBeanList = dreamBeanDao.queryForEq("status", type);
            adapterFragmentMain.setDreamBeans(dreamBeanList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
        EventBus.getDefault().unregister(this);
    }

    /**
     * 获取余额
     */
    private double getBalance() {
        double income = getSumCount(AccountBean.INCOME);
        double cost = getSumCount(AccountBean.EXPENSE);
        return income - cost;
    }

    /**
     * 获取总额
     */
    private double getSumCount(int type) {
        double sumCount = 0;
        try {
            QueryBuilder<AccountBean, Integer> queryBuilder = accountBeanDao.queryBuilder();
            queryBuilder.where().eq("type", type);
            queryBuilder.selectRaw("SUM(amount)");
            GenericRawResults<String[]> rawResults = accountBeanDao.queryRaw(queryBuilder.prepareStatementString());
            String[] values = rawResults.getFirstResult();
            if (values != null && values.length > 0) {
                sumCount = Double.valueOf(values[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sumCount = 0;
        }
        return sumCount;
    }
}
