package com.mobilemauj.rewards.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.mobilemauj.rewards.R;
import com.mobilemauj.rewards.RedeemActivity;
import com.mobilemauj.rewards.model.Rewards;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class RewardsAdapter extends RecyclerView.Adapter<RewardsAdapter.RewardsViewHolder> {

    private ArrayList<Rewards> dataSet;
    private Context context;

    public RewardsAdapter(Context context, ArrayList<Rewards> data) {
        this.dataSet = data;
        this.context = context;
    }

    @Override
    public RewardsViewHolder onCreateViewHolder(ViewGroup parent,
                                                int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rewards, parent, false);
        return new RewardsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RewardsViewHolder holder, final int listPosition) {
        TextView txtDisplay = holder.txtDisplay;
        TextView txtValue = holder.txtValue;
        ImageView imgCoin = holder.imgCoin;

        ImageView imgReward = holder.imgReward;
        CardView cardView = holder.cardView;
        if(dataSet != null) {
            txtDisplay.setText(dataSet.get(listPosition).getDisplay());
            txtValue.setText("" + dataSet.get(listPosition).getValue());
            String url = Rewards.IMAGES_BASE_URL + dataSet.get(listPosition).getBrand().toLowerCase() + "_small.png";
            imgCoin.setBackground(context.getResources().getDrawable(R.mipmap.ic_coin_in));
            Picasso.with(context).load(url).into(imgReward);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, RedeemActivity.class);
                    intent.putExtra(Rewards.REWARD_EXTRAS, dataSet.get(listPosition));
                    context.startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class RewardsViewHolder extends RecyclerView.ViewHolder {

        private TextView txtDisplay;
        private TextView txtValue;
        private ImageView imgReward;
        private ImageView imgCoin;
        private CardView cardView;

        public RewardsViewHolder(View itemView) {
            super(itemView);
            this.txtDisplay = (TextView) itemView.findViewById(R.id.tv_display);
            this.txtValue =(TextView) itemView.findViewById(R.id.tv_value);
            this.imgReward = (ImageView) itemView.findViewById(R.id.imgReward);
            this.imgCoin = (ImageView) itemView.findViewById(R.id.imgCoin);
            this.cardView = (CardView) itemView.findViewById(R.id.card_view);

        }
    }

}
