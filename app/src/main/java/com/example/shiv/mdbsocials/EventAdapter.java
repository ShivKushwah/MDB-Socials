package com.example.shiv.mdbsocials;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Shiv on 2/20/17.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.CustomViewHolder>  {

        Context context;
        public static ArrayList<Event> events;

        public EventAdapter(Context context, ArrayList<Event> events) {
            this.context = context;
            this.events = events;
        }


        @Override
        public EventAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view_feed, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final EventAdapter.CustomViewHolder holder, int position) {
            Event currentEvent = events.get(events.size() - position - 1);


            holder.eventName.setText(currentEvent.eventName);
            holder.email.setText(currentEvent.email);
            holder.numInterested.setText(currentEvent.numInterested);
            holder.date = currentEvent.date;
            holder.description = currentEvent.description;
            holder.imageURL = currentEvent.imageURL;
            holder.key = currentEvent.key;
            holder.pplInterested = currentEvent.peopleInterested;


            //Use glide in background
            class DownloadFilesTask extends AsyncTask<String, Void, Bitmap> {
                protected Bitmap doInBackground(String... strings) {
                    try {return Glide.
                            with(context).
                            load(strings[0]).
                            asBitmap().
                            into(100, 100). // Width and height
                            get();}
                    catch (Exception e) {return null;}



                }


                protected void onProgressUpdate(Void... progress) {}

                protected void onPostExecute(Bitmap result) {
                    holder.pic.setImageBitmap(result);
                }
            }
            FirebaseStorage.getInstance().getReferenceFromUrl("gs://mdbsocials-56ed5.appspot.com").child(currentEvent.imageURL+ ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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

        @Override
        public int getItemCount() {
            return events.size();
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {


            TextView eventName;
            TextView email;
            TextView numInterested;
            ImageView pic;
            String date;
            String description;
            String imageURL;
            String key;
            ArrayList<String> pplInterested;

            public CustomViewHolder(View view) {
                super(view);

                eventName = (TextView) view.findViewById(R.id.textView13);
                email = (TextView) view.findViewById(R.id.textView12);
                numInterested = (TextView) view.findViewById(R.id.textView14);
                pic = (ImageView) view.findViewById(R.id.imageView5);

                CardView cardView = (CardView) view.findViewById(R.id.cardView);
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, DetailActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("email",email.getText().toString());
                        intent.putExtra("name",eventName.getText().toString());
                        intent.putExtra("number",numInterested.getText().toString());
                        intent.putExtra("description", description);
                        intent.putExtra("date", date);
                        intent.putExtra("imageURL",imageURL);
                        intent.putExtra("key",key);
                        intent.putExtra("peopleInterested",pplInterested);

                        context.startActivity(intent);
                    }
                });


            }
        }

}
