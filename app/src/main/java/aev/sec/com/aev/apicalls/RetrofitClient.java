package aev.sec.com.aev.apicalls;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient
{
    public static RetrofitCalls getRetrofitCalls() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.246:8084/AEV/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(RetrofitCalls.class);
    }

    public static RetrofitCalls getRetrofitMapCalls() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(RetrofitCalls.class);
    }
}
