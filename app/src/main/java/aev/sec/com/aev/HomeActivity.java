package aev.sec.com.aev;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import aev.sec.com.aev.apicalls.ApiResponse;
import aev.sec.com.aev.apicalls.BookTaxiRequest;
import aev.sec.com.aev.apicalls.GetDistanceRoute;
import aev.sec.com.aev.apicalls.TripCostRequest;
import aev.sec.com.aev.interfaces.CallbackHandler;
import aev.sec.com.aev.model.BookTaxiRequestBody;
import aev.sec.com.aev.model.BookedTripDetails;
import aev.sec.com.aev.model.Example;
import aev.sec.com.aev.model.Pricing;
import aev.sec.com.aev.model.TripDetails;
import aev.sec.com.aev.model.UserDetail;
import aev.sec.com.aev.sharedPreference.SharedPreferenceUtility;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,
        VoiceEnabledDialogBox.VoiceEnabledDialogListener, TtsService.ServiceCallbacks
{

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private ImageView openDrawer;
    private SharedPreferenceUtility mSharedPreferences;
    private UserDetail userDetail;
    private RelativeLayout carlayout,carSmall,car;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private ProgressBar progressBar;
    private TextView pickUpLocation,destinationLocation,distance,time,price,smallCarPrice;
    private LatLng srcLatlng,decLatLng;
    public CurrentLocationFinder mLocationFinder;
    private boolean isCurrentLocationFound;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest.Builder mBuilder;
    final static int REQUEST_LOCATION = 199;
    final static int PREMISSION_GRANTED=121;
    private Polyline line;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private String[] permissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
            ,Manifest.permission.ACCESS_COARSE_LOCATION
    };
    protected ProgressDialog mProgressDialog;
    private RelativeLayout myLocationButton;

    private TtsService ttsService;
    private boolean bound = false;
    private Intent ttsserviceIntent;
    private String pickAdd;
    private String desAdd;

    private ImageView pickMic;
    private ImageView destMic;
    public Pricing pricing;

//    private boolean pickupAddFlag = false;
//    private boolean pickupAddConfirmFlag = false;
//    private boolean desAddFlag = false;
//    private boolean desAddConfirmFlag = false;

    private static final int SPEECH_RECOGINIZE_PICKUP_ADD = 221;
    private static final int SPEECH_RECOGINIZE_DESTINATION_ADD = 222;
    private static final int SPEECH_RECOGINIZE_PICKUP_CONFIRM = 223;
    private static final int SPEECH_RECOGINIZE_DESTINATION_CONFIRM = 224;
    private static final int SPEECH_RECOGINIZE_CAR_SELECT = 225;
    private static final int SPEECH_RECOGINIZE_CAR_SELECT_CONFIRM = 226;

    public boolean checkGPSStatus(Context context) {
        return mLocationFinder.canGetLocation(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mLocationFinder = CurrentLocationFinder.getInstance();
        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        userDetail = new UserDetail();
        userDetail = (UserDetail) getIntent().getSerializableExtra("userDetail");
        srcLatlng=null;
        decLatLng=null;
        hideKeyboard();
        init();
        checkPermissionForMarshMellow(permissions);
       //checkGPSStatus();

    }

    private void createMap() {
        FragmentManager fm = getSupportFragmentManager();
        mapFragment = (SupportMapFragment) fm.findFragmentByTag("map_fragment");
        if (mapFragment == null) {
            mapFragment = new SupportMapFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.map, mapFragment, "map_fragment");
            ft.commit();
            fm.executePendingTransactions();
        }
        progressBar.setVisibility(View.GONE);
        if (mapFragment != null) {
            mapFragment.getMapAsync(HomeActivity.this);
        }
    }

    private void init() {
        mSharedPreferences=new SharedPreferenceUtility(this);
        progressBar=(ProgressBar)findViewById(R.id.progress_bar);
        pickUpLocation=(TextView)findViewById(R.id.pick_up_location);
        destinationLocation=(TextView)findViewById(R.id.destination_location);
        distance=(TextView)findViewById(R.id.distance);
        time=(TextView)findViewById(R.id.time);
        price=(TextView)findViewById(R.id.price);
        car=(RelativeLayout)findViewById(R.id.car_layout);
        carSmall=(RelativeLayout)findViewById(R.id.car_small_layout);
        carlayout=(RelativeLayout)findViewById(R.id.car_main_layout);
        smallCarPrice=(TextView)findViewById(R.id.price_small_car);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        openDrawer =(ImageView)findViewById(R.id.drawer_menu);
        myLocationButton = (RelativeLayout) findViewById(R.id.my_location_button);
        View headerView = navigationView.getHeaderView(0);
        TextView headText = (TextView)headerView.findViewById(R.id.headText);

        pickMic = (ImageView)findViewById(R.id.iv_Mic_pick);
        destMic = (ImageView)findViewById(R.id.iv_Mic_dest);
        ttsserviceIntent = new Intent(getApplicationContext(),TtsService.class);
        bindService(ttsserviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomeActivity.this,"Booking success",Toast.LENGTH_SHORT).show();
            }
        });
        carSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomeActivity.this,"Booking success",Toast.LENGTH_SHORT).show();
            }
        });
        openDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        navigationView.setNavigationItemSelectedListener(this);

        if(userDetail!=null) {
            mSharedPreferences.setUserEmail(userDetail.getEmail());
            mSharedPreferences.setUserName(userDetail.getUserName());
            headText.setText(mSharedPreferences.getUserName());
        }

        pickUpLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPlacePicker(view,110);
            }
        });

        destinationLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPlacePicker(view,111);

            }
        });

        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrentLocationOnMap();
            }
        });

//        DialogFragment newFragment = new VoiceEnabledDialogBox();
//        newFragment.show(getSupportFragmentManager(), "VoiceMode");

        pickMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPickUpAddressFromVoice();
            }
        });

        destMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDestinationAddressFromVoice();
            }
        });

    }

    private void getDestinationAddressFromVoice() {
        showLocationLoader("Speaking");
        ttsserviceIntent.putExtra("TextToSpeak","Please Speak Destination Address");
        ttsserviceIntent.putExtra("ID","desAdd");
        startService(ttsserviceIntent);
    }

    private void getPickUpAddressFromVoice() {
        showLocationLoader("Speaking");
//        ttsserviceIntent = new Intent(getApplicationContext(),TtsService.class);
        ttsserviceIntent.putExtra("TextToSpeak","Please Speak Pick Up Address");
        ttsserviceIntent.putExtra("ID","pickAdd");
        startService(ttsserviceIntent);
    }

    private void drawMarker() {

        mMap.clear();
        if(srcLatlng!=null)
        {
            MarkerOptions options = new MarkerOptions();
            options.position(srcLatlng);
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mMap.addMarker(options);
        }

        if(decLatLng!=null)
        {
            MarkerOptions options = new MarkerOptions();
            options.position(decLatLng);
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mMap.addMarker(options);
        }

    }

    private void openPlacePicker(View view,int id) {
        findPlace(view,id);
    }

    private void findPlace(View view,int id) {
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

        switch (requestCode){
            case REQUEST_LOCATION: {
                if (resultCode==RESULT_OK)
                {
                    checkGPSStatus();
                }
                break;
            }
            case SPEECH_RECOGINIZE_PICKUP_ADD: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    this.pickAdd = text.get(0);
                    showLocationLoader("Speaking");
                    ttsserviceIntent.putExtra("TextToSpeak","Select "+ text.get(0).toString() +" as Pick Up Address");
                    ttsserviceIntent.putExtra("ID","pickAddConfirm");
                    startService(ttsserviceIntent);
//                    speechRecoginationResults(text);
                }
                break;
            }
            case SPEECH_RECOGINIZE_PICKUP_CONFIRM: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if(text.get(0).equalsIgnoreCase("yes")){
                        List<LatLng> cordinates = findLatLong(this.pickAdd);
                        if(cordinates != null && cordinates.size()>0){
                            pickUpLocation.setText(this.pickAdd);
                            srcLatlng = cordinates.get(0);
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(srcLatlng,15));
                            performAction();
                        }

//                        ttsserviceIntent.putExtra("TextToSpeak","Please Speak Destination Address");
//                        ttsserviceIntent.putExtra("ID","desAdd");
//                        startService(ttsserviceIntent);
                    }else {
                        this.pickAdd = "";
                        // ask pick add again
                        showLocationLoader("Speaking");
                        ttsserviceIntent.putExtra("TextToSpeak","Please Speak Pick Up Address");
                        ttsserviceIntent.putExtra("ID","pickAdd");
                        startService(ttsserviceIntent);
                    }
                }
                break;
            }
            case SPEECH_RECOGINIZE_DESTINATION_ADD: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    this.desAdd = text.get(0);
                    showLocationLoader("Speaking");
                    ttsserviceIntent.putExtra("TextToSpeak","Select "+ text.get(0).toString() +" as Destination Address");
                    ttsserviceIntent.putExtra("ID","desAddConfirm");
                    startService(ttsserviceIntent);
                    Log.e("@@@","done");
                }
                break;
            }
            case SPEECH_RECOGINIZE_DESTINATION_CONFIRM: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if(text.get(0).equalsIgnoreCase("yes")){


                        List<LatLng> cordinates = findLatLong(this.desAdd);
                        if(cordinates != null && cordinates.size()>0){
                            destinationLocation.setText(this.desAdd);
                            decLatLng = cordinates.get(0);
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(decLatLng,15));
                            performAction();
                        }
                        // book
//                        ttsserviceIntent.putExtra("TextToSpeak","Please Select Type of Car. Big. or . Small");
//                        ttsserviceIntent.putExtra("ID","carSize");
//                        startService(ttsserviceIntent);
                    }else {
                        this.desAdd = "";
                        // ask des add again
                        showLocationLoader("Speaking");
                        ttsserviceIntent.putExtra("TextToSpeak","Please Speak Destination Address");
                        ttsserviceIntent.putExtra("ID","desAdd");
                        startService(ttsserviceIntent);
                    }
                }
                break;
            }
            case SPEECH_RECOGINIZE_CAR_SELECT:{
                if (resultCode == RESULT_OK && null != data) {
                    showLocationLoader("Speaking");
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    ttsserviceIntent.putExtra("TextToSpeak","Did you select "+text.get(0)+" car");
                    ttsserviceIntent.putExtra("ID","carSizeConfirm");
                    startService(ttsserviceIntent);
                }
                break;
            }
            case SPEECH_RECOGINIZE_CAR_SELECT_CONFIRM:{
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if(text.get(0).equalsIgnoreCase("yes")){
                        // speak distance and time
                        showLocationLoader("Speaking");
                        ttsserviceIntent.putExtra("TextToSpeak",this.time.getText() + " and "+this.distance.getText());
                        ttsserviceIntent.putExtra("ID","DistanceDetails");
                        startService(ttsserviceIntent);
                    }else{
                        showLocationLoader("Speaking");
                        ttsserviceIntent.putExtra("TextToSpeak","THe cost for Small Car is "+ this.pricing.getSmallCarCost()+ " dollars and for Big Car is "+ this.pricing.getBigCarCost()+ ". Please Select Type of Car. Big. or . Small");
                        ttsserviceIntent.putExtra("ID","carSize");
                        startService(ttsserviceIntent);
                    }
                }
                break;
            }

        }
//        if (requestCode==REQUEST_LOCATION)
//        {
//            if (resultCode==RESULT_OK)
//            {
//                checkGPSStatus();
//            }
//        }

        if ((requestCode == 110 || requestCode == 111) && resultCode==RESULT_OK)
        {
            if (requestCode==110)
            {
                Place place = PlacePicker.getPlace(this, data);
                String placeName = String.format("%s", place.getName());
                srcLatlng = place.getLatLng();
                pickUpLocation.setText(placeName);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(srcLatlng,15));
            }else if (requestCode==111)
            {
                Place place = PlacePicker.getPlace(this, data);
                String placeName = String.format("%s", place.getName());
                decLatLng = place.getLatLng();
                destinationLocation.setText(placeName);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(decLatLng,15));
            }
            performAction();

        }
    }

    private void performAction() {
        drawMarker();

        if(srcLatlng!=null && decLatLng!=null)
        {
            build_retrofit_and_get_response("driving");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.navigation_item_payment)
        {
            openPaymentActivity();
        }

        else if (id == R.id.navigation_item_logout) {
            logOut();

        }
        else if (id==R.id.navigation_item_change_password)
        {
           changePassword();
        }
        else if (id==R.id.navigation_item_rides)
        {

        }
        else if (id==R.id.navigation_item_trips)
        {

        }
        else if (id==R.id.navigation_item_settings)
        {

        }
        else if (id==R.id.navigation_item_help)
        {

        }
        mDrawerLayout.closeDrawers();
        return true;
    }

    private void openPaymentActivity() {
        Intent intent = new Intent(HomeActivity.this, PaymentsDetailsActivity.class);
        intent.putExtra("type",getString(R.string.payment));
        startActivity(intent);
    }

    private void changePassword() {
        startActivity(new Intent(HomeActivity.this, ChangePasswordActivity.class));
    }

    private void logOut() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

    mSharedPreferences.setUserName(null);
    mSharedPreferences.setUserEmail(null);
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();

            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    public void checkGPSStatus() {
        if(doesUserHavePermission()) {
            if (!checkGPSStatus(HomeActivity.this)) {
                showGpsDialogAndGetLocation();

            } else {
                createMap();
                mLocationFinder.getLocation(this, this);
                showLocationLoader("Fetching your current location");
            }
        }
    }

    public void showLocationLoader(String message){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    public void hideLocationLoader(){
        if (mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
    
    public boolean doesUserHavePermission(){
        int result = this.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void showGpsDialogAndGetLocation() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(HomeActivity.this)
                .addOnConnectionFailedListener(HomeActivity.this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        //initialize the mBuilder and add location request paramenter like HIGH Aurracy
        mLocationRequest = LocationRequest.create()
                .setInterval(10 * 60 * 1000) // every 10 minutes
                .setExpirationDuration(10 * 1000) // After 10 seconds
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        // set mBuilder to always true (Shows the dialog after never operation too)
        mBuilder.setAlwaysShow(true);

        // Then check whether current location settings are satisfied:
        try {


            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, mBuilder.build());

            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    // final LocationSettingsStates locationSettingsStates = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can initialize location
                            // requests here.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(
                                        HomeActivity.this,
                                        PREMISSION_GRANTED);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(
                                        HomeActivity.this,
                                        REQUEST_LOCATION);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void build_retrofit_and_get_response(final String type) {


        if(isNetworkAvailable(this))
        {
            try {
                new GetDistanceRoute(this, "metric",srcLatlng.latitude + "," + srcLatlng.longitude,decLatLng.latitude + "," + decLatLng.longitude,type, new CallbackHandler<Example>() {
                    @Override
                    public void onResponse(Example example) {
//                        if (isApiResponseErrorFreeElseHandle(apiResponsePojo)) {
                        Example result = example;
                        if(result!=null) {
                            if (line != null) {
                                line.remove();
                            }
                            carlayout.setVisibility(View.VISIBLE);
                            for (int i = 0; i < result.getRoutes().size(); i++) {
                                String distances = result.getRoutes().get(i).getLegs().get(i).getDistance().getText();
                                String durration = result.getRoutes().get(i).getLegs().get(i).getDuration().getText();

                                String currentString = distances;
                                String[] separatedSpace = currentString.split(" ");
                               String s= separatedSpace[0];
                                if (s.contains(",")) {
                                    String[] separatedComa=s.split(",");
                                    String value1=separatedComa[0];
                                    String value2=separatedComa[1];
                                    s=value1+value2;
                                }
                                double calculateDistance=Double.parseDouble(s);
                                time.setText("Duration:" + durration);
                                distance.setText("Distance:" + distances );
//                                price.setText("Price:" +" "+"$"+" "+calculateDistance*5);
//                                smallCarPrice.setText("Price:" +" "+"$"+" "+calculateDistance*4);
                                price.setText("Big Car");
                                smallCarPrice.setText("Small Car");
//                                mBinding.showDistanceTime.setText("Distance:" + distance + ", Duration:" + time);
                                String encodedString = result.getRoutes().get(0).getOverviewPolyline().getPoints();
                                List<LatLng> list = decodePoly(encodedString);
                                line = mMap.addPolyline(new PolylineOptions()
                                        .addAll(list)
                                        .width(10)
                                        .color(getResources().getColor(R.color.colorPrimary))
                                        .geodesic(true)
                                );
                            }
                            //call APi to get cost



//                            Intent bookedTaxiDetails = new Intent(HomeActivity.this,BookedTaxiDetailsActivity.class);
//                            startActivity(bookedTaxiDetails);
//                            finish();

                            getTripCost();
                        }
                        else {
                            Toast.makeText(HomeActivity.this,"Error",Toast.LENGTH_SHORT).show();
                            carlayout.setVisibility(View.GONE);
                        }

//                        }
                    }
                }).call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }

    public static boolean isNetworkAvailable(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isAvailable();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLocationFinder.setLatitude(location.getLatitude());
        mLocationFinder.setLongitude(location.getLongitude());
        unregisterLocationListener();
        setCurrentLocationOnMap();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideLocationLoader();
            }
        });

    }

    private void setCurrentLocationOnMap() {
        LatLng location = new LatLng(mLocationFinder.getLatitude(), mLocationFinder.getLongitude());

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);

            String knownName = addresses.get(0).getFeatureName();
            srcLatlng = location;
            pickUpLocation.setText(knownName);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(srcLatlng,15));
            performAction();
        }
        catch (Exception e)
        {

        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void unregisterLocationListener(){
        try {
            mLocationFinder.unRegisterLocationListener(this);
        }catch (Exception ex) {}
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterLocationListener();
        unbindService(ttsService);
    }

    public void checkPermissionForMarshMellow(String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
           ActivityCompat.requestPermissions(this, permissions, permissions.length);
        }
        else
        {
            checkGPSStatus();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults[0] == 0)
        {
            checkGPSStatus();
        }
        else
        {
            Toast.makeText(this, "Allow location permission from settings and try again", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

//        ttsserviceIntent = new Intent(getApplicationContext(),TtsService.class);
//        ttsserviceIntent.putExtra("TextToSpeak","Please Speak Pick Up Address");
//        ttsserviceIntent.putExtra("ID","pickAdd");
//        bindService(ttsserviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
//        startService(ttsserviceIntent);

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Toast.makeText(HomeActivity.this,"No clicked",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void TTSSericeCallback(String id) {
        if (id.equalsIgnoreCase("bookTaxi")){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideLocationLoader();
                }
            });

            callBookTaxiApi();
            // call booking api here
            //show loading icon.
        }else {
            startRecognizeSpeech(id);
        }

    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // cast the IBinder and get MyService instance
            TtsService.LocalBinder binder = (TtsService.LocalBinder) service;
            ttsService = binder.getService();
            bound = true;
            ttsService.setCallbacks(HomeActivity.this); // register
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

    private void startRecognizeSpeech(String id) {

        Intent recognizeSpeechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizeSpeechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

        int RESULT_SPEECH = -1;

        switch (id){
            case "pickAdd":{
                RESULT_SPEECH = SPEECH_RECOGINIZE_PICKUP_ADD;
                break;
            }
            case "pickAddConfirm":{
                RESULT_SPEECH = SPEECH_RECOGINIZE_PICKUP_CONFIRM;
                break;
            }
            case "desAdd":{
                RESULT_SPEECH = SPEECH_RECOGINIZE_DESTINATION_ADD;
                break;
            }
            case "desAddConfirm":{
                RESULT_SPEECH = SPEECH_RECOGINIZE_DESTINATION_CONFIRM;
                break;
            }
            case "carSize":{
                RESULT_SPEECH = SPEECH_RECOGINIZE_CAR_SELECT;
                break;
            }
            case "carSizeConfirm":{
                RESULT_SPEECH = SPEECH_RECOGINIZE_CAR_SELECT_CONFIRM;
                break;
            }
            case "DistanceDetails":{
                // call tts again booking Taxi.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLocationLoader();
                    }
                });
                bookTaxi();
                break;
            }

        }

        try {
            startActivityForResult(recognizeSpeechIntent, RESULT_SPEECH);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideLocationLoader();
                }
            });


        } catch (ActivityNotFoundException a) {
            Toast.makeText(
                    getApplicationContext(),
                    "Oops! First you must download \"Voice Search\" App from Store",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void bookTaxi() {
        ttsserviceIntent.putExtra("TextToSpeak","Please wait while we book your Taxi.");
        ttsserviceIntent.putExtra("ID","bookTaxi");
        startService(ttsserviceIntent);
        showLocationLoader("Speaking");
    }

    private void getTripCost() {
        progressBar.setVisibility(View.VISIBLE);
        TripDetails  tripDetails = new TripDetails();
        // send trip details.. Pending
        new TripCostRequest(tripDetails, new CallbackHandler<ApiResponse<Pricing>>() {


            @Override
            public void onResponse(ApiResponse<Pricing> response) {
                if(response!=null)
                {
                    if(response.isSuccess()) {
                        pricing = new Pricing();
                        pricing = response.getResult();
                        speakTripCost();
                    }
                    else
                    {
                        showLoginErrorMessage(response.getError());
                    }
                }
                else
                {
                    Log.d("msg","fail");
                }
                progressBar.setVisibility(View.GONE);
            }
        }).call();
    }

    private void speakTripCost() {
        showLocationLoader("Speaking");
        ttsserviceIntent.putExtra("TextToSpeak","THe cost for Small Car is "+ this.pricing.getSmallCarCost()+ " dollars and for Big Car is "+ this.pricing.getBigCarCost()+ ". Please Select Type of Car. Big. or . Small");
        ttsserviceIntent.putExtra("ID","carSize");
        startService(ttsserviceIntent);
    }

    private void showLoginErrorMessage(String string) {
        Snackbar.make(findViewById(R.id.container), string, Snackbar.LENGTH_LONG).show();
    }

    private void callBookTaxiApi() {
        progressBar.setVisibility(View.VISIBLE);
        BookTaxiRequestBody bookTaxiBody = new BookTaxiRequestBody();
        new BookTaxiRequest(bookTaxiBody, new CallbackHandler<ApiResponse<BookedTripDetails>>() {
            @Override
            public void onResponse(ApiResponse<BookedTripDetails> response) {
                if(response!=null)
                {
                    if(response.isSuccess()) {
                        // start new activity to show booked taxi details
                        Intent bookedTaxiDetails = new Intent(HomeActivity.this,BookedTaxiDetailsActivity.class);
                        startActivity(bookedTaxiDetails);
                        finish();

                    }
                    else
                    {
                        showLoginErrorMessage(response.getError());
                    }
                }
                else
                {
                    Log.d("msg","fail");
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public List<LatLng> findLatLong(String placeName){
        if(Geocoder.isPresent()){
            try {
                Geocoder gc = new Geocoder(this);
                List<Address> addresses= gc.getFromLocationName(placeName, 5); // get the found Address Objects
                List<LatLng> ll = new ArrayList<LatLng>(addresses.size()); // A list to save the coordinates if they are available
                for(Address a : addresses){
                    if(a.hasLatitude() && a.hasLongitude()){
                        ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                    }
                }
                return ll;
            } catch (IOException e) {

            }
        }
        return null;
    }

//    private void speechRecoginationResults(ArrayList<String> text) {
//
//        if (text != null && pickupAddFlag){
//            this.pickAdd = text.get(0);
//            ttsserviceIntent.putExtra("TextToSpeak","Select "+ text.get(0).toString() +" as Pick Up Address");
//            ttsserviceIntent.putExtra("ID","pickAddConfirm");
//            startService(ttsserviceIntent);
//            Log.e("@@@","done");
//        }else if (text != null && pickupAddConfirmFlag){
//            if(text.get(0).equalsIgnoreCase("yes")){
//                ttsserviceIntent.putExtra("TextToSpeak","Please Speak Destination Address");
//                ttsserviceIntent.putExtra("ID","desAdd");
//                startService(ttsserviceIntent);
//
//            }else {
//                this.pickAdd = "";
//                // ask pick add again
//                ttsserviceIntent.putExtra("TextToSpeak","Please Speak Pick Up Address");
//                ttsserviceIntent.putExtra("ID","pickAdd");
//                startService(ttsserviceIntent);
//            }
//
//        }else if (text != null && desAddFlag){
//            this.desAdd = text.get(0);
//            ttsserviceIntent.putExtra("TextToSpeak","Select "+ text.get(0).toString() +" as Destination Address");
//            ttsserviceIntent.putExtra("ID","desAddConfirm");
//            startService(ttsserviceIntent);
//            Log.e("@@@","done");
//        }else if (text != null && desAddConfirmFlag){
//            if(text.get(0).equalsIgnoreCase("yes")){
//                // book
//
//            }else {
//                this.desAdd = "";
//                // ask des add again
//                ttsserviceIntent.putExtra("TextToSpeak","Please Speak Destination Address");
//                ttsserviceIntent.putExtra("ID","desAdd");
//                startService(ttsserviceIntent);
//            }
//        }
//    }


}
