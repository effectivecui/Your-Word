package com.micsay.yourword.network;

import com.micsay.yourword.beans.Translate;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by cuicui on 17-3-26.
 */

public interface APIService {
    //http://fanyi.youdao.com/openapi.do?
    @GET("/openapi.do")
    Observable<Translate> fetchFeed(
            @Query("q") String text,
            @Query("keyfrom") String appName,
            @Query("key") String key,
            @Query("type") String type,
            @Query("doctype") String doctype,
            @Query("version") String version
    );

}
