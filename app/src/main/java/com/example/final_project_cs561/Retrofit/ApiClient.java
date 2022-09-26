package com.example.final_project_cs561.Retrofit;

import com.example.final_project_cs561.Notification.SendMsgToFCM;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.final_project_cs561.Notification.SendMsgToFCM.FCM_API;

public class ApiClient {
    private static Retrofit retrofit;
    private static OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
    public static Retrofit getApiClient(){

        okHttpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .header("Content-Type","application/json")
                        .header("Authorization","key=AAAAhAbWRRc:APA91bFl6c-vgIR5hT7tm2HqGfU8JN0v0UE_CdJn_jJ_v0kvBONIgLBtdbuUGvW1MPbCPIT6kDvAK_hXP_7uoaBfDp_KJdNtxD211SLC8F0DsSJHAvjJUr9KXh8BGPoiTRmf7hrjhstF")
                        .build();
                return chain.proceed(request);
            }
        });
       return retrofit = new Retrofit.Builder().
                 baseUrl(FCM_API)
                .addConverterFactory(GsonConverterFactory.create())
                .callFactory(okHttpClient.build())
                .build();
    }
}
