package com.marijan.red;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marijan.red.Model.HomePost;
import com.marijan.red.Model.User;

import java.util.HashMap;

public class HomePostActivity extends AppCompatActivity {
    ImageView image, mediaImage, userImage;
    TextView title, description, userName, followTxt;
    String userid;
    String profileid;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_post);
        userImage = findViewById(R.id.profile_post);
        userName = findViewById(R.id.name_home_post);
        followTxt = findViewById(R.id.follow_home_post);
        mediaImage = findViewById(R.id.media_image_a);
        image = findViewById(R.id.post_image);
        title = findViewById(R.id.post_title);
        description = findViewById(R.id.post_content);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userid = getIntent().getStringExtra("homepostuserid");
        getInfo(userid);


    }
    private void getInfo(String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts")
                .child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HomePost homePost = dataSnapshot.getValue(HomePost.class);
                userName.setText(homePost.getPublisher());
                profileid = homePost.getUserid();
                userInfo();
                isFollowing(profileid);

                if(homePost.getType().equals("article")) {
                    Glide.with(getApplicationContext()).load(homePost.getPostimage())
                            .into(image);
                    Log.d("PATH", homePost.getPostimage());
                    title.setText(homePost.getTitle());
                    description.setText(homePost.getText());
                }
                else if(homePost.getType().equals("media")) {
                    image.setVisibility(View.INVISIBLE);
                    title.setVisibility(View.INVISIBLE);
                    description.setVisibility(View.INVISIBLE);
                    mediaImage.setVisibility(View.VISIBLE);
                    Glide.with(getApplicationContext()).load(homePost.getPostimage())
                            .into(mediaImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void userInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageurl())
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(15))).into(userImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void isFollowing(final String userid) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { if (dataSnapshot.child(userid).exists()) {
                    followTxt.setText("Following");
                } else {
                    followTxt.setText("Follow");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    void followClick(){
        followTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (followTxt.getText().toString().equals("Follow")) {

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(userid).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(userid)
                            .child("followers").child(firebaseUser.getUid()).setValue(true);
                    addNotification(userid);

                } else if (followTxt.getText().toString().equals("Following")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(userid).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(userid)
                            .child("followers").child(firebaseUser.getUid()).removeValue();

                }
            }
        });

    }
    private void addNotification(String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "started following you");
        hashMap.put("postid", "");
        hashMap.put("ispost", false);

        reference.push().setValue(hashMap);
    }
    }
