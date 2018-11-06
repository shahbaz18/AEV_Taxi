package aev.sec.com.aev;

import android.app.Activity;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import aev.sec.com.aev.apicalls.ApiResponse;
import aev.sec.com.aev.apicalls.LoginRequest;
import aev.sec.com.aev.interfaces.CallbackHandler;
import aev.sec.com.aev.model.UserDetail;

public class LoginActivity extends AppCompatActivity {
    EditText email,password;
    Button login, register;
    TextView forgotPassword;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        hideKeyboard();
        init();
    }

    private void init() {
        email = (EditText)findViewById(R.id.input_email);
        password=(EditText)findViewById(R.id.input_password);
        forgotPassword=(TextView)findViewById(R.id.forgot_password);
        login=(Button)findViewById(R.id.btn_login);
        progressBar=(ProgressBar)findViewById(R.id.progress_bar);
        register=(Button)findViewById(R.id.btn_register);
        login.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            hideKeyboard();
           login();
         }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotPassword();
            }
        });


    }

    private void forgotPassword() {
        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
    }

    private void register() {
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));

    }

    private void login() {
        String userEmail;
        String userPassword;
        userEmail = email.getText().toString().trim();
        userPassword = password.getText().toString().trim();

        if (TextUtils.isEmpty(userEmail) ||!isValidEmail(userEmail))
        {
            showLoginErrorMessage(getString(R.string.enter_valid_email));
            return;
        }

        if (TextUtils.isEmpty(userPassword))
        {
            showLoginErrorMessage(getString(R.string.enter_your_password));
            return;
        }
        if (password.length()<6)
        {
            showLoginErrorMessage(getString(R.string.password_characters_length));
            return;
        }

        final UserDetail userDetail = new UserDetail();
        userDetail.setEmail(userEmail);
        userDetail.setPassword(userPassword);
        progressBar.setVisibility(View.VISIBLE);

        // starts here
        userDetail.setUserName("shahbaz");
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.putExtra("userDetail",userDetail);
        startActivity(intent);
        finish();
        progressBar.setVisibility(View.GONE);
        //ends here
        // comment for development
        /*new LoginRequest(userDetail, new CallbackHandler<ApiResponse<UserDetail>>() {
            @Override
            public void onResponse(ApiResponse<UserDetail> response) {
                if(response!=null)
                {
                    if(response.isSuccess()) {
                        UserDetail userDetail= new UserDetail();
                        userDetail = response.getResult();
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.putExtra("userDetail",userDetail);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        showLoginErrorMessage(response.getError());
//                        Log.d("msg",response.getError());
                    }
                }
                else
                {
                    Log.d("msg","fail");
                }
                progressBar.setVisibility(View.GONE);
            }
        }).call();*/

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
