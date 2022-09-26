package com.example.final_project_cs561.Organization;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.final_project_cs561.Client.SecondActivity;
import com.example.final_project_cs561.Fragment.LoginFragment;
import com.example.final_project_cs561.MainActivity;
import com.example.final_project_cs561.Model.Organization;
import com.example.final_project_cs561.Model.UserData;
import com.example.final_project_cs561.Organization.Adapter.ApproveSignUserAdapter;
import com.example.final_project_cs561.R;
import com.example.final_project_cs561.Utils.Constant;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static com.example.final_project_cs561.Utils.Constant.isLogIn;
import static com.example.final_project_cs561.Utils.Constant.userEmail;
import static com.example.final_project_cs561.Utils.Constant.userName;

public class MainOrganizationActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView rvList;
    private TextView noList;
    private ApproveSignUserAdapter approveSignUserAdapter;
    private ArrayList<UserData> userDataArrayList;
    private Button btnSize,btnResume,btnStop,btnRequest,btnFind,btnReset,btnAbort,btnLogOff;
    private SharedPreferences sharedPreferences;
    public GoogleApiClient mGoogleApiClient;
    private DatabaseReference mDatabase;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_organization);
        initView();
        initVar();
        initListener();
        getDataFromFireBase();
    }

    private void initVar() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
        sharedPreferences = getSharedPreferences(Constant.UserInfo,MODE_PRIVATE);
        initFirebase();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        userDataArrayList = new ArrayList<>();
        approveSignUserAdapter = new ApproveSignUserAdapter(this,userDataArrayList);
        rvList.setLayoutManager(mLayoutManager);
        rvList.setItemAnimator(new DefaultItemAnimator());
        rvList.setAdapter(approveSignUserAdapter);
    }

    private void initView() {
        rvList = findViewById(R.id.rvList);
        noList = findViewById(R.id.tvNoList);
        btnAbort = findViewById(R.id.btnAbort);
        btnFind = findViewById(R.id.btnFind);
        btnLogOff = findViewById(R.id.btnLogoff);
        btnRequest = findViewById(R.id.btnRequest);
        btnReset = findViewById(R.id.btnReset);
        btnResume = findViewById(R.id.btnResume);
        btnSize = findViewById(R.id.btnSize);
        btnStop = findViewById(R.id.btnStop);
    }
    private void initListener() {
        btnAbort.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnSize.setOnClickListener(this);
        btnResume.setOnClickListener(this);
        btnRequest.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        btnLogOff.setOnClickListener(this);
        btnFind.setOnClickListener(this);
    }



    private void initFirebase() {
        try {
            firebaseDatabase = FirebaseDatabase.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference("organization");
            mDatabase.orderByChild("email").equalTo(sharedPreferences.getString(userEmail, "")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        String id = mDatabase.push().getKey();
                        Organization organization = new Organization();
                        organization.setEmail(sharedPreferences.getString(userEmail, ""));
                        organization.setUserName(sharedPreferences.getString(userName, ""));
                        organization.setSignaturePath("");
                        if (id != null)
                            mDatabase.child(id).setValue(organization);
                    }else {
                        for (DataSnapshot datas : dataSnapshot.getChildren()) {
                            if (!TextUtils.isEmpty(datas.child("email").getValue(String.class))) {
                                sharedPreferences.edit().putString("userEmail",datas.child("email").getValue(String.class)).apply();
                                sharedPreferences.edit().putString("userName",datas.child("userName").getValue(String.class)).apply();
                                sharedPreferences.edit().putString("imageName",datas.child("signaturePath").getValue(String.class).contains("/")?datas.child("signaturePath").toString().split("/")[1].replace("}","").trim():"").apply();
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        /*mDatabase.child("-LtQjuiJZ1N3n-44WB_G").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Organization> organizations;
                Log.d("LoginFragment", dataSnapshot.getValue(Organization.class).getEmail());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSize:
                break;
            case R.id.btnAbort :
                break;
            case R.id.btnResume :
                break;
            case R.id.btnStop :
                break;
            case R.id.btnRequest :
                break;
            case R.id.btnFind: openFindAndRequestActivity(FindAndRequestClient.class);
                break;
            case R.id.btnReset:
                break;
            case R.id.btnLogoff:
                if(sharedPreferences.getBoolean(isLogIn,false))
                    revokeAccess();
                else openFindAndRequestActivity(MainActivity.class);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        sharedPreferences.edit().putBoolean(isLogIn,false).apply();
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("/topics/"+sharedPreferences.getString(userEmail,"").replace("@",""));
                        finishAffinity();
                        Intent intent = new Intent(MainOrganizationActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
    }
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        signOut();
                    }
                });
    }
    private void openFindAndRequestActivity(Class classObject){
        Intent intent = new Intent(MainOrganizationActivity.this,classObject);
        startActivity(intent);
    }
    private void getDataFromFireBase() {
        try {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("organization");
            ref.orderByChild("email").equalTo(sharedPreferences.getString(Constant.userEmail,"")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        userDataArrayList.clear();
                        for (DataSnapshot datas : dataSnapshot.getChildren()) {
                            for(DataSnapshot dataSnapshot1 : datas.getChildren()){
                                if(!TextUtils.isEmpty(dataSnapshot1.child("clientName").getValue(String.class))
                                && !(Boolean) dataSnapshot1.child("pending").getValue()) {
                                    if(!(userDataArrayList.size() >0 && userDataArrayList.get(userDataArrayList.size() -1).getUserEmail().equalsIgnoreCase(dataSnapshot1.child("clientEmail").getValue(String.class)))) {
                                        UserData userData = new UserData();
                                        userData.setPending(false);
                                        userData.setSignaturePath(dataSnapshot1.child("signaturePath").getValue(String.class));
                                        userData.setUserEmail(dataSnapshot1.child("clientEmail").getValue(String.class));
                                        userData.setUserName(dataSnapshot1.child("clientName").getValue(String.class));
                                        userDataArrayList.add(userData);
                                    }
                                }
                            }
                        }
                        approveSignUserAdapter.notifyDataSetChanged();
                        rvList.setVisibility(userDataArrayList.size() == 0?View.GONE:View.VISIBLE);
                        noList.setVisibility(userDataArrayList.size() == 0?View.VISIBLE:View.GONE);
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
