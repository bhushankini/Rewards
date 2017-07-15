package com.mobilemauj.rewards.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobilemauj.rewards.R;
import com.mobilemauj.rewards.adapter.LeaderBoardAdapter;
import com.mobilemauj.rewards.model.User;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by bkini on 6/18/17.
 */

public class LeaderBoardFragment extends Fragment {
    private LeaderBoardAdapter adapter;
    private ArrayList<User> userArrayList = new ArrayList<>();
    private RecyclerView recycler;
    public LeaderBoardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        recycler = (RecyclerView) root.findViewById(R.id.lb_recycler_view);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setHasFixedSize(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLeaderBoard();
    }

    private void updateLeaderBoard(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(User.FIREBASE_USER_ROOT);
        ref.orderByChild("points").limitToLast(10).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userArrayList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    userArrayList.add(user);
                }

                Collections.reverse(userArrayList);
                adapter = new LeaderBoardAdapter(getActivity(), userArrayList);
                recycler.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}