package com.example.cars.account;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.example.cars.BottomNavigationViewHelper;
import com.example.cars.ChatActivity;
import com.example.cars.EditProfileActivity;
import com.example.cars.IntroActivity;
import com.example.cars.LoginActivity;
import com.example.cars.MainActivity;
import com.example.cars.MapsActivity;
import com.example.cars.R;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import io.fabric.sdk.android.Fabric;

/**
 * Created by nulrybekkarshyga on 07.03.18.
 * Modified by Ruslan Shakirov
 */

public class AccountActivity extends AppCompatActivity {
    //firebase
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //widgets
    private Button mSignOut;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth mAuth;
    private Uri filePath;
    private ImageView img;
    private final int PICK_IMAGE_REQUEST=71;
    private TextView email;
    private TextView name;
    private TextView age;
    private TextView userId;
    private ImageButton settings;
    private ImageButton chat;
    DatabaseReference demoRef;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userID;
    public Button info;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide titlebar
        getSupportActionBar().hide();
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_account);
        // allow Up navigation with the app icon in the action bar
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        name = findViewById(R.id.displayed_name);
        age = findViewById(R.id.displayed_age);
        img = findViewById(R.id.displayed_img);
        email = findViewById(R.id.displayed_email);
        mSignOut = findViewById(R.id.sign_out);
        info = findViewById(R.id.introslides);
        userID = user.getUid();
        demoRef = FirebaseDatabase.getInstance().
                getReference("users").child(userID);
        chat = findViewById(R.id.group_chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountActivity.this,
                        ChatActivity.class));
            }
        });
        settings = findViewById(R.id.ed_profile);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountActivity.this,
                        EditProfileActivity.class));}});
        setupFirebaseListener();
        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();}});
        //get firebase auth instance
        mAuth = FirebaseAuth.getInstance();
        //get current user
        FirebaseUser user = FirebaseAuth.
                getInstance().getCurrentUser();
        assert user != null;
        setDataToView(user);
        BottomNavigationView bottomNavigationView =
                findViewById(R.id.navigation);
        BottomNavigationViewHelper.
                disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.
                        OnNavigationItemSelectedListener() {
                    @Override
                    public boolean
                    onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_search:
                                Intent intent0 = new Intent(
                                        AccountActivity.this, MainActivity.class);
                                startActivity(intent0);
                                break;
                            case R.id.action_navigation:
                                Intent intent1 = new
                                        Intent(AccountActivity.this,
                                        MapsActivity.class);
                                startActivity(intent1);
                                break;
                            case R.id.action_chat:
                                Intent intent2 = new
                                        Intent(AccountActivity.this,
                                        ChatActivity.class);
                                startActivity(intent2);
                                break;
                            case R.id.action_account:
                                break;
                        }
                        return false;
                    }
                });


        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openIntro = new Intent(AccountActivity.this, IntroActivity.class);
                startActivity(openIntro);
            }
        });
}
    private void setupFirebaseListener(){
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    String displayName = user.getDisplayName();
                    for (UserInfo userInfo : user.getProviderData()) {
                        if (displayName == null && userInfo.getDisplayName() != null) {
                            displayName = userInfo.getDisplayName();
                        }
                    }
                }else{
                    Toast.makeText(AccountActivity.
                            this, "Signed out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AccountActivity.
                            this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };
    }

    @SuppressLint("SetTextI18n")
    private void setDataToView(FirebaseUser user) {
        email.setText("Email: " + user.getEmail());
        name.setText(user.getDisplayName());
        demoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("name")) {
                    name.setText(dataSnapshot.child("name").getValue().toString());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        demoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("age")) {
                    age.setText(dataSnapshot.child("age").getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().
                addAuthStateListener(mAuthStateListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if(mAuthStateListener != null){
            FirebaseAuth.getInstance().
                    removeAuthStateListener(mAuthStateListener);
        }
    }
    // Get back to the  parent activity
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        this.finish();
    }
    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }
}