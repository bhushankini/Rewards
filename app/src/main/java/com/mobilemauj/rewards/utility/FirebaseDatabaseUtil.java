package com.mobilemauj.rewards.utility;

import android.content.Context;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.mobilemauj.rewards.R;
import com.mobilemauj.rewards.model.User;
import com.mobilemauj.rewards.model.UserTransaction;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class FirebaseDatabaseUtil {

    public static void rewardsPoints(Context context, final int points, final String source, final String type) {
        final Context ctx = context;
        final DatabaseReference mFirebaseUserDatabase = FirebaseDatabase.getInstance().getReference().child(User.FIREBASE_USER_ROOT);
        final DatabaseReference mFirebaseTransactionDatabase = FirebaseDatabase.getInstance().getReference().child(UserTransaction.FIREBASE_TRANSACTION_ROOT);
        final String userId = Utils.getUserId(context);
        mFirebaseUserDatabase.child(userId).child(User.FIREBASE_USER_CHILD_POINT).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    long totalPoints = (long) dataSnapshot.getValue();
                    mFirebaseUserDatabase.child(userId).child(User.FIREBASE_USER_CHILD_POINT).setValue(totalPoints + points);
                    UserTransaction ut = new UserTransaction();
                    ut.setSource(source);
                    ut.setPoints(points);
                    ut.setType(type);
                    mFirebaseTransactionDatabase.child(userId).push().setValue(ut.toMap());
                    showPointsRewardsDialog(ctx, points);
                } else {
                    mFirebaseUserDatabase.child(userId).child(User.FIREBASE_USER_CHILD_POINT).setValue(points);
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

  // Toast.makeText(context, "Congratulations you got "+points+ " points",Toast.LENGTH_LONG).show();
    }


    public static void incrementVideoCounter(Context context,final boolean resetCounter) {
        final String userId = Utils.getUserId(context);
        final DatabaseReference mFirebaseUserDatabase = FirebaseDatabase.getInstance().getReference().child(User.FIREBASE_USER_ROOT).child(userId).child(User.FIREBASE_USER_VIDEO_COUNT);

        mFirebaseUserDatabase.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(final MutableData currentData) {
                if(!resetCounter) {
                    if (currentData.getValue() == null) {
                        currentData.setValue(1);
                    } else {
                        currentData.setValue((Long) currentData.getValue() + 1);
                    }
                } else {
                    currentData.setValue((Long) currentData.getValue() - 100);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    LogUtil.e("Database failure.");
                }
            }
        });
    }
}
