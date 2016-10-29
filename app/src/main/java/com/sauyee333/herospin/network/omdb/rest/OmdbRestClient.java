package com.sauyee333.herospin.network.omdb.rest;

import com.sauyee333.herospin.network.omdb.model.imdb.ImdbInfo;
import com.sauyee333.herospin.network.omdb.model.search.SearchResponse;

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
 * Created by sauyee on 28/10/16.
 */

public class OmdbRestClient {
    private static final String BASE_URL = "https://www.omdbapi.com/";
    private Retrofit retrofit;
    private OmdbRestInterface rxInterface;

    private OmdbRestClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();
        rxInterface = retrofit.create(OmdbRestInterface.class);
    }

    private static class SingletonHolder {
        private static final OmdbRestClient INSTANCE = new OmdbRestClient();
    }

    public static OmdbRestClient getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private <T> void setupSubscribe(Observable<T> observable, Subscriber<T> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public void getMovieListApi(Subscriber<SearchResponse> subscriber,
                                    String search) {
        Observable observable = rxInterface.getMovieList(search);
        setupSubscribe(observable, subscriber);
    }

    public void getMovieDetailApi(Subscriber<ImdbInfo> subscriber,
                                  String imdbId) {
        Observable observable = rxInterface.getMovieDetail(imdbId);
        setupSubscribe(observable, subscriber);
    }
}
