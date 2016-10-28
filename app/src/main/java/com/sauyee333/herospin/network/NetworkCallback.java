package com.sauyee333.herospin.network;

import java.io.IOException;

import okhttp3.Callback;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sauyee on 28/10/16.
 */

public abstract class NetworkCallback<T> implements retrofit2.Callback<T>, Callback {

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            T responseObj = response.body();
            onSuccess(responseObj);
        } else {
            onFailure(false, "Unknown error", "");
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable throwable) {
        onFailure(true, throwable.toString(), "");
    }

    @Override
    public void onFailure(okhttp3.Call call, IOException e) {
        onFailure(true, e.toString(), "");
    }

    @Override
    public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
        if (response.isSuccessful()) {
            onSuccess(null);
        } else {
            onFailure(false, "Unknown error", "");
        }
    }

    protected abstract void onSuccess(T result);

    protected abstract void onFailure(boolean isNetworkFailure, String responseDetails, String responseStatus);
}
