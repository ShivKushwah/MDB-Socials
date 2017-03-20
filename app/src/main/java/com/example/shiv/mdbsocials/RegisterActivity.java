package com.example.shiv.mdbsocials;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //set up onclick listeners for buttons
        Button register = (Button) findViewById(R.id.button4);
        register.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("Register Status", "onAuthStateChanged:signed_in:" + user.getUid());
                    MainActivity.email = user.getEmail();
                    Intent intent = new Intent(getApplicationContext(),FeedActivity.class);
                    startActivity(intent);
                } else {
                    // User is signed out
                    Log.d("Register Status", "onAuthStateChanged:signed_out");
                }

            }
        };

    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void onClick(View view) {
        if (view.getId() == R.id.button4) {
            FirebaseUtils.attemptRegister(((EditText) findViewById(R.id.editText6)).getText().toString(),((EditText) findViewById(R.id.editText5)).getText().toString(),mAuth,getApplicationContext(),this);
        }
    }
}
