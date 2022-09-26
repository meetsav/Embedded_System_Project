package com.example.final_project_cs561.Fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.final_project_cs561.Client.SecondActivity;
import com.example.final_project_cs561.Model.UserData;
import com.example.final_project_cs561.R;
import com.example.final_project_cs561.Utils.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestList extends Fragment {

    private SecondActivity activity;
    private SharedPreferences sharedPreferences;
    RecyclerView rvUserData;
    OrganizationAdapter organizationAdapter;
    ArrayList<UserData> userData;
    private DatabaseReference mDatabase;
    private FirebaseDatabase firebaseDatabase;
    public RequestList() {
        // Required empty public constructor
    }

    public static RequestList getIntsance(){
        RequestList requestList = new RequestList();
        return requestList;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request_list, container, false);
    }


    @Override
    public void onResume() {
        super.onResume();
        getDataFromFireBase();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (SecondActivity) getActivity();
        if(activity == null)
            return;
        sharedPreferences = activity.getSharedPreferences(Constant.UserInfo, Context.MODE_PRIVATE);
        rvUserData = view.findViewById(R.id.rvUserData);
        intiVar();
    }

    private void intiVar() {
        userData = new ArrayList<>();
        organizationAdapter = new OrganizationAdapter(this.getContext(),userData);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity.getApplicationContext());
        rvUserData.setLayoutManager(mLayoutManager);
        rvUserData.setItemAnimator(new DefaultItemAnimator());
        rvUserData.setAdapter(organizationAdapter);
        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("userdata");
    }

    private void getDataFromFireBase() {
        try {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("userdata");
            ref.orderByChild("email").equalTo(sharedPreferences.getString(Constant.userEmail,"")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        userData.clear();
                        for (DataSnapshot datas : dataSnapshot.getChildren()) {
                            for(DataSnapshot dataSnapshot1 : datas.getChildren()){
                                if(!TextUtils.isEmpty(dataSnapshot1.child("clientName").getValue(String.class))) {
                                    if(!(userData.size() >0 && userData.get(userData.size() -1).getUserEmail().equalsIgnoreCase(dataSnapshot1.child("clientEmail").getValue(String.class)))) {
                                        boolean isPending = (Boolean) dataSnapshot1.child("pending").getValue();
                                        userData.add(new UserData(dataSnapshot1.child("clientName").getValue(String.class), dataSnapshot1.child("clientEmail").getValue(String.class), isPending));
                                    }
                                }
                            }
                        }
                        organizationAdapter.notifyDataSetChanged();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
