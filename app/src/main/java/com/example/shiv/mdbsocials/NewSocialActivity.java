package com.example.shiv.mdbsocials;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class NewSocialActivity extends AppCompatActivity implements View.OnClickListener {

    //codes for telling what dialog action was taken
    public static final int GET_FROM_GALLERY = 3;
    public static final int GET_FROM_CAMERA = 4;
    public Uri currentImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_social);

        //Set up onClick Listeners
        Button add = (Button) findViewById(R.id.button7);
        add.setOnClickListener(this);
        Button butt = (Button) findViewById(R.id.button8);
        butt.setOnClickListener(this);

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {



            Uri selectedImage = data.getData();
            currentImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
                ((ImageButton) findViewById(R.id.imageButton)).setBackgroundDrawable(bitmapDrawable);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else if (requestCode == GET_FROM_CAMERA) {

            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                //BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
                ((ImageView) findViewById(R.id.imageButton)).setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    /**
     * Add the textfields and images to an event on the server
     */
    private void sendToServer(){

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final String key = ref.child("events").push().getKey();
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mdbsocials-56ed5.appspot.com");
        StorageReference riversRef = storageRef.child(key + ".png");

        class DownloadFilesTask extends AsyncTask<String, Void, Bitmap> {
            protected Bitmap doInBackground(String... strings) {


                String description = strings[0];
                String date = strings[1];
                String name = strings[2];
                String email = MainActivity.email;
                ref.child("events").child(key).child("name").setValue(name);
                ref.child("events").child(key).child("url").setValue(key);
                ref.child("events").child(key).child("date").setValue(date);
                ref.child("events").child(key).child("description").setValue(description);
                ref.child("events").child(key).child("email").setValue(email);
                ref.child("events").child(key).child("interested").setValue("1");
                ArrayList<String> temp = new ArrayList<String>();
                temp.add(email);

                ref.child("events").child(key).child("peopleinterested").setValue(temp);
                ref.child("events").child(key).child("timestamp").setValue(ServerValue.TIMESTAMP);
                return null;


            }


            protected void onProgressUpdate(Void... progress) {}

            protected void onPostExecute(Bitmap result) {
                Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
                startActivity(intent);
            }
        }



        riversRef.putFile(currentImage).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(NewSocialActivity.this, "need an image!", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot x) {

                new DownloadFilesTask().execute(((EditText) findViewById(R.id.editText7)).getText().toString(),((EditText) findViewById(R.id.editText4)).getText().toString(),((EditText) findViewById(R.id.editText3)).getText().toString());
            }
        });


    }

    /**
     * Makes sure the fields are valid
     * @return whether the fields are valid
     */
    public boolean verifyFields() {
        String x =((EditText) findViewById(R.id.editText7)).getText().toString();
        String y = ((EditText) findViewById(R.id.editText4)).getText().toString();
        String z =  ((EditText) findViewById(R.id.editText3)).getText().toString();
        if (x != null && y != null && z != null && currentImage != null) {
            return true;
        }
        return false;

    }

    public void onClick(View view) {
        if (view.getId() == R.id.button7) {
            //processing data and sending to server
            ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar2);
            pb.setVisibility(ProgressBar.VISIBLE);
            Toast.makeText(getApplicationContext(), "Adding Event",Toast.LENGTH_SHORT).show();
            if (verifyFields()) {
                sendToServer();
                Toast.makeText(getApplicationContext(), "Event Saved!",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Fields Incomplete",Toast.LENGTH_SHORT).show();
                pb.setVisibility(ProgressBar.INVISIBLE);
            }

        }
        else if (view.getId() == R.id.button8) {
            //Adding an image

            AlertDialog alertDialog = new AlertDialog.Builder(NewSocialActivity.this).create();
            alertDialog.setTitle("Set a Photo");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Take a Photo",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //Open Camera
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(takePictureIntent, GET_FROM_CAMERA);
                            }

                            dialog.dismiss();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Upload from Gallery",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //launch gallery
                            dialog.dismiss();
                            startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
                        }
                    });
            alertDialog.show();

        }
    }
}
