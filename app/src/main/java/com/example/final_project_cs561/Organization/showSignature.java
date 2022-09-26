package com.example.final_project_cs561.Organization;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.final_project_cs561.Client.SecondActivity;
import com.example.final_project_cs561.R;
import com.example.final_project_cs561.Utils.Constant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class showSignature extends AppCompatActivity {

    ImageView ivSignature;
    private SharedPreferences sharedPreferences;
    private String userName;
    private String userEmail;
    private String signaturePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_signature);
        Intent intent = getIntent();
        if(intent == null) return;
        userName = intent.getStringExtra("userName");
        userEmail = intent.getStringExtra("userEmail");
        signaturePath = intent.getStringExtra("signaturePath");
        showProgressDialog();
        initView();
        initVar();
    }

    private void initView() {
        ivSignature = findViewById(R.id.ivSignature);
    }

    private void initVar() {
        sharedPreferences = getSharedPreferences(Constant.UserInfo, Context.MODE_PRIVATE);
        try {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            storageReference.child(signaturePath.trim()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(showSignature.this).load(uri)
                            .into(ivSignature);
                    hideProgressDialog();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    hideProgressDialog();
                    // Handle any errors
                }
            });
        }catch (Exception ex){
            hideProgressDialog();
            Log.e("", ex.getMessage());
        }
    }


    private ProgressDialog mProgressDialog;
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.Loader));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

}
