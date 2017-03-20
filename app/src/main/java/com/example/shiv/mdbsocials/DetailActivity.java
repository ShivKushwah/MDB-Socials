package com.example.shiv.mdbsocials;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    public Context context;
    String firebaseKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //initialize
        context = getApplicationContext();
        TextView eventName = (TextView) findViewById(R.id.textView7);
        final ImageView img = (ImageView) findViewById(R.id.imageView2);
        TextView date = (TextView) findViewById(R.id.textView8);
        TextView description = (TextView) findViewById(R.id.textView9);
        TextView email = (TextView) findViewById(R.id.textView10);

        //set onClick listeners
        Button interested = (Button) findViewById(R.id.button10);
        interested.setOnClickListener(this);
        Button numPeopleGoing = (Button) findViewById(R.id.button9);
        numPeopleGoing.setOnClickListener(this);

        //Fill out variables with the values from the intent
        Intent intent = getIntent();
        String emailExtra = intent.getStringExtra("email");
        email.setText(emailExtra);
        String eventNameExtra = intent.getStringExtra("name");
        eventName.setText(eventNameExtra);
        String dateExtra = intent.getStringExtra("date");
        date.setText(dateExtra);
        String descriptionExtra = intent.getStringExtra("description");
        description.setText(descriptionExtra);
        firebaseKey = intent.getStringExtra("key");

        //retrieve all the people interested and display this number
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/events");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals(firebaseKey)) {
                    ArrayList<String> a = (ArrayList) dataSnapshot.child("peopleinterested").getValue();
                     displayNumPeopleButton(a.size());
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

        //Display progress bar as the image loads
        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar3);
        pb.setVisibility(ProgressBar.VISIBLE);

        //Load the image in the background
        class DownloadFilesTask extends AsyncTask<String, Void, Bitmap> {

            protected Bitmap doInBackground(String... strings) {
                    try {
                    URL url = new URL(strings[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                    return myBitmap;
                } catch (IOException e) {
                    return null;
                }

            }

            protected void onProgressUpdate(Void... progress) {}

            protected void onPostExecute(Bitmap result) {
                img.setImageBitmap(result);
                //after loading the image, make the progress bar invisible
                ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar3);
                pb.setVisibility(ProgressBar.INVISIBLE);
            }
        }
        String imageURLExtra = intent.getStringExtra("imageURL");
        FirebaseStorage.getInstance().getReferenceFromUrl("gs://mdbsocials-56ed5.appspot.com").child(imageURLExtra+ ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                new DownloadFilesTask().execute(uri.toString()); Log.d("ye", uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("sad", exception.toString());
            }
        });
    }

    /**
     * Method increments the number of people interested in the event
     */
    private void addInterested() {

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/events");



        ref.child(firebaseKey).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                String numStringInterested = mutableData.child("interested").getValue().toString();
                int numInterested = Integer.parseInt(numStringInterested);
                ArrayList<String> a = (ArrayList) mutableData.child("peopleinterested").getValue();
                if (!a.contains(MainActivity.email)) {
                    a.add(MainActivity.email);
                    ref.child(firebaseKey).child("peopleinterested").setValue(a);
                    numInterested++;

                }

                DetailActivity.addInterestedToDatabase(numInterested,firebaseKey);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    /**
     * Updates the actual database with the new number of interested people
     * @param interested
     * @param firekey
     */

    public static void addInterestedToDatabase(int interested, String firekey) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/events");
        ref.child(firekey).child("interested").setValue(Integer.toString(interested));

    }

    /**
     * updates the buttontext of the number of people going
     */

    public void displayNumPeopleButton(int num) {
        Button numPeopleGoing = (Button) findViewById(R.id.button9);
        if (num > 0) {
            numPeopleGoing.setText(num + " People Going");
        }
        else {
            numPeopleGoing.setText("0 People Going");
        }


    }

    public void onClick(View view){
        if (view.getId() == R.id.button10) {
            ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar3);
            pb.setVisibility(ProgressBar.VISIBLE);
            addInterested();
            pb.setVisibility(ProgressBar.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Marked as Interested", Toast.LENGTH_SHORT).show();
        }
        else if (view.getId() == R.id.button9) {
            Intent intent = new Intent(getApplicationContext(), UsersInterestedActivity.class);
            intent.putExtra("key",firebaseKey);
            startActivity(intent);

        }
    }
}
