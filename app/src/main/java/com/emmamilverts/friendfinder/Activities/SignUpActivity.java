package com.emmamilverts.friendfinder.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.emmamilverts.friendfinder.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private EditText email_id, password_check;
    private FirebaseAuth mAuth;
    private static final String TAG = "LOG TAG";
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        FirebaseApp.initializeApp(getApplicationContext());

        TextView btnSignUp = findViewById(R.id.login_page);

        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
        });

        mAuth = FirebaseAuth.getInstance();

        email_id = findViewById(R.id.input_email);
        progressBar = findViewById(R.id.progressBar);
        password_check = findViewById(R.id.input_password);
        Button ahSignUp = findViewById(R.id.btn_signup);

        ahSignUp.setOnClickListener(v -> {
            String email = email_id.getText().toString();
            String password = password_check.getText().toString();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplicationContext(), getString(R.string.Please_enter_email), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getApplicationContext(), getString(R.string.Please_enter_password), Toast.LENGTH_SHORT).show();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignUpActivity.this, task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
