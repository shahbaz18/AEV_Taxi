package aev.sec.com.aev.apicalls;

import aev.sec.com.aev.interfaces.CallbackHandler;
import aev.sec.com.aev.model.UserDetail;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpOtpRequest implements Callback<UserDetail>
{

    private CallbackHandler<UserDetail> mCallback;
    private UserDetail userDetail;
    public SignUpOtpRequest(UserDetail userDetail,CallbackHandler<UserDetail> mCallback)
    {
        this.userDetail = userDetail;
        this.mCallback = mCallback;
    }

    public void call() {
        try {
            RetrofitCalls retrofitCalls = RetrofitClient.getRetrofitCalls();
            Call<UserDetail> registerResponseCall = retrofitCalls.otp(userDetail);
            registerResponseCall.enqueue(this);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onResponse(Call<UserDetail> call, Response<UserDetail> response) {
        mCallback.onResponse(response.body());
    }

    @Override
    public void onFailure(Call<UserDetail> call, Throwable t) {
        mCallback.onResponse(null);
    }
}
