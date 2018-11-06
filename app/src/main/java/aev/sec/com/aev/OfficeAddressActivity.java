package aev.sec.com.aev;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.IOException;
import java.util.List;

import aev.sec.com.aev.model.UserDetail;

public class OfficeAddressActivity extends AppCompatActivity {
private EditText userOfficeAddress,userOfficePostalCode,userOfficeCity,userProvince;
private TextView skip;
private Button next;
private UserDetail userDetail;
private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office_address);
        userDetail = new UserDetail();
        userDetail = (UserDetail) getIntent().getSerializableExtra("userDetail");
        hideKeyboard();
        init();
    }

    private void init() {
        back=(ImageView)findViewById(R.id.back_icon);
        userOfficeAddress=(EditText)findViewById(R.id.input_office_address);
        userOfficePostalCode=(EditText)findViewById(R.id.input_office_postal_code);
        userOfficeCity=(EditText)findViewById(R.id.input_office_city);
        userProvince=(EditText)findViewById(R.id.input_province);
        skip=(TextView) findViewById(R.id.skip);
        next=(Button) findViewById(R.id.btn_next);
        userProvince.setOnFocusChangeListener(new View.OnFocusChangeListener() {
    @Override
    public void onFocusChange(View view, boolean b) {
        if(b) {
            openPlacePicker(view, 111);
        }
    }
});

        userOfficeCity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    openPlacePicker(view, 110);
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OfficeAddressActivity.this, PaymentsDetailsActivity.class);
                intent.putExtra("type","type");
                intent.putExtra("userDetail",userDetail);
                startActivity(intent);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                officeDetail();
            }
        });
    }

    private void openPlacePicker(View view, int id) {
        findPlace(view,id);
    }

    private void findPlace(View view, int id) {
        try {
            Intent intent =
                    new PlaceAutocomplete
                            .IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, id);
        } catch (GooglePlayServicesRepairableException e) {
            Toast.makeText(this, getString(R.string.unable_to_search), Toast.LENGTH_SHORT).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(this, getString(R.string.unable_to_search), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK)
        {
        if (requestCode==110)
        {

                Place place = PlacePicker.getPlace(this, data);
                String placeName = String.format("%s", place.getName());
                userOfficeCity.setText(placeName);
            }else if (requestCode==111)
        {

            Place place = PlacePicker.getPlace(this, data);
            String placeName = String.format("%s", place.getName());
            userProvince.setText(placeName);
        }
        }
    }

    private void officeDetail() {
        String officeAddress,officePostalCode,officeCity,province;
        officeAddress= userOfficeAddress.getText().toString().trim();
        officePostalCode= userOfficePostalCode.getText().toString().trim();
        officeCity= userOfficeCity.getText().toString().trim();
        province= userProvince.getText().toString().trim();

        if (TextUtils.isEmpty(officeAddress))
        {
            showLoginErrorMessage(getString(R.string.enter_your_office_address));
            return;
        }

        if (TextUtils.isEmpty(officePostalCode))
        {
            showLoginErrorMessage(getString(R.string.enter_your_office_postalCode));
            return;
        }

        if (TextUtils.isEmpty(officeCity))
        {
            showLoginErrorMessage(getString(R.string.enter_your_office_city));
            return;
        }

        if (TextUtils.isEmpty(province))
        {
            showLoginErrorMessage(getString(R.string.enter_your_province));
            return;
        }

        userDetail.setOfficeAddress(officeAddress);
        userDetail.setOfficePostalCode(officePostalCode);
        userDetail.setOfficeCity(officeCity);
        userDetail.setProvince(province);

        Intent intent = new Intent(OfficeAddressActivity.this, PaymentsDetailsActivity.class);
        intent.putExtra("userDetail",userDetail);
        startActivity(intent);

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
