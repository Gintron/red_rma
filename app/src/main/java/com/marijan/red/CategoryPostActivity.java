package com.marijan.red;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marijan.red.Adapter.HomePostAdapter;
import com.marijan.red.Model.HomePost;

import java.util.ArrayList;
import java.util.List;

public class CategoryPostActivity extends AppCompatActivity {
    private List<HomePost> postList;
    private HomePostAdapter homePostAdapter;
    private RecyclerView postRecycler;
    private String category, categoryE;
    private List<String> followingList;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_post);

        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        categoryE = intent.getStringExtra("categoryE");
        toolbar = findViewById(R.id.toolbar_category);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        postRecycler = findViewById(R.id.category_post_recycler);
        postRecycler.setHasFixedSize(true);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        postRecycler.setLayoutManager(layoutManager);
        postRecycler.setHasFixedSize(true);
        postList = new ArrayList<>();

        homePostAdapter = new HomePostAdapter(this, postList);
        postRecycler.setAdapter(homePostAdapter);

        if(category==null){
            readPostE();
            getSupportActionBar().setTitle(categoryE);
        }

        else{
            checkFollowing();
            getSupportActionBar().setTitle(category);
        }
    }
    private void checkFollowing(){
        followingList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //followingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(!followingList.contains(snapshot.getKey()))
                    followingList.add(snapshot.getKey());
                }


               readPost();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void readPost(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                HomePost homePost = null;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    homePost = snapshot.getValue(HomePost.class);
                    for (String id : followingList){
                        if(homePost.getUserid().equals(id)  ){
                            if(homePost.getCategory().equals(category))
                            postList.add(homePost);
                        }
                    }
                }

                homePostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void readPostE(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                HomePost homePost = null;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    homePost = snapshot.getValue(HomePost.class);
                                if(homePost.getCategory().equals(categoryE))
                                postList.add(homePost);


                }

                homePostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
