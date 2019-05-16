package com.emmamilverts.friendfinder.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.emmamilverts.friendfinder.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "";
    private EditText inputEmail, inputPassword;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private String mCurrentId;
    DatabaseReference databaseUsers;

    SignInButton btnSignInGoogle;
    private final static int RC_SIGN_IN = 123;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth.AuthStateListener mAuthListner;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListner);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        FirebaseApp.initializeApp(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        mCurrentId = FirebaseAuth.getInstance().getUid();
        databaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null){
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
        }

        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        Button signInLogin = findViewById(R.id.sign_in_button);
        progressBar = findViewById(R.id.progressBar);
        Button signUpLogin = findViewById(R.id.sign_up_button);
        btnSignInGoogle = findViewById(R.id.sign_in_google);


        btnSignInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        signUpLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();

        //Checking if the email id and password is empty
        signInLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Please enter email id", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //Authenticate user
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            Log.d(TAG, "signinWithEmail:success");
                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Log.d(TAG, "signInWithEmail:Fail");
                            Toast.makeText(SignInActivity.this, "failed", Toast.LENGTH_SHORT).show(); //har fjernet noget.
                        }
                    }
                });
            }
        });

        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("972479203302-d47si4oflonlqttg1gb9slas9pn3tkb7.apps.googleusercontent.com").requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithCredential:success");
                }
                else {
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    Toast.makeText(SignInActivity.this, "Aut fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
