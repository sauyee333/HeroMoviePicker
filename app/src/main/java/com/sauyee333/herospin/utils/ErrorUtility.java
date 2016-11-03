package com.sauyee333.herospin.utils;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.sauyee333.herospin.R;
import com.sauyee333.herospin.network.marvel.model.characterList.MarvelError;
import com.sauyee333.herospin.network.omdb.model.OmdbError;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava.HttpException;

/**
 * Created by sauyee on 30/10/16.
 */

public class ErrorUtility {

    public static String parseApiError(Context context, Throwable throwable) {
        String errorMsg = "";
        HttpException httpException = null;
        if (throwable instanceof HttpException) {
            httpException = (HttpException) throwable;
            ResponseBody body = ((HttpException) throwable).response().errorBody();
            errorMsg = "(" + httpException.code() + " " + httpException.message() + ") ";
            try {
                Gson gson = new Gson();
                TypeAdapter<MarvelError> adapter = gson.getAdapter
                        (MarvelError.class);
                MarvelError errorParser = adapter.fromJson(body.string());
                errorMsg += errorParser.getCode() + " : " + errorParser.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(errorMsg)) {
                try {
                    Gson gson = new Gson();
                    TypeAdapter<OmdbError> adapter = gson.getAdapter
                            (OmdbError.class);
                    OmdbError errorParser = adapter.fromJson(body.string());
                    errorMsg += errorParser.getError() + " " + errorParser.getResponse();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if(throwable instanceof UnknownHostException){
            if(context != null) {
                errorMsg = context.getResources().getString(R.string.connectionError);
            }
        } else if(throwable instanceof SocketTimeoutException){
            if(context != null) {
                errorMsg = context.getResources().getString(R.string.connectionTimeout);
            }
        }

        if (TextUtils.isEmpty(errorMsg)) {
            errorMsg = throwable.toString();
        }
        return errorMsg;
    }
}
