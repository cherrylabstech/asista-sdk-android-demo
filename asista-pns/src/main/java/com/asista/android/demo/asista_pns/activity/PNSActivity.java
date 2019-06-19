package com.asista.android.demo.asista_pns.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.asista.android.demo.asista_pns.R;
import com.asista.android.demo.asista_pns.db.DBHelper;
import com.asista.android.demo.asista_pns.model.Message;
import com.asista.android.pns.AsistaPNS;
import com.asista.android.pns.Result;
import com.asista.android.pns.exceptions.AsistaException;
import com.asista.android.pns.interfaces.Callback;
import com.asista.android.pns.model.RegistrationRequestDetails;
import com.asista.android.pns.util.CommonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Benjamin J on 30-05-2019.
 */
public class PNSActivity extends AppCompatActivity {
    private static final String TAG = PNSActivity.class.getSimpleName();
    private static final String BUNDLE = "bundle";
    public static final String IS_SUBSCRIPTION = "isSubscription";

    private ProgressBar progressBar;

    private PNSActivity context;
    private Bundle bundle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pns);
        context = this;

        checkNotificationPermission();

        bundle = getIntent().getBundleExtra(BUNDLE);
        progressBar = findViewById(R.id.progressbar);

        setToolbar();
        if (null != bundle) {
            Message message = new Message();
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                Log.e(TAG, "KEY: " + key + "   VALUE: " + value);
                if ("google.message_id".equals(key)) {
                    message.setId(String.valueOf(value));
                }
            }
            if (!bundle.getBoolean("is_custom_message",false)) {
                message.setTitle("messageId: "+message.getId());
                message.setBody(getResources().getString(R.string.background_notification_body));
                DBHelper.getInstance(context).saveMessage(message);
                Log.i(TAG, "onCreate: msg saved... .");
            }else
                Log.e(TAG, "onCreate: msg not saved... ." );
        }else
            Log.e(TAG, "onCreate: bundle is null");

        AsistaPNS.init(context, new Callback<Void>() {
            @Override
            public void onSuccess(Result<Void> result) {
                Log.i(TAG, "onSuccess: SDK initialisation success... ");
                RegistrationRequestDetails details = new RegistrationRequestDetails();
                details.setUserCustomData("asista PNS user");
                Map<String, Object> moreUserData = new HashMap<>();
                moreUserData.put("DeviceType", "Android");
                moreUserData.put("Type", "GeneralUser");
                details.setMore(moreUserData);
                AsistaPNS.register(details, new Callback<Void>() {
                    @Override
                    public void onSuccess(Result<Void> result) {
                        Log.i(TAG, "onSuccess: user registration successful... ");
                    }

                    @Override
                    public void onFailed(AsistaException exception) {
                        Log.e(TAG, "onFailed: user registeration failed... " );
                        exception.printStackTrace();
                    }
                });
                initViews();
            }

            @Override
            public void onFailed(AsistaException exception) {
                Log.e(TAG, "onFailed: SDK initialisation failed... " );
                exception.printStackTrace();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showNotificationIcon();
    }

    private void setToolbar(){
        if (null != getSupportActionBar()){
            getSupportActionBar().setTitle(getResources().getString(R.string.activity_asista_pns_toolbar_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_back_wht);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(Menu.NONE, 1, Menu.NONE, "Refresh");
        item.setIcon(R.drawable.ic_action_refresh_wht);
        item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showNotificationIcon();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * to check if permission to display notification in the notification is disabled
     * * Starting Android Oreo(API 26) and above notification for individual
     */
    public void checkNotificationPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel defaultchannel = manager.getNotificationChannel(getString(R.string.default_notification_channel_id));
            NotificationChannel FCMchannel = manager.getNotificationChannel("fcm_fallback_notification_channel");
            final boolean isFcmChannelDisabled = null != FCMchannel && FCMchannel.getImportance() == NotificationManagerCompat.IMPORTANCE_NONE;
            final boolean isDefaultChannelDisabled = null != defaultchannel && defaultchannel.getImportance() == NotificationManagerCompat.IMPORTANCE_NONE;

            if (isFcmChannelDisabled || isDefaultChannelDisabled)
                CommonUtil.displayDialog(getString(R.string.app_notification_perms_disabled), context);
        } else {
            if (!NotificationManagerCompat.from(context).areNotificationsEnabled())
                CommonUtil.displayDialog(getString(R.string.app_notification_perm_disabled), context);
        }
    }

    private void initViews(){
        findViewById(R.id.topic_subscribe_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TopicsActivity.class);
                intent.putExtra(IS_SUBSCRIPTION, true);
                startActivity(intent);
            }
        });

        findViewById(R.id.topic_unsubscribe_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TopicsActivity.class);
                intent.putExtra(IS_SUBSCRIPTION, false);
                startActivity(intent);
            }
        });

        findViewById(R.id.topic_unsubscribe_all_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                AsistaPNS.unsubscribe(new Callback<Void>() {
                    @Override
                    public void onSuccess(Result<Void> result) {
                        Log.i(TAG, "onSuccess: unsubscribed to all topics...");
                        progressBar.setVisibility(View.GONE);
                        CommonUtil.displayDialog("Unsubscribed from all topics", context);
                    }

                    @Override
                    public void onFailed(AsistaException exception) {
                        Log.e(TAG, "onFailed: unsubscription from all topics failed... " );
                        progressBar.setVisibility(View.GONE);
                        CommonUtil.displayDialog("Unsubscription from all topics failed. "+exception.getMessage(), context);
                        exception.printStackTrace();
                    }
                });
            }
        });


        findViewById(R.id.pns_unregister_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableClicks(false);
                progressBar.setVisibility(View.VISIBLE);
                AsistaPNS.unRegister(new Callback<Void>() {
                    @Override
                    public void onSuccess(Result<Void> result) {
                        Log.i(TAG, "onSuccess: unregistering successful... result: "+result.data);
                        progressBar.setVisibility(View.GONE);
                        enableClicks(true);
                        CommonUtil.showToast("Device Unregistered", context);
                    }

                    @Override
                    public void onFailed(AsistaException exception) {
                        Log.e(TAG, "onFailed: unregistering failed... " );
                        progressBar.setVisibility(View.GONE);
                        exception.printStackTrace();
                        enableClicks(true);
                        CommonUtil.displayDialog(exception.getMessage(), context);
                    }
                });
            }
        });

        findViewById(R.id.copy_token).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CommonUtil.checkIsEmpty(AsistaPNS.getDeviceToken())) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("fcm_deviceToken", AsistaPNS.getDeviceToken());
                    clipboard.setPrimaryClip(clip);
                    CommonUtil.showToast("DeviceToken copied to clipBoard ", context);
                }else
                    CommonUtil.showToast("DeviceToken is empty", context);
            }
        });
    }

    private void showNotificationIcon(){
        Log.i(TAG, "showNotificationIcon: ");
        List messages = DBHelper.getInstance(context).fetchMessages();
        if (!CommonUtil.checkIsEmpty(messages)){
            findViewById(R.id.notification_icon_lyout).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.notification_count)).setText(String.valueOf(messages.size()));

            findViewById(R.id.notification_icon).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context, NotificationListActivity.class));
                }
            });
        }else
            findViewById(R.id.notification_icon_lyout).setVisibility(View.GONE);
    }

    private void enableClicks(boolean isEnable){
        if (isEnable)
            findViewById(R.id.progressbar).setVisibility(View.GONE);
        else
            findViewById(R.id.progressbar).setVisibility(View.VISIBLE);

        findViewById(R.id.topic_subscribe_tv).setEnabled(isEnable);
        findViewById(R.id.topic_unsubscribe_tv).setEnabled(isEnable);
        findViewById(R.id.topic_unsubscribe_all_tv).setEnabled(isEnable);
        findViewById(R.id.pns_unregister_tv).setEnabled(isEnable);
    }

}
