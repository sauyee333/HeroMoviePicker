package com.sauyee333.herospin.network.omdb.rest;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by sauyee on 28/10/16.
 */

public interface OmdbRestInterface {
    @GET("?")
    Observable<Void> getMovieList(@Query("s") String search);

    @GET("?")
    Observable<Void> getMovieDetail(@Query("i") String imdbId);
}
