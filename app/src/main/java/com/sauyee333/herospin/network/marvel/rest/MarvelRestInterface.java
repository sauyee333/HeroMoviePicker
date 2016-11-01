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
                                               @Query("nameStartsWith") String nameStartsWith,
                                               @Query("name") String name,
                                               @Query("orderBy") String orderBy,
                                               @Query("limit") String limit,
                                               @Query("offset") String offset,
                                               @Query("modifiedSince") String modifiedSince);

    @GET("characters/{characterId}")
    Observable<CharacterInfo> getCharacterId(@Path("characterId") String characterId,
                                    @Query("apikey") String apikey,
                                    @Query("ts") String timeStamp,
                                    @Query("hash") String hash);
}
