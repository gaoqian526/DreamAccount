package com.gao.dreamaccount.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.gao.dreamaccount.R;
import com.gao.dreamaccount.abs.AbsActivity;
import com.gao.dreamaccount.adapter.AdapterFragmentYearAccount;
import com.gao.dreamaccount.bean.AccountBean;
import com.gao.dreamaccount.bean.AccountTotalBean;
import com.gao.dreamaccount.event.UpdateEvent;
import com.gao.dreamaccount.views.RoundedLetterView;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import de.greenrobot.event.EventBus;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;

/**
 * Created by Gao on 2014/11/15.
 */
public class ActivityAccountTotalPager extends AbsActivity implements SwipeBackActivityBase {

    @InjectView(R.id.fab)
    FloatingActionButton floatingActionButton;
    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;
    @InjectView(R.id.activity_account_year_total)
    RoundedLetterView mActivityAccountYearTotal;
    @InjectView(R.id.activity_account_year_listview)
    ListView mActivityAccountYearListview;


    private SwipeBackActivityHelper swipeBackActivityHelper;
    private Dao<AccountBean, Integer> accountBeanDao;
    private AdapterFragmentYearAccount adapterFragmentAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_year);
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);
        swipeBackActivityHelper = new SwipeBackActivityHelper(this);
        swipeBackActivityHelper.onActivityCreate();
        accountBeanDao = getDataBaseHelper().getAccountBeanDao();
        adapterFragmentAccount = new AdapterFragmentYearAccount(this);
        initView();
    }

    private void initView() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setTitle("全部账单");
        View footerView = LayoutInflater.from(this).inflate(R.layout.view_footer_view, null);
        mActivityAccountYearListview.addFooterView(footerView);
        mActivityAccountYearListview.setAdapter(adapterFragmentAccount);
        List<AccountTotalBean> accountTotalBeans = getTotalAccountData();
        adapterFragmentAccount.setAccountTotalBeans(accountTotalBeans);
        floatingActionButton.attachToListView(mActivityAccountYearListview);
        mActivityAccountYearTotal.setTitleText("年");
    }

    @OnClick(R.id.fab)
    void goAddAccount() {
        launchActivity(ActivityNewAccount.class);
    }

    @OnItemClick(R.id.activity_account_year_listview)
    void itemClick(int position) {
        if (position >= adapterFragmentAccount.getCount()) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("year", adapterFragmentAccount.getItem(position).getTitle());
        launchActivity(ActivityAccountYearPager.class, bundle);
    }

    public void onEvent(UpdateEvent event) {
        List<AccountTotalBean> accountTotalBeans = getTotalAccountData();
        adapterFragmentAccount.setAccountTotalBeans(accountTotalBeans);
    }

    public void attchListView(ListView listView) {
        if (floatingActionButton != null) {
            floatingActionButton.attachToListView(listView);
            floatingActionButton.show();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        swipeBackActivityHelper.onPostCreate();
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return swipeBackActivityHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        getSwipeBackLayout().scrollToFinishActivity();
    }

    /**
     * 获取金额汇总
     */
    private List<AccountTotalBean> getTotalAccountData() {
        try {
            String incomeSql = "";
            incomeSql = "select strftime('%Y',datetime((setTime/1000),'unixepoch','localtime')),setTime from table_account group by strftime('%Y',datetime((setTime/1000),'unixepoch','localtime'))";
            GenericRawResults<String[]> rawResults = accountBeanDao.queryRaw(incomeSql);
            List<String[]> values = rawResults.getResults();
            if (values != null && values.size() > 0) {
                List<AccountTotalBean> accountTotalBeans = new ArrayList<AccountTotalBean>();
                for (String[] itemArray : values) {
                    double incomeAmount = getTotalAccountIncomeData(itemArray[0]);
                    double expenseAmount = getTotalAccountExpenseData(itemArray[0]);
                    if (incomeAmount == 0 && expenseAmount == 0) {
                        continue;
                    } else {
                        AccountTotalBean accountTotalBean = new AccountTotalBean();
                        accountTotalBean.setExpenseAmount(expenseAmount);
                        accountTotalBean.setIncomeAmount(incomeAmount);
                        accountTotalBean.setTitle(itemArray[0]);
                        accountTotalBean.setColorId(R.color.blue_300);
                        accountTotalBeans.add(accountTotalBean);
                    }
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


    /**
     * 获取金额汇总
     */
    private double getTotalAccountExpenseData(String year) {
        try {
            String incomeSql = "";
            incomeSql = "select uuid,name, sum(amount),strftime('%Y',datetime((setTime/1000),'unixepoch','localtime')),setTime from table_account where type=" + AccountBean.EXPENSE + " and strftime('%Y',datetime((setTime/1000),'unixepoch','localtime'))='" + year + "'";
            GenericRawResults<String[]> rawResults = accountBeanDao.queryRaw(incomeSql);
            List<String[]> values = rawResults.getResults();
            if (values != null && values.size() > 0) {
                String[] itemArray = values.get(0);
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
     */
    private double getTotalAccountIncomeData(String year) {
        try {
            String incomeSql = "";
            incomeSql = "select uuid,name, sum(amount),strftime('%Y',datetime((setTime/1000),'unixepoch','localtime')),setTime from table_account where type=" + AccountBean.INCOME + " and strftime('%Y',datetime((setTime/1000),'unixepoch','localtime'))='" + year + "'";
            GenericRawResults<String[]> rawResults = accountBeanDao.queryRaw(incomeSql);
            List<String[]> values = rawResults.getResults();
            if (values != null && values.size() > 0) {
                String[] itemArray = values.get(0);
                return Double.valueOf(itemArray[2]);
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}
