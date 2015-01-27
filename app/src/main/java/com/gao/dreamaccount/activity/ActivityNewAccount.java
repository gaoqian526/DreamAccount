package com.gao.dreamaccount.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.gao.dreamaccount.R;
import com.gao.dreamaccount.abs.AbsActivity;
import com.gao.dreamaccount.bean.AccountBean;
import com.gao.dreamaccount.event.UpdateEvent;
import com.gao.dreamaccount.util.LogUtil;
import com.gao.dreamaccount.util.Utils;
import com.gao.dreamaccount.views.ldialogs.BaseDialog;
import com.gao.dreamaccount.views.ldialogs.CustomDialog;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.materialedittext.MaterialAutoCompleteTextView;
import com.materialedittext.MaterialEditText;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;

/**
 * Created by Gao on 2014/11/13.
 */
public class ActivityNewAccount extends AbsActivity implements SwipeBackActivityBase {
    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;
    @InjectView(R.id.activity_new_account_title_input)
    MaterialAutoCompleteTextView nameInput;
    @InjectView(R.id.activity_new_account_description_input)
    MaterialEditText desInput;
    @InjectView(R.id.activity_new_account_budget_input)
    MaterialEditText accountInput;
    @InjectView(R.id.activity_new_account_set_time)
    TextView timeTxt;
    @InjectView(R.id.activity_new_account_type_expense)
    RadioButton expenseRadio;
    @InjectView(R.id.activity_new_account_type_income)
    RadioButton incomeRadio;
    @InjectView(R.id.activity_new_account_selet_hint)
    TextView selectHintTxt;
    @InjectView(R.id.activity_new_account_name_1)
    TextView nameTxt1;
    @InjectView(R.id.activity_new_account_name_2)
    TextView nameTxt2;
    @InjectView(R.id.activity_new_account_name_3)
    TextView nameTxt3;
    @InjectView(R.id.activity_new_account_name_lay_1)
    View nameLay1;
    @InjectView(R.id.activity_new_account_name_lay_2)
    View nameLay2;
    @InjectView(R.id.activity_new_account_name_lay_3)
    View nameLay3;

    private int type = AccountBean.INCOME;//收入还是支出
    private long setTime;
    private Dao<AccountBean, Integer> accountBeanDao;
    private String[] nameArray;
    private String[] nameArray1;

    private String uuid;
    private AccountBean accountBean;

    private CustomDialog customDialog;

    private SwipeBackActivityHelper swipeBackActivityHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);
        ButterKnife.inject(this);
        swipeBackActivityHelper = new SwipeBackActivityHelper(this);
        swipeBackActivityHelper.onActivityCreate();
        uuid = getIntent().getStringExtra("uuid");
        toolbar.setTitle(R.string.string_new_bill);
        if (TextUtils.isEmpty(uuid)) {
            toolbar.inflateMenu(R.menu.menu_ok);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if (menuItem.getItemId() == R.id.menu_ok) {
                        if (checkData()) {
                            if (type == AccountBean.EXPENSE) {
                                showCommitDialog(getResources().getString(R.string.string_new_expence_hint));
                            } else {
                                showCommitDialog(getResources().getString(R.string.string_new_income_hint));
                            }
                        } else {
                            showToast(getResources().getString(R.string.string_empty_hint));
                        }
                    }
                    return false;
                }
            });
        } else {
            accountBean = selectData(uuid);
            toolbar.inflateMenu(R.menu.menu_delete);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if (menuItem.getItemId() == R.id.menu_ok) {
                        if (checkData()) {
                            if (type == AccountBean.EXPENSE) {
                                showCommitDialog(getResources().getString(R.string.string_new_expence_hint));
                            } else {
                                showCommitDialog(getResources().getString(R.string.string_new_income_hint));
                            }
                        } else {
                            showToast(getResources().getString(R.string.string_empty_hint));
                        }
                    } else if (menuItem.getItemId() == R.id.menu_delete) {
                        showDeleteDialog(accountBean);
                    }
                    return false;
                }
            });
        }
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        accountBeanDao = dataBaseHelper.getAccountBeanDao();
        nameInput.setThreshold(1);
        getNameArray();
        getMostUsedNameArray();
        if (!TextUtils.isEmpty(uuid)) {
            accountBean = selectData(uuid);
            nameInput.setText(accountBean.getName());
            if (!TextUtils.isEmpty(accountBean.getDes())) {
                desInput.setText(accountBean.getDes());
            }
            String timeT = Utils.getDay(accountBean.getSetTime());
            timeTxt.setText(timeT);
            if (accountBean.getType() == AccountBean.EXPENSE) {
                expenseRadio.setChecked(true);
            } else {
                incomeRadio.setChecked(true);
            }
            type = accountBean.getType();
            accountInput.setText(accountBean.getAmount() + "");
            setTime = accountBean.getSetTime();
        } else {
            setTime = System.currentTimeMillis();
            String timeT = Utils.getDay(System.currentTimeMillis());
            timeTxt.setText(timeT);
        }
    }


    @OnCheckedChanged(R.id.activity_new_account_type_expense)
    void onExpenseChecked(boolean checked) {
        if (checked) {
            type = AccountBean.EXPENSE;
            getNameArray();
            getMostUsedNameArray();
        }
    }

    @OnCheckedChanged(R.id.activity_new_account_type_income)
    void onIncomeChecked(boolean checked) {
        if (checked) {
            type = AccountBean.INCOME;
            getNameArray();
            getMostUsedNameArray();
        }
    }

    @OnClick(R.id.activity_new_account_name_1)
    void quickFillName1() {
        nameInput.setText(nameArray1[0]);
        nameInput.setSelection(nameArray1[0].length());
    }

    @OnClick(R.id.activity_new_account_name_2)
    void quickFillName2() {
        nameInput.setText(nameArray1[1]);
        nameInput.setSelection(nameArray1[1].length());
    }

    @OnClick(R.id.activity_new_account_name_3)
    void quickFillName3() {
        nameInput.setText(nameArray1[2]);
        nameInput.setSelection(nameArray1[2].length());
    }

    @OnClick(R.id.activity_new_account_set_time)
    void setTime() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                String time = year + "-" + (month + 1) + "-" + day;
                setTime = Utils.getTimeTemp(time, null);
                timeTxt.setText(Utils.getDay(setTime));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
        datePickerDialog.setVibrate(false);
        datePickerDialog.setYearRange(1985, 2028);
        datePickerDialog.setCloseOnSingleTapDay(false);
        datePickerDialog.show(getSupportFragmentManager(), "datepicker");
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

    private boolean checkData() {
        return setTime != 0 && !TextUtils.isEmpty(nameInput.getText().toString())
                && !TextUtils.isEmpty(accountInput.getText().toString());
    }

    /**
     * 获取填写的数据
     */
    private void gatherData() {
        AccountBean accountBean = new AccountBean();
        if (!TextUtils.isEmpty(uuid)) {
            accountBean.setUuid(uuid);
        } else {
            accountBean.setUuid(Utils.getUUID());
        }
        accountBean.setSetTime(setTime);
        accountBean.setInsetTime(System.currentTimeMillis());
        accountBean.setType(type);
        accountBean.setAmount(Double.parseDouble(accountInput.getText().toString()));
        accountBean.setName(nameInput.getText().toString());
        accountBean.setDes(desInput.getText().toString());
        accountBean.setTimeStr(timeTxt.getText().toString());
        try {
            accountBeanDao.createOrUpdate(accountBean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据uuid获取值
     */
    private AccountBean selectData(String uuid) {
        try {
            List<AccountBean> accountBeans = accountBeanDao.queryForEq("uuid", uuid);
            if (accountBeans != null && accountBeans.size() > 0) {
                return accountBeans.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据uuid删除
     */
    private void deleteData(AccountBean accountBean) {
        try {
            accountBeanDao.delete(accountBean);
        } catch (Exception e) {
        }
    }

    /**
     * 获取名称字符数组
     */
    private void getNameArray() {
        try {
            QueryBuilder<AccountBean, Integer> queryBuilder = accountBeanDao.queryBuilder();
            queryBuilder.where().eq("type", type);
            queryBuilder.selectRaw("name");
            GenericRawResults<String[]> rawResults = accountBeanDao.queryRaw(queryBuilder.prepareStatementString());
            String[] values = rawResults.getFirstResult();
            if (values != null && values.length > 0) {
                nameArray = values;
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, nameArray);
                nameInput.setAdapter(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
            nameArray = null;
        }
    }

    /**
     * 获取名称使用最多的3个
     */
    private void getMostUsedNameArray() {
        nameLay1.setVisibility(View.GONE);
        nameLay2.setVisibility(View.GONE);
        nameLay3.setVisibility(View.GONE);
        try {
            String incomeSql = "select name,count(name) from table_account where type=" + type + " group by name order by count(name) desc limit 3";
            GenericRawResults<String[]> rawResults = accountBeanDao.queryRaw(incomeSql);
            List<String[]> values = rawResults.getResults();
            if (values != null && values.size() > 0) {
                selectHintTxt.setVisibility(View.VISIBLE);
                nameArray1 = new String[values.size()];
                LogUtil.e(values.size() + "");
                for (int i = 0; i < values.size(); i++) {
                    nameArray1[i] = values.get(i)[0];
                }
                if (nameArray1.length == 2) {
                    nameLay1.setVisibility(View.VISIBLE);
                    nameLay2.setVisibility(View.VISIBLE);
                    nameTxt1.setText(nameArray1[0]);
                    nameTxt2.setText(nameArray1[1]);
                } else if (nameArray1.length == 1) {
                    nameLay1.setVisibility(View.VISIBLE);
                    nameTxt1.setText(nameArray1[0]);
                } else {
                    nameLay1.setVisibility(View.VISIBLE);
                    nameLay2.setVisibility(View.VISIBLE);
                    nameLay3.setVisibility(View.VISIBLE);
                    nameTxt1.setText(nameArray1[0]);
                    nameTxt2.setText(nameArray1[1]);
                    nameTxt3.setText(nameArray1[2]);
                }
            } else {
                selectHintTxt.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 再记一笔重置数据
     */
    private void resetDate() {
        nameInput.setText("");
        accountInput.setText("");
        desInput.setText("");
        setTime = 0l;
        type = AccountBean.INCOME;
        expenseRadio.setChecked(false);
        incomeRadio.setChecked(true);
        timeTxt.setText(Utils.getDay(System.currentTimeMillis()));
    }

    private void showCommitDialog(String content) {
        CustomDialog.Builder builder = new CustomDialog.Builder(this, getResources().getString(R.string.string_alert), getResources().getString(R.string.string_continue));
        builder.content(content);
        builder.negativeText(getResources().getString(R.string.string_ok));
        builder.darkTheme(true);
        builder.titleAlignment(BaseDialog.Alignment.LEFT); // Use either Alignment.LEFT, Alignment.CENTER or Alignment.RIGHT
        builder.titleColorRes(R.color.black_26); // int res, or int colorRes parameter versions available as well.
        builder.contentColorRes(R.color.black_54); // int res, or int colorRes parameter versions available as well.
        builder.negativeColorRes(R.color.blue_600); // int res, or int colorRes parameter versions available as well.
        builder.negativeColorRes(R.color.color_light_red); // int res, or int colorRes parameter versions available as well.
        builder.buttonAlignment(BaseDialog.Alignment.RIGHT);
        customDialog = builder.build();
        customDialog.show();
        customDialog.setClickListener(new CustomDialog.ClickListener() {
            @Override
            public void onConfirmClick() {
                gatherData();
                EventBus.getDefault().post(new UpdateEvent());
                resetDate();
                customDialog.dismiss();
            }

            @Override
            public void onCancelClick() {
                gatherData();
                customDialog.dismiss();
                EventBus.getDefault().post(new UpdateEvent());
                finish();
            }
        });
    }

    private void showDeleteDialog(final AccountBean accountBean) {
        CustomDialog.Builder builder = new CustomDialog.Builder(this, getResources().getString(R.string.string_alert), getResources().getString(R.string.string_ok));
        builder.content("确定要删除吗");
        builder.negativeText(getResources().getString(R.string.string_cancel));
        builder.darkTheme(true);
        builder.titleColorRes(R.color.black_26);
        builder.titleAlignment(BaseDialog.Alignment.LEFT); // Use either Alignment.LEFT, Alignment.CENTER or Alignment.RIGHT
        builder.contentColorRes(R.color.black_54); // int res, or int colorRes parameter versions available as well.
        builder.negativeColorRes(R.color.blue_600); // int res, or int colorRes parameter versions available as well.
        builder.positiveColorRes(R.color.color_light_red);
        builder.buttonAlignment(BaseDialog.Alignment.RIGHT);
        final CustomDialog customDialog = builder.build();
        customDialog.setClickListener(new CustomDialog.ClickListener() {
            @Override
            public void onConfirmClick() {//确定
                deleteData(accountBean);
                EventBus.getDefault().post(new UpdateEvent());
                customDialog.dismiss();
                finish();
            }

            @Override
            public void onCancelClick() {//取消
                customDialog.dismiss();
            }
        });
        customDialog.show();
    }
}
