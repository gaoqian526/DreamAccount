package com.gao.dreamaccount.adapter;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gao.dreamaccount.R;
import com.gao.dreamaccount.abs.AbsAdapter;
import com.gao.dreamaccount.activity.ActivityNewDream;
import com.gao.dreamaccount.bean.AccountBean;
import com.gao.dreamaccount.bean.DreamBean;
import com.gao.dreamaccount.util.LogUtil;
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
            viewHolder.timeTxt.setText(getDoneDateSpan(dreamBean.getSetTime()));
            viewHolder.okTxt.setVisibility(View.VISIBLE);
            viewHolder.sHint.setVisibility(View.GONE);
            viewHolder.statusTxt.setVisibility(View.GONE);
            viewHolder.timeTxt.setBackgroundResource(R.color.blue_300);
        } else {
            viewHolder.timeTxt.setText(getDateSpan(dreamBean.getSetTime()));
            viewHolder.okTxt.setVisibility(View.GONE);
            viewHolder.sHint.setVisibility(View.VISIBLE);
            if (balanceAmount > dreamBean.getBudget()) {
                viewHolder.timeTxt.setBackgroundResource(R.color.blue_300);
                viewHolder.statusTxt.setTextColor(mContext.getResources().getColor(R.color.blue_600));
                viewHolder.statusTxt.setText("可完成");
            } else {
                viewHolder.timeTxt.setBackgroundResource(R.color.color_light_orange);
                viewHolder.statusTxt.setTextColor(mContext.getResources().getColor(R.color.color_light_red));
                viewHolder.statusTxt.setText(Utils.formateDouble(dreamBean.getBudget() - balanceAmount));
            }
        }
        viewHolder.budgetTxt.setTextColor(mContext.getResources().getColor(R.color.blue_600));
        viewHolder.budgetTxt.setText(Utils.formateDouble(dreamBean.getBudget()));
        if (TextUtils.isEmpty(dreamBean.getDes())) {
//            viewHolder.desTxt.setVisibility(View.GONE);
            viewHolder.desTxt.setText("");
        } else {
//            viewHolder.desTxt.setVisibility(View.VISIBLE);
            LogUtil.e(dreamBean.getDes());
            viewHolder.desTxt.setText(dreamBean.getDes());
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                LogUtil.e(dreamBean.getUuid());
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
        @InjectView(R.id.item_dream_ok)
        TextView okTxt;
        @InjectView(R.id.item_dream__status_hint)
        TextView sHint;
    }

    private SpannableStringBuilder getDateSpan(long time) {
        if (Utils.getDayCount(time).equals("已到期")) {
            SpannableStringBuilder spannable = new SpannableStringBuilder("已到期");
            ForegroundColorSpan span_0 = new ForegroundColorSpan(mContext.getResources().getColor(R.color.white));
            spannable.setSpan(span_0, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannable;
        }
        String finalString = "距离：" + Utils.getDay(time)
                + " 还有：" + Utils.getDayCount(time);
        SpannableStringBuilder spannable = new SpannableStringBuilder(finalString);//用于可变字符串
        ForegroundColorSpan span_0 = new ForegroundColorSpan(mContext.getResources().getColor(R.color.black_54));
        ForegroundColorSpan span_01 = new ForegroundColorSpan(mContext.getResources().getColor(R.color.black_54));
        AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(mContext.getResources().getDimensionPixelOffset(R.dimen.text_size_12));
        AbsoluteSizeSpan sizeSpan1 = new AbsoluteSizeSpan(mContext.getResources().getDimensionPixelOffset(R.dimen.text_size_12));
        spannable.setSpan(sizeSpan, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(sizeSpan1, finalString.indexOf("还"), finalString.indexOf("还") + 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(span_0, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(span_01, finalString.indexOf("还"), finalString.indexOf("还") + 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    private SpannableStringBuilder getDoneDateSpan(long time) {
        if (Utils.getDayCount(time).equals("已到期")) {
            SpannableStringBuilder spannable = new SpannableStringBuilder("已到期");
            ForegroundColorSpan span_0 = new ForegroundColorSpan(mContext.getResources().getColor(R.color.white));
            spannable.setSpan(span_0, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannable;
        }
        String finalString = "距离：" + Utils.getDay(time)
                + " 提前：" + Utils.getDayCount(time);
        SpannableStringBuilder spannable = new SpannableStringBuilder(finalString);//用于可变字符串
        ForegroundColorSpan span_0 = new ForegroundColorSpan(mContext.getResources().getColor(R.color.black_54));
        ForegroundColorSpan span_01 = new ForegroundColorSpan(mContext.getResources().getColor(R.color.black_54));
        AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(mContext.getResources().getDimensionPixelOffset(R.dimen.text_size_12));
        AbsoluteSizeSpan sizeSpan1 = new AbsoluteSizeSpan(mContext.getResources().getDimensionPixelOffset(R.dimen.text_size_12));
        spannable.setSpan(sizeSpan, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(sizeSpan1, finalString.indexOf("提"), finalString.indexOf("提") + 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(span_0, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(span_01, finalString.indexOf("提"), finalString.indexOf("提") + 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }
}
