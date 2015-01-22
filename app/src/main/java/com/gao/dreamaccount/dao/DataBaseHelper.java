package com.gao.dreamaccount.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gao.dreamaccount.bean.AccountBean;
import com.gao.dreamaccount.bean.DreamBean;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Created by Gao on 2014/11/12.
 */
public class DataBaseHelper extends OrmLiteSqliteOpenHelper {
    private Context mContext;
    private final static String DB_NAME = "dream.db";
    private final static int DB_VERSION = 1;

    private Dao<DreamBean, Integer> dreamBeanIntegerDao;
    private Dao<AccountBean, Integer> accountBeanIntegerDao;

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, DreamBean.class);
            TableUtils.createTableIfNotExists(connectionSource, AccountBean.class);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(DataBaseHelper.class.getName(), "创建数据库失败", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i2) {

    }

    /**
     * 获得dream数据
     */
    public Dao<DreamBean, Integer> getDreamBeanDao() {
        if (dreamBeanIntegerDao == null) {
            try {
                dreamBeanIntegerDao = getDao(DreamBean.class);
            } catch (Exception e) {
                dreamBeanIntegerDao = null;
            }
        }
        return dreamBeanIntegerDao;
    }

    /**
     * 获得财务数据
     */
    public Dao<AccountBean, Integer> getAccountBeanDao() {
        if (accountBeanIntegerDao == null) {
            try {
                accountBeanIntegerDao = getDao(AccountBean.class);
            } catch (Exception e) {
                accountBeanIntegerDao = null;
            }
        }
        return accountBeanIntegerDao;
    }
}
