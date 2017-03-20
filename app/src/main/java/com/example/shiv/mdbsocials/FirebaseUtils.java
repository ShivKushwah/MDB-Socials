package com.example.shiv.mdbsocials;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * Created by Shiv on 2/28/17.
 */

public class FirebaseUtils {

    /**
     * Try to login to server with current credentials
     * @param email
     * @param password
     * @param mAuth
     * @param context
     * @param var
     */
    public static void attemptLogin(String email, String password, FirebaseAuth mAuth, final Context context, final MainActivity var){
        final ArrayList<Integer> bool = new ArrayList<>();
        if (!email.equals("") && !password.equals("")) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(var, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("SignIn Status", "signInWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.w("SignIn Status", "signInWithEmail", task.getException());
                                Toast.makeText(context, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

        }
    }

    /**
     * Try to create new account on server
     * @param email
     * @param password
     * @param mAuth
     * @param context
     * @param var
     */
    public static void attemptRegister(String email, String password, FirebaseAuth mAuth, final Context context, final RegisterActivity var) {

        if (!email.equals("") && !password.equals("")) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(var, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("SignUp Status", "createUserWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Toast.makeText(context, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }

    }





}
