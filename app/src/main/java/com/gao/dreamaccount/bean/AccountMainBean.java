package com.gao.dreamaccount.bean;

import com.gao.dreamaccount.abs.AbsBean;

/**
 * Created by Gao on 2014/11/19.
 * 用于主页财务统计显示
 */
public class AccountMainBean extends AbsBean {
    //=====================统计时间点标识================
    public final static int YEAR = 0;
    public final static int MONTH = 1;
    public final static int DAY = 2;
    public final static int TOTAL = 3;
    //====================================================

    private String title;
    private double income;
    private double expense;
    private double balance;//余额
    private int type;//统计时间点标识

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public double getExpense() {
        return expense;
    }

    public void setExpense(double expense) {
        this.expense = expense;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
        switch (type) {
            case YEAR:
                title = "本年";
                break;
            case MONTH:
                title = "本月";
                break;
            case DAY:
                title = "本日";
                break;
            case TOTAL:
                title = "累计";
                break;
        }
    }
}
