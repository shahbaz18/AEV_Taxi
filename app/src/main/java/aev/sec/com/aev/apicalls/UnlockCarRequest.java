package aev.sec.com.aev.apicalls;

import aev.sec.com.aev.interfaces.CallbackHandler;
import aev.sec.com.aev.model.AccessCode;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Shahbaz on 2018-11-22.
 */

public class UnlockCarRequest implements Callback<String> {

    private AccessCode accessCode;
    private CallbackHandler<String> mCallback;

    public UnlockCarRequest(AccessCode accessCode, CallbackHandler<String> mCallback)
    {
        this.accessCode = accessCode;
        this.mCallback = mCallback;
    }

    public void call() {
        try {
            RetrofitCalls retrofitCalls = RetrofitClient.getRetrofitCalls();
            Call<String> registerResponseCall = retrofitCalls.unlockCar(accessCode);
            registerResponseCall.enqueue(this);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onResponse(Call<String> call, Response<String> response) {
        mCallback.onResponse(response.body());
    }

    @Override
    public void onFailure(Call<String> call, Throwable t) {

    }
}
