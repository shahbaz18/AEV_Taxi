package aev.sec.com.aev;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import aev.sec.com.aev.model.UserDetail;

public class OtpActivity extends AppCompatActivity {
    private EditText otpPin;
    private Button submit;
    private UserDetail userDetail;
    private ImageView back;
    private TextView email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("msg","otp");
        super.onCreate(savedInstanceState);
        Log.d("msg","otp1");
        setContentView(R.layout.activity_otp);
        userDetail = new UserDetail();
        userDetail = (UserDetail) getIntent().getSerializableExtra("userDetail");
        hideKeyboard();
        init();
    }

    private void init() {
        back=(ImageView)findViewById(R.id.back_icon);
        email=(TextView)findViewById(R.id.email);
        otpPin=(EditText)findViewById(R.id.txt_pin_entry);
        submit=(Button)findViewById(R.id.btn_submit);
        email.setText("at "+userDetail.getEmail());
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
                checkOtp();
            }
        });
    }

    private void checkOtp() {
        String otp= otpPin.getText().toString().trim();
        if(TextUtils.isEmpty(otp))
        {
            showLoginErrorMessage(getString(R.string.enter_otp));
            return;
        }

        if(!otp.equalsIgnoreCase(userDetail.getOtp()))
        {
            showLoginErrorMessage(getString(R.string.enter_correct_otp));
            return;
        }

        if(userDetail.getType().equalsIgnoreCase("forgot")) {
            Intent intent = new Intent(OtpActivity.this, NewPasswordActivity.class);
            intent.putExtra("userDetail", userDetail);
            startActivity(intent);
        }else if(userDetail.getType().equalsIgnoreCase("register"))
        {
            Intent intent = new Intent(OtpActivity.this, OfficeAddressActivity.class);
            intent.putExtra("userDetail", userDetail);
            startActivity(intent);
        }
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

