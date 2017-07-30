package com.mobilemauj.rewards;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class MyRewardsApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
      //  FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

}
