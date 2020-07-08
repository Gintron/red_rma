package com.marijan.red;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.marijan.red.Adapter.ViewPagerAdapter;
import com.marijan.red.Fragments.APIService;
import com.marijan.red.Fragments.MyStoriessFragment;
import com.marijan.red.Fragments.UserFragmentPost;
import com.marijan.red.Model.User;
import com.marijan.red.Notifications.Client;
import com.marijan.red.Notifications.Data;
import com.marijan.red.Notifications.MyResponse;
import com.marijan.red.Notifications.Sender;
import com.marijan.red.Notifications.Token;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Profile extends AppCompatActivity {

    ImageView image_profile;
    TextView followers, following, fullname, bio, username;
    Button followBtn;
    Intent intent;
    String id;
    FirebaseUser firebaseUser;
    String MESSAGE_KEY = "Started to follow you!";
    private ViewPager viewPager;
    TabLayout tabLayout;
    APIService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        intent = getIntent();
        id = intent.getStringExtra("idProfile");



        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        image_profile = findViewById(R.id.visit_image_profile);
        following = findViewById(R.id.visit_following);
        followers = findViewById(R.id.visit_followers);
        bio = findViewById(R.id.visit_bio);
        fullname = findViewById(R.id.visit_fullname);
        username = findViewById(R.id.visit_username);
        followBtn = findViewById(R.id.visit_follow_btn);

        viewPager = findViewById(R.id.visit_profile_viewpager);
        tabLayout = findViewById(R.id.visit_tab);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        userInfo();
        getFollowers();
        checkFollow();
        getUserData();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setupWithViewPager(viewPager);
        setupViewPager(viewPager);
        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(followBtn.getText().toString().equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(id).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(id)
                            .child("followers").child(firebaseUser.getUid()).setValue(true);
                    sendNotifiaction(id,Persitance.currentUserName);
                    checkFollow();
                }else if(followBtn.getText().toString().equals("following")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(id).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(id)
                            .child("followers").child(firebaseUser.getUid()).removeValue();
                    checkFollow();
                }
            }
        });

    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new UserFragmentPost(), "Posts");
        viewPagerAdapter.addFragment(new MyStoriessFragment(), "Story");

        viewPager.setAdapter(viewPagerAdapter);
    }
    private void userInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                Glide.with(getApplicationContext()).load(user.getImageurl()).apply(RequestOptions.bitmapTransform(new RoundedCorners(30))).into(image_profile);
                username.setText(user.getUsername());
                fullname.setText(user.getFullname());
                bio.setText(user.getBio());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void checkFollow(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(id).exists()){
                    followBtn.setText("following");
                } else{
                    followBtn.setText("follow");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getFollowers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").child(id).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                followers.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Follow").child(id).child("following");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                following.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public String getUserData(){
        return id;
    }

    private void sendNotifiaction(final String receiver, final String username){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    String message = "Started to follow you!";
                    String type = "other";

                    Data data = new Data(firebaseUser.getUid(),Persitance.currentUserName,Persitance.currentUserImage, R.mipmap.ic_launcher, username+": "+message, "Follow",
                            receiver,MESSAGE_KEY, type, "", Persitance.currentUserId);


                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        if (response.body().success != 1){
                                            Toast.makeText(Profile.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Toast.makeText(Profile.this, "Could not send the message", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
