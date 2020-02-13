package com.marijan.red;

import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marijan.red.Fragments.EventFragment;
import com.marijan.red.Fragments.HomeFragment;
import com.marijan.red.Fragments.MessagesFragment;
import com.marijan.red.Fragments.NotificationFragment;
import com.marijan.red.Fragments.ProfileFragment;
import com.marijan.red.Model.User;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottom_navigation;
    boolean isHome = false;
    Fragment selectedfragment = null;
    DatabaseReference reference;
    FirebaseDatabase database;
    FirebaseUser firebaseUser;
    long back_pressed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottom_navigation = findViewById(R.id.bottom_navigation);
        bottom_navigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();


        currentUserInfo();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                    switch (item.getItemId()){
                        case R.id.nav_home:
                            selectedfragment = new HomeFragment();
                            isHome = true;
                            break;
                        case R.id.messages__icon:
                            selectedfragment = new MessagesFragment();
                            isHome = false;
                            break;
                        case R.id.nav_add:
                            selectedfragment = new EventFragment();
                            isHome = false;
                            break;
                        case R.id.nav_heart:
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

                Persitance.currentUserId=user.getId();
                Persitance.currentUserImage=user.getImageurl();
                Persitance.currentUserName=user.getUsername();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
