package com.gao.dreamaccount.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.test.mock.MockApplication;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.gao.dreamaccount.R;
import com.gao.dreamaccount.abs.AbsActivity;
import com.gao.dreamaccount.util.DreamRestClient;
import com.gao.dreamaccount.util.LogUtil;
import com.gao.dreamaccount.util.Utils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Gao on 2014/12/2.
 */
public class WelcomActivity extends AbsActivity {
    @InjectView(R.id.activity_welcome_img)
    ImageView mActivityWelcomeImg;
    @InjectView(R.id.activity_welcome_title)
    TextView mActivityWelcomeTitle;
    @InjectView(R.id.activity_welcome_content)
    TextView mActivityWelcomeContent;

    private long startTime;//开始时间
    private long timeWaite = 5000;//等待时间


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏
        setContentView(R.layout.activity_welcome);
        ButterKnife.inject(this);
        MobclickAgent.setDebugMode(false);
        MobclickAgent.updateOnlineConfig(getApplicationContext());
        mActivityWelcomeImg.setImageResource(R.color.blue_500);
        handler.sendEmptyMessageDelayed(0, timeWaite);
        String time = Utils.getDay(System.currentTimeMillis());
        mActivityWelcomeTitle.setText(time);
        DreamRestClient.get("", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                parserJson(response);
//                LogUtil.e(response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            if (msg.what == 0) {
                DreamRestClient.cancel();
                launchActivity(MainActivity.class);
                finish();
            }
        }
    };

    private void parserJson(JSONArray jsonArray) {
        try {
            if (jsonArray.length() > 0) {
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String content = jsonObject.getString("content");
                if (!TextUtils.isEmpty(content)) {
                    mActivityWelcomeContent.setText(content);
                }
                String imgPath = jsonObject.getString("path");
                if (!TextUtils.isEmpty(imgPath)) {
                    imgPath = "http://daiwoyige.com" + imgPath;
                    ImageLoader.getInstance().displayImage(imgPath, mActivityWelcomeImg, getDisplayImageOptions());
                }
            }
        } catch (Exception e) {

        }
    }
}
