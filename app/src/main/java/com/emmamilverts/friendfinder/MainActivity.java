package com.emmamilverts.friendfinder;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.emmamilverts.friendfinder.DTO.FriendDTO;
import com.emmamilverts.friendfinder.FriendList.FriendListFragment;
import com.emmamilverts.friendfinder.FriendRequestList.FriendRequestListFragment;
import com.emmamilverts.friendfinder.HistoryList.HistoryListFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LocationService mService;
    private boolean mBound = false;
    FirebaseAuth mAuth;
    DatabaseReference databaseCurrentUser;
    private String selectedUsername;
    DatabaseReference databaseFriendRequests;
    FirebaseAuth.AuthStateListener mAuthListener;
    String activeFragmentName;
    private boolean showAddFriendDialog;
    private boolean chooseUserNameState;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String friendUId = intent.getAction();
        mService.getLocation(friendUId);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(this, LocationService.class);
        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mBound){
            unbindService(mConnection);
            mBound = false;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

    

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        Fragment fragment = null;
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new FriendListFragment();
                    activeFragmentName = "FriendListFragment";
                    break;
                case R.id.navigation_dashboard:
                    fragment = new HistoryListFragment();
                    activeFragmentName = "HistoryListFragment";
                    break;
                case R.id.navigation_notifications:
                    fragment = new FriendRequestListFragment();
                    activeFragmentName = "FriendRequestListFragment";
                    break;
            }
            return loadFragment(fragment);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this,LocationService.class));
        if (savedInstanceState != null){
            if(savedInstanceState.getBoolean("chooseUserNameState")) {
                chooseUserName();
            };
        }
        Button btnSignOut = findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(v -> mAuth.signOut());
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null)
                {
                    startActivity(new Intent(MainActivity.this, SignInActivity.class));
                }
            }
        };
        if (mAuth.getCurrentUser() != null) {
            databaseCurrentUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());

            databaseCurrentUser.child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Object userNameObject = dataSnapshot.getValue();
                    if (userNameObject == null)
                    {
                        chooseUserName();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            FirebaseMessaging.getInstance().subscribeToTopic(mAuth.getUid());
        }

        if (savedInstanceState != null){
            String fragmentName = savedInstanceState.getString("ActiveFragmentName");
            showAddFriendDialog = savedInstanceState.getBoolean("ShowAddFriendDialog");
            switch (fragmentName) {
                case "FriendListFragment":
                    loadFragment(new FriendListFragment());
                    activeFragmentName = "FriendListFragment";
                    break;
                case "HistoryListFragment":
                    loadFragment(new HistoryListFragment());
                    activeFragmentName = "HistoryListFragment";
                    break;
                case "FriendRequestListFragment":
                    loadFragment(new FriendRequestListFragment());
                    activeFragmentName = "FriendRequestListFragment";
                    break;
            }
        }
        else {
            activeFragmentName = "FriendListFragment";
            loadFragment(new FriendListFragment());
        }
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.frameLayout, fragment)
                    .commit();
            return true;
        }
        return false;
    }
    @Nullable
    public LocationService getLocationService(){
        if(mBound)
            return mService;
        else
            return null;
    }

    private void AddUserToFirebaseDB(){
        List<FriendDTO> Friends = new ArrayList<>();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String userEmail = firebaseUser.getEmail();

        User user = new User(selectedUsername, Friends, userEmail);

        databaseCurrentUser.setValue(user);
        databaseFriendRequests = FirebaseDatabase.getInstance().getReference().child("FriendRequests").child(mAuth.getUid());

        Toast.makeText(this, "User added", Toast.LENGTH_SHORT).show();
    }

    private void chooseUserName()
    {
        chooseUserNameState = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please choose a username");

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Add", (dialog, which) -> {
            selectedUsername= input.getText().toString();
            String id = mAuth.getUid();

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Usernames").child(selectedUsername);
            ref.setValue(id);
            AddUserToFirebaseDB();
            chooseUserNameState = false;
        });

        builder.setCancelable(false);
        builder.show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("ActiveFragmentName",activeFragmentName);
        outState.putBoolean("ShowAddFriendDialog", showAddFriendDialog);
        outState.putBoolean("chooseUserNameState", chooseUserNameState);
    }

    public void setDialogState(boolean bool){
        showAddFriendDialog = bool;
    }

    public boolean showDialogState() {
        return showAddFriendDialog;
    }
}
