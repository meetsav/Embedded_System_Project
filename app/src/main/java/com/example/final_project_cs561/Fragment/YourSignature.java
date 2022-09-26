package com.example.final_project_cs561.Fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.example.final_project_cs561.R;
import com.example.final_project_cs561.Client.SecondActivity;
import com.example.final_project_cs561.Utils.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * A simple {@link Fragment} subclass.
 */
public class YourSignature extends Fragment {

    private ImageView ivSignature;
    private SecondActivity activity;
    private StorageReference storageReference;
    private SharedPreferences sharedPreferences;

    public YourSignature() {
        // Required empty public constructor
    }
    public static YourSignature getInstance(){
        YourSignature yourSignature= new YourSignature();
        return yourSignature;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_your_signature, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (SecondActivity) getActivity();
        sharedPreferences = activity.getSharedPreferences(Constant.UserInfo, Context.MODE_PRIVATE);
        showProgressDialog();
        ivSignature = view.findViewById(R.id.ivSignature);
        try {
            storageReference = FirebaseStorage.getInstance().getReference();
            storageReference.child("images/"+sharedPreferences.getString("imageName","").trim()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(activity).load(uri)
                            .into(ivSignature);
                    hideProgressDialog();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    hideProgressDialog();
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
            mProgressDialog = new ProgressDialog(activity);
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
