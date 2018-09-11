package com.example.android.letspark.service;

import com.example.android.letspark.dependencyinjection.LetsParkAppScope;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This is a Dagger module. This module class provide Retrofit object.
 */
@Module
public class DistanceMatrixServiceModule {

    private String baseUrl;

    public DistanceMatrixServiceModule(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Provides
    @LetsParkAppScope
    HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    @Provides
    @Inject
    @LetsParkAppScope
    OkHttpClient provideOkHttpClient(HttpLoggingInterceptor interceptor) {
        return new OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).
                writeTimeout(30, TimeUnit.SECONDS).addInterceptor(interceptor).build();
    }

    @Provides
    @Inject
    @LetsParkAppScope
    Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Inject
    @LetsParkAppScope
    Api provideApi(Retrofit retrofit) {
        return retrofit.create(Api.class);
    }
}
