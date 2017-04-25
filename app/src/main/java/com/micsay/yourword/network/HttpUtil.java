package com.micsay.yourword.network;

import com.micsay.yourword.beans.Translate;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by cuicui on 17-3-26.
 */

public class HttpUtil {
    private static APIService youdaoService;
    //keyfrom=yourword&key=681105987&type=data&doctype=json&version=1.1&q=vat
    private static String keyfrom = "yourword";
    private static String key = "681105987";
    private static String type = "data";
    private static String doctype = "json";
    private static String version = "1.1";
    private HttpUtil(){}

    public static Observable<Translate> getTranslate(String text){
        if(youdaoService == null){
            synchronized (HttpUtil.class){
                if(youdaoService == null){
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("http://fanyi.youdao.com")
                                .addConverterFactory(GsonConverterFactory.create())
                                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                .build();
                        youdaoService = retrofit.create(APIService.class);
                }
            }
        }

        return youdaoService.fetchFeed(text, keyfrom, key, type, doctype, version);
    }
}
