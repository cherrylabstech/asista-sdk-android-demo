package com.asista.android.demo.asista_pns.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.asista.android.demo.asista_pns.R;
import com.asista.android.demo.asista_pns.activity.PNSActivity;
import com.asista.android.demo.asista_pns.db.DBHelper;
import com.asista.android.demo.asista_pns.model.Message;
import com.asista.android.pns.AsistaPNS;
import com.asista.android.pns.Result;
import com.asista.android.pns.exceptions.AsistaException;
import com.asista.android.pns.interfaces.Callback;
import com.asista.android.pns.webservice.services.AsistaFCMService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

/**
 * Created by Benjamin J on 27-05-2019.
 */
public class MessagingService extends AsistaFCMService {
    private static final String TAG = MessagingService.class.getSimpleName();

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.i(TAG, "onNewToken: newToken: "+s);
        AsistaPNS.setDeviceToken(s);
        try {
            AsistaPNS.updateDeviceToken(s, new Callback<Void>() {
                @Override
                public void onSuccess(Result<Void> result) {
                    Log.i(TAG, "onSuccess: new device token updated successfully");
                }

                @Override
                public void onFailed(AsistaException exception) {
                    Log.e(TAG, "onFailed: unable to update new device token" );
                    exception.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
//        if (null != remoteMessage) {
//            Log.i(TAG, "onMessageReceived: remoteMessage-custom: " + remoteMessage.getNotification().getTitle() + "\n body: " + remoteMessage.getNotification().getBody() + ",\n data: " + remoteMessage.getData());
//            Message message = new Message();
//            message.setId(remoteMessage.getMessageId());
//            if (null != remoteMessage.getNotification()) {
//                message.setTitle(remoteMessage.getNotification().getTitle());
//                message.setBody(remoteMessage.getNotification().getBody());
//            }
//            DBHelper.getInstance(getApplicationContext()).saveMessage(message);
//            Log.i(TAG, "onMessageReceived: message saved... "+DBHelper.getInstance(getApplicationContext()).fetchMessages());
//
//            if (remoteMessage.getData() != null) {
//                Log.i(TAG, "onMessageReceived:message data... ");
//                for (String key : remoteMessage.getData().keySet()) {
//                    Object value = remoteMessage.getData().get(key);
//                    Log.e(TAG, "Key-custom: " + key + " Value-custom: " + value);
//                }
//            }
//        }

        super.onMessageReceived(remoteMessage);
        Log.i(TAG, "onMessageReceived: message-id: "+remoteMessage.getMessageId());
        try {
            Message message = new Message();
            message.setId(remoteMessage.getMessageId());
            if (null != remoteMessage.getNotification()) {
                Log.i(TAG, "onMessageReceived :notification remoteMessage-custom: " + remoteMessage.getNotification().getTitle() + "\n body: " + remoteMessage.getNotification().getBody() + ",\n data: " + remoteMessage.getData());
                message.setTitle(remoteMessage.getNotification().getTitle());

                String messageBody = remoteMessage.getNotification().getBody()+"\nData: { ";
                if (remoteMessage.getData() != null) {
                    for (String key : remoteMessage.getData().keySet()) {
                        Object value = remoteMessage.getData().get(key);
                        Log.e(TAG, "Key-custom: " + key + " Value-custom: " + value);
                        messageBody = messageBody.concat(key+" : "+value+", ");
                    }
                }
                messageBody = messageBody.concat("}");
                messageBody = messageBody.replace(", }", " }");
                message.setBody(messageBody);
            }else{
                message.setTitle("messageId: "+remoteMessage.getMessageId());
                String messageBody = "Message body: { ";
                if (remoteMessage.getData() != null) {
                    for (String key : remoteMessage.getData().keySet()) {
                        Object value = remoteMessage.getData().get(key);
                        messageBody = messageBody.concat(key+" : "+value+", ");
                    }
                    messageBody = messageBody.concat("}");
                    messageBody = messageBody.replace(", }", " }");
                }
                message.setBody(messageBody);
            }

            DBHelper.getInstance(getApplicationContext()).saveMessage(message);

            if (!isAppOnForeground())
                showNotification(message);
            else
                Log.e(TAG, "onMessageReceived: app is in foreground" );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * to show notification in the systems notification tray
     *
     * @param message ,{@link Message} instance with the necessary notification data
     */
    private void showNotification(Message message){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(getString(R.string.default_notification_channel_id), getString(R.string.default_notification_channel_name), importance);
            notificationManager.createNotificationChannel(notificationChannel);
            builder = new NotificationCompat.Builder(getApplicationContext(), notificationChannel.getId());
        } else {
            builder = new NotificationCompat.Builder(getApplicationContext());
        }

        Intent intent = new Intent(this, PNSActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();
        bundle.putString("google.message_id", message.getId());
        bundle.putString("message_title", message.getTitle());
        bundle.putString("message_body", message.getBody());
        bundle.putBoolean("is_custom_message", true);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        builder = builder
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                .setContentTitle(message.getTitle())
                .setContentText(message.getBody())
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(2, builder.build());
    }

    /**
     * to check if the sample app is in foreground(opened) or background/killed
     *
     * @return true if app is in foreground,
     *         false if app is in background/killed
     */
    public boolean isAppOnForeground() {
        final Context context = getApplicationContext();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}
