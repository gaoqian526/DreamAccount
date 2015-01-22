package com.gao.dreamaccount.bean;

import com.gao.dreamaccount.abs.AbsBean;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Gao on 2014/11/12.
 */
@DatabaseTable(tableName = "table_dream")
public class DreamBean extends AbsBean {

    public static final int STATUS_WAITE = 0;//可完成
    public static final int STATUS_DONE = 1;//已经完成
    public static final int STATUS_UNDO = 2;//尚差

    @DatabaseField(id = true)
    private String uuid;
    @DatabaseField
    private long setTime;//设定的时间
    @DatabaseField
    private long insertTime;//插库的时间
    @DatabaseField
    private String name;
    @DatabaseField
    private String des;
    @DatabaseField
    private double budget;
    @DatabaseField
    private int status;//完成状态
    @DatabaseField
    private int priority;//是否为主要梦想
    @DatabaseField
    private String temp1;//备用字段1
    @DatabaseField
    private String temp2;//备用字段2
    @DatabaseField
    private String temp3;//备用字段3

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getSetTime() {
        return setTime;
    }

    public void setSetTime(long setTime) {
        this.setTime = setTime;
    }

    public long getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(long insertTime) {
        this.insertTime = insertTime;
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

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
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
