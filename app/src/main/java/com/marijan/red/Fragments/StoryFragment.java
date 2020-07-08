package com.marijan.red.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marijan.red.Adapter.StoryAdapter;
import com.marijan.red.Model.Story;
import com.marijan.red.R;

import java.util.ArrayList;
import java.util.List;


public class StoryFragment extends Fragment {
    private RecyclerView recyclerView_story;
    private StoryAdapter storyAdapter;
    private List<Story> storyList;

    private List<String> followingList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story, container, false);
        recyclerView_story = view.findViewById(R.id.recycler_view_story);
        recyclerView_story.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager
                = new GridLayoutManager(getContext(), 2);
        recyclerView_story.setLayoutManager(linearLayoutManager);
        storyList = new ArrayList<>();
        storyAdapter = new StoryAdapter(getContext(), storyList);
        recyclerView_story.setAdapter(storyAdapter);
        checkFollowing();
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


                readStory();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    private void readStory(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long timecurrent = System.currentTimeMillis();
                storyList.clear();
                storyList.add(new Story("", 0, 0, "",
                        FirebaseAuth.getInstance().getCurrentUser().getUid()));
                for (String id : followingList) {
                    int countStory = 0;
                    Story story = null;
                    for (DataSnapshot snapshot : dataSnapshot.child(id).getChildren()) {
                        story = snapshot.getValue(Story.class);
                        if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()) {
                            countStory++;
                        }
                    }
                    if (countStory > 0){
                        storyList.add(story);
                    }
                }

                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
