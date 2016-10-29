package com.sauyee333.herospin.network.omdb.rest;

import com.sauyee333.herospin.network.omdb.model.imdb.ImdbInfo;
import com.sauyee333.herospin.network.omdb.model.searchapi.MovieInfo;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by sauyee on 28/10/16.
 */

public interface OmdbRestInterface {
    @GET("?")
    Observable<MovieInfo> getMovieList(@Query("s") String search);

    @GET("?")
    Observable<ImdbInfo> getMovieDetail(@Query("i") String imdbId);
}
