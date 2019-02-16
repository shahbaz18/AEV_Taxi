package aev.sec.com.aev;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import aev.sec.com.aev.R;
import aev.sec.com.aev.apicalls.ApiResponse;
import aev.sec.com.aev.apicalls.ForgotPasswordRequest;
import aev.sec.com.aev.apicalls.LoginRequest;
import aev.sec.com.aev.interfaces.CallbackHandler;
import aev.sec.com.aev.model.UserDetail;

public class ForgotPasswordActivity extends AppCompatActivity {
 private ImageView back;
 private EditText email;
 private Button submit;
 private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        hideKeyboard();
        init();
    }

    private void init() {
        progressBar=(ProgressBar)findViewById(R.id.progress_bar);
        back=(ImageView)findViewById(R.id.back_icon);
        email=(EditText)findViewById(R.id.input_email);
        submit=(Button)findViewById(R.id.btn_submit);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitEmail();
            }
        });
    }

    private void submitEmail() {
        String userEmail;
        userEmail = email.getText().toString().trim();
        if (TextUtils.isEmpty(userEmail) || isValidEmail(userEmail))
        {
            showLoginErrorMessage(getString(R.string.enter_valid_email));
            return;
        }

        UserDetail userDetail= new UserDetail();
        userDetail.setEmail(userEmail);
        progressBar.setVisibility(View.VISIBLE);
        new ForgotPasswordRequest(userDetail, new CallbackHandler<UserDetail>() {

            @Override
            public void onResponse(UserDetail response) {
                if(response!=null)
                {

                    UserDetail userDetail = new UserDetail();
                    userDetail= response;
                    userDetail.setType("forgot");
                    Intent intent = new Intent(ForgotPasswordActivity.this, OtpActivity.class);
                    intent.putExtra("userDetail",userDetail);
                    startActivity(intent);
                    finish();

                }
                else
                {
                    Log.d("msg","fail");
                    showLoginErrorMessage("Error in Forgot Password");
                }
                progressBar.setVisibility(View.GONE);
            }
        }).call();
    }


    public boolean isValidEmail(String uname) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(uname);
        return matcher.matches();
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
