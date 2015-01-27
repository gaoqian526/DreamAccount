package com.gao.dreamaccount.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.gao.dreamaccount.R;
import com.gao.dreamaccount.abs.AbsActivity;
import com.gao.dreamaccount.adapter.AdapterFragmentYearAccount;
import com.gao.dreamaccount.bean.AccountBean;
import com.gao.dreamaccount.bean.AccountTotalBean;
import com.gao.dreamaccount.event.UpdateEvent;
import com.gao.dreamaccount.util.Constant;
import com.gao.dreamaccount.util.LogUtil;
import com.gao.dreamaccount.util.Utils;
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
public class ActivityAccountYearPager extends AbsActivity implements SwipeBackActivityBase {

    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;
    @InjectView(R.id.activity_account_year_listview)
    ListView mActivityAccountYearListview;
    @InjectView(R.id.fab)
    FloatingActionButton mFab;


    private SwipeBackActivityHelper swipeBackActivityHelper;
    private String yearStr;

    private AdapterFragmentYearAccount adapterFragmentAccount;
    private Dao<AccountBean, Integer> accountBeanDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_year);
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);
        swipeBackActivityHelper = new SwipeBackActivityHelper(this);
        swipeBackActivityHelper.onActivityCreate();
        yearStr = getIntent().getStringExtra("year");
        accountBeanDao = getDataBaseHelper().getAccountBeanDao();
        adapterFragmentAccount = new AdapterFragmentYearAccount(this);
        initView();
        getData();
    }

    private void initView() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (TextUtils.isEmpty(yearStr)) {
            toolbar.setTitle("本年账单");
            toolbar.inflateMenu(R.menu.menu_more);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if (menuItem.getItemId() == R.id.action_more) {
                        launchActivity(ActivityAccountTotalPager.class);
                    }
                    return false;
                }
            });
        } else {
            toolbar.setTitle(yearStr + "年账单");
        }
        View footerView = LayoutInflater.from(this).inflate(R.layout.view_footer_view, null);
        mActivityAccountYearListview.addFooterView(footerView);
        mActivityAccountYearListview.setAdapter(adapterFragmentAccount);
        mFab.attachToListView(mActivityAccountYearListview);
    }

    public void onEvent(UpdateEvent event) {
        getData();
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
        bundle.putString("date", adapterFragmentAccount.getItem(position).getDateStr());
        launchActivity(ActivityAccountMonthPager.class, bundle);
    }

    public void attchListView(ListView listView) {
        if (mFab != null) {
            mFab.attachToListView(listView);
            mFab.show();
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

    private void getData() {
        String year = "";
        if (TextUtils.isEmpty(yearStr)) {
            year = Utils.getCurrentYear() + "";
        } else {
            year = yearStr;
        }
        List<AccountTotalBean> accountTotalBeans = new ArrayList<AccountTotalBean>();
        for (int i = 1; i < 13; i++) {
            AccountTotalBean accountTotalBean = new AccountTotalBean();
            double expenseAmount = getYearExpenseAccountData(Utils.formateInteger(i));
            double incomeAmount = getYearIncomeAccountData(Utils.formateInteger(i));
            if (expenseAmount == 0 && incomeAmount == 0) {
                continue;
            } else {
                accountTotalBean.setExpenseAmount(expenseAmount);
                accountTotalBean.setIncomeAmount(incomeAmount);
                accountTotalBean.setColorId(Constant.COLOR_MONTH[i - 1]);
                accountTotalBean.setTitle(Constant.STRING_MONTH[i - 1]);
                accountTotalBean.setDateStr(year + "-" + Utils.formateInteger(i));
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
