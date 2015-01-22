package com.gao.dreamaccount.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.daimajia.swipe.implments.SwipeItemMangerImpl;
import com.gao.dreamaccount.R;
import com.gao.dreamaccount.abs.AbsActivity;
import com.gao.dreamaccount.adapter.AdapterFragmentDayAccount;
import com.gao.dreamaccount.bean.AccountBean;
import com.gao.dreamaccount.bean.AccountTotalBean;
import com.gao.dreamaccount.util.Constant;
import com.gao.dreamaccount.util.LogUtil;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;

/**
 * Created by Gao on 2014/11/15.
 */
public class ActivityAccountDayPager extends AbsActivity implements SwipeBackActivityBase {

    @InjectView(R.id.fab)
    FloatingActionButton floatingActionButton;
    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;
    @InjectView(R.id.activity_account_day_listview)
    ListView mActivityAccountYearListview;


    private SwipeBackActivityHelper swipeBackActivityHelper;
    private String dateStr;
    private Dao<AccountBean, Integer> accountBeanDao;
    private AdapterFragmentDayAccount adapterFragmentDayAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_day);
        ButterKnife.inject(this);
        swipeBackActivityHelper = new SwipeBackActivityHelper(this);
        swipeBackActivityHelper.onActivityCreate();
        dateStr = getIntent().getStringExtra("date");
        accountBeanDao = getDataBaseHelper().getAccountBeanDao();
        adapterFragmentDayAccount = new AdapterFragmentDayAccount(this);
        adapterFragmentDayAccount.setAccountBeanDao(accountBeanDao);
        adapterFragmentDayAccount.setMode(SwipeItemMangerImpl.Mode.Single);
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
        toolbar.setTitle(dateStr);
        mActivityAccountYearListview.setAdapter(adapterFragmentDayAccount);
        List<AccountTotalBean> incomeAccountTotalBeans = getDayIncomeAccountData();
        List<AccountTotalBean> expenseAccountTotalBeans = getDayExpenseAccountData();
        List<AccountTotalBean> totalBeans = new ArrayList<>();
        if (incomeAccountTotalBeans != null) {
            totalBeans.addAll(incomeAccountTotalBeans);
        }
        if (expenseAccountTotalBeans != null) {
            totalBeans.addAll(expenseAccountTotalBeans);
        }
        adapterFragmentDayAccount.setAccountTotalBeans(totalBeans);
    }

    @OnClick(R.id.fab)
    void goAddAccount() {
        launchActivity(ActivityNewAccount.class);
    }

    @OnItemClick(R.id.activity_account_day_listview)
    void itemClick(int position) {
        AccountTotalBean accountTotalBean = adapterFragmentDayAccount.getItem(position);
        String uuid = accountTotalBean.getUuid();
        if (!TextUtils.isEmpty(uuid)) {
            Bundle bundle = new Bundle();
            bundle.putString("uuid", uuid);
            launchActivity(ActivityNewAccount.class, bundle);
        }
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
    private List<AccountTotalBean> getDayIncomeAccountData() {
        try {
            String incomeSql = "";
            incomeSql = "select uuid,name, amount,strftime('%Y-%m-%d',datetime((setTime/1000),'unixepoch','localtime')) from table_account where  strftime('%Y-%m-%d',datetime((setTime/1000),'unixepoch','localtime'))='" + dateStr + "' and type=" + AccountBean.INCOME;
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
                    accountTotalBean.setsType(AccountBean.INCOME);
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

    /**
     * 获取金额汇总
     */
    private List<AccountTotalBean> getDayExpenseAccountData() {
        try {
            String incomeSql = "";
            incomeSql = "select uuid,name, amount,strftime('%Y-%m-%d',datetime((setTime/1000),'unixepoch','localtime')) from table_account where  strftime('%Y-%m-%d',datetime((setTime/1000),'unixepoch','localtime'))='" + dateStr + "' and type=" + AccountBean.EXPENSE;
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
                    accountTotalBean.setsType(AccountBean.EXPENSE);
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
}
