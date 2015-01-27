package com.gao.dreamaccount.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.gao.dreamaccount.R;
import com.gao.dreamaccount.abs.AbsActivity;
import com.gao.dreamaccount.bean.AccountBean;
import com.gao.dreamaccount.bean.DreamBean;
import com.gao.dreamaccount.event.UpdateEvent;
import com.gao.dreamaccount.util.Constant;
import com.gao.dreamaccount.util.DataBaseUtil;
import com.gao.dreamaccount.util.FileUtil;
import com.gao.dreamaccount.util.MD5Util;
import com.gao.dreamaccount.util.PreferencesUtils;
import com.gao.dreamaccount.util.Utils;
import com.gao.dreamaccount.views.ldialogs.BaseDialog;
import com.gao.dreamaccount.views.ldialogs.CustomDialog;
import com.j256.ormlite.dao.Dao;
import com.materialedittext.MaterialEditText;
import com.umeng.fb.FeedbackAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import lecho.lib.filechooser.FilechooserActivity;
import lecho.lib.filechooser.ItemType;
import lecho.lib.filechooser.SelectionMode;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;

/**
 * Created by Gao on 2014/11/18.
 */
public class ActivitySetting extends AbsActivity implements SwipeBackActivityBase {
    private static final int CHOSE_FILE = 1;//选择文件
    private static final String FILE_NAME = "梦想本";//文件名
    private int importType = 0;//导入类型 0目标 1财务

    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;

    private SwipeBackActivityHelper swipeBackActivityHelper;

    private Dao<AccountBean, Integer> accountBeanDao;
    private Dao<DreamBean, Integer> dreamBeanDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.inject(this);
        swipeBackActivityHelper = new SwipeBackActivityHelper(this);
        swipeBackActivityHelper.onActivityCreate();
        toolbar.setTitle(R.string.string_setting);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initValue();
    }

    private void initValue() {
        accountBeanDao = getDataBaseHelper().getAccountBeanDao();
        dreamBeanDao = getDataBaseHelper().getDreamBeanDao();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOSE_FILE) {
            if (resultCode == Activity.RESULT_OK) {
                ArrayList<String> paths = data.getStringArrayListExtra(FilechooserActivity.BUNDLE_SELECTED_PATHS);
                StringBuilder sb = new StringBuilder();
                for (String path : paths) {
                    sb.append(path);
                }
                if (sb.toString().contains("目标")) {
                    importType = 0;
                } else {
                    importType = 1;
                }
                String json = FileUtil.readFile(new File(sb.toString()));
                importData(importType, json);
            }
        }
    }

    @OnClick(R.id.activity_setting_export)
    void export() {
        showExportBottomSheet();
    }

    @OnClick(R.id.activity_setting_import)
    void importFile() {
        showImportBottomSheet();
    }

    @OnClick(R.id.activity_setting_setpossword)
    void setPassword() {
        showModifyPwdDialog();
    }

    @OnClick(R.id.activity_setting_about)
    void about() {
        showAboutDialog("邮  箱：gq-526@163.com\r\nQQ群：273805923");
    }

    @OnClick(R.id.activity_setting_feedback)
    void feedBack() {
        FeedbackAgent agent = new FeedbackAgent(this);
        agent.startFeedbackActivity();
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
     * 打开文件夹
     */
    private void openFile() {
        Intent intent = new Intent(this, FilechooserActivity.class);
        intent.putExtra(FilechooserActivity.BUNDLE_ITEM_TYPE, ItemType.FILE);
        intent.putExtra(FilechooserActivity.BUNDLE_SELECTION_MODE, SelectionMode.SINGLE_ITEM);
        startActivityForResult(intent, CHOSE_FILE);
    }

    /**
     * 导出数据
     *
     * @param type 导出目标0 导出财务1
     */
    private void exportData(int type) {
        String dataStr = "";
        String houzhui = "";
        try {
            if (type == 0) {
                List<DreamBean> dreamBeans = dreamBeanDao.queryForAll();
                dataStr = DataBaseUtil.exportDreamData(dreamBeans);
                houzhui = "-目标.json";
            } else {
                List<AccountBean> accountBeans = accountBeanDao.queryForAll();
                dataStr = DataBaseUtil.exportAccountData(accountBeans);
                houzhui = "-财务.json";
            }
        } catch (Exception e) {
            e.printStackTrace();
            dataStr = null;
            houzhui = null;
        }
        String path = Utils.getFilePath();
        try {
            File file = new File(path, FILE_NAME + houzhui);
            FileOutputStream outStream = new FileOutputStream(file, true);
            outStream.write(dataStr.toString().getBytes("utf-8"));
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 导入数据
     *
     * @param type 导入目标0 导入财务1
     */
    private void importData(int type, String json) {
        try {
            if (type == 0) {
                final List<DreamBean> dreamBeans = DataBaseUtil.importDreamData(json);
                dreamBeanDao.callBatchTasks(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        for (DreamBean dreamBean : dreamBeans) {
                            dreamBeanDao.createIfNotExists(dreamBean);
                        }
                        return null;
                    }
                });
            } else {
                final List<AccountBean> accountBeans = DataBaseUtil.importAccountData(json);
                accountBeanDao.callBatchTasks(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        for (AccountBean accountBean : accountBeans) {
                            accountBeanDao.createIfNotExists(accountBean);
                        }
                        return null;
                    }
                });
            }
            EventBus.getDefault().post(new UpdateEvent());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showExportBottomSheet() {
        new BottomSheet.Builder(this).title(R.string.string_setting_export).sheet(R.menu.menu_setting_dialog).listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case R.id.setting_dream:
                        exportData(0);
                        break;
                    case R.id.setting_account:
                        exportData(1);
                        break;
                }
            }
        }).show();
    }

    private void showImportBottomSheet() {
        new BottomSheet.Builder(this).title(R.string.string_setting_import).sheet(R.menu.menu_setting_dialog).listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case R.id.setting_dream:
                        importType = 0;
                        openFile();
                        break;
                    case R.id.setting_account:
                        importType = 1;
                        openFile();
                        break;
                }
            }
        }).show();
    }

    private void showModifyPwdDialog() {
        CustomDialog.Builder builder = new CustomDialog.Builder(this, getResources().getString(R.string.string_alert), getResources().getString(R.string.string_ok));
        builder.negativeText(getResources().getString(R.string.string_cancel));
        builder.darkTheme(true);
        builder.titleAlignment(BaseDialog.Alignment.CENTER); // Use either Alignment.LEFT, Alignment.CENTER or Alignment.RIGHT
        builder.contentColorRes(R.color.black); // int res, or int colorRes parameter versions available as well.
        builder.positiveColorRes(R.color.blue_600);
        builder.buttonAlignment(BaseDialog.Alignment.RIGHT);
        final CustomDialog customDialog = builder.build();
        View view = LayoutInflater.from(this).inflate(R.layout.view_dialog_input, null);
        final MaterialEditText pwdInput = (MaterialEditText) view.findViewById(R.id.view_dialog_input);
        customDialog.setCustomView(view);
        customDialog.show();
        customDialog.setClickListener(new CustomDialog.ClickListener() {
            @Override
            public void onConfirmClick() {
                String pwd = pwdInput.getText().toString();
                if (!TextUtils.isEmpty(pwd)) {
                    pwd = MD5Util.getMD5(pwd);
                    PreferencesUtils preferencesUtils = new PreferencesUtils(ActivitySetting.this);
                    preferencesUtils.putBoolean(Constant.KEY_IS_SET_PWD, true);
                    preferencesUtils.putString(Constant.KEY_PWD, pwd);
                } else {
                    Toast.makeText(ActivitySetting.this, R.string.string_setting_password_hint, Toast.LENGTH_SHORT).show();
                }
                customDialog.dismiss();
            }

            @Override
            public void onCancelClick() {
                customDialog.dismiss();
            }
        });
    }

    private void showAboutDialog(String content) {
        CustomDialog.Builder builder = new CustomDialog.Builder(this, "联系我", getResources().getString(R.string.string_ok));
        builder.content(content);
        builder.darkTheme(true);
        builder.titleAlignment(BaseDialog.Alignment.LEFT); // Use either Alignment.LEFT, Alignment.CENTER or Alignment.RIGHT
        builder.contentColorRes(R.color.black); // int res, or int colorRes parameter versions available as well.
        builder.positiveColorRes(R.color.blue_600); // int res, or int colorRes parameter versions available as well.
        builder.buttonAlignment(BaseDialog.Alignment.RIGHT);
        final CustomDialog customDialog = builder.build();
        customDialog.show();
        customDialog.setClickListener(new CustomDialog.ClickListener() {
            @Override
            public void onConfirmClick() {
                customDialog.dismiss();
            }

            @Override
            public void onCancelClick() {
                customDialog.dismiss();
            }
        });
    }
}
