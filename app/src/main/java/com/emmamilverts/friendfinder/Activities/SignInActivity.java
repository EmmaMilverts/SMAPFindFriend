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
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "LOG TAG";
    private EditText inputEmail, inputPassword;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    DatabaseReference databaseUsers;

    SignInButton btnSignInGoogle;
    private final static int RC_SIGN_IN = 123;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        FirebaseApp.initializeApp(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
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


        btnSignInGoogle.setOnClickListener(v -> signIn());

        signUpLogin.setOnClickListener(v -> startActivity(new Intent(SignInActivity.this, SignUpActivity.class)));

        mAuth = FirebaseAuth.getInstance();

        //Checking if the email id and password is empty
        signInLogin.setOnClickListener(v -> {
            String email = inputEmail.getText().toString();
            final String password = inputPassword.getText().toString();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplicationContext(), getString(R.string.Please_enter_email), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getApplicationContext(), getString(R.string.Please_enter_password), Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            //Authenticate user
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(SignInActivity.this, task -> {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Log.d(TAG, "signinWithEmail:success");
                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Log.d(TAG, "signInWithEmail:Fail");
                    Toast.makeText(SignInActivity.this, getString(R.string.Login_failed), Toast.LENGTH_SHORT).show();
                }
            });
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.REQUEST_ID_TOKEN)).requestEmail().build();
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
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "signInWithCredential:success");
            }
            else {
                Log.w(TAG, "signInWithCredential:failure", task.getException());
                Toast.makeText(SignInActivity.this, getString(R.string.Authentication_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
