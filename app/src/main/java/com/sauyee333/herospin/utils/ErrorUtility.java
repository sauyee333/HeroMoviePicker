package com.sauyee333.herospin.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.sauyee333.herospin.network.marvel.model.characterList.MarvelError;
import com.sauyee333.herospin.network.omdb.model.OmdbError;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava.HttpException;

/**
 * Created by sauyee on 30/10/16.
 */

public class ErrorUtility {

    public static String parseApiError(Throwable throwable) {
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
        }
        if (TextUtils.isEmpty(errorMsg)) {
            errorMsg = throwable.toString();
        }
        return errorMsg;
    }
}
