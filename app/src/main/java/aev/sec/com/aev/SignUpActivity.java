package aev.sec.com.aev;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.SyncStateContract;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import aev.sec.com.aev.apicalls.ApiResponse;
import aev.sec.com.aev.apicalls.SignUpOtpRequest;
import aev.sec.com.aev.apicalls.SignUpRequest;
import aev.sec.com.aev.interfaces.CallbackHandler;
import aev.sec.com.aev.model.UserDetail;

public class SignUpActivity extends AppCompatActivity {
    EditText userName,password,email,homeAddress,postalCode,city,phone;
    Button next;
    TextInputLayout cityLayout;
    ProgressBar progressBar;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        hideKeyboard();
        init();
    }

    private void init() {
      back=(ImageView)findViewById(R.id.back_icon);
      email = (EditText)findViewById(R.id.input_email);
      password =(EditText) findViewById(R.id.input_password);
      userName=(EditText)findViewById(R.id.input_user_name);
      homeAddress = (EditText)findViewById(R.id.input_home_address);
      postalCode =(EditText)findViewById(R.id.input_postal_code);
      city=(EditText)findViewById(R.id.input_city);
      cityLayout=(TextInputLayout)findViewById(R.id.city_layout);
      phone=(EditText)findViewById(R.id.input_phone);
      next=(Button)findViewById(R.id.btn_next);
      progressBar=(ProgressBar)findViewById(R.id.progress_bar);
      next.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              hideKeyboard();
              register();
          }
      });
      city.setOnFocusChangeListener(new View.OnFocusChangeListener() {
          @Override
          public void onFocusChange(View view, boolean b) {
              if(b) {
                  openPlacePicker(view);
              }
          }
      });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void openPlacePicker(View view) {
        findPlace(view);
    }

    private void findPlace(View view) {
        try {
            Intent intent =
                    new PlaceAutocomplete
                            .IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, 110);
        } catch (GooglePlayServicesRepairableException e) {
            Toast.makeText(this, getString(R.string.unable_to_search), Toast.LENGTH_SHORT).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(this, getString(R.string.unable_to_search), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==110)
        {
            if (resultCode==RESULT_OK)
            {
                Place place = PlacePicker.getPlace(this, data);
                String placeName = String.format("%s", place.getName());
                city.setText(placeName);
            }
        }

    }

    private void register() {
        final String userEmail,userPassword,name,userHomeAddress,userPostalCode,userCity,userPhone;
        userEmail= email.getText().toString().trim();
        userPassword= password.getText().toString().trim();
        name= userName.getText().toString().trim();
        userHomeAddress= homeAddress.getText().toString().trim();
        userPostalCode= postalCode.getText().toString().trim();
        userCity= city.getText().toString().trim();
        userPhone= phone.getText().toString().trim();

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
        if (userPassword.length()<6)
        {
            showLoginErrorMessage(getString(R.string.password_characters_length));
            return;
        }

        if (TextUtils.isEmpty(name))
        {
            showLoginErrorMessage(getString(R.string.enter_your_name));
            return;
        }

        if (TextUtils.isEmpty(userHomeAddress))
        {
            showLoginErrorMessage(getString(R.string.enter_your_home_address));
            return;
        }

        if (TextUtils.isEmpty(userPostalCode))
        {
            showLoginErrorMessage(getString(R.string.enter_your_postalCode));
            return;
        }

        if (TextUtils.isEmpty(userCity))
        {
            showLoginErrorMessage(getString(R.string.enter_your_city));
            return;
        }

        if (TextUtils.isEmpty(userPhone)|| userPhone.length()<10)
        {
            showLoginErrorMessage(getString(R.string.enter_your_phone));
            return;
        }

        final UserDetail userDetail = new UserDetail();
        userDetail.setEmail(userEmail);
        progressBar.setVisibility(View.VISIBLE);
        new SignUpOtpRequest(userDetail, new CallbackHandler<UserDetail>() {
            @Override
            public void onResponse(UserDetail response) {
                if(response!=null)
                {

                        UserDetail userDetails= new UserDetail();
                        userDetails = response;
                        userDetail.setOtp(userDetails.getOtp());
                        userDetail.setType("register");
                        userDetail.setUserName(name);
                        userDetail.setPassword(userPassword);
                        userDetail.setHomeAddress(userHomeAddress);
                        userDetail.setPostalCode(userPostalCode);
                        userDetail.setCity(userCity);
                        userDetail.setPhoneNumber(userPhone);

                        Intent intent = new Intent(SignUpActivity.this, OtpActivity.class);
                        intent.putExtra("userDetail",userDetail);
                        startActivity(intent);
                }
                else
                {
                    showLoginErrorMessage("Sign Up otp Error");
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
