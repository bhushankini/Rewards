package com.mobilemauj.rewards.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.mobilemauj.rewards.R;
import com.mobilemauj.rewards.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.LeaderBoardViewHolder> {

    private ArrayList<User> dataSet;
    private Context context;

    public static class LeaderBoardViewHolder extends RecyclerView.ViewHolder {

        private TextView txtName;
        private TextView txtPoints;
        private TextView txtRank;
        private ImageView imgProfile;

        public LeaderBoardViewHolder(View itemView) {
            super(itemView);

            this.txtName = (TextView) itemView.findViewById(R.id.tv_name);
            this.txtPoints = (TextView) itemView.findViewById(R.id.tv_points);
            this.txtRank = (TextView) itemView.findViewById(R.id.tv_rank);
            this.imgProfile = (ImageView) itemView.findViewById(R.id.img_profile);

        }
    }

    public LeaderBoardAdapter(Context context, ArrayList<User> data) {
        this.context = context;
        this.dataSet = data;
    }

    @Override
    public LeaderBoardViewHolder onCreateViewHolder(ViewGroup parent,
                                                int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_leaderboard, parent, false);

        return new LeaderBoardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final LeaderBoardViewHolder holder, final int listPosition) {
        TextView txtName = holder.txtName;
        TextView txtPoints = holder.txtPoints;
        TextView txtRank = holder.txtRank;
        ImageView imgProfile = holder.imgProfile;

        txtName.setText(dataSet.get(listPosition).getName());
        txtPoints.setText(""+dataSet.get(listPosition).getPoints());
        txtRank.setText("#"+(listPosition+1));
        Picasso.with(context).load(dataSet.get(listPosition).getPhotoUrl()).placeholder(R.drawable.user_icon).into(imgProfile);

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
