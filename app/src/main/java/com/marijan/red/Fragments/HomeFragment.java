package com.marijan.red.Fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.marijan.red.Adapter.HomePostAdapter;
import com.marijan.red.CategoryPostActivity;
import com.marijan.red.CreateMediaActivity;
import com.marijan.red.Model.HomePost;
import com.marijan.red.R;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {



    private RecyclerView recyclerView_post;

    private List<HomePost> postList;
    private HomePostAdapter homePostAdapter;

    private List<String> followingList;

    private ImageView  addPost;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        addPost = view.findViewById(R.id.add_post);


        recyclerView_post = view.findViewById(R.id.recycler_view_posts);
        recyclerView_post.setHasFixedSize(true);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView_post.setLayoutManager(layoutManager);
        recyclerView_post.setHasFixedSize(true);
        postList = new ArrayList<>();

        homePostAdapter = new HomePostAdapter(getContext(), postList);
        recyclerView_post.setAdapter(homePostAdapter);


        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CreateMediaActivity.class);
                startActivity(intent);
            }
        });
        checkFollowing();
        menuLogic(view);
        return view;
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
        Query query = reference.orderByChild("time");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                HomePost homePost = null;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    homePost = snapshot.getValue(HomePost.class);
                    for (String id : followingList){
                        if(homePost.getUserid().equals(id) ){
                            postList.add(homePost);
                        }
                    }
                }
                homePostAdapter.notifyDataSetChanged();
                Collections.reverse(postList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void menuLogic(View view){
        ConstraintLayout fun;
        fun = view.findViewById(R.id.fun);
        fun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CategoryPostActivity.class);
                intent.putExtra("category", "Fun");
                startActivity(intent);
            }
        });
        ConstraintLayout style;
        style = view.findViewById(R.id.fashion);
        style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CategoryPostActivity.class);
                intent.putExtra("category", "Fashion");
                startActivity(intent);
            }
        });
        ConstraintLayout animals;
        animals = view.findViewById(R.id.animals);
        animals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CategoryPostActivity.class);
                intent.putExtra("category", "Animals");
                startActivity(intent);
            }
        });

        ConstraintLayout sport;
        sport = view.findViewById(R.id.sport);
        sport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CategoryPostActivity.class);
                intent.putExtra("category", "Sport");
                startActivity(intent);
            }
        });
        ConstraintLayout games;
        games = view.findViewById(R.id.games);
        games.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CategoryPostActivity.class);
                intent.putExtra("category", "Games");
                startActivity(intent);
            }
        });
        ConstraintLayout cars;
        cars = view.findViewById(R.id.cars);
        cars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CategoryPostActivity.class);
                intent.putExtra("category", "Cars");
                startActivity(intent);
            }
        });
        ConstraintLayout movies;
        movies = view.findViewById(R.id.movies);
        movies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CategoryPostActivity.class);
                intent.putExtra("category", "Movies");
                startActivity(intent);
            }
        });
        ConstraintLayout news;
        news = view.findViewById(R.id.news);
        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CategoryPostActivity.class);
                intent.putExtra("category", "News");
                startActivity(intent);
            }
        });
        ConstraintLayout food;
        food = view.findViewById(R.id.food);
        food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CategoryPostActivity.class);
                intent.putExtra("category", "Food");
                startActivity(intent);
            }
        });
        ConstraintLayout technology;
        technology =view. findViewById(R.id.technology);
        technology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CategoryPostActivity.class);
                intent.putExtra("category", "Technology");
                startActivity(intent);
            }
        });
        ConstraintLayout business;
        business = view.findViewById(R.id.business);
        business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CategoryPostActivity.class);
                intent.putExtra("category", "Business");
                startActivity(intent);
            }
        });
        ConstraintLayout health;
        health = view.findViewById(R.id.health);
        health.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CategoryPostActivity.class);
                intent.putExtra("category", "Health");
                startActivity(intent);
            }
        });
        ConstraintLayout science;
        science = view.findViewById(R.id.science);
        science.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CategoryPostActivity.class);
                intent.putExtra("category", "Science");
                startActivity(intent);
            }
        });
        ConstraintLayout art;
        art = view.findViewById(R.id.art);
        art.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CategoryPostActivity.class);
                intent.putExtra("category", "Art");
                startActivity(intent);
            }
        });
        ConstraintLayout travel;
        travel = view.findViewById(R.id.travel);
        travel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CategoryPostActivity.class);
                intent.putExtra("category", "Travel");
                startActivity(intent);
            }
        });
    }
}
