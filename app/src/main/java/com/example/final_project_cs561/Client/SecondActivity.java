package com.example.final_project_cs561.Client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.final_project_cs561.Fragment.LoginFragment;
import com.example.final_project_cs561.Fragment.ProfileFragment;
import com.example.final_project_cs561.Fragment.RequestList;
import com.example.final_project_cs561.Fragment.SignaturePad;
import com.example.final_project_cs561.Fragment.YourSignature;
import com.example.final_project_cs561.MainActivity;
import com.example.final_project_cs561.Model.Organization;
import com.example.final_project_cs561.Model.UserData;
import com.example.final_project_cs561.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import static com.example.final_project_cs561.Utils.Constant.UserInfo;
import static com.example.final_project_cs561.Utils.Constant.isLogIn;
import static com.example.final_project_cs561.Utils.Constant.userEmail;
import static com.example.final_project_cs561.Utils.Constant.userName;
import static com.example.final_project_cs561.Utils.Constant.userPhotoUrl;
import static com.example.final_project_cs561.Utils.Constant.userSignPath;

public class SecondActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private FrameLayout flContainer;
    private Toolbar toolbar;
    private View navHeader;
    private int navItemIndex = 0;
    private ImageView ivUser;
    private TextView tvUserName;
    private SharedPreferences sharedPreferences;
    private DatabaseReference mDatabase;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_activty);
        sharedPreferences = getSharedPreferences(UserInfo,MODE_PRIVATE);
        initView();
        initVariable();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    private void initVariable() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
        loadHomeFragment();
        tvUserName.setText(sharedPreferences.getString(userName,"Login"));
        try {
            Glide.with(this).load(sharedPreferences.getString(userPhotoUrl,"")).into(ivUser);
        }catch (Exception e){

        }
        initFirebase();
    }

    private void initView(){
        drawer =  findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        flContainer = findViewById(R.id.flContainer);
        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        navHeader = navigationView.getHeaderView(0);
        setUpNavigationView();
        ivUser = navHeader.findViewById(R.id.ivUser);
        tvUserName = navHeader.findViewById(R.id.tvUserName);
    }


    private void pushFragment(Fragment fragment){
        try {
            FragmentManager manager = getSupportFragmentManager();
            if(manager.getBackStackEntryCount()> 1)
                manager.popBackStackImmediate();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction
                .replace(R.id.flContainer, fragment);//R.id.content_frame is the layout you want to replace
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        drawer.closeDrawers();
    }

    private void addFragment(Fragment fragment){
        try {
            FragmentManager manager = getSupportFragmentManager();
            manager.popBackStackImmediate();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction
                .add(R.id.flContainer, fragment);//R.id.content_frame is the layout you want to replace
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        drawer.closeDrawers();
    }


    public void SaveImage(Bitmap bitmap) {
        Bitmap finalBitmap = Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth()*0.8), (int)(bitmap.getHeight()*0.8), true);
        sharedPreferences.edit().putString("imageName",Calendar.getInstance().getTimeInMillis()+".png").apply();
        String root = getFilesDir().getPath();
        File myDir = new File(root + "/saved_images");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        String fname = sharedPreferences.getString("imageName","");
        File file = new File (myDir, fname);
        if (file.exists ())
            file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Snackbar.make(flContainer, "Save Signature Successfully", Snackbar.LENGTH_SHORT).show();
    }

    public void getFileUriAndStore() {
        if (isNetworkAvailable(this)) {
            Uri file = Uri.fromFile(new File(this.getFilesDir().getPath() + "/saved_images/"+
                    sharedPreferences.getString("imageName","")));
            storeFile(file);
        }
    }

    private void storeFile(Uri file){
        try {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        final String oldFilePath = sharedPreferences.getString(userSignPath,"") ;
        StorageReference ref = storageReference.child("images/"+sharedPreferences.getString("imageName",""));
            ref.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if(taskSnapshot.getMetadata() != null)
                        sharedPreferences.edit().putString(userSignPath,taskSnapshot.getMetadata().getPath()).apply();
                    updateFireBaseData();
                    if(!TextUtils.isEmpty(oldFilePath)){
                        StorageReference ref = storageReference.child(oldFilePath);
                        ref.delete();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }catch (Exception ex){
           ex.printStackTrace();
        }

    }

    private void updateFireBaseData(){
        try {
            boolean isExits = true;
            firebaseDatabase = FirebaseDatabase.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference("userdata");
            mDatabase.orderByChild("email").equalTo(sharedPreferences.getString(userEmail, "")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot datas : dataSnapshot.getChildren()) {
                            String key = datas.getKey();
                            if (key != null) {
                                //ref.child(key).child("email").setValue(isdone);
                                mDatabase.child(key).child("signaturePath").setValue(sharedPreferences.getString(userSignPath, ""));
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
    }
    private void initFirebase() {
        try {
            firebaseDatabase = FirebaseDatabase.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference("userdata");
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
    public static boolean isNetworkAvailable(Context context) {
        NetworkInfo activeNetworkInfo = null;
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager != null) activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setUpNavigationView() {
        Menu menuNav = navigationView.getMenu();
        MenuItem menuItem = menuNav.findItem(R.id.nav_Login);
        if(sharedPreferences.getBoolean(isLogIn,false)){
            menuItem.setTitle("Logout");
        }
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_create_sign:
                        navItemIndex = 1;
                        break;
                    case R.id.nav_profile:
                        navItemIndex = 2;
                        break;
                    case R.id.nav_signature:
                        navItemIndex = 3;
                        break;
                    case R.id.nav_request :
                        navItemIndex = 4;
                        break;
                    case R.id.nav_Login :
                        navItemIndex = 5;
                        break;
                   /* case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_privacy_policy:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
                        drawer.closeDrawers();
                        return true;*/
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };
        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);
        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    public GoogleApiClient mGoogleApiClient;
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        finishAffinity();
                        sharedPreferences.edit().putBoolean(isLogIn,false).apply();
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("/topics/"+sharedPreferences.getString(userEmail,"").replace("@",""));
                        Intent intent = new Intent(SecondActivity.this,MainActivity.class);
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

    private void loadHomeFragment() {
        switch (navItemIndex){
            case 1 :  pushFragment(SignaturePad.getInstance());
            break;
            case 2 : pushFragment(ProfileFragment.getInstance());
            break;
            case 3 : pushFragment(YourSignature.getInstance());
            break;
            case 4 : pushFragment(RequestList.getIntsance());
            break;
            case 5 : if(sharedPreferences.getBoolean(isLogIn,false)) {
                        revokeAccess();
                    }else pushFragment(LoginFragment.getIntsance());
                break;
            default: pushFragment(SignaturePad.getInstance());
            break;
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();
        if(manager.getBackStackEntryCount() <= 1)
            finish();
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
