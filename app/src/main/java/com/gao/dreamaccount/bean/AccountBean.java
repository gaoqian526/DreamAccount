package com.gao.dreamaccount.bean;

import com.gao.dreamaccount.abs.AbsBean;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Gao on 2014/11/13.
 */
@DatabaseTable(tableName = "table_account")
public class AccountBean extends AbsBean {

    public static final int INCOME = 1;//收入
    public static final int EXPENSE = 2;//支出
    @DatabaseField(id = true)
    private String uuid;//uuid主键
    @DatabaseField
    private String name;//名字
    @DatabaseField
    private String des;//简介
    @DatabaseField
    private int type;//类型，收入还是支出
    @DatabaseField
    private String timeStr;//时间字符型 xxx-xx-xx
    @DatabaseField
    private long setTime;//建立时间
    @DatabaseField
    private long insetTime;//入库时间
    @DatabaseField
    private double amount;//金额
    @DatabaseField
    private String temp1;
    @DatabaseField
    private String temp2;
    @DatabaseField
    private String temp3;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTimeStr() {
        return timeStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }

    public long getSetTime() {
        return setTime;
    }

    public void setSetTime(long setTime) {
        this.setTime = setTime;
    }

    public long getInsetTime() {
        return insetTime;
    }

    public void setInsetTime(long insetTime) {
        this.insetTime = insetTime;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTemp1() {
        return temp1;
    }

    public void setTemp1(String temp1) {
        this.temp1 = temp1;
    }

    public String getTemp2() {
        return temp2;
    }

    public void setTemp2(String temp2) {
        this.temp2 = temp2;
    }

    public String getTemp3() {
        return temp3;
    }

    public void setTemp3(String temp3) {
        this.temp3 = temp3;
    }
}
