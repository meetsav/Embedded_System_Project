package com.example.final_project_cs561.Notification;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.final_project_cs561.Retrofit.ApiClient;
import com.example.final_project_cs561.Retrofit.ApiInterface;
import com.example.final_project_cs561.Retrofit.Data;
import com.example.final_project_cs561.Retrofit.Notification;
import com.example.final_project_cs561.Retrofit.SendNotificationData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendMsgToFCM {
    public static final String FCM_API = "https://fcm.googleapis.com/fcm/";
    public static final String serverKey =
            "key=" + "AAAAhAbWRRc:APA91bFl6c-vgIR5hT7tm2HqGfU8JN0v0UE_CdJn_jJ_v0kvBONIgLBtdbuUGvW1MPbCPIT6kDvAK_hXP_7uoaBfDp_KJdNtxD211SLC8F0DsSJHAvjJUr9KXh8BGPoiTRmf7hrjhstF";
    public static final String contentType = "application/json";



    public static class SendNotification extends AsyncTask<Void,Void,Void> {
        boolean isApproved;
        String userName,fromUser;
        public SendNotification(boolean isApprove,String userName,String fromUser){
            this.isApproved = isApprove;
            this.userName = userName;
            this.fromUser =fromUser;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            return SendMsgToFCM.sendNotification(isApproved,userName,fromUser);
        }
    }

    private void addFireBaseToken(){
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {

                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            // Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                    }
                });
    }
    public static Void sendNotification(boolean isApproved,String userName,String fromUser) {
        //ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        SendNotificationData sendNotificationData = new SendNotificationData();
        Data data = new Data();
        data.setTitle("Hello");
        data.setKey1("hi");
        data.setKey2("my");
        data.setMessage("my First");
        Notification notification = new Notification();
        notification.setTitle(isApproved?"Your Request Approved By":"Get Signature Request From");
        notification.setBody(fromUser);
        notification.setText("dgsdgg");
        sendNotificationData.setTo("/topics/"+userName);
        sendNotificationData.setNotification(notification);
        //sendNotificationData.setData(data);
        Gson gson = new Gson();
        Log.d("sendNotification",gson.toJson(sendNotificationData));
        try {
            callWebService(gson.toJson(sendNotificationData));
        } catch (IOException e) {
            e.printStackTrace();
        }
       /* Call<String> call = apiInterface.sendMsg(gson.toJson(sendNotificationData));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("sendNotification",String.valueOf(response.code()));
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });*/
        return null;
    }

    public static String callWebService(String requestData) throws IOException {
        InputStream inputStream = null;
        String response = "";
        String serviceURL = "";
        try {
            serviceURL = "https://fcm.googleapis.com/fcm/send";

            URL url = new URL(serviceURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // connection.setReadTimeout(60000);
            connection.setConnectTimeout(60000);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty ("Authorization", "key =AAAAhAbWRRc:APA91bFl6c-vgIR5hT7tm2HqGfU8JN0v0UE_CdJn_jJ_v0kvBONIgLBtdbuUGvW1MPbCPIT6kDvAK_hXP_7uoaBfDp_KJdNtxD211SLC8F0DsSJHAvjJUr9KXh8BGPoiTRmf7hrjhstF");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();
            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.write(requestData.getBytes());
            dataOutputStream.flush();
            dataOutputStream.close();

            if (connection.getResponseCode() != 200) {
                Log.d(" Requested Url : ", serviceURL + " :: Response code : " + connection.getResponseCode());
            }
            if (connection.getResponseCode() == 400) {
                inputStream = connection.getErrorStream();
            } else {
                inputStream = connection.getInputStream();
            }
            response = readString(inputStream);
            connection.disconnect();
        }catch (Exception ex){
            Log.d("send",ex+"");
        } finally{
            //log.debug(TAG+" "+serviceURL+" ,Content-Type = application/json; charset=UTF-8"+" ,JSONBody: "+requestData+" , response: "+response);
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return response;
    }

    public static String readString(InputStream inputStream) throws IOException {

        ByteArrayOutputStream into = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        for (int n; 0 < (n = inputStream.read(buf));) {
            into.write(buf, 0, n);
        }
        into.close();
        return new String(into.toByteArray(), "UTF-8"); // Or whatever encoding
    }
}
