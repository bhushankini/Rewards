package com.mobilemauj.rewards.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.applovin.adview.AppLovinIncentivizedInterstitial;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdClickListener;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinAdRewardListener;
import com.applovin.sdk.AppLovinAdVideoPlaybackListener;
import com.applovin.sdk.AppLovinErrorCodes;
import com.applovin.sdk.AppLovinSdk;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.mobilemauj.rewards.R;
import com.mobilemauj.rewards.games.dice.DiceActivity;
import com.mobilemauj.rewards.games.tictactoe.TicTacToeActivity;
import com.mobilemauj.rewards.model.User;
import com.mobilemauj.rewards.utility.Constants;
import com.mobilemauj.rewards.utility.FirebaseDatabaseUtil;
import com.mobilemauj.rewards.utility.PrefUtils;
import com.mobilemauj.rewards.utility.Utils;
import com.vungle.publisher.EventListener;
import com.vungle.publisher.VunglePub;
import java.util.Map;

public class VideoFragment extends Fragment implements View.OnClickListener ,RewardedVideoAdListener {

    private final String TAG = "REWARDS";
    private Button btnDailyReward;
    private Button btnAppLovin;
    private Button btnAdmob;
    private Button btnVungle;
    RelativeLayout rlDice;
    RelativeLayout rlTicTacToe;
    private RewardedVideoAd mRewardedVideoAd;
    private DatabaseReference mFirebaseUserDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private final VunglePub vunglePub = VunglePub.getInstance();
    private AppLovinIncentivizedInterstitial myIncent;


    public VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate Video");
        // initialize the Publisher SDK
        AppLovinSdk.initializeSdk(getActivity());
        vunglePub.init(getActivity(), Constants.VUNGLE_APP_ID);
        vunglePub.setEventListeners(vungleAdListener);
        myIncent = AppLovinIncentivizedInterstitial.create(getActivity());

        // Preload call using a new load listener
        myIncent.preload(new AppLovinAdLoadListener() {
            @Override
            public void adReceived(AppLovinAd appLovinAd) {
                // A rewarded video was successfully received.
                Log.d(TAG, "BHUSHAN applovin add loded");
            }

            @Override
            public void failedToReceiveAd(int errorCode) {
                // A rewarded video failed to load.
                Log.d(TAG, "BHUSHAN applovin failed " + errorCode);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG,"onCreateView Video");


        final View view = inflater.inflate(R.layout.fragment_video, container, false);
        btnDailyReward = (Button) view.findViewById(R.id.btnDailyReward);
        btnDailyReward.setOnClickListener(this);

        btnAppLovin = (Button) view.findViewById(R.id.btnAppLovin);
        btnAppLovin.setOnClickListener(this);

        btnAdmob = (Button) view.findViewById(R.id.btnAdmob);
        btnAdmob.setOnClickListener(this);

        btnVungle = (Button) view.findViewById(R.id.btnVungle);
        btnVungle.setOnClickListener(this);
        rlDice = (RelativeLayout) view.findViewById(R.id.rl_dice);
        rlDice.setOnClickListener(this);
        rlTicTacToe = (RelativeLayout) view.findViewById(R.id.rl_tictactoe);
        rlTicTacToe.setOnClickListener(this);
        MobileAds.initialize(getActivity(), Constants.ADMOB_APP_ID);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseUserDatabase = mFirebaseInstance.getReference(User.FIREBASE_USER_ROOT);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnDailyReward:
                long serverTime = PrefUtils.getLongFromPrefs(getActivity(), Constants.SERVER_TIME, (long) 0.0);
                long lastDailyReward = PrefUtils.getLongFromPrefs(getActivity(), Constants.LAST_DAILY_REWARD, (long) 0.0);
                boolean dailyReward = Utils.isNewDate(lastDailyReward,serverTime);
                if(dailyReward){
                //    rewardsPoints(15,"Daily", "Reward");
                    FirebaseDatabaseUtil.rewardsPoints(getActivity(),15,"Daily","Reward");
                    PrefUtils.saveLongToPrefs(getActivity(), Constants.LAST_DAILY_REWARD, serverTime);

                    mFirebaseUserDatabase.child(Utils.getUserId(getActivity())).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getValue() != null) {
                                Log.d(TAG, "KHUSHI111 snapshot setting last open" + dataSnapshot.getValue());
                                mFirebaseUserDatabase.child(Utils.getUserId(getActivity())).child("lastopen").setValue(ServerValue.TIMESTAMP);


                            } else {
                                Log.d(TAG, "KHUSHI111 snapshot Error setting lastopne");

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                } else {
                    Toast.makeText(getActivity(),getString(R.string.already_claimed),Toast.LENGTH_LONG).show();

                }

                break;
            case  R.id.btnAdmob:
                showRewardedVideo();

                break;
            case R.id.btnAppLovin:

                if (myIncent.isAdReadyToDisplay()) {
                    //
                    // OPTIONAL: Create listeners
                    //

                    // Reward Listener
                    AppLovinAdRewardListener adRewardListener = new AppLovinAdRewardListener() {
                        @Override
                        public void userRewardVerified(AppLovinAd appLovinAd, Map map) {
                            // AppLovin servers validated the reward. Refresh user balance from your server.  We will also pass the number of coins
                            // awarded and the name of the currency.  However, ideally, you should verify this with your server before granting it.

                            // i.e. - "Coins", "Gold", whatever you set in the dashboard.
                            String currencyName = (String) map.get("currency");

                            // For example, "5" or "5.00" if you've specified an amount in the UI.
                            String amountGivenString = (String) map.get("amount");

                            log("Rewarded " + amountGivenString + " " + currencyName);

                            // By default we'll show a alert informing your user of the currency & amount earned.
                            // If you don't want this, you can turn it off in the Manage Apps UI.
                        }

                        @Override
                        public void userOverQuota(AppLovinAd appLovinAd, Map map) {
                            // Your user has already earned the max amount you allowed for the day at this point, so
                            // don't give them any more money. By default we'll show them a alert explaining this,
                            // though you can change that from the AppLovin dashboard.

                            log("Reward validation request exceeded quota with response: " + map);
                        }

                        @Override
                        public void userRewardRejected(AppLovinAd appLovinAd, Map map) {
                            // Your user couldn't be granted a reward for this view. This could happen if you've blacklisted
                            // them, for example. Don't grant them any currency. By default we'll show them an alert explaining this,
                            // though you can change that from the AppLovin dashboard.

                            log("Reward validation request was rejected with response: " + map);
                        }

                        @Override
                        public void validationRequestFailed(AppLovinAd appLovinAd, int responseCode) {
                            if (responseCode == AppLovinErrorCodes.INCENTIVIZED_USER_CLOSED_VIDEO) {
                                // Your user exited the video prematurely. It's up to you if you'd still like to grant
                                // a reward in this case. Most developers choose not to. Note that this case can occur
                                // after a reward was initially granted (since reward validation happens as soon as a
                                // video is launched).
                            } else if (responseCode == AppLovinErrorCodes.INCENTIVIZED_SERVER_TIMEOUT || responseCode == AppLovinErrorCodes.INCENTIVIZED_UNKNOWN_SERVER_ERROR) {
                                // Some server issue happened here. Don't grant a reward. By default we'll show the user
                                // a alert telling them to try again later, but you can change this in the
                                // AppLovin dashboard.
                            } else if (responseCode == AppLovinErrorCodes.INCENTIVIZED_NO_AD_PRELOADED) {
                                // Indicates that the developer called for a rewarded video before one was available.
                                // Note: This code is only possible when working with rewarded videos.
                            }

                            log("Reward validation request failed with error code: " + responseCode);
                        }

                        @Override
                        public void userDeclinedToViewAd(AppLovinAd appLovinAd) {
                            // This method will be invoked if the user selected "no" when asked if they want to view an ad.
                            // If you've disabled the pre-video prompt in the "Manage Apps" UI on our website, then this method won't be called.

                            log("User declined to view ad");
                        }
                    };

                    // Video Playback Listener
                    AppLovinAdVideoPlaybackListener adVideoPlaybackListener = new AppLovinAdVideoPlaybackListener() {
                        @Override
                        public void videoPlaybackBegan(AppLovinAd appLovinAd) {
                            log("Video Started");
                        }

                        @Override
                        public void videoPlaybackEnded(AppLovinAd appLovinAd, double v, boolean b) {
                            log("Video Ended");
                           // rewardsPoints(2,"Applovin", "Video");
                            FirebaseDatabaseUtil.rewardsPoints(getActivity(),2,"Applovin", "Video");

                        }
                    };

                    // Ad Dispaly Listener
                    AppLovinAdDisplayListener adDisplayListener = new AppLovinAdDisplayListener() {
                        @Override
                        public void adDisplayed(AppLovinAd appLovinAd) {
                            log("Ad Displayed");
                        }

                        @Override
                        public void adHidden(AppLovinAd appLovinAd) {
                            log("Ad Dismissed");
                            myIncent.preload(null);

                        }
                    };

                    // Ad Click Listener
                    AppLovinAdClickListener adClickListener = new AppLovinAdClickListener() {
                        @Override
                        public void adClicked(AppLovinAd appLovinAd) {
                            log("Ad Click");
                        }
                    };

                    /*
                     NOTE: We recommend the use of placements (AFTER creating them in your dashboard):
                     incentivizedInterstitial.show("REWARDED_VIDEO_DEMO_SCREEN", adRewardListener, adVideoPlaybackListener, adDisplayListener, adClickListener);
                     To learn more about placements, check out https://applovin.com/integration#androidPlacementsIntegration
                     */
                    myIncent.show(getActivity() ,adRewardListener, adVideoPlaybackListener, adDisplayListener, adClickListener);
                } else {
                    Toast.makeText(getActivity(),getString(R.string.video_not_ready),Toast.LENGTH_LONG).show();
                }


                break;
            case  R.id.btnVungle:
                if(vunglePub.isAdPlayable()) {
                    vunglePub.playAd();
                } else {
                    Toast.makeText(getActivity(),getString(R.string.video_not_ready),Toast.LENGTH_LONG).show();
                }
                break;

            case  R.id.rl_dice:
                startActivity(new Intent(getActivity(), DiceActivity.class));
                break;

            case  R.id.rl_tictactoe:
                startActivity(new Intent(getActivity(), TicTacToeActivity.class));
                break;
            default:
        }
    }

    private void log(String s){
        Log.d(TAG,"BHUSHAN "+s);
    }
    //Admob Start
    @Override
    public void onRewardedVideoAdLoaded() {
        Log.d(TAG,"onRewardedVideoAdLoaded");
        btnAdmob.setEnabled(true);
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Log.d(TAG,"onRewardedVideoAdOpened");
    }

    @Override
    public void onRewardedVideoStarted() {
        Log.d(TAG,"onRewardedVideoStarted");
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Log.d(TAG,"onRewardedVideoAdClosed");
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        Log.d(TAG,"onRewarded");
       // rewardsPoints(2,"Admob", "Video");
        FirebaseDatabaseUtil.rewardsPoints(getActivity(),2,"Admob", "Video");

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Log.d(TAG,"onRewardedVideoAdLeftApplication");
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        Log.d(TAG,"onRewardedVideoAdFailedToLoad");
    }

    //Admob End


    @Override
    public void onResume() {
        super.onResume();
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getActivity());
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
    }

    private void loadRewardedVideoAd() {
        if (!mRewardedVideoAd.isLoaded()) {
            //Nexus 5
            mRewardedVideoAd.loadAd(Constants.ADMOB_AD_UNIT_ID, new AdRequest.Builder().addTestDevice("F98B32499B302F1D5145AF987EACC26E").build());

            //Moto g
          //  mRewardedVideoAd.loadAd(Constants.ADMOB_AD_UNIT_ID, new AdRequest.Builder().addTestDevice("56480886047D624B5EC3065A430E7E04").build());
        }
    }

    private void showRewardedVideo() {
        btnAdmob.setEnabled(false);
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        } else {
            Toast.makeText(getActivity(),getString(R.string.video_not_ready),Toast.LENGTH_LONG).show();
        }
    }


    //Vungle
    private final EventListener vungleAdListener = new EventListener() {
        @Override
        public void onAdEnd(boolean b, boolean b1) {
            Log.d(TAG, "KHUSHI Vungle ad ended b= "+b+ "  b1= "+b1  );
            if(b){
                FirebaseDatabaseUtil.rewardsPoints(getActivity(),2,"Vungle", "Video");
            }
        }

        @Override
        public void onAdStart() {
            Log.d(TAG, "KHUSHI Vungle onAdStart " );

        }

        @Override
        public void onAdUnavailable(String s) {
            Log.d(TAG, "KHUSHI Vungle ad onAdUnavailable " );

        }

        @Override
        public void onAdPlayableChanged(boolean b) {
            Log.d(TAG, "KHUSHI Vungle ad onAdPlayableChanged " );

        }

        @Override
        public void onVideoView(boolean b, int i, int i1) {
            Log.d(TAG, "KHUSHI Vungle ad onVideoView " );

        }
    };

  /*  private void rewardsPoints(final int points, final String source, final String type) {
        Log.d(TAG, "KHUSHI updatePoints ");
        final String userId = PrefUtils.getStringFromPrefs(getActivity(), Constants.USER_ID, "unknownuser");
        Log.d(TAG, "KHUSHI updatePoints userId "+userId);
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
                    showPointsRewardsDialog(points);
                } else {
                    mFirebaseUserDatabase.child(userId).child("points").setValue(points);
                    // txtPoints.setText(points + "  "); //update points label

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //
    }


    private void showPointsRewardsDialog(int points){
        new SweetAlertDialog(getActivity(), SweetAlertDialog.BUTTON_POSITIVE)
                .setTitleText("Congratulations!!!")
                .setCustomImage(R.mipmap.ic_launcher)
                .setContentText("Congratulations you got "+points+ " points")
                .show();
    }
*/
}
