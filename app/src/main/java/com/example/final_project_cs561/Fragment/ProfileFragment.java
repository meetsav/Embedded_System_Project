package com.example.final_project_cs561.Fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.final_project_cs561.R;

import static android.content.Context.MODE_PRIVATE;
import static com.example.final_project_cs561.Utils.Constant.UserInfo;
import static com.example.final_project_cs561.Utils.Constant.userEmail;
import static com.example.final_project_cs561.Utils.Constant.userName;
import static com.example.final_project_cs561.Utils.Constant.userPhotoUrl;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
   private ImageView ivImage;
   private TextView tvMainName,tvMainEmail,tvName,tvEmail,tvBack;
   private SharedPreferences sharedPreferences;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment getInstance(){
        ProfileFragment profileFragment= new ProfileFragment();
        return profileFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initVar();
    }

    private void initVar() {
        if(getActivity() == null) return;
        sharedPreferences = getActivity().getSharedPreferences(UserInfo,MODE_PRIVATE);
        tvName.setText(sharedPreferences.getString(userName,"Login"));
        tvMainName.setText(sharedPreferences.getString(userName,"Login"));
        try {
            Glide.with(this).load(sharedPreferences.getString(userPhotoUrl,"")).into(ivImage);
        }catch (Exception e){
           e.printStackTrace();
        }
        tvEmail.setText(sharedPreferences.getString(userEmail," "));
        tvMainEmail.setText(sharedPreferences.getString(userEmail," "));
    }

    private void initView(View view) {
        ivImage = view.findViewById(R.id.ivImage);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvMainEmail = view.findViewById(R.id.tvMainEmail);
        tvName = view.findViewById(R.id.tvName);
        tvMainName = view.findViewById(R.id.tvMainName);
        tvBack = view.findViewById(R.id.tvBack);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getActivity() != null && ((AppCompatActivity)getActivity()).getSupportActionBar() != null )
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }


    @Override
    public void onStop() {
        if(getActivity() != null && ((AppCompatActivity)getActivity()).getSupportActionBar() != null )
         ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        super.onStop();
    }
}
