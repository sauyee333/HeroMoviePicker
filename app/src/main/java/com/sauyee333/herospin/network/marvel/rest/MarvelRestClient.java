package com.sauyee333.herospin.network.marvel.rest;

import com.sauyee333.herospin.network.marvel.model.characterList.CharacterInfo;
import com.sauyee333.herospin.utils.Constants;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sauyee on 29/10/16.
 */

public class MarvelRestClient {
    private static final String BASE_URL = "http://gateway.marvel.com/v1/public/";
    private Retrofit retrofit;
    private MarvelRestInterface rxInterface;

    private MarvelRestClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(Constants.HTTP_LOG_ENABLED ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();
        rxInterface = retrofit.create(MarvelRestInterface.class);
    }

    private static class SingletonHolder {
        private static final MarvelRestClient INSTANCE = new MarvelRestClient();
    }

    public static MarvelRestClient getInstance() {
        return MarvelRestClient.SingletonHolder.INSTANCE;
    }

    private <T> void setupSubscribe(Observable<T> observable, Subscriber<T> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public void getCharacterListApi(Subscriber<CharacterInfo> subscriber,
                                    String apikey,
                                    String timeStamp,
                                    String hash,
                                    String name,
                                    String nameStartsWith,
                                    String orderBy,
                                    String limit,
                                    String offset,
                                    String modifiedSince) {
        Observable observable = rxInterface.getCharacterList(apikey, timeStamp, hash, name,
                nameStartsWith, orderBy, limit, offset, modifiedSince);
        setupSubscribe(observable, subscriber);
    }

    public void getCharacterIdApi(Subscriber<CharacterInfo> subscriber,
                                  String characterId,
                                  String apikey,
                                  String timeStamp,
                                  String hash) {
        Observable observable = rxInterface.getCharacterId(characterId, apikey, timeStamp, hash);
        setupSubscribe(observable, subscriber);
    }
}
