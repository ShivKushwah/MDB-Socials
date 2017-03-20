package com.example.shiv.mdbsocials;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UsersInterestedActivity extends AppCompatActivity {

    RecyclerView rview;
    UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_interested);

        //setup recyclerview
        rview = (RecyclerView) findViewById(R.id.rview2);
        rview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        userAdapter = new UserAdapter(getApplicationContext(), new ArrayList<User>());
        rview.setAdapter(userAdapter);

        Intent intent = getIntent();
        final String firebasekey = intent.getStringExtra("key");

        final ArrayList<String> interestedPeople = new ArrayList<>();

        //read the users interested and put it into a array
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/events");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals(firebasekey)) {
                    ArrayList<String> a = (ArrayList) dataSnapshot.child("peopleinterested").getValue();
                    for (int i = 0; i < a.size(); i++) {
                        interestedPeople.add(a.get(i));
                    }
                    ArrayList<User> arr = new ArrayList<>();
                    for (int i = 0; i < interestedPeople.size(); i++) {
                        arr.add(new User(interestedPeople.get(i),"temp"));
                    }

                    userAdapter.updateUsers(arr);
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
