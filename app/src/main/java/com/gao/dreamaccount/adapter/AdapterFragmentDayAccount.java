package com.gao.dreamaccount.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import com.gao.dreamaccount.R;
import com.gao.dreamaccount.bean.AccountBean;
import com.gao.dreamaccount.bean.AccountTotalBean;
import com.gao.dreamaccount.util.Utils;
import com.gao.dreamaccount.views.RoundedLetterView;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Gao on 2014/11/21.
 */
public class AdapterFragmentDayAccount extends BaseSwipeAdapter {

    private List<AccountTotalBean> accountTotalBeans;
    private Context mContext;
    private Dao<AccountBean, Integer> accountBeanDao;

    public AdapterFragmentDayAccount(Context context) {
        mContext = context;
    }

    public void setAccountBeanDao(Dao<AccountBean, Integer> accountBeanDao) {
        this.accountBeanDao = accountBeanDao;
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
    public void fillValues(final int position, View convertView) {
        ViewHolder viewHolder = null;
        if (viewHolder == null) {
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final AccountTotalBean accountTotalBean = getItem(position);
        if (accountTotalBean == null) {
            return;
        }
        viewHolder.nameText.setText(accountTotalBean.getName());
//        viewHolder.dateTxt.setBackgroundColor(mContext.getResources().getColor(accountTotalBean.getColorId()));
//        viewHolder.dateTxt.setTitleText(accountTotalBean.getTitle());
        if (accountTotalBean.getsType() == AccountBean.INCOME) {
            viewHolder.dateTxt.setBackgroundColor(mContext.getResources().getColor(R.color.blue_300));
            viewHolder.amountTxt.setTextColor(mContext.getResources().getColor(R.color.blue_300));
            viewHolder.dateTxt.setTitleText("收");
        } else {
            viewHolder.dateTxt.setBackgroundColor(mContext.getResources().getColor(R.color.color_light_orange));
            viewHolder.amountTxt.setTextColor(mContext.getResources().getColor(R.color.color_light_orange));
            viewHolder.dateTxt.setTitleText("支");
        }
        viewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountBean accountBean = new AccountBean();
                accountBean.setUuid(accountTotalBean.getUuid());
                accountTotalBeans.remove(position);
                closeItem(position);
                try {
                    accountBeanDao.delete(accountBean);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                notifyDataSetChanged();
            }
        });
        viewHolder.amountTxt.setText(Utils.formateDouble(accountTotalBean.getIncomeAmount()));
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.item_fragment_day_account, null);
        return convertView;
    }

    class ViewHolder {
        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

        @InjectView(R.id.item_fragment_account_amount)
        TextView amountTxt;
        @InjectView(R.id.item_fragment_account_date)
        RoundedLetterView dateTxt;
        @InjectView(R.id.item_fragment_account_name)
        TextView nameText;
        @InjectView(R.id.delete)
        View deleteBtn;
    }
}
