package com.mobilemauj.rewards.utility;

import android.content.Context;
import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobilemauj.rewards.R;
import com.mobilemauj.rewards.model.User;
import com.mobilemauj.rewards.model.UserTransaction;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class FirebaseDatabaseUtil {

private final static String TAG = "FirebaseDatabaseUtil";

    public static void rewardsPoints(Context context, final int points, final String source, final String type) {
        final Context ctx = context;
        final DatabaseReference mFirebaseUserDatabase = FirebaseDatabase.getInstance().getReference().child(User.FIREBASE_USER_ROOT);
        final DatabaseReference mFirebaseTransactionDatabase = FirebaseDatabase.getInstance().getReference().child(UserTransaction.FIREBASE_TRANSACTION_ROOT);
        final String userId = Utils.getUserId(context);
        mFirebaseUserDatabase.child(userId).child("points").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "KHUSHI snapshot " + dataSnapshot.getValue());
                if (dataSnapshot.getValue() != null) {
                    long totalPoints = (long) dataSnapshot.getValue();
                    mFirebaseUserDatabase.child(userId).child("points").setValue(totalPoints + points);
                    UserTransaction ut = new UserTransaction();
                    ut.setSource(source);
                    ut.setPoints(points);
                    ut.setType(type);
                    mFirebaseTransactionDatabase.child(userId).push().setValue(ut.toMap());
                    showPointsRewardsDialog(ctx, points);
                } else {
                    mFirebaseUserDatabase.child(userId).child("points").setValue(points);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static void showPointsRewardsDialog(Context context, int points){
        new SweetAlertDialog(context, SweetAlertDialog.BUTTON_POSITIVE)
                .setTitleText("Congratulations!!!")
                .setCustomImage(R.mipmap.ic_launcher)
                .setContentText("Congratulations you got "+points+ " points")
                .show();
    }

}
