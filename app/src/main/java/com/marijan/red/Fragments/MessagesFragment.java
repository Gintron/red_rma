package com.marijan.red.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.marijan.red.Persitance;
import com.marijan.red.Adapter.ChatListAdapter;
import com.marijan.red.Adapter.UserChatAdapter;
import com.marijan.red.Model.Chats;
import com.marijan.red.Model.User;
import com.marijan.red.Notifications.Token;
import com.marijan.red.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MessagesFragment extends Fragment {
    private ImageView cancelChat;
    private RecyclerView recyclerViewUsersList;
    private RecyclerView recyclerViewChatList;

    private UserChatAdapter userAdapter;
    private ChatListAdapter myChatList_adapter;

    private List<User> userList;
    private List<Chats> chatListList;

    EditText search_bar;

    DatabaseReference reference;

    boolean IS_CHAT_CLEARED=false;
    FirebaseUser fuser;
    public static final String ALL_CHATS="all_chats";

    boolean IS_CHANGED=true;
    ValueEventListener listener,listener1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        reference = FirebaseDatabase.getInstance().getReference().child(ALL_CHATS);

        fuser = FirebaseAuth.getInstance().getCurrentUser();


        recyclerViewUsersList = view.findViewById(R.id.chat_users_recycler);
        recyclerViewUsersList.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewUsersList.setLayoutManager(mLayoutManager);
        userList = new ArrayList<>();
        userAdapter = new UserChatAdapter(getContext(), userList, true);
        recyclerViewUsersList.setAdapter(userAdapter);

        recyclerViewChatList = view.findViewById(R.id.chat_list_recycler);
        recyclerViewChatList.setHasFixedSize(true);
        recyclerViewChatList.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerViewChatList.setVisibility(View.VISIBLE);
        recyclerViewUsersList.setVisibility(View.GONE);

        search_bar = view.findViewById(R.id.chat_search_bar);

        cancelChat = view.findViewById(R.id.cancel_search_chat);

        chatListList = new ArrayList<>();

        cancelChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewChatList.setVisibility(View.VISIBLE);
                recyclerViewUsersList.setVisibility(View.GONE);
            }
        });
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                recyclerViewChatList.setVisibility(View.GONE);
                recyclerViewUsersList.setVisibility(View.VISIBLE);

                searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            //Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        updateToken(token);
                        // Log and toast

                    }
                });



        return view;



    }


    @Override
    public void onResume() {
        super.onResume();
        getChatsBySender();
    }



    void getChatsBySender(){


        chatListList.clear();

        reference.keepSynced(false);

        //if(listener!=null){
        //    reference.removeEventListener(listener);
        //}

        listener=reference.orderByChild("sender").equalTo(Persitance.currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()) {

                    IS_CHAT_CLEARED=true;
                    chatListList.clear();

                    if(myChatList_adapter!=null){
                    myChatList_adapter.notifyDataSetChanged();}

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chats chatlist = snapshot.getValue(Chats.class);
                        chatListList.add(chatlist);
                    }

                    getChatByReceiver();

                }
                else{
               getChatByReceiver();    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    void getChatByReceiver(){


        IS_CHANGED=false;


        if(listener1!=null){
            reference.removeEventListener(listener1);
        }

        listener1=reference.orderByChild("receiver").equalTo(Persitance.currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if(dataSnapshot.exists()) {

                    if(!IS_CHAT_CLEARED||IS_CHANGED){chatListList.clear();

                    if(myChatList_adapter!=null){ myChatList_adapter.notifyDataSetChanged();}

                    }

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chats chatlist = snapshot.getValue(Chats.class);
                        chatListList.add(chatlist);
                    }



                    if(IS_CHANGED){getChatBySender();}else{setAdapter();}

                }
                else
                {

                    if(IS_CHANGED){getChatBySender();}else{setAdapter();}

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    void setAdapter(){

        if(chatListList.size()>0) {
            Collections.sort(chatListList, new Comparator<Chats>() {
                @Override
                public int compare(Chats o1, Chats o2) {

                    return o2.time.compareTo(o1.time);
                }
            });

            myChatList_adapter = new ChatListAdapter(getContext(), chatListList);
            recyclerViewChatList.setAdapter(myChatList_adapter);

            IS_CHAT_CLEARED=false;
        }

        IS_CHANGED=true;


    }

    void getChatBySender(){


        reference.orderByChild("sender").equalTo(Persitance.currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()) {


                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chats chatlist = snapshot.getValue(Chats.class);
                        chatListList.add(chatlist);
                    }

                   setAdapter();

                }
                else{
                    setAdapter();   }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }
    private void searchUsers(String s) {
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    userList.add(user);
                }

                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }





    }


