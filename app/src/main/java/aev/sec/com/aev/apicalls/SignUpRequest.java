package aev.sec.com.aev.apicalls;

import aev.sec.com.aev.interfaces.CallbackHandler;
import aev.sec.com.aev.model.UserDetail;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SignUpRequest implements Callback<ApiResponse<UserDetail>>
{

    private CallbackHandler<ApiResponse<UserDetail>> mCallback;
    private UserDetail userDetail;
    public SignUpRequest(UserDetail userDetail,CallbackHandler<ApiResponse<UserDetail>> mCallback)
    {
        this.userDetail = userDetail;
        this.mCallback = mCallback;
    }

    public void call() {
        try {
            RetrofitCalls retrofitCalls = RetrofitClient.getRetrofitCalls();
            Call<ApiResponse<UserDetail>> registerResponseCall = retrofitCalls.register(userDetail);
            registerResponseCall.enqueue(this);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onResponse(Call<ApiResponse<UserDetail>> call, Response<ApiResponse<UserDetail>> response) {
        mCallback.onResponse(response.body());
    }

    @Override
    public void onFailure(Call<ApiResponse<UserDetail>> call, Throwable t) {
        mCallback.onResponse(null);
    }
}
