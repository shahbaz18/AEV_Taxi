package aev.sec.com.aev.apicalls;


import aev.sec.com.aev.model.AccessCode;
import aev.sec.com.aev.model.BookTaxiRequestBody;
import aev.sec.com.aev.model.BookedTripDetails;
import aev.sec.com.aev.model.Example;
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
    Call<ApiResponse<UserDetail>> otp(@Body UserDetail userDetail);

    @POST("register")
    Call<ApiResponse<UserDetail>> register(@Body UserDetail userDetail);

    @POST("login")
    Call<ApiResponse<UserDetail>> logIn(@Body UserDetail userDetail);

    @POST("forgot_password")
    Call<ApiResponse<UserDetail>> forgotPassword(@Body UserDetail userDetail);

    @POST("change_password")
    Call<ApiResponse<UserDetail>> changePassword(@Body UserDetail userDetail);

    @GET("api/directions/json?key=AIzaSyD_MoZW7prXb5W8P42AfYzJQj6tPPRpraQ")
    Call<Example> retrofitMaps(@Query("units") String units, @Query("origin") String origin, @Query("destination") String destination, @Query("mode") String mode);

    @POST("AEVAndroidTripCost")
    Call<ApiResponse<Pricing>> getCarPricing(@Body TripDetails tripDetails);

    @POST("AEVAndroidTaxiSelection")
    Call<ApiResponse<BookedTripDetails>> bookTaxi(@Body BookTaxiRequestBody bookTaxiRequestBody);

    @POST("AEVAndroidTripPlanned")
    Call<ApiResponse<String>> unlockCar(@Body AccessCode accessCodeRequest);

    //AIzaSyD_MoZW7prXb5W8P42AfYzJQj6tPPRpraQ

    // older AIzaSyBuo1K8ZSuh6efkwzLUmdQQMTNoMEMhCus
}
