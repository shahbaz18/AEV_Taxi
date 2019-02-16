package aev.sec.com.aev.apicalls;

import aev.sec.com.aev.interfaces.CallbackHandler;
import aev.sec.com.aev.model.BookTaxiRequestBody;
import aev.sec.com.aev.model.BookedTripDetails;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Shahbaz on 2018-11-17.
 */

public class BookTaxiRequest implements Callback<BookedTripDetails> {

    private CallbackHandler<BookedTripDetails> mCallback;
    private BookTaxiRequestBody bookTaxiRequestBody;


    public BookTaxiRequest(BookTaxiRequestBody bookTaxiRequestBody, CallbackHandler<BookedTripDetails> mCallback)
    {
        this.bookTaxiRequestBody = bookTaxiRequestBody;
        this.mCallback = mCallback;
    }

    public void call() {
        try {
            RetrofitCalls retrofitCalls = RetrofitClient.getRetrofitCalls();
            Call<BookedTripDetails> registerResponseCall = retrofitCalls.bookTaxi(bookTaxiRequestBody);
            registerResponseCall.enqueue(this);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onResponse(Call<BookedTripDetails> call, Response<BookedTripDetails> response) {
        mCallback.onResponse(response.body());
    }

    @Override
    public void onFailure(Call<BookedTripDetails> call, Throwable t) {

    }
}
