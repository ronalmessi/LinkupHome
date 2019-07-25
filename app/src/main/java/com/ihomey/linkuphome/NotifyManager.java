package com.ihomey.linkuphome;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class NotifyManager {

    public static final String CHANNEL_ID = "com.ihomey.linkuphome";
    public static final String CHANNEL_NAME = "linkuphome";

    // 单例开始
    private volatile static NotifyManager INSTANCE;

    private NotifyManager(Context context) {
        initNotifyManager(context);
    }

    public static NotifyManager getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (NotifyManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NotifyManager(context);
                }
            }
        }
        return INSTANCE;
    }
    // 单例结束

    private NotificationManager manager;
    // NotificationManagerCompat
    private NotificationCompat.Builder builder;

    //初始化通知栏配置
    private void initNotifyManager(Context context) {
        context = context.getApplicationContext();
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // 如果存在则清除上一个消息
//        manager.cancel(news_flag);
        builder = new NotificationCompat.Builder(context,CHANNEL_ID);
        setONotifyChannel(manager,builder,CHANNEL_ID,CHANNEL_NAME);
        // 设置通知栏的优先级
        builder.setPriority(Notification.PRIORITY_HIGH);
        // 设置点击可消失
        builder.setAutoCancel(true);
        // 设置是否震动等
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        // 设置icon
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher));
        // 设置点击意图
//        Intent intent = new Intent(context, GongdenggeActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putBoolean(Contants.INTENT_GOTO_MYLMAP, true);
//        intent.putExtras(bundle);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 230, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(pendingIntent);
    }

    public void showNotify(String title,String content) {
       NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.bigText(content);
        style.setBigContentTitle(title);
        builder.setWhen(System.currentTimeMillis());
        builder.setStyle(style);
        builder.setContentTitle(title);
        builder.setContentText(content);
        manager.notify(13259, builder.build());
    }


    private  void setONotifyChannel(NotificationManager manager, NotificationCompat.Builder builder, String channelId, String channelName) {
        if (TextUtils.isEmpty(channelId)||TextUtils.isEmpty(channelName)){
            Log.e("NotifyManager","Notify Warning:  ".concat("安卓8.0的通知兼容库中 channeId 与 channelName 不能为empty"));
        }
        if (Build.VERSION.SDK_INT >= 26) {
            //第三个参数设置通知的优先级别
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.canBypassDnd();//是否可以绕过请勿打扰模式
            channel.canShowBadge();//是否可以显示icon角标
            channel.enableLights(true);//是否显示通知闪灯
            channel.enableVibration(true);//收到小时时震动提示
            channel.setBypassDnd(true);//设置绕过免打扰
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_SECRET);
            channel.setLightColor(Color.RED);//设置闪光灯颜色
            channel.getAudioAttributes();//获取设置铃声设置
            channel.setVibrationPattern(new long[]{100, 200, 100});//设置震动模式
            channel.shouldShowLights();//是否会闪光
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
            if (builder != null) {
                builder.setChannelId(channelId);//这个id参数要与上面channel构建的第一个参数对应
            }
        }
    }
}

