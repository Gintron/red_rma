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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marijan.red.Adapter.HomePostAdapter;
import com.marijan.red.CategoryPostActivity;
import com.marijan.red.Model.HomePost;
import com.marijan.red.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ExploreFragment extends Fragment {
    private RecyclerView recyclerView_post;

    private List<HomePost> postList;
    private HomePostAdapter homePostAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        recyclerView_post = view.findViewById(R.id.recycler_post_explore);
        recyclerView_post.setHasFixedSize(true);
        StaggeredGridLayoutManager layoutPostManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView_post.setLayoutManager(layoutPostManager);
        recyclerView_post.setHasFixedSize(true);
        postList = new ArrayList<>();

        homePostAdapter = new HomePostAdapter(getContext(), postList);
        recyclerView_post.setAdapter(homePostAdapter);
        readPost();
        menuLogic(view);
        return view;
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
                        if(!homePost.getCategory().equals("Friends"))
                            postList.add(homePost);


                }
                Collections.reverse(postList);
                homePostAdapter.notifyDataSetChanged();
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
                intent.putExtra("categoryE", "Fun");
                startActivity(intent);
            }
        });
        ConstraintLayout style;
        style = view.findViewById(R.id.fashion);
        style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CategoryPostActivity.class);
                intent.putExtra("categoryE", "Fashion");
                startActivity(intent);
            }
        });
        ConstraintLayout animals;
        animals = view.findViewById(R.id.animals);
        animals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CategoryPostActivity.class);
                intent.putExtra("categoryE", "Animals");
                startActivity(intent);
            }
        });

        ConstraintLayout sport;
        sport = view.findViewById(R.id.sport);
        sport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CategoryPostActivity.class);
                intent.putExtra("categoryE", "Sport");
                startActivity(intent);
            }
        });
        ConstraintLayout games;
        games = view.findViewById(R.id.games);
        games.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CategoryPostActivity.class);
                intent.putExtra("categoryE", "Games");
                startActivity(intent);
            }
        });
        ConstraintLayout cars;
        cars = view.findViewById(R.id.cars);
        cars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CategoryPostActivity.class);
                intent.putExtra("categoryE", "Cars");
                startActivity(intent);
            }
        });
        ConstraintLayout movies;
        movies = view.findViewById(R.id.movies);
        movies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CategoryPostActivity.class);
                intent.putExtra("categoryE", "Movies");
                startActivity(intent);
            }
        });
        ConstraintLayout news;
        news = view.findViewById(R.id.news);
        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CategoryPostActivity.class);
                intent.putExtra("categoryE", "News");
                startActivity(intent);
            }
        });
        ConstraintLayout food;
        food = view.findViewById(R.id.food);
        food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CategoryPostActivity.class);
                intent.putExtra("categoryE", "Food");
                startActivity(intent);
            }
        });
        ConstraintLayout technology;
        technology =view. findViewById(R.id.technology);
        technology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CategoryPostActivity.class);
                intent.putExtra("categoryE", "Technology");
                startActivity(intent);
            }
        });
        ConstraintLayout business;
        business = view.findViewById(R.id.business);
        business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CategoryPostActivity.class);
                intent.putExtra("categoryE", "Business");
                startActivity(intent);
            }
        });
        ConstraintLayout health;
        health = view.findViewById(R.id.health);
        health.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CategoryPostActivity.class);
                intent.putExtra("categoryE", "Health");
                startActivity(intent);
            }
        });
        ConstraintLayout science;
        science = view.findViewById(R.id.science);
        science.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CategoryPostActivity.class);
                intent.putExtra("categoryE", "Science");
                startActivity(intent);
            }
        });
        ConstraintLayout art;
        art = view.findViewById(R.id.art);
        art.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CategoryPostActivity.class);
                intent.putExtra("categoryE", "Art");
                startActivity(intent);
            }
        });
        ConstraintLayout travel;
        travel = view.findViewById(R.id.travel);
        travel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CategoryPostActivity.class);
                intent.putExtra("categoryE", "Travel");
                startActivity(intent);
            }
        });
    }
}
