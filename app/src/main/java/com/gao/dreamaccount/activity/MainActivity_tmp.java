package com.gao.dreamaccount.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.colorpicker.ColorPickerDialog;
import com.android.colorpicker.ColorPickerSwatch;
import com.cocosw.bottomsheet.BottomSheet;
import com.gao.dreamaccount.R;
import com.gao.dreamaccount.abs.AbsActivity;
import com.gao.dreamaccount.bean.AccountBean;
import com.gao.dreamaccount.bean.AccountMainBean;
import com.gao.dreamaccount.bean.DreamBean;
import com.gao.dreamaccount.event.UpdateEvent;
import com.gao.dreamaccount.receiver.AlarmReceiver;
import com.gao.dreamaccount.util.Constant;
import com.gao.dreamaccount.util.MD5Util;
import com.gao.dreamaccount.util.PreferencesUtils;
import com.gao.dreamaccount.util.Utils;
import com.gao.dreamaccount.views.ldialogs.BaseDialog;
import com.gao.dreamaccount.views.ldialogs.CustomDialog;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.materialedittext.MaterialEditText;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by Gao on 2014/11/25.
 */
public class MainActivity_tmp extends AbsActivity {
    @InjectView(R.id.activity_main_dream_name)
    TextView nameTxt;
    @InjectView(R.id.activity_main_dream_status)
    TextView statusTxt;
    @InjectView(R.id.activity_main_dream_des)
    TextView desTxt;
    @InjectView(R.id.activity_main_dream_set_date)
    TextView dateTxt;
    @InjectView(R.id.activity_main_dream_budget)
    TextView budgetTxt;
    @InjectView(R.id.activity_main_dream_more)
    TextView moreTxt;

    @InjectView(R.id.activity_main_account_income)
    TextView incomeTxt;
    @InjectView(R.id.activity_main_account_expense)
    TextView expenseTxt;
    @InjectView(R.id.activity_main_account_balance)
    TextView balanceTxt;
    @InjectView(R.id.activity_main_account_look)
    View lookMore;

    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;

    private Dao<DreamBean, Integer> dreamBeanDao;
    private Dao<AccountBean, Integer> accountBeanDao;
    private DreamBean dreamBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        ButterKnife.inject(this);
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.update(this);
        FeedbackAgent agent = new FeedbackAgent(this);
        agent.sync();
        initValue();
        if (isSetPassword()) {
            showCommitDialog();
        } else {
            getData();
        }
        alertNotifyTiming();
    }


    private void initValue() {
        final ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
        colorPickerDialog.initialize(R.string.app_name, new int[]{Color.CYAN, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.RED, Color.YELLOW}, Color.YELLOW, 3, 2);
        colorPickerDialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {

            @Override
            public void onColorSelected(int color) {
                toolbar.setBackgroundColor(color);
                Toast.makeText(MainActivity_tmp.this, "selectedColor : " + color, Toast.LENGTH_SHORT).show();
            }
        });
        toolbar.setTitle(R.string.app_name);
        toolbar.inflateMenu(R.menu.main);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_settings) {
                    launchActivity(ActivitySetting.class);
//                    colorPickerDialog.show(getSupportFragmentManager(), "");
                }
                return false;
            }
        });
        dreamBeanDao = getDataBaseHelper().getDreamBeanDao();
        accountBeanDao = getDataBaseHelper().getAccountBeanDao();
    }

    public void onEvent(UpdateEvent event) {
        getData();
    }

    private void getData() {
        getNewsDream();
        final AccountMainBean tStatisticsBean = new AccountMainBean();
        tStatisticsBean.setType(AccountMainBean.TOTAL);
        double ti = getSumCount(AccountBean.INCOME);
        double te = getSumCount(AccountBean.EXPENSE);
        double tb = ti - te;
        tStatisticsBean.setBalance(tb);
        tStatisticsBean.setExpense(te);
        tStatisticsBean.setIncome(ti);
        if (tStatisticsBean.getBalance() > 0) {
            balanceTxt.setTextColor(getResources().getColor(R.color.blue_600));
        } else {
            balanceTxt.setTextColor(getResources().getColor(R.color.color_light_red));
        }
        balanceTxt.setText(Utils.formateDouble(tStatisticsBean.getBalance()));
        expenseTxt.setText(Utils.formateDouble(tStatisticsBean.getExpense()));
        incomeTxt.setText(Utils.formateDouble(tStatisticsBean.getIncome()));
        lookMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivity(ActivityAccountYearPager.class);
            }
        });
    }


    @OnClick(R.id.activity_main_dream_more)
    void goDreamPager() {
        launchActivity(ActivityDreamPager.class);
    }

    @OnClick(R.id.fab)
    void showAddDialog() {
        new BottomSheet.Builder(this).title("新建").sheet(R.menu.menu_main_dialog).listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case R.id.new_dream:
                        launchActivity(ActivityNewDream.class);
                        break;
                    case R.id.new_account:
                        launchActivity(ActivityNewAccount.class);
                        break;
                }
            }
        }).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 填充视图
     */
    private void fillView() {
        nameTxt.setText(dreamBean.getName());
        dateTxt.setText(Utils.getDay(dreamBean.getSetTime()));
        budgetTxt.setText(Utils.formateDouble(dreamBean.getBudget()));
        if (getBalance() > dreamBean.getBudget()) {
            statusTxt.setTextColor(getResources().getColor(R.color.blue_500));
            statusTxt.setText("可完成");
        } else {
            statusTxt.setTextColor(getResources().getColor(R.color.color_light_red));
            String status = Utils.formateDouble(dreamBean.getBudget() - getBalance());
            statusTxt.setText(status);
        }
        if (!TextUtils.isEmpty(dreamBean.getDes())) {
            desTxt.setText(dreamBean.getDes());
        }
    }

    private void clearView() {
        nameTxt.setText("");
    }


    /**
     * 获取最新梦想 1个
     */
    private void getNewsDream() {
        QueryBuilder<DreamBean, Integer> queryBuilder = dreamBeanDao.queryBuilder();
        try {
            queryBuilder.where().eq("status", DreamBean.STATUS_UNDO);
            queryBuilder.orderBy("insertTime", false);
            queryBuilder.prepareStatementString();
            List<DreamBean> dreamBeans = queryBuilder.query();
            dreamBeanDao.queryRaw(queryBuilder.prepareStatementString());
            if (dreamBeans != null && dreamBeans.size() > 0) {
                dreamBean = dreamBeans.get(0);
                fillView();
            } else {

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    private void showCommitDialog() {
        CustomDialog.Builder builder = new CustomDialog.Builder(this, "提示", "确定");
        builder.darkTheme(true);
        builder.titleAlignment(BaseDialog.Alignment.CENTER); // Use either Alignment.LEFT, Alignment.CENTER or Alignment.RIGHT
        builder.contentColorRes(R.color.black); // int res, or int colorRes parameter versions available as well.
        builder.positiveColorRes(R.color.blue_600);
        builder.buttonAlignment(BaseDialog.Alignment.RIGHT);
        final CustomDialog customDialog = builder.build();
        View view = LayoutInflater.from(this).inflate(R.layout.view_dialog_input, null);
        final MaterialEditText pwdInput = (MaterialEditText) view.findViewById(R.id.view_dialog_input);
        customDialog.setCustomView(view);
        customDialog.setCancelable(false);
        customDialog.show();
        customDialog.setClickListener(new CustomDialog.ClickListener() {
            @Override
            public void onConfirmClick() {
                String pwd = pwdInput.getText().toString();
                if (!TextUtils.isEmpty(pwd)) {
                    pwd = MD5Util.getMD5(pwd);
                    if (checkPwd(pwd)) {
                        customDialog.dismiss();
                        getData();
                    } else {
                        Toast.makeText(MainActivity_tmp.this, "密码错误", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity_tmp.this, "请输入密码", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelClick() {
            }
        });
    }

    private boolean checkPwd(String pwd) {
        PreferencesUtils preferencesUtils = new PreferencesUtils(this);
        String tmpPwd = preferencesUtils.getString(Constant.KEY_PWD, null);
        return tmpPwd.equals(pwd);
    }

    private boolean isSetPassword() {
        PreferencesUtils preferencesUtils = new PreferencesUtils(this);
        boolean issp = preferencesUtils.getBoolean(Constant.KEY_IS_SET_PWD, false);
        return issp;
    }


    /**
     * 定时提醒
     */
    private void alertNotifyTiming() {
        long day = 24 * 60 * 60 * 1000L;
        long firstTime = System.currentTimeMillis(); // 开机之后到现在的运行时间(包括睡眠时间)
        long systemTime = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        // 这里时区需要设置一下，不然会有8个小时的时间差
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.HOUR_OF_DAY, 19);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        // 选择的定时时间
        long selectTime = calendar.getTimeInMillis();
        // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
        if (systemTime > selectTime) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            selectTime = calendar.getTimeInMillis();
        }
        // 计算现在时间到设定时间的时间差
        long time = selectTime - systemTime;
        firstTime += time;
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.setAction("com.dreamaccount.gao");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, firstTime, day, pendingIntent);
    }

}
