package com.emadabel.mydishes.data.network;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.emadabel.mydishes.data.network.Constants.API_KEY;
import static com.emadabel.mydishes.data.network.Constants.BASE_URL;

public class FoodClient {

    private static final Object LOCK = new Object();
    private static FoodClient sInstance = null;

    private FoodService foodService;

    private FoodClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        HttpUrl originalHttpUrl = original.url();

                        HttpUrl url = originalHttpUrl.newBuilder()
                                .addQueryParameter("key", API_KEY)
                                .build();

                        Request.Builder requestBuilder = original.newBuilder()
                                .url(url);

                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        foodService = retrofit.create(FoodService.class);
    }

    public static FoodClient getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new FoodClient();
            }
        }

        return sInstance;
    }

    public FoodService getFoodService() {
        return foodService;
    }
}
