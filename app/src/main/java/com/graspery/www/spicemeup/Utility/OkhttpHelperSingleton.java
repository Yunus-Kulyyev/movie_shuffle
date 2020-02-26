package com.graspery.www.spicemeup.Utility;

import okhttp3.OkHttpClient;

public class OkhttpHelperSingleton {
    private static final OkhttpHelperSingleton ourInstance = new OkhttpHelperSingleton();

    private OkHttpClient mOkHttpClient;

    public static OkhttpHelperSingleton getInstance() {
        return ourInstance;
    }

    private OkhttpHelperSingleton() {
        mOkHttpClient = new OkHttpClient();
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }
}
