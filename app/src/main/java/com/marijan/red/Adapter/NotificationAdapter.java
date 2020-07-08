package com.marijan.red.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marijan.red.Fragments.ProfileFragment;
import com.marijan.red.Model.Notification;
import com.marijan.red.Model.User;
import com.marijan.red.Profile;
import com.marijan.red.R;
import com.marijan.red.StoryCommentActivity;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ImageViewHolder> {

    private Context mContext;
    private List<Notification> mNotification;

    public NotificationAdapter(Context context, List<Notification> notification){
        mContext = context;
        mNotification = notification;
    }

    @NonNull
    @Override
    public NotificationAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notificaion_item, parent, false);
        return new NotificationAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationAdapter.ImageViewHolder holder, final int position) {

        final Notification notification = mNotification.get(position);

        holder.text.setText(notification.getText());

        getUserInfo(holder.image_profile, holder.username, notification.getUserid());

        if (notification.isIspost()) {
            holder.post_image.setVisibility(View.VISIBLE);
            //getPostImage(holder.post_image, notification.getPostid());
        } else {
            holder.post_image.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notification.isIspost()) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("storyid", notification.getStoryid());
                    editor.putString("commentid", notification.getCommentid());
                    editor.apply();

                   //Intent intent = new Intent(mContext, StoryCommentActivity.class);

                   // mContext.startActivity(intent);
                } else {
                    /*SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("idProfile", notification.getUserid());
                    editor.apply();*/
                    String uid = notification.getUserid();
                    Intent intent = new Intent(mContext, Profile.class);
                    intent.putExtra("idProfile", uid);
                    mContext.startActivity(intent);
                }
            }
        });



    }
    //
    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView image_profile, post_image;
        public TextView username, text;

        public ImageViewHolder(View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            username = itemView.findViewById(R.id.username);
            text = itemView.findViewById(R.id.comment);
        }
    }

    private void getUserInfo(final ImageView imageView, final TextView username, String publisherid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(publisherid);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).apply(RequestOptions.bitmapTransform(new RoundedCorners(30))).into(imageView);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*private void getPostImage(final ImageView post_image, String storyid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Story").child(storyid);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Story story = dataSnapshot.getValue(Story.class);
               try{
                   Glide.with(mContext).load(story.getImageurl()).into(post_image);

               } catch (Exception e){}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/
}