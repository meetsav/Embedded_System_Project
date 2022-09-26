package com.example.final_project_cs561.Organization;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;

import com.example.final_project_cs561.Model.UserData;
import com.example.final_project_cs561.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FindAndRequestClient extends AppCompatActivity implements SearchView.OnQueryTextListener {

    SearchView searchView;
    RecyclerView rvUserData;
    UserAdapter userAdapter;
    ArrayList<UserData> userData;
    private DatabaseReference mDatabase;
    private FirebaseDatabase firebaseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_and_request_client);
        initView();
        intiVar();
    }

    private void intiVar() {
        userData = new ArrayList<>();
        userAdapter = new UserAdapter(this,userData);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvUserData.setLayoutManager(mLayoutManager);
        rvUserData.setItemAnimator(new DefaultItemAnimator());
        rvUserData.setAdapter(userAdapter);
        mDatabase = FirebaseDatabase.getInstance().getReference("userdata");
    }

    private void initView(){
        searchView = findViewById(R.id.svFindUser);
        rvUserData = findViewById(R.id.rvUserData);
        searchView.setOnQueryTextListener(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if(TextUtils.isEmpty(query)) {
            userData.clear();
            userAdapter.notifyDataSetChanged();
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(TextUtils.isEmpty(newText)){
            userData.clear();
            userAdapter.notifyDataSetChanged();
        }else
            getDataFromFireBase(newText);
        return false;
    }

    private void getDataFromFireBase(final String text) {
        try {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("userdata");
            ref.orderByChild("email").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        userData.clear();
                        for (DataSnapshot datas : dataSnapshot.getChildren()) {
                            String userEmail = datas.child("email").getValue(String.class);
                              if(userEmail != null && userEmail.contains(text)){
                                  userData.add(new UserData(datas.child("userName").getValue(String.class),userEmail));
                              }
                        }
                        userAdapter.notifyDataSetChanged();
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
