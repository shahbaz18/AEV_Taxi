package aev.sec.com.aev.apicalls;

import android.content.Context;


import aev.sec.com.aev.interfaces.CallbackHandler;
import aev.sec.com.aev.model.Example;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetDistanceRoute  implements Callback<Example> {
    private String units,origin,destination,type;
    private final CallbackHandler<Example> callbackHandler;

    public GetDistanceRoute(Context context, String units,String origin,String destination, String type, CallbackHandler<Example> callbackHandler) {

        this.units=units;
        this.origin=origin;
        this.destination=destination;
        this.type=type;
        this.callbackHandler=callbackHandler;

    }



    public void call() throws Exception {
        final RetrofitCalls apiInterface = RetrofitClient.getRetrofitMapCalls();
        Call<Example> registerResponseCall =apiInterface.retrofitMaps(units,origin,destination,type);
        registerResponseCall.enqueue(this);
    }

    @Override
    public void onResponse(Call<Example> call, Response<Example> response) {
            callbackHandler.onResponse(response.body());
    }

    @Override
    public void onFailure(Call<Example> call, Throwable t) {
        callbackHandler.onResponse(null);
    }

}