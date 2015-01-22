package com.gao.dreamaccount.receiver;
//                            _ooOoo_
//                           o8888888o
//                           88" . "88
//                           (| -_- |)
//                            O\ = /O
//                        ____/`---'\____
//                      .   ' \\| |// `.
//                       / \\||| : |||// \
//                     / _||||| -:- |||||- \
//                       | | \\\ - /// | |
//                     | \_| ''\---/'' | |
//                      \ .-\__ `-` ___/-. /
//                   ___`. .' /--.--\ `. . __
//                ."" '< `.___\_<|>_/___.' >'"".
//               | | : `- \`.;`\ _ /`;.`/ - ` : | |
//                 \ \ `-. \_ __\ /__ _/ .-` / /
//         ======`-.____`-.___\_____/___.-`____.-'======
//                            `=---='
//         .............................................
//                  佛祖保佑             永无BUG

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.gao.dreamaccount.R;
import com.gao.dreamaccount.activity.MainActivity;
import com.gao.dreamaccount.util.LogUtil;

/**
 * Created by Gao on 2015/1/8.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.e("闹钟响了");
        notifyNewMessage("Hi", "今天为你的愿望努力了吗？", context);
    }

    /**
     * 在状态栏提示一下
     */
    protected void notifyNewMessage(String msg, String name, Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        /*
         * 创建新的Intent，作为点击Notification留言条时， 会运行的Activity
		 */
        Intent notifyIntent = new Intent(context, MainActivity.class);
             /* 创建PendingIntent作为设置递延运行的Activity */
        PendingIntent appIntent = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		/* 创建Notication，并设置相关参数 */
        Notification myNoti = new Notification();
        // 点击自动消失
        myNoti.flags = Notification.FLAG_AUTO_CANCEL;
        /* 设置statusbar显示的icon */
        myNoti.icon = R.drawable.icon;
        /* 设置statusbar显示的文字信息 */
        myNoti.tickerText = "小愿望";
        /* 设置notification发生时同时发出默认声音 */
        myNoti.defaults = Notification.DEFAULT_SOUND;
        /* 设置Notification留言条的参数 */
        myNoti.setLatestEventInfo(context, msg, name, appIntent);
        /* 送出Notification */
        notificationManager.notify(0, myNoti);
    }
}
