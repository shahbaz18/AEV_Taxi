package aev.sec.com.aev.apicalls;


import rx.Observable;
import aev.sec.com.aev.model.AccessCode;
import aev.sec.com.aev.model.BookTaxiRequestBody;
import aev.sec.com.aev.model.BookedTripDetails;
import aev.sec.com.aev.model.Example;
import aev.sec.com.aev.model.LoginRequestBody;
import aev.sec.com.aev.model.LoginResponse;
import aev.sec.com.aev.model.Pricing;
import aev.sec.com.aev.model.TripDetails;
import aev.sec.com.aev.model.UserDetail;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitCalls {
//    @GET("profile/login.php")
//    Call<ApiResponse<Student>> getStudent();

    @POST("register_validation")
    Call<UserDetail> otp(@Body UserDetail userDetail);

    @POST("register")
    Call<UserDetail> register(@Body UserDetail userDetail);

    @POST("AEVAndroidLogin")
    Call<LoginResponse> logIn(@Body LoginRequestBody userDetail);

    @POST("forgot_password")
    Call<UserDetail> forgotPassword(@Body UserDetail userDetail);

    @POST("change_password")
    Call<UserDetail> changePassword(@Body UserDetail userDetail);

    @GET("api/directions/json?key=AIzaSyD_MoZW7prXb5W8P42AfYzJQj6tPPRpraQ")
    Call<Example> retrofitMaps(@Query("units") String units, @Query("origin") String origin, @Query("destination") String destination, @Query("mode") String mode);

    @POST("AEVAndroidTripCost")
    Call<Pricing> getCarPricing(@Body TripDetails tripDetails);

    @POST("AEVAndroidTaxiSelection")
    Call<BookedTripDetails> bookTaxi(@Body BookTaxiRequestBody bookTaxiRequestBody);

    @POST("AEVAndroidTripPlanned")
    Call<String> unlockCar(@Body AccessCode accessCodeRequest);

    //AIzaSyD_MoZW7prXb5W8P42AfYzJQj6tPPRpraQ

    // older AIzaSyBuo1K8ZSuh6efkwzLUmdQQMTNoMEMhCus
}
