package com.gao.dreamaccount.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gao.dreamaccount.R;
import com.gao.dreamaccount.abs.AbsAdapter;
import com.gao.dreamaccount.bean.AccountTotalBean;
import com.gao.dreamaccount.util.Utils;
import com.gao.dreamaccount.views.RoundedLetterView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Gao on 2014/11/21.
 */
public class AdapterFragmentYearAccount extends AbsAdapter {

    private List<AccountTotalBean> accountTotalBeans;

    public AdapterFragmentYearAccount(Context context) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_fragment_year_account, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final AccountTotalBean accountTotalBean = getItem(position);
        if (accountTotalBean == null) {
            return convertView;
        }
        viewHolder.dateTxt.setBackgroundColor(mContext.getResources().getColor(accountTotalBean.getColorId()));
        viewHolder.dateTxt.setTitleText(accountTotalBean.getTitle());
        viewHolder.incomeAmountTxt.setText(Utils.formateDouble(accountTotalBean.getIncomeAmount()));
        viewHolder.expenseAmountTxt.setText(Utils.formateDouble(accountTotalBean.getExpenseAmount()));
        return convertView;
    }

    class ViewHolder {
        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

        @InjectView(R.id.item_fragment_account_incomeamount)
        TextView incomeAmountTxt;
        @InjectView(R.id.item_fragment_account_expenseamount)
        TextView expenseAmountTxt;
        @InjectView(R.id.item_fragment_account_date)
        RoundedLetterView dateTxt;
    }
}
