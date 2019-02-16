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
import android.widget.TextView;

import aev.sec.com.aev.apicalls.ApiResponse;
import aev.sec.com.aev.apicalls.SignUpRequest;
import aev.sec.com.aev.interfaces.CallbackHandler;
import aev.sec.com.aev.model.UserDetail;

public class PaymentsDetailsActivity extends AppCompatActivity {
private TextView skip;
private UserDetail userDetail;
private ProgressBar progressBar;
private String type;
private Button next;
private ImageView back;
private EditText cardnumber,cardHolderName,cvv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments_details);
        userDetail = new UserDetail();
        type  = getIntent().getStringExtra("type");
        userDetail = (UserDetail) getIntent().getSerializableExtra("userDetail");
        hideKeyboard();
        init();
    }

    private void init() {
        back=(ImageView) findViewById(R.id.back_icon);
        progressBar =(ProgressBar)findViewById(R.id.progress_bar);
        skip =(TextView)findViewById(R.id.skip);
        cardnumber=(EditText)findViewById(R.id.input_card_number);
        cardHolderName=(EditText)findViewById(R.id.input_card_holder_name);
        cvv=(EditText)findViewById(R.id.input_cvv);
        next=(Button) findViewById(R.id.btn_next);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkpaymentDetails();
            }
        });
        if(type.equalsIgnoreCase(getString(R.string.payment)))
        {
            skip.setVisibility(View.GONE);
            next.setText(getString(R.string.btn_submit));
            next.setEnabled(false);

        }else
        {
            skip.setVisibility(View.VISIBLE);
            next.setText(getString(R.string.btn_next));
        }

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest();
            }
        });
    }

    private void checkpaymentDetails() {
        String userCardNumber,cardholderName,userCvv;
        userCardNumber= cardnumber.getText().toString().trim();
        cardholderName=cardHolderName.getText().toString().trim();
        userCvv=cvv.getText().toString().trim();
        if(TextUtils.isEmpty(userCardNumber))
        {
            showLoginErrorMessage(getString(R.string.enter_card_number));
            return;
        }
        if(TextUtils.isEmpty(cardholderName))
        {
            showLoginErrorMessage(getString(R.string.enter_card_holder_name));
            return;
        }
        if(TextUtils.isEmpty(userCvv))
        {
            showLoginErrorMessage(getString(R.string.enter_cvv));
            return;
        }


    }

    private void sendRequest() {
        progressBar.setVisibility(View.VISIBLE);
        new SignUpRequest(userDetail, new CallbackHandler<UserDetail>() {
            @Override
            public void onResponse(UserDetail response) {
                if(response!=null)
                {

                    UserDetail userDetails=new UserDetail();
                    userDetails= response;
                    Intent intent = new Intent(PaymentsDetailsActivity.this, HomeActivity.class);
                    intent.putExtra("userDetail",userDetails);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    showLoginErrorMessage("Error in Sign UP");
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
