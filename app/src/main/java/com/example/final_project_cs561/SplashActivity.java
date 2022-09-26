package com.example.final_project_cs561;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import com.example.final_project_cs561.Client.SecondActivity;
import com.example.final_project_cs561.Notification.SendMsgToFCM;
import com.example.final_project_cs561.Organization.FindAndRequestClient;
import com.example.final_project_cs561.Organization.MainOrganizationActivity;
import com.example.final_project_cs561.Utils.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

public class SplashActivity extends AppCompatActivity {

    Handler handler;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            SharedPreferences sharedPreferences = getSharedPreferences(Constant.UserInfo,MODE_PRIVATE);
            if(sharedPreferences.getBoolean("isFirstTime",true)){
                sharedPreferences.getBoolean("isFirstTime",false);
                try {
                    FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }

            Intent intent = null;
            if(!sharedPreferences.getBoolean(Constant.isLogIn,false)){
                intent = new Intent(SplashActivity.this,MainActivity.class);
            }else {
                intent = new Intent(SplashActivity.this,sharedPreferences.getBoolean(Constant.isOrganization,false)? MainOrganizationActivity.class: SecondActivity.class);
            }
            startActivity(intent);
            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        handler = new Handler();
        handler.postDelayed(runnable,3000L);
        //addFireBaseToken();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
