package com.asista.android.demo.asista_bot;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.asista.android.bot.AsistaBot;

/**
 * Created by Benjamin J on 17-04-2019.
 */
public class BotActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            AsistaBot.init(this);
            AsistaBot.showChatBotUi(this);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
