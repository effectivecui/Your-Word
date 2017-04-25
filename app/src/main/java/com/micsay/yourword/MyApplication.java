package com.micsay.yourword;

import android.app.Application;
import android.content.Context;

/**
 * Created by cuicui on 17-3-23.
 */

public class MyApplication extends Application {
    private static Context context;
    @Override
    public void onCreate(){
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}
