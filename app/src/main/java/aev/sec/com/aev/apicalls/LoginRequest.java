package aev.sec.com.aev.apicalls;


import aev.sec.com.aev.interfaces.CallbackHandler;
import aev.sec.com.aev.model.LoginRequestBody;
import aev.sec.com.aev.model.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginRequest implements Callback<LoginResponse>
{

    private CallbackHandler<LoginResponse> mCallback;
    private LoginRequestBody loginRequestBody;

    public LoginRequest(LoginRequestBody loginRequestBody,CallbackHandler<LoginResponse> mCallback)
    {
        this.loginRequestBody = loginRequestBody;
        this.mCallback = mCallback;
    }

    public void call() {
        try {
            RetrofitCalls retrofitCalls = RetrofitClient.getRetrofitCalls();
            Call<LoginResponse> registerResponseCall = retrofitCalls.logIn(loginRequestBody);
            registerResponseCall.enqueue(this);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
        mCallback.onResponse(response.body());
    }

    @Override
    public void onFailure(Call<LoginResponse> call, Throwable t) {
        mCallback.onResponse(null);
    }
}
