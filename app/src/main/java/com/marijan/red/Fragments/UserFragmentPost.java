package com.marijan.red.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.marijan.red.Adapter.HomePostAdapter;
import com.marijan.red.Model.HomePost;
import com.marijan.red.Profile;
import com.marijan.red.R;

import java.util.ArrayList;
import java.util.List;



public class UserFragmentPost extends Fragment {
    private RecyclerView recyclerView_post;
    private List<HomePost> postList;
    private HomePostAdapter homePostAdapter;
    String profileid;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_post, container, false);

       Profile profile = (Profile) getActivity();
        profileid = profile.getUserData();
        recyclerView_post = view.findViewById(R.id.recycler_user_post);

        recyclerView_post.setHasFixedSize(true);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView_post.setLayoutManager(layoutManager);
        recyclerView_post.setHasFixedSize(true);
        postList = new ArrayList<>();
        homePostAdapter = new HomePostAdapter(getContext(), postList);
        recyclerView_post.setAdapter(homePostAdapter);
        myPosts();
        int n = postList.size();
        String s = Integer.toString(n);
        Log.d("Nr of posts", s);
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
