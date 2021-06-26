package com.marijan.red;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
/* KREIRAJ PROFILE */
public class CreateProfileActivity extends AppCompatActivity {

    EditText userName, fullName;
    Button createProfileBtn;

    DatabaseReference reference;
    ProgressDialog pd;
    String auth;

    GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        userName = findViewById(R.id.create_profile_username);
        fullName = findViewById(R.id.create_profile_fullname);
        createProfileBtn = findViewById(R.id.create_profile_button);
        auth = getIntent().getStringExtra("auth");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("295321716221-vsrnnefnf0h1u0ht47i20uf742lm1mm9.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        createProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_username = userName.getText().toString();
                String str_fullname = fullName.getText().toString();
                if (TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullname)){
                    Toast.makeText(CreateProfileActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                } else {
                    pd = new ProgressDialog(CreateProfileActivity.this);
                    pd.setMessage("Please wait...");
                    pd.show();
                    register(str_username, str_fullname);
                }
            }
        });

    }
    public void register(final String username, final String fullname) {

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(auth);
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", auth);
        map.put("username", username.toLowerCase());
        map.put("fullname", fullname);
        map.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/instagramtest-fcbef.appspot.com/o/placeholder.png?alt=media&token=b09b809d-a5f8-499b-9563-5252262e9a49");
        map.put("bio", "");
        reference.setValue(map);
        pd.dismiss();
        Intent intent = new Intent(CreateProfileActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);




    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
      mGoogleSignInClient.signOut();
      FirebaseAuth.getInstance().signOut();
    }
}
