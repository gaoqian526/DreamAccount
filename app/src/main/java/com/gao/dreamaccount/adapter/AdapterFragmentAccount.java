package com.gao.dreamaccount.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gao.dreamaccount.R;
import com.gao.dreamaccount.abs.AbsAdapter;
import com.gao.dreamaccount.activity.ActivityAccountPager;
import com.gao.dreamaccount.bean.AccountTotalBean;
import com.gao.dreamaccount.util.Utils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Gao on 2014/11/21.
 */
public class AdapterFragmentAccount extends AbsAdapter {

    private List<AccountTotalBean> accountTotalBeans;

    public AdapterFragmentAccount(Context context) {
        super(context);
    }

    public void setAccountTotalBeans(List<AccountTotalBean> accountTotalBeans) {
        this.accountTotalBeans = accountTotalBeans;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return accountTotalBeans == null ? 0 : accountTotalBeans.size();
    }

    @Override
    public AccountTotalBean getItem(int position) {
        return accountTotalBeans == null ? null : accountTotalBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_fragment_account, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final AccountTotalBean accountTotalBean = getItem(position);
        if (accountTotalBean == null) {
            return convertView;
        }
        if (accountTotalBean.getType() != 1) {
            viewHolder.dateTxt.setText(accountTotalBean.getDateStr());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("date", accountTotalBean.getDateStr());
                    bundle.putInt("stype", accountTotalBean.getsType());
                    launchActivity(ActivityAccountPager.class, bundle);
                }
            });
        } else {
            viewHolder.dateTxt.setText(accountTotalBean.getTitle());
        }
        viewHolder.amountTxt.setText(Utils.formateDouble(accountTotalBean.getIncomeAmount()));
        return convertView;
    }

    class ViewHolder {
        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

        @InjectView(R.id.item_fragment_account_amount)
        TextView amountTxt;
        @InjectView(R.id.item_fragment_account_date)
        TextView dateTxt;
    }
}
