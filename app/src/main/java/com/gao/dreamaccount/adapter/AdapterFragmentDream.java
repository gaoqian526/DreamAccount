package com.gao.dreamaccount.adapter;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gao.dreamaccount.R;
import com.gao.dreamaccount.abs.AbsAdapter;
import com.gao.dreamaccount.activity.ActivityNewDream;
import com.gao.dreamaccount.bean.AccountBean;
import com.gao.dreamaccount.bean.DreamBean;
import com.gao.dreamaccount.util.Utils;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Gao on 2014/11/4.
 */
public class AdapterFragmentDream extends AbsAdapter {

    private List<DreamBean> dreamBeans;

    private double balanceAmount;

    public AdapterFragmentDream(Context context, double balanceAmount) {
        super(context);
        this.balanceAmount = balanceAmount;
    }

    public void setDreamBeans(List<DreamBean> dreamBeans) {
        this.dreamBeans = dreamBeans;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dreamBeans == null ? 0 : dreamBeans.size();
    }

    @Override
    public DreamBean getItem(int position) {
        return dreamBeans == null ? null : dreamBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_fragment_dream, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final DreamBean dreamBean = getItem(position);
        if (dreamBean == null) {
            return convertView;
        }
        viewHolder.nameTxt.setText(dreamBean.getName());
        if (dreamBean.getStatus() == DreamBean.STATUS_DONE) {
            viewHolder.timeTxt.setText(Utils.getDay(dreamBean.getSetTime()));
            viewHolder.okTxt.setVisibility(View.VISIBLE);
            viewHolder.statusLay.setVisibility(View.GONE);
        } else {
            viewHolder.timeTxt.setText(Utils.getDay(dreamBean.getSetTime()));
            viewHolder.okTxt.setVisibility(View.GONE);
            viewHolder.statusLay.setVisibility(View.VISIBLE);
            if (balanceAmount > dreamBean.getBudget()) {
                viewHolder.statusTxt.setTextColor(mContext.getResources().getColor(R.color.blue_600));
                viewHolder.statusTxt.setText("可完成");
            } else {
                viewHolder.statusTxt.setTextColor(mContext.getResources().getColor(R.color.color_light_red));
                viewHolder.statusTxt.setText(Utils.formateDouble(dreamBean.getBudget() - balanceAmount));
            }
            viewHolder.budgetTxt.setTextColor(mContext.getResources().getColor(R.color.blue_600));
            viewHolder.budgetTxt.setText(Utils.formateDouble(dreamBean.getBudget()));
        }
        if (TextUtils.isEmpty(dreamBean.getDes())) {
            viewHolder.desTxt.setVisibility(View.GONE);
//            viewHolder.desTxt.setText("");
        } else {
            viewHolder.desTxt.setVisibility(View.VISIBLE);
            viewHolder.desTxt.setText(dreamBean.getDes());
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("dream", dreamBean);
                launchActivity(ActivityNewDream.class, bundle);
            }
        });
        return convertView;
    }

    class ViewHolder {
        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

        @InjectView(R.id.item_dream_name)
        TextView nameTxt;
        @InjectView(R.id.item_dream_status)
        TextView statusTxt;
        @InjectView(R.id.item_dream_des)
        TextView desTxt;
        @InjectView(R.id.item_dream_time)
        TextView timeTxt;
        @InjectView(R.id.item_dream_budget)
        TextView budgetTxt;
        @InjectView(R.id.item_dream__status_lay)
        View statusLay;
        @InjectView(R.id.item_dream_ok)
        TextView okTxt;
    }
}
