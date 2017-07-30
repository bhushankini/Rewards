package com.mobilemauj.rewards.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.database.ValueEventListener;
import com.mobilemauj.rewards.R;
import com.mobilemauj.rewards.model.User;
import com.mobilemauj.rewards.utility.Constants;
import com.mobilemauj.rewards.utility.FirebaseDatabaseUtil;
import com.mobilemauj.rewards.utility.LogUtil;
import com.mobilemauj.rewards.utility.Utils;
import com.vungle.publisher.EventListener;
import com.vungle.publisher.VunglePub;
import java.util.Map;

public class VideoFragment extends Fragment implements View.OnClickListener ,RewardedVideoAdListener {

    private final VunglePub vunglePub = VunglePub.getInstance();
    //Vungle
    private final EventListener vungleAdListener = new EventListener() {
        @Override
        public void onAdEnd(boolean b, boolean b1) {
            if(b){
                FirebaseDatabaseUtil.rewardsPoints(getActivity(),2,"Vungle", "Video");
                FirebaseDatabaseUtil.incrementVideoCounter(getActivity(),false);
                updateVideoCountUI(1);
            }
        }

        @Override
        public void onAdStart() {

        }

        @Override
        public void onAdUnavailable(String s) {

        }

        @Override
        public void onAdPlayableChanged(boolean b) {
        }

        @Override
        public void onVideoView(boolean b, int i, int i1) {
        }
    };
    private Button btnAppLovin;
    private Button btnAdmob;
    private Button btnVungle;
    private Button btnVideoBonus;
    private RewardedVideoAd mRewardedVideoAd;
    private AppLovinIncentivizedInterstitial myIncent;

    public VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            }

            @Override
            public void failedToReceiveAd(int errorCode) {
                // A rewarded video failed to load.
                LogUtil.e("Failed loading ad " + errorCode);
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_video, container, false);
        btnAppLovin = (Button) view.findViewById(R.id.btnAppLovin);
        btnAppLovin.setOnClickListener(this);

        btnAdmob = (Button) view.findViewById(R.id.btnAdmob);
        btnAdmob.setOnClickListener(this);

        btnVungle = (Button) view.findViewById(R.id.btnVungle);
        btnVungle.setOnClickListener(this);

        btnVideoBonus =(Button) view.findViewById( R.id.btnVideoBonus);
        btnVideoBonus.setOnClickListener(this);
      //  MobileAds.initialize(getActivity(), Constants.ADMOB_APP_ID);
        getVideoCount();

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case  R.id.btnAdmob:
                showRewardedVideo();

                break;
            case R.id.btnAppLovin:

                if (myIncent.isAdReadyToDisplay()) {
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


                            // By default we'll show a alert informing your user of the currency & amount earned.
                            // If you don't want this, you can turn it off in the Manage Apps UI.
                        }

                        @Override
                        public void userOverQuota(AppLovinAd appLovinAd, Map map) {
                            // Your user has already earned the max amount you allowed for the day at this point, so
                            // don't give them any more money. By default we'll show them a alert explaining this,
                            // though you can change that from the AppLovin dashboard.

                        }

                        @Override
                        public void userRewardRejected(AppLovinAd appLovinAd, Map map) {
                            // Your user couldn't be granted a reward for this view. This could happen if you've blacklisted
                            // them, for example. Don't grant them any currency. By default we'll show them an alert explaining this,
                            // though you can change that from the AppLovin dashboard.

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

                        }

                        @Override
                        public void userDeclinedToViewAd(AppLovinAd appLovinAd) {
                            // This method will be invoked if the user selected "no" when asked if they want to view an ad.
                            // If you've disabled the pre-video prompt in the "Manage Apps" UI on our website, then this method won't be called.

                        }
                    };

                    // Video Playback Listener
                    AppLovinAdVideoPlaybackListener adVideoPlaybackListener = new AppLovinAdVideoPlaybackListener() {
                        @Override
                        public void videoPlaybackBegan(AppLovinAd appLovinAd) {
                        }

                        @Override
                        public void videoPlaybackEnded(AppLovinAd appLovinAd, double v, boolean b) {
                            // rewardsPoints(2,"Applovin", "Video");
                            if(b) {
                                FirebaseDatabaseUtil.rewardsPoints(getActivity(), 2, "Applovin", "Video");
                                FirebaseDatabaseUtil.incrementVideoCounter(getActivity(), false);
                                updateVideoCountUI(1);
                            }

                        }
                    };

                    // Ad Dispaly Listener
                    AppLovinAdDisplayListener adDisplayListener = new AppLovinAdDisplayListener() {
                        @Override
                        public void adDisplayed(AppLovinAd appLovinAd) {
                       }

                        @Override
                        public void adHidden(AppLovinAd appLovinAd) {
                            myIncent.preload(null);

                        }
                    };

                    // Ad Click Listener
                    AppLovinAdClickListener adClickListener = new AppLovinAdClickListener() {
                        @Override
                        public void adClicked(AppLovinAd appLovinAd) {
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
            case
            R.id.btnVideoBonus:
                btnVideoBonus.setEnabled(false);
                FirebaseDatabaseUtil.incrementVideoCounter(getActivity(),true);
                FirebaseDatabaseUtil.rewardsPoints(getActivity(),100,"Video", "Bonus");
                updateVideoCountUI(-100);
            break;
            default:
        }
    }


    //Admob Start
    @Override
    public void onRewardedVideoAdLoaded() {
        btnAdmob.setEnabled(true);
    }

    @Override
    public void onRewardedVideoAdOpened() {
    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        FirebaseDatabaseUtil.incrementVideoCounter(getActivity(),false);
        FirebaseDatabaseUtil.rewardsPoints(getActivity(),2,"Admob", "Video");
        updateVideoCountUI(1);
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
    }

    //Admob End

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onResume() {
        super.onResume();
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getActivity());
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
      //  getVideoCount();
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

    private void getVideoCount(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(User.FIREBASE_USER_ROOT).child(Utils.getUserId(getActivity())).child(User.FIREBASE_USER_VIDEO_COUNT);
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LogUtil.d(dataSnapshot.toString());
                if(dataSnapshot.getValue() != null) {
                    int videoCount = Integer.parseInt(dataSnapshot.getValue().toString());
                    btnVideoBonus.setText(videoCount + "/100");
                    if (videoCount < 100) {
                        btnVideoBonus.setEnabled(false);
                    } else
                        btnVideoBonus.setEnabled(true);
                } else {
                    btnVideoBonus.setText("0/100");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateVideoCountUI(int number){
        String buttonText = btnVideoBonus.getText().toString();
        buttonText = buttonText.substring(0,buttonText.indexOf("/"));
        final int currentCount = Integer.parseInt(buttonText) + number;

        new Handler().post(new Runnable() { // Tried new Handler(Looper.myLopper()) also
            @Override
            public void run() {
                btnVideoBonus.setText(currentCount+"/100");
            }
        });
    }
}
