package com.example.final_project_cs561.Organization;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.final_project_cs561.Model.Organization;
import com.example.final_project_cs561.Model.Request;
import com.example.final_project_cs561.Notification.SendMsgToFCM;
import com.example.final_project_cs561.R;
import com.example.final_project_cs561.Utils.CircleImageView;
import com.example.final_project_cs561.Utils.Constant;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static com.example.final_project_cs561.Utils.Constant.UserInfo;

public class RequestActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tvUserName,tvUserEmail,tvRequest;
    CircleImageView ivUser;
    String userName,userEmail,userPhotoUrl;
    private DatabaseReference mDatabase;
    private FirebaseDatabase firebaseDatabase;
    private RelativeLayout rvRequest;
    private SharedPreferences sharedPreferences;
    private ProgressDialog mProgressDialog;
    private boolean isFromOrganization = true;
    private boolean isApproved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        initView();
        postInit();
    }

    private void postInit() {
        Intent intent = getIntent();
        if(intent == null) return;
        userName = intent.getStringExtra("userName");
        userEmail = intent.getStringExtra("userEmail");
        if(intent.getBooleanExtra("isClient",false)) {
            isFromOrganization = false;
            isApproved = intent.getBooleanExtra("isApproved",false);
        }

        tvUserEmail.setText(userEmail);
        tvUserName.setText(userName);
        mProgressDialog = new ProgressDialog(this);
        sharedPreferences = getSharedPreferences(UserInfo,MODE_PRIVATE);
        if(isFromOrganization) {
            showProgressDialog();
            addFirebaseData();
        }else{
            if(isApproved)
                approved();
            else tvRequest.setText(getResources().getString(R.string.approve));
        }
    }

    private void initView() {
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvRequest = findViewById(R.id.tvRequest);
        rvRequest = findViewById(R.id.rvRequest);
        tvRequest.setOnClickListener(this);
    }


    private void addFirebaseData() {
        try {
            mDatabase = FirebaseDatabase.getInstance().getReference("organization");
            mDatabase.orderByChild("email").equalTo(sharedPreferences.getString(Constant.userEmail, "")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        String id = mDatabase.push().getKey();
                        Organization organization = new Organization();
                        organization.setEmail(sharedPreferences.getString(Constant.userEmail, ""));
                        organization.setUserName(sharedPreferences.getString(Constant.userName, ""));
                        organization.setSignaturePath("");
                        if (id != null)
                            mDatabase.child(id).setValue(organization);
                    }else {
                        for (DataSnapshot datas : dataSnapshot.getChildren())
                            checkRequest(datas.getKey(),mDatabase);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void checkRequest(String key,DatabaseReference reference) {
        try {
            reference.child(key).orderByChild("clientEmail").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                        pending();
                    hideProgressDialog();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void pending(){
        tvRequest.setAlpha(0.5f);
        tvRequest.setText(getResources().getString(R.string.pending));
        tvRequest.setEnabled(false);
    }
    private void approved(){
        tvRequest.setAlpha(0.5f);
        tvRequest.setText(getResources().getString(R.string.approved));
        tvRequest.setEnabled(false);
    }
    private void updateFireBaseData(){
        try {
            firebaseDatabase = FirebaseDatabase.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference("organization");
            mDatabase.orderByChild("email").equalTo(sharedPreferences.getString(Constant.userEmail, "")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot datas : dataSnapshot.getChildren()) {
                            String key = datas.getKey();
                            if (key != null) {
                                Request request = new Request();
                                request.setClientEmail(userEmail);
                                request.setPending(true);
                                request.setClientName(userName);
                                request.setSignaturePath("");
                                mDatabase.child(key).push().setValue(request);
                                addDataToInClientDatabase();
                                pending();
                                Snackbar.make(rvRequest,"Request Send Successfully",Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else addFirebaseData();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Snackbar.make(rvRequest,"Error When Request Send",Snackbar.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void addDataToInClientDatabase(){
        try {
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("userdata");
            ref.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot datas : dataSnapshot.getChildren()) {
                            String key = datas.getKey();
                            if (key != null) {
                                Request request = new Request();
                                request.setClientEmail(sharedPreferences.getString(Constant.userEmail, ""));
                                request.setPending(true);
                                request.setClientName(sharedPreferences.getString(Constant.userName, ""));
                                //ref.child(key).child("email").setValue(isdone);
                                ref.child(key).push().setValue(request);
                                // ref.child(key).child("userName").setValue(priorities);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

        new SendMsgToFCM.SendNotification(false,userEmail.replace("@",""),sharedPreferences.getString(Constant.userEmail, "")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

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
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvRequest :
                if(isFromOrganization)
                   updateFireBaseData();
                else updateApproveData(false);
                break;
        }
    }
    private void updateApproveData(final boolean isPending) {
        try {
            firebaseDatabase = FirebaseDatabase.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference("organization");
            mDatabase.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot datas : dataSnapshot.getChildren()) {
                            String key = datas.getKey();
                            final DatabaseReference  newRe = dataSnapshot.getRef();
                            if (key != null) {
                              newRe.child(key).orderByChild("clientEmail").equalTo(sharedPreferences.getString(Constant.userEmail, "")).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot datas : dataSnapshot.getChildren()) {
                                            String key = datas.getKey();
                                            if (key != null) {
                                                dataSnapshot.child(key).getRef().child("pending").setValue(isPending);
                                                dataSnapshot.child(key).getRef().child("signaturePath").setValue("images/"+sharedPreferences.getString("imageName",""));
                                                approvedUpdateDataClient(isPending);
                                                approved();
                                                Snackbar.make(rvRequest, "Approved Request Successfully", Snackbar.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Snackbar.make(rvRequest,"Error When Request Send",Snackbar.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void approvedUpdateDataClient(final boolean isPending){
        try {
            mDatabase = FirebaseDatabase.getInstance().getReference("userdata");
            mDatabase.orderByChild("email").equalTo(sharedPreferences.getString(Constant.userEmail, "")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot datas : dataSnapshot.getChildren()) {
                            String key = datas.getKey();
                            final DatabaseReference  newRe = dataSnapshot.getRef();
                            if (key != null) {
                                newRe.child(key).orderByChild("clientEmail").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot datas : dataSnapshot.getChildren()) {
                                            String key = datas.getKey();
                                            if (key != null) {
                                                dataSnapshot.child(key).getRef().child("pending").setValue(isPending);
                                                dataSnapshot.child(key).getRef().child("signaturePath").setValue("images/"+sharedPreferences.getString("imageName",""));
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Snackbar.make(rvRequest,"Error When Request Send",Snackbar.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

        new SendMsgToFCM.SendNotification(true,userEmail.replace("@",""),sharedPreferences.getString(Constant.userEmail, "")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
