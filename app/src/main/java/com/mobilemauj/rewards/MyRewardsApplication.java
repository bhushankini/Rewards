package com.mobilemauj.rewards;

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class MyRewardsApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
      //  FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

}
