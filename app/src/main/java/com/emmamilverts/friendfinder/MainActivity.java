package com.emmamilverts.friendfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.emmamilverts.friendfinder.FriendList.FriendListFragment;
import com.emmamilverts.friendfinder.FriendRequestList.FriendRequestListFragment;
import com.emmamilverts.friendfinder.HistoryList.HistoryListFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    DatabaseReference databaseUsers;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        Fragment fragment = null;
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new FriendListFragment();
                    break;
                case R.id.navigation_dashboard:
                    fragment = new HistoryListFragment();
                    break;
                case R.id.navigation_notifications:
                    fragment = new FriendRequestListFragment();
                    break;
            }
            return loadFragment(fragment);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnSignOut = findViewById(R.id.btnSignOut);
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

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
            }
        });

        databaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        AddUser();

        loadFragment(new FriendListFragment());
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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

    private void AddUser(){
        String Name = "TestName1";
        String id = databaseUsers.push().getKey();
        List<User> Friends = new ArrayList<>();
        User Friend1 = new User("1", "Friend1");
        User Friend2 = new User("2", "Friend2");
        Friends.add(Friend1);
        Friends.add(Friend2);
        User user = new User(id, Name, Friends);

        String message = "TestMessage";
        Map<String, Object> notificationMessage = new HashMap<>();
        notificationMessage.put("message", message);

        databaseUsers.child(id).setValue(user);
        //databaseUsers.child(id + "/Notification").setValue(notificationMessage);
        Toast.makeText(this, "User added", Toast.LENGTH_SHORT).show();
    }
}
