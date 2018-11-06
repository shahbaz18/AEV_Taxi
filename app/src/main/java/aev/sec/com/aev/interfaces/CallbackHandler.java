package aev.sec.com.aev.interfaces;

import aev.sec.com.aev.model.Example;

public interface CallbackHandler<T>
{
    void onResponse(T response);

}
