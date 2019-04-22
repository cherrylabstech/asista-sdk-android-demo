package com.asista.android.asistasdkdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.asista.android.asistasdkdemo.R;
import com.asista.android.asistasdkdemo.asista_bot.BotActivity;
import com.asista.android.asistasdkdemo.asista_support.SupportMainActivity;

/**
 * <p>
 * Created by Benjamin J on 02-11-2018.
 * </p>
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();


    private MainActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        setToolbar();
    }

    private void setToolbar(){
        if (null != getSupportActionBar()){
            getSupportActionBar().setTitle("asista SDK Samples");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_back_wht);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        initViews();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initViews() {
        findViewById(R.id.support_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(context, SupportMainActivity.class), 1);
            }
        });
        findViewById(R.id.bot_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(context, BotActivity.class), 2);
            }
        });
    }
}
