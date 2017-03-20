package com.example.shiv.mdbsocials;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //The current user's email address for other classes to use
    public static String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Initialize onClickListeners for the buttons
        Button loginButton = (Button) findViewById(R.id.button2);
        loginButton.setOnClickListener(this);
        Button signUpButton = (Button) findViewById(R.id.button3);
        signUpButton.setPaintFlags(signUpButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        signUpButton.setOnClickListener(this);


        ImageView logo = (ImageView) findViewById(R.id.imageView);
        logo.setImageResource(R.drawable.logo);


        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in

                    Log.d("Login Status", "onAuthStateChanged:signed_in:" + user.getUid());
                    MainActivity.email = user.getEmail();
                    Intent intent = new Intent(getApplicationContext(),FeedActivity.class);
                    startActivity(intent);

                } else {
                    // User is signed out
                    Log.d("Login Status", "onAuthStateChanged:signed_out");
                }

            }
        };


    }

    /**
     * Takes user to signup activity
     */
    private void attemptSignup() {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
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
        if (view.getId() == R.id.button2) {
            ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar4);
            pb.setVisibility(ProgressBar.VISIBLE);
            FirebaseUtils.attemptLogin(((EditText) findViewById(R.id.editText)).getText().toString(),((EditText) findViewById(R.id.editText2)).getText().toString(),mAuth,getApplicationContext(),this);//attemptLogin();
            pb.setVisibility(View.INVISIBLE);
        }
        else {
            attemptSignup();
        }
    }

    //Make loading bar disappear on resume
    @Override
    protected void onResume() {
        super.onResume();
        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar4);
        pb.setVisibility(View.INVISIBLE);

    }


}
