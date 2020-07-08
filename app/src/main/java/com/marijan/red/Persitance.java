package com.marijan.red;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class Persitance extends Application {

   public static String currentUserName="";
    public static String currentUserId="";
    public static String currentUserImage="";

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseDatabase.getInstance().setPersistenceCacheSizeBytes(1100000);
    }
}
