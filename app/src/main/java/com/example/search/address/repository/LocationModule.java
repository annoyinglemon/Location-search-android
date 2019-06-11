package com.example.search.address.repository;

import android.util.Log;

import com.example.search.address.BuildConfig;
import com.example.search.address.repository.endpoint.AddressEndpoint;
import com.example.search.address.repository.endpoint.GeocodeEndpoint;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class LocationModule {

    private AddressEndpoint addressEndpoint;
    private GeocodeEndpoint geocodeEndpoint;

    public LocationModule() {
        Retrofit retrofit = createRetrofit(createOkHttpClient());
        this.addressEndpoint = createAddressEndpoint(retrofit);
        this.geocodeEndpoint = createGeocodeEndpoint(retrofit);
    }

    public AddressEndpoint getAddressEndpoint() {
        return addressEndpoint;
    }

    public GeocodeEndpoint getGeocodeEndpoint() {
        return geocodeEndpoint;
    }

    private AddressEndpoint createAddressEndpoint(Retrofit retrofit) {
       return retrofit.create(AddressEndpoint.class);
    }

    private GeocodeEndpoint createGeocodeEndpoint(Retrofit retrofit) {
        return retrofit.create(GeocodeEndpoint.class);
    }

    private Retrofit createRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.MAP_ENDPOINT)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    private OkHttpClient createOkHttpClient() {
       return new OkHttpClient.Builder()
               .addInterceptor(createApiKeyInterceptor())
               .addInterceptor(createLoggingInterceptor())
               .build();
    }

    private Interceptor createApiKeyInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                HttpUrl httpUrlWithKey = request
                        .url()
                        .newBuilder()
                        .addQueryParameter("key", BuildConfig.MAP_API_KEY)
                        .build();

                request = request
                        .newBuilder()
                        .url(httpUrlWithKey)
                        .build();

                return chain.proceed(request);
            }
        };
    }

    private HttpLoggingInterceptor createLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor =
                new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d(LocationModule.class.getSimpleName(), message);
            }
        });

        if (BuildConfig.DEBUG) {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

        return httpLoggingInterceptor;
    }
}
