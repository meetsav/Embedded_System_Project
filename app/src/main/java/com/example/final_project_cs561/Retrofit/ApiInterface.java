package com.example.final_project_cs561.Retrofit;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {

    /*@Headers({
            "Content-Type:application/json",
            "Authorization: key=AAAAhAbWRRc:APA91bFl6c-vgIR5hT7tm2HqGfU8JN0v0UE_CdJn_jJ_v0kvBONIgLBtdbuUGvW1MPbCPIT6kDvAK_hXP_7uoaBfDp_KJdNtxD211SLC8F0DsSJHAvjJUr9KXh8BGPoiTRmf7hrjhstF"
    })*/
    @POST("send")
    public Call<String> sendMsg(@Body String json);
}
