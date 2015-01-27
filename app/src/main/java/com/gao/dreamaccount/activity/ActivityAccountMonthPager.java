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
import com.gao.dreamaccount.util.Constant;
import com.gao.dreamaccount.util.Utils;
import com.gao.dreamaccount.views.RoundedLetterView;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
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
public class ActivityAccountMonthPager extends AbsActivity implements SwipeBackActivityBase {

    @InjectView(R.id.fab)
    FloatingActionButton floatingActionButton;
    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;
    @InjectView(R.id.activity_account_year_total)
    RoundedLetterView mActivityAccountYearTotal;
    @InjectView(R.id.activity_account_year_listview)
    ListView mActivityAccountYearListview;


    private SwipeBackActivityHelper swipeBackActivityHelper;
    private String dateStr;
    private Dao<AccountBean, Integer> accountBeanDao;
    private AdapterFragmentYearAccount adapterFragmentAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_year);
        ButterKnife.inject(this);
        accountBeanDao = getDataBaseHelper().getAccountBeanDao();
        swipeBackActivityHelper = new SwipeBackActivityHelper(this);
        swipeBackActivityHelper.onActivityCreate();
        dateStr = getIntent().getStringExtra("date");
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
        toolbar.setTitle(dateStr);
        View footerView = LayoutInflater.from(this).inflate(R.layout.view_footer_view, null);
        mActivityAccountYearListview.addFooterView(footerView);
        mActivityAccountYearListview.setAdapter(adapterFragmentAccount);
        mActivityAccountYearTotal.setTitleText("日");
    }

    @OnClick(R.id.fab)
    void goAddDream() {
        launchActivity(ActivityNewAccount.class);
    }

    @OnItemClick(R.id.activity_account_year_listview)
    void onItemClick(int position) {
        if (position >= adapterFragmentAccount.getCount()) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("date", adapterFragmentAccount.getItem(position).getDateStr());
        launchActivity(ActivityAccountDayPager.class, bundle);
    }

    public void attchListView(ListView listView) {
        if (floatingActionButton != null) {
            floatingActionButton.attachToListView(listView);
            floatingActionButton.show();
        }
    }

    public void onEvent(UpdateEvent event) {
        getData();
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
        long time = Utils.getTimeTemp(dateStr, "yyyy-MM");
        int mdc = Utils.countDay(new Date(time));
        List<AccountTotalBean> accountTotalBeans = new ArrayList<AccountTotalBean>();
        for (int i = 1; i < mdc + 1; i++) {
            double incomeAmount = getMonthIncomeAccountData(i);
            double expenseAmount = getMonthExpenseAccountData(i);
            if (incomeAmount == 0 && expenseAmount == 0) {
                continue;
            } else {
                AccountTotalBean accountTotalBean = new AccountTotalBean();
                String dateTme = dateStr + "-" + Utils.formateInteger(i);
                accountTotalBean.setIncomeAmount(incomeAmount);
                accountTotalBean.setExpenseAmount(expenseAmount);
                accountTotalBean.setTitle(Constant.STRING_DAY[i - 1]);
                accountTotalBean.setDateStr(dateTme);
                accountTotalBean.setColorId(R.color.blue_300);
                accountTotalBeans.add(accountTotalBean);
            }
        }
        adapterFragmentAccount.setAccountTotalBeans(accountTotalBeans);
    }

    /**
     * 获取金额汇总
     *
     * @param day 天
     */
    private double getMonthExpenseAccountData(int day) {
        try {
            String dateTme = dateStr + "-" + Utils.formateInteger(day);
            String incomeSql = "";
            incomeSql = "select uuid,name, sum(amount),strftime('%Y-%m-%d',datetime((setTime/1000),'unixepoch','localtime')) from table_account where  strftime('%Y-%m-%d',datetime((setTime/1000),'unixepoch','localtime'))='" + dateTme
                    + "' and type=" + AccountBean.EXPENSE + " group by strftime('%Y-%m-%d',datetime((setTime/1000),'unixepoch','localtime'))";
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
     *
     * @param day 天
     */
    private double getMonthIncomeAccountData(int day) {
        try {
            String dateTme = dateStr + "-" + Utils.formateInteger(day);
            String incomeSql = "";
            incomeSql = "select uuid,name,sum(amount),strftime('%Y-%m-%d',datetime((setTime/1000),'unixepoch','localtime')) from table_account where  strftime('%Y-%m-%d',datetime((setTime/1000),'unixepoch','localtime'))='" + dateTme
                    + "' and type=" + AccountBean.INCOME + " group by strftime('%Y-%m-%d',datetime((setTime/1000),'unixepoch','localtime'))";
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
