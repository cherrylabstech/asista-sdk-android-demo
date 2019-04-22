package com.asista.android.asistasdkdemo.asista_support;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.asista.android.core.AsistaCore;
import com.asista.android.core.Result;
import com.asista.android.core.exceptions.AsistaException;
import com.asista.android.core.exceptions.NullDataFoundException;
import com.asista.android.core.interfaces.Callback;
import com.asista.android.core.util.CommonUtils;
import com.asista.android.ui.AsistaUi;

public class SupportMainActivity extends AppCompatActivity {

    private static final String TAG = SupportMainActivity.class.getSimpleName();

    private TextInputEditText editText;

    private SupportMainActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        context = this;


        try {
//            AsistaUi.setAssetsFooterEnabled(true);
//            AsistaUi.setKbFooterEnabled(true);
//            AsistaUi.setUserProfileFooterEnabled(true);
//            AsistaUi.setMyTicketsFooterEnabled(true);
//            AsistaUi.setTicketCreationFooterEnabled(true);
//            AsistaUi.setMainMenuFooterEnabled(true);

            AsistaCore.init(this);
            setToolbar();

            findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "onClick: login clicked... ");
                    try {
                        authenticate();
                    } catch (NullDataFoundException e) {
                        e.printStackTrace();
                    }
                }
            });

            findViewById(R.id.register_tv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "onClick: register button clicked... ");
                    startActivity(new Intent(context, RegisterActivity.class));
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setToolbar(){
        if (null != getSupportActionBar()){
            getSupportActionBar().setTitle("Login");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back_white);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void authenticate() throws NullDataFoundException {
        Log.i(TAG, "authenticate: ...");
        editText = findViewById(R.id.username_ed);
        String usrname = editText.getText()+"";
        if (CommonUtils.checkIsEmpty(usrname)) {
            editText.setError("Enter Username");
        }else {
            findViewById(R.id.login_prgsbar).setVisibility(View.VISIBLE);
            AsistaCore.getInstance().getAuthService().authenticate(context, usrname, new Callback<Boolean>() {
                @Override
                public void onSuccess(Result<Boolean> result) {
                    Log.i(TAG, "onSuccess: USER LOGIN SUCCESS...  accessToken: "+AsistaCore.getAccessToken());

                    findViewById(R.id.login_prgsbar).setVisibility(View.GONE);
                    try {
                        AsistaUi.showAsistaMainMenu(context, true, true, false, true, true);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailed(AsistaException exception) {
                    Log.e(TAG, "onFailed: USER LOGIN FAILED");
                    findViewById(R.id.login_prgsbar).setVisibility(View.GONE);
                    String msg = exception.getMessage();
                    if (CommonUtils.checkIsEmpty(msg))
                        msg = "Login failed... errorcode: "+exception.getErrorCode();
                    CommonUtils.displayDialog(msg,context);
                    exception.printStackTrace();
                }
            });
        }
    }
}
