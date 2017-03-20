package com.example.shiv.mdbsocials;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FeedActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView rview;
    EventAdapter eventAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);


        //initialize on click listeners for buttons
        Button logout = (Button) findViewById(R.id.button5);
        logout.setOnClickListener(this);
        Button newsocial = (Button) findViewById(R.id.button6);
        newsocial.setOnClickListener(this);

        //set up recycler view
        rview = (RecyclerView) findViewById(R.id.recyclableView);
        rview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        eventAdapter = new EventAdapter(getApplicationContext(), getList());//events);
        rview.setAdapter(eventAdapter);
    }


    /**
     * Creates a list of events based on server data and returns it
     * @return arraylist
     */
    private ArrayList<Event> getList() {
        final ArrayList<Event> currEvents = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/events");

        

        ref.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                currEvents.clear();

                for (DataSnapshot x : dataSnapshot.getChildren()) {

                    Log.d(x.getKey(), x.getValue().toString());

                    Event e = new Event();
                    e.date = x.child("date").getValue(String.class);
                    e.description = x.child("description").getValue(String.class);
                    e.eventName = x.child("name").getValue(String.class);
                    e.imageURL = x.child("url").getValue(String.class);
                    e.email = x.child("email").getValue(String.class);

                    e.numInterested = x.child("interested").getValue(String.class);
                    e.key = x.getKey();

                    currEvents.add(e);
                    eventAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return currEvents;

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onClick(View view) {
        if (view.getId() == R.id.button5) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);

        } else if (view.getId() == R.id.button6) {
            Intent intent = new Intent(getApplicationContext(),NewSocialActivity.class);
            startActivity(intent);
        }
    }
}






