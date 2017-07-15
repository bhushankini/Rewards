package com.mobilemauj.rewards;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobilemauj.rewards.adapter.MyRewardsAdapter;
import com.mobilemauj.rewards.model.Redeem;
import com.mobilemauj.rewards.model.Rewards;
import com.mobilemauj.rewards.utility.Utils;

import java.util.ArrayList;


public class MyRewardsActivity extends BaseActivity {

    private static final String TAG = "MyRewardsActivity";

    private DatabaseReference mFirebaseRedeemDatabase;

    private MyRewardsAdapter adapter;
    private ArrayList<Redeem> redeemList = new ArrayList<>();
    private RecyclerView recycler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myrewards);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        recycler = (RecyclerView)findViewById(R.id.myrewards_recycler_view);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setHasFixedSize(true);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private void getRewardsList() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Rewards.FIREBASE_REDEEM_ROOT);
        ref.child(Utils.getUserId(this)).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                redeemList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Redeem txn = ds.getValue(Redeem.class);
                    redeemList.add(txn);
                }

                adapter = new MyRewardsAdapter(MyRewardsActivity.this,redeemList);
                recycler.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRewardsList();
    }
}