package com.marijan.red.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.marijan.red.Adapter.HomePostAdapter;
import com.marijan.red.Model.HomePost;
import com.marijan.red.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class MyPostsFragment extends Fragment {
    private RecyclerView recyclerView_post;
    private List<HomePost> postList;
    private HomePostAdapter homePostAdapter;
    String profileid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_posts, container, false);
        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");
        recyclerView_post = view.findViewById(R.id.recycler_my_post);

        recyclerView_post.setHasFixedSize(true);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView_post.setLayoutManager(layoutManager);
        recyclerView_post.setHasFixedSize(true);
        postList = new ArrayList<>();
        homePostAdapter = new HomePostAdapter(getContext(), postList);
        recyclerView_post.setAdapter(homePostAdapter);
        myPosts();
        return view;
    }
    private void myPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = reference.orderByChild("time");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                HomePost homePost = null;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    homePost = snapshot.getValue(HomePost.class);
                    if (homePost.getUserid().equals(profileid)){
                        postList.add(homePost);
                    }
                }
                homePostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
