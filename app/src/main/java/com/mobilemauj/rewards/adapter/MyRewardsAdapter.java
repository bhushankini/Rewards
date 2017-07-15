package com.mobilemauj.rewards.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobilemauj.rewards.R;
import com.mobilemauj.rewards.model.Redeem;
import com.mobilemauj.rewards.model.Rewards;
import com.mobilemauj.rewards.utility.Utils;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class MyRewardsAdapter extends RecyclerView.Adapter<MyRewardsAdapter.RewardsViewHolder> {

    private ArrayList<Redeem> dataSet;
    private Context context;

    public MyRewardsAdapter(Context context, ArrayList<Redeem> data) {
        this.dataSet = data;
        this.context = context;
    }

    @Override
    public RewardsViewHolder onCreateViewHolder(ViewGroup parent,
                                                int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_myrewards, parent, false);

        return new RewardsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RewardsViewHolder holder, final int listPosition) {
        TextView txtDisplay = holder.txtDisplay;
        TextView txtValue = holder.txtValue;
        TextView txtStatus = holder.txtStatus;
        TextView txtRedeem = holder.txtRedeem;
        TextView txtTo = holder.txtTo;

        ImageView imgReward = holder.imgReward;

        txtDisplay.setText(dataSet.get(listPosition).getDisplay());
        txtValue.setText("" + dataSet.get(listPosition).getValue());
        String status;
        if (dataSet.get(listPosition).getStatus() == 0) {
            status = String.format(context.getString(R.string.status), context.getString(R.string.pending),"");

        } else {
            status = String.format(context.getString(R.string.status
            ),context.getString(R.string.completed
            ), Utils.localDate(dataSet.get(listPosition).getProcessDate()));
        }
        txtStatus.setText(status);
        //String redeem = String.format(context.getString(R.string.redeem_date), Utils.localDate(dataSet.get(listPosition).getTimestamp()));
        txtRedeem.setText(String.format(context.getString(R.string.redeem_date), Utils.localDate(dataSet.get(listPosition).getTimestamp())));

        //       String recipient = String.format(context.getString(R.string.to_recipient), dataSet.get(listPosition).getRecipient());
        txtTo.setText(String.format(context.getString(R.string.to_recipient), dataSet.get(listPosition).getRecipient()));

        String url = Rewards.IMAGES_BASE_URL + dataSet.get(listPosition).getBrand().toLowerCase() + "_small.png";

        Picasso.with(context).load(url).into(imgReward);

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class RewardsViewHolder extends RecyclerView.ViewHolder {

        private TextView txtDisplay;
        private TextView txtValue;
        private ImageView imgReward;
        private TextView txtStatus;
        private TextView txtRedeem;
        private TextView txtTo;

        public RewardsViewHolder(View itemView) {
            super(itemView);
            this.txtDisplay = (TextView) itemView.findViewById(R.id.tv_display);
            this.txtValue = (TextView) itemView.findViewById(R.id.tv_value);
            this.imgReward = (ImageView) itemView.findViewById(R.id.imgReward);
            this.txtStatus = (TextView) itemView.findViewById(R.id.txtStatus);
            this.txtRedeem = (TextView) itemView.findViewById(R.id.txtRequestDate);
            this.txtTo = (TextView) itemView.findViewById(R.id.txtTo);
        }
    }

}
