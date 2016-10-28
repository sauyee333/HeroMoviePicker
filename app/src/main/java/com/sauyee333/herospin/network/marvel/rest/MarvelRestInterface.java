package com.sauyee333.herospin.network.marvel.rest;

import com.sauyee333.herospin.network.marvel.model.characterList.CharacterInfo;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by sauyee on 29/10/16.
 */

public interface MarvelRestInterface {
    @GET("characters")
    Observable<CharacterInfo> getCharacterList(@Query("apikey") String apikey,
                                               @Query("ts") String timeStamp,
                                               @Query("hash") String hash,
                                               @Query("name") String name,
                                               @Query("nameStartsWith") String nameStartsWith,
                                               @Query("orderBy") String orderBy,
                                               @Query("limit") String limit);

    @GET("characters/{characterId}")
    Observable<Void> getCharacterId(@Path("characterId") String user,
                                    @Query("apikey") String apikey,
                                    @Query("ts") String timeStamp,
                                    @Query("hash") String hash);
}
