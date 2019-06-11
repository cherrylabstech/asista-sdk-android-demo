package com.asista.android.demo.asista_support;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.asista.android.asistasdkdemo.asista_support.R;
import com.asista.android.core.AsistaCore;
import com.asista.android.core.Result;
import com.asista.android.core.exceptions.AsistaException;
import com.asista.android.core.exceptions.NullDataFoundException;
import com.asista.android.core.interfaces.Callback;
import com.asista.android.core.model.RegisterProperties;
import com.asista.android.core.util.CommonUtils;

/**
 * Created by Benjamin J on 13-02-2019.
 */
public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();

    private TextInputEditText firstNameEd;
    private TextInputEditText lastNameEd;
    private TextInputEditText emailEd;
    private TextInputEditText usernameEd;
    private TextInputEditText phoneEd;

    private RegisterActivity context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        context = this;
        setToolbar();
        try {
            initViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        firstNameEd = findViewById(R.id.firstname_ed);
        lastNameEd = findViewById(R.id.lastname_ed);
        emailEd = findViewById(R.id.email_ed);
        usernameEd = findViewById(R.id.username_ed);
        phoneEd = findViewById(R.id.phone_ed);

        findViewById(R.id.register_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    registerUser();
                } catch (NullDataFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setToolbar(){
        if (null != getSupportActionBar()){
            getSupportActionBar().setTitle("Register");
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

    private void registerUser() throws NullDataFoundException {
        if (CommonUtils.checkIsEmpty(firstNameEd.getText()+"")){
            firstNameEd.setError("this field is mandatory");
        }else if (CommonUtils.checkIsEmpty(usernameEd.getText()+"")){
            usernameEd.setError("this field is mandatory");
        }else if (CommonUtils.checkIsEmpty(phoneEd.getText()+"")){
            phoneEd.setError("this field is mandatory");
        }else{
            findViewById(R.id.reg_prgsbar).setVisibility(View.VISIBLE);
            RegisterProperties properties = new RegisterProperties();
            properties.setFirstName(firstNameEd.getText().toString());
            properties.setUserId(usernameEd.getText().toString());
            properties.setPhone(phoneEd.getText().toString());
            if (null != lastNameEd.getText()) {
                properties.setLastName(lastNameEd.getText().toString());
            }else{
                properties.setLastName(null);
                Log.i(TAG, "registerUser: lastname  is null");
            }
            if (null != emailEd.getText()) {
                properties.setEmail(emailEd.getText().toString());
            }else{
                properties.setEmail(null);
                Log.i(TAG, "registerUser: email is null");
            }

            AsistaCore.getInstance().getAuthService().register(properties, new Callback<Boolean>() {
                @Override
                public void onSuccess(Result<Boolean> result) {
                    Log.i(TAG, "onSuccess: registration successful... ");
                    findViewById(R.id.reg_prgsbar).setVisibility(View.GONE);
                    CommonUtils.showToast("User Registration Successful", context);
                    finish();
                }

                @Override
                public void onFailed(AsistaException exception) {
                    Log.e(TAG, "onFailed: registration failed" );
                    findViewById(R.id.reg_prgsbar).setVisibility(View.GONE);
                    String msg = exception.getMessage();
                    if (CommonUtils.checkIsEmpty(msg))
                        msg = "User Registeration failed... errorCode: "+exception.getErrorCode();
                    CommonUtils.displayDialog(msg, context);
                    exception.printStackTrace();
                }
            });
        }
    }
}
