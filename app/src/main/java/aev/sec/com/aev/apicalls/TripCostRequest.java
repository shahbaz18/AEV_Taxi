package aev.sec.com.aev.apicalls;

import aev.sec.com.aev.interfaces.CallbackHandler;
import aev.sec.com.aev.model.Pricing;
import aev.sec.com.aev.model.TripDetails;
import aev.sec.com.aev.model.UserDetail;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Shahbaz on 2018-11-15.
 */

public class TripCostRequest implements Callback<Pricing> {

    private CallbackHandler<Pricing> mCallback;
    private TripDetails tripDetails;

    public TripCostRequest(TripDetails tripDetails, CallbackHandler<Pricing> mCallback)
    {
        this.tripDetails = tripDetails;
        this.mCallback = mCallback;
    }

    public void call() {
        try {
            RetrofitCalls retrofitCalls = RetrofitClient.getRetrofitCalls();
            Call<Pricing> registerResponseCall = retrofitCalls.getCarPricing(tripDetails);
            registerResponseCall.enqueue(this);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onResponse(Call<Pricing> call, Response<Pricing> response) {
        mCallback.onResponse(response.body());
    }

    @Override
    public void onFailure(Call<Pricing> call, Throwable t) {

    }
}
