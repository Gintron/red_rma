package com.marijan.red;


import android.content.SharedPreferences;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.marijan.red.Adapter.ViewPagerAdapter;
import com.marijan.red.Fragments.ExploreFragment;
import com.marijan.red.Fragments.HomeFragment;

import com.marijan.red.Fragments.MainFragment;
import com.marijan.red.Fragments.NotificationFragment;
import com.marijan.red.Fragments.ProfileFragment;
import com.marijan.red.Fragments.SearchFragment;
import com.marijan.red.Model.User;
import com.marijan.red.Notifications.Token;

public class MainActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "com.marijan.red";
    BottomNavigationView bottom_navigation;
    boolean isHome = false;
    Fragment selectedfragment = null;
    DatabaseReference reference;
    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottom_navigation = findViewById(R.id.bottom_navigation);
        bottom_navigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("Verify", firebaseUser.toString() );




        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new MainFragment()).commit();


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


        currentUserInfo();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                    switch (item.getItemId()){
                        case R.id.nav_home:
                            selectedfragment = new MainFragment();
                            isHome = true;
                            break;
                        case R.id.nav_add:
                            selectedfragment = new SearchFragment();
                            isHome = false;
                            break;
                        case R.id.nav_bell:
                            selectedfragment = new NotificationFragment();
                            isHome = false;
                            break;
                        case R.id.nav_profile:
                            isHome = false;
                            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                            editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            editor.apply();
                            selectedfragment = new ProfileFragment();
                            break;
                    }
                    if (selectedfragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedfragment).commit();
                    }

                    return true;
                }
            };

    @Override
    public void onBackPressed() {

        if (isHome){

            finish();
        }
        else{
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
            isHome=true;
            bottom_navigation.setSelectedItemId(R.id.nav_home);
        }
    }


    void currentUserInfo(){

        reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                User user = dataSnapshot.getValue(User.class);

                Persitance.currentUserId=firebaseUser.getUid();
                try {
                    Persitance.currentUserImage = user.getImageurl();
                }catch (Exception e){
                    Log.d("Error", "Could not get the image");
                }
                Persitance.currentUserName=user.getUsername();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(firebaseUser.getUid()).setValue(token1);
    }
}
