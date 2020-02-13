package com.marijan.red.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marijan.red.Model.Story;
import com.marijan.red.Model.User;
import com.marijan.red.R;

import java.util.List;



public class MyStoryAdapter extends RecyclerView.Adapter<MyStoryAdapter.ImageViewHolder> {

    private Context mContext;
    private List<Story> mStory;

    public MyStoryAdapter(Context context, List<Story> stories){
        mContext = context;
        mStory = stories;
    }

    @NonNull
    @Override
    public MyStoryAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.my_story_items, parent, false);
        return new MyStoryAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyStoryAdapter.ImageViewHolder holder, final int position) {

        final Story stor = mStory.get(position);

        Glide.with(mContext).load(stor.getImageurl()).into(holder.story_image);

        //publisherInfo( holder.username, stor.getUserid());

    }
    private void publisherInfo( final TextView username, final String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                //username.setText(user.getUsername());
                //publisher.setText(user.getUsername());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    public int getItemCount() {
        return mStory.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView story_image;
        public TextView username;


        public ImageViewHolder(View itemView) {
            super(itemView);

            story_image = itemView.findViewById(R.id.my_story_image);
            //username = itemView.findViewById(R.id.my_story_username);


        }
    }
}