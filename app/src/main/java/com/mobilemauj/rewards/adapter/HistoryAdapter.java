package com.mobilemauj.rewards.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobilemauj.rewards.R;
import com.mobilemauj.rewards.model.UserTransaction;
import com.mobilemauj.rewards.utility.Utils;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private ArrayList<UserTransaction> dataSet;

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {

        private TextView txtSource;
        private TextView txtPoints;
        private TextView txtTime;


        public HistoryViewHolder(View itemView) {
            super(itemView);
            this.txtSource = (TextView) itemView.findViewById(R.id.tv_display);
            this.txtPoints = (TextView) itemView.findViewById(R.id.tv_points);
            this.txtTime = (TextView) itemView.findViewById(R.id.tv_timestamp);
        }
    }

    public HistoryAdapter(ArrayList<UserTransaction> data) {
        this.dataSet = data;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent,
                                                int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);

        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final HistoryViewHolder holder, final int listPosition) {
        TextView txtSource = holder.txtSource;
        TextView txtPoints = holder.txtPoints;
        TextView txtTime = holder.txtTime;


        txtSource.setText(dataSet.get(listPosition).getSource() + " " + dataSet.get(listPosition).getType());
        txtPoints.setText(""+dataSet.get(listPosition).getPoints());
        if (dataSet.get(listPosition).getPoints() > 0) {
            txtPoints.setTextColor(Color.parseColor("#006600"));
            txtPoints.setText(""+dataSet.get(listPosition).getPoints());

        } else {
            txtPoints.setTextColor(Color.RED);
        }

        txtTime.setText(Utils.localTime(dataSet.get(listPosition).getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
