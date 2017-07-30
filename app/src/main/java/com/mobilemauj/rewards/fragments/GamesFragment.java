package com.mobilemauj.rewards.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.mobilemauj.rewards.R;
import com.mobilemauj.rewards.games.dice.DiceActivity;
import com.mobilemauj.rewards.games.tictactoe.NewTicTacToeActivity;
import com.mobilemauj.rewards.model.User;
import com.mobilemauj.rewards.utility.Constants;
import com.mobilemauj.rewards.utility.FirebaseDatabaseUtil;
import com.mobilemauj.rewards.utility.LogUtil;
import com.mobilemauj.rewards.utility.PrefUtils;
import com.mobilemauj.rewards.utility.Utils;

public class GamesFragment extends Fragment implements View.OnClickListener {

    private RelativeLayout rlDice;
    private RelativeLayout rlTicTacToe;
    private RelativeLayout rlDailyReward;

    private ImageView imgCoin;
    private CallbackManager callbackManager;


    public GamesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(  "onCreate Video");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_game, container, false);
        rlDailyReward = (RelativeLayout) view.findViewById(R.id.rl_dailyreward);
        rlDailyReward.setOnClickListener(this);
        rlDice = (RelativeLayout) view.findViewById(R.id.rl_dice);
        rlDice.setOnClickListener(this);
        rlTicTacToe = (RelativeLayout) view.findViewById(R.id.rl_tictactoe);
        rlTicTacToe.setOnClickListener(this);

        imgCoin = (ImageView) view.findViewById(R.id.ic_coin);
        imgCoin.setImageDrawable(getActivity().getResources().getDrawable(R.mipmap.ic_coin_in));
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.rl_dice:
                startActivity(new Intent(getActivity(), DiceActivity.class));
                break;

            case R.id.rl_tictactoe:
                startActivity(new Intent(getActivity(), NewTicTacToeActivity.class));
                break;

           // case R.id.rl_fbpost:
           //     LogUtil.d( "POST ON FB");
           //     share();
                //shareOnWall();
           //     break;

            case R.id.rl_dailyreward:
                if (isEligibleForDailyReward()) {
                    FirebaseDatabase.getInstance().getReference(User.FIREBASE_USER_ROOT)
                            .child(Utils.getUserId(getActivity())).child("lastopen").setValue(ServerValue.TIMESTAMP);

                    FirebaseDatabaseUtil.rewardsPoints(getActivity(),15,"Daily", "Reward");


                } else {
                    Toast.makeText(getActivity(), getString(R.string.already_claimed), Toast.LENGTH_LONG).show();
                }
                break;
            default:
        }
    }


    private void share() {
        Intent shareIntent;

        shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My Rewards app");

        String link = PrefUtils.getStringFromPrefs(getActivity(), Constants.REFERRAL_LINK, null);
        if (link != null) {
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey get mobile recharge and gift cards " + link);

        } else {
            String referrer = Utils.encryptData(Utils.getUserId(getActivity()));
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey get mobile recharge and gift cards " + "https://play.google.com/store/apps/details?id=com.mobilemauj.rewards&referrer=" + referrer);
        }
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, "Share with"));

    }



    private boolean isEligibleForDailyReward() {
        long serverTime = PrefUtils.getLongFromPrefs(getActivity(), Constants.SERVER_TIME, (long) 0.0);
        long lastDailyReward = PrefUtils.getLongFromPrefs(getActivity(), Constants.LAST_DAILY_REWARD, (long) 0.0);
       return Utils.isNewDate(lastDailyReward, serverTime);
    }

    void shareOnWall() {

        ShareDialog shareDialog = new ShareDialog(getActivity());
        callbackManager = CallbackManager.Factory.create();
        shareDialog.registerCallback(callbackManager, new

                FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {

                        LogUtil.d(  "onSuccess: " + result.toString());
                        LogUtil.d(  "onSuccess: ");
                        Toast.makeText(getActivity(), "onSuccess", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                        LogUtil.d( "onCancel: ");
                        Toast.makeText(getActivity(), "onCancel", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        LogUtil.d(  "onError: ");
                        Toast.makeText(getActivity(), "onError" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse("http://mobilemauj.com/rewards/images/myrewards.png"))
                    .build();

            shareDialog.show(linkContent);
        }
    }
}
