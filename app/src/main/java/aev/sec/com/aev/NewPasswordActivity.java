package aev.sec.com.aev;

import android.content.Context;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.concurrent.TimeUnit;

import aev.sec.com.aev.apicalls.ApiResponse;
import aev.sec.com.aev.apicalls.SetPasswordRequest;
import aev.sec.com.aev.interfaces.CallbackHandler;
import aev.sec.com.aev.model.UserDetail;
import aev.sec.com.aev.sharedPreference.SharedPreferenceUtility;

public class NewPasswordActivity extends AppCompatActivity {
    private ImageView back;
    private EditText newPassword,confirmPassword;
    private Button submit;
    private ProgressBar progressBar;
    private SharedPreferenceUtility mSharedPreferences;
    private static final long SPLASH_TIME_OUT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        mSharedPreferences=new SharedPreferenceUtility(this);
        hideKeyboard();
        init();
    }

    private void init() {
        back=(ImageView)findViewById(R.id.back_icon);
        progressBar=(ProgressBar)findViewById(R.id.progress_bar);
        newPassword=(EditText)findViewById(R.id.input_new_password);
        confirmPassword=(EditText)findViewById(R.id.input_confirm_password);
        submit=(Button) findViewById(R.id.btn_submit);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                currentPassword();
            }
        });
    }

    private void currentPassword() {
        String userNewPassword,userConfirmPassword;
        userNewPassword= newPassword.getText().toString().trim();
        userConfirmPassword= confirmPassword.getText().toString().trim();
        if(TextUtils.isEmpty(userNewPassword))
        {
            showLoginErrorMessage(getString(R.string.enter_new_password));
            return;
        }

        if(TextUtils.isEmpty(userConfirmPassword))
        {
            showLoginErrorMessage(getString(R.string.enter_confirm_password));
            return;
        }

        if(!userNewPassword.equalsIgnoreCase(userConfirmPassword))
        {
            showLoginErrorMessage(getString(R.string.new_password_not_matched));
        }

        UserDetail userDetail= new UserDetail();
        userDetail.setEmail(mSharedPreferences.getUserEmail());
        userDetail.setPassword(userNewPassword);
        userDetail.setType("forgot");
        progressBar.setVisibility(View.VISIBLE);
        new SetPasswordRequest(userDetail, new CallbackHandler<ApiResponse<UserDetail>>()
        {

            @Override
            public void onResponse(ApiResponse<UserDetail> response) {
                if(response!=null)
                {
                    if(response.isSuccess()) {
                        showLoginErrorMessage(getString(R.string.password_changed_successfully));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                        finish();
                            }
                        }, TimeUnit.SECONDS.toMillis(SPLASH_TIME_OUT));
                  }
                    else
                    {
//                                Log.d("msg",response.getError());
                        showLoginErrorMessage(response.getError());
                    }
                }
                else
                {
//                            Log.d("msg","fail");
                }

                progressBar.setVisibility(View.GONE);
            }
        }).call();


    }

    private void showLoginErrorMessage(String string) {
        Snackbar.make(findViewById(R.id.container), string, Snackbar.LENGTH_LONG).show();
    }
    public void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getWindow().getCurrentFocus().getWindowToken(), 0);
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
