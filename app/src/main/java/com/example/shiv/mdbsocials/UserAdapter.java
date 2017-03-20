package com.example.shiv.mdbsocials;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

/**
 * Created by Shiv on 2/23/17.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.CustomViewHolder>{

    Context context;
    public static ArrayList<User> users;

    public UserAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }
    @Override
    public UserAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view_usersinterested, parent, false);
        return new CustomViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final UserAdapter.CustomViewHolder holder, int position) {
        User currentUser = users.get(position);

        holder.emailText.setText(currentUser.email);
        holder.imgView = currentUser.img;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void updateUsers(ArrayList<User> arr) {
        users = arr;
        notifyDataSetChanged();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {


        TextView emailText;
        String imgView;

        public CustomViewHolder(View view) {
            super(view);
            emailText = (TextView) view.findViewById(R.id.textView5);
        }
    }

}
