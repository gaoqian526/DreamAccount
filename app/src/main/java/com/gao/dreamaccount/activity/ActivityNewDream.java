package com.gao.dreamaccount.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.gao.dreamaccount.R;
import com.gao.dreamaccount.abs.AbsActivity;
import com.gao.dreamaccount.bean.AccountBean;
import com.gao.dreamaccount.bean.DreamBean;
import com.gao.dreamaccount.event.UpdateEvent;
import com.gao.dreamaccount.util.LogUtil;
import com.gao.dreamaccount.util.Utils;
import com.gao.dreamaccount.views.ldialogs.BaseDialog;
import com.gao.dreamaccount.views.ldialogs.CustomDialog;
import com.j256.ormlite.dao.Dao;
import com.materialedittext.MaterialEditText;

import java.sql.SQLException;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;

/**
 * Created by Gao on 2014/11/10.
 */
public class ActivityNewDream extends AbsActivity implements SwipeBackActivityBase {
    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;
    @InjectView(R.id.activity_new_dream_set_time)
    TextView timeTxt;
    @InjectView(R.id.activity_new_dream_title_input)
    MaterialEditText titleInut;
    @InjectView(R.id.activity_new_dream_description_input)
    MaterialEditText desInput;
    @InjectView(R.id.activity_new_dream_budget_input)
    MaterialEditText budgetInput;
    @InjectView(R.id.activity_new_dream_complete_btn)
    Button completeBtn;

    private long setTime = 0l;
    private Dao<DreamBean, Integer> dreamBeanDao;
    private Dao<AccountBean, Integer> accountBeanDao;
    private DreamBean dreamBean;

    private SwipeBackActivityHelper swipeBackActivityHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_dream);
        ButterKnife.inject(this);
        swipeBackActivityHelper = new SwipeBackActivityHelper(this);
        swipeBackActivityHelper.onActivityCreate();
        dreamBean = (DreamBean) getIntent().getSerializableExtra("dream");
        dreamBeanDao = dataBaseHelper.getDreamBeanDao();
        accountBeanDao = dataBaseHelper.getAccountBeanDao();
        if (dreamBean != null) {
            LogUtil.e("++++"+dreamBean.getUuid());
            toolbar.setTitle(dreamBean.getName());
            toolbar.inflateMenu(R.menu.menu_delete);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if (menuItem.getItemId() == R.id.menu_delete) {
                        showDeleteDialog();
                    } else {
                        if (checkData()) {
                            showCommitDialog();
                        } else {
                            showToast(getResources().getString(R.string.string_empty_hint));
                        }
                    }
                    return false;
                }
            });
            setTime = dreamBean.getSetTime();
            fillView(dreamBean);
        } else {
            toolbar.setTitle(R.string.string_new_dream);
            completeBtn.setVisibility(View.GONE);
            toolbar.inflateMenu(R.menu.menu_ok);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if (menuItem.getItemId() == R.id.menu_ok) {
                        if (checkData()) {
                            showCommitDialog();
                        } else {
                            showToast(getResources().getString(R.string.string_empty_hint));
                        }
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
    }

    private void fillView(DreamBean dreamBean) {
        timeTxt.setText(Utils.getDay(dreamBean.getSetTime()));
        titleInut.setText(dreamBean.getName());
        if (!TextUtils.isEmpty(dreamBean.getDes())) {
            desInput.setText(dreamBean.getDes());
        }
        completeBtn.setVisibility(View.VISIBLE);
        budgetInput.setText(dreamBean.getBudget() + "");
        if (dreamBean.getStatus() == DreamBean.STATUS_DONE) {
            titleInut.setEnabled(false);
            desInput.setEnabled(false);
            budgetInput.setEnabled(false);
            completeBtn.setBackgroundColor(getResources().getColor(R.color.blue_600));
            completeBtn.setText(R.string.string_done);
        } else {
            completeBtn.setBackgroundColor(getResources().getColor(R.color.color_light_red));
            completeBtn.setText(R.string.btn_done);
        }
    }

    @OnClick(R.id.activity_new_dream_complete_btn)
    void complete() {
        if (dreamBean != null && dreamBean.getStatus() == DreamBean.STATUS_DONE) {
            return;
        }
        dreamBean.setStatus(DreamBean.STATUS_DONE);
        gatherAccountData();
        try {
            dreamBeanDao.createOrUpdate(dreamBean);
            EventBus.getDefault().post(new UpdateEvent());
            finish();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.activity_new_dream_set_time)
    void setTime() {
        if (dreamBean != null && dreamBean.getStatus() == DreamBean.STATUS_DONE) {
            return;
        }
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                timeTxt.setText(year + "-" + (month + 1) + "-" + day);
                setTime = Utils.getTimeTemp(timeTxt.getText().toString(), null);
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
        return setTime != 0 && !TextUtils.isEmpty(titleInut.getText().toString())
                && !TextUtils.isEmpty(budgetInput.getText().toString());
    }

    private void gatherData() {
        DreamBean tdreamBean = new DreamBean();
        String name = titleInut.getText().toString();
        String des = desInput.getText().toString();
        double buget = Double.parseDouble(budgetInput.getText().toString());
        if (dreamBean != null) {
            LogUtil.e("==="+dreamBean.getUuid());
            tdreamBean.setUuid(dreamBean.getUuid());
        } else {
            String uuid = Utils.getUUID();
            tdreamBean.setUuid(uuid);
        }
        LogUtil.e("***"+tdreamBean.getUuid());
        tdreamBean.setName(name);
        tdreamBean.setDes(des);
        tdreamBean.setBudget(buget);
        tdreamBean.setSetTime(setTime);
        tdreamBean.setStatus(DreamBean.STATUS_UNDO);
        tdreamBean.setInsertTime(System.currentTimeMillis());
        try {
            dreamBeanDao.createOrUpdate(tdreamBean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取填写的数据
     */
    private void gatherAccountData() {
        AccountBean accountBean = new AccountBean();
        accountBean.setUuid(Utils.getUUID());
        accountBean.setSetTime(System.currentTimeMillis());
        accountBean.setInsetTime(System.currentTimeMillis());
        accountBean.setType(AccountBean.EXPENSE);
        accountBean.setAmount(dreamBean.getBudget());
        accountBean.setName(dreamBean.getName());
        accountBean.setDes(dreamBean.getDes());
        accountBean.setTimeStr(Utils.getDay(System.currentTimeMillis()));
        try {
            accountBeanDao.createOrUpdate(accountBean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteDream() {
        try {
            dreamBeanDao.delete(dreamBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDeleteDialog() {
        CustomDialog.Builder builder = new CustomDialog.Builder(ActivityNewDream.this, getResources().getString(R.string.string_alert), getResources().getString(R.string.string_ok));
        builder.content(getResources().getString(R.string.string_dream_delete));
        builder.negativeText(getResources().getString(R.string.string_cancel));
        builder.darkTheme(true);
        builder.titleAlignment(BaseDialog.Alignment.LEFT); // Use either Alignment.LEFT, Alignment.CENTER or Alignment.RIGHT
        builder.titleColorRes(R.color.black_26); // int res, or int colorRes parameter versions available as well.
        builder.contentColorRes(R.color.black_54); // int res, or int colorRes parameter versions available as well.
        builder.positiveColorRes(R.color.blue_600); // int res, or int colorRes parameter versions available as well.
        builder.negativeColorRes(R.color.color_light_red); // int res, or int colorRes parameter versions available as well.
        final CustomDialog customDialog = builder.build();
        customDialog.show();
        customDialog.setClickListener(new CustomDialog.ClickListener() {
            @Override
            public void onConfirmClick() {
                deleteDream();
                customDialog.dismiss();
                EventBus.getDefault().post(new UpdateEvent());
                finish();
            }

            @Override
            public void onCancelClick() {
                customDialog.dismiss();
            }
        });
    }

    private void showCommitDialog() {
        CustomDialog.Builder builder = new CustomDialog.Builder(ActivityNewDream.this, getResources().getString(R.string.string_alert), getResources().getString(R.string.string_ok));
        builder.content(getResources().getString(R.string.string_dream_default));
        builder.negativeText(getResources().getString(R.string.string_cancel));
        builder.darkTheme(true);
        builder.contentColorRes(R.color.black_54);
        builder.titleAlignment(BaseDialog.Alignment.LEFT); // Use either Alignment.LEFT, Alignment.CENTER or Alignment.RIGHT
        builder.titleColorRes(R.color.black_26); // int res, or int colorRes parameter versions available as well.
        builder.positiveColorRes(R.color.blue_600); // int res, or int colorRes parameter versions available as well.
        builder.negativeColorRes(R.color.color_light_red); // int res, or int colorRes parameter versions available as well.
        final CustomDialog customDialog = builder.build();
        customDialog.show();
        customDialog.setClickListener(new CustomDialog.ClickListener() {
            @Override
            public void onConfirmClick() {
                gatherData();
                customDialog.dismiss();
                EventBus.getDefault().post(new UpdateEvent());
                finish();
            }

            @Override
            public void onCancelClick() {
                customDialog.dismiss();
            }
        });
    }
}
