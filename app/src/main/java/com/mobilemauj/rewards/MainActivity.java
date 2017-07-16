package com.mobilemauj.rewards;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.mobilemauj.rewards.fragments.AccountFragment;
import com.mobilemauj.rewards.fragments.HistoryFragment;
import com.mobilemauj.rewards.fragments.LeaderBoardFragment;
import com.mobilemauj.rewards.fragments.RewardsFragment;
import com.mobilemauj.rewards.fragments.VideoFragment;
import com.mobilemauj.rewards.model.Statistics;
import com.mobilemauj.rewards.model.User;
import com.mobilemauj.rewards.utility.Constants;
import com.mobilemauj.rewards.utility.FirebaseDatabaseUtil;
import com.mobilemauj.rewards.utility.PrefUtils;
import com.mobilemauj.rewards.utility.ShortURL;
import com.mobilemauj.rewards.utility.Utils;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Rewards" ;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView txtPoints;
    private DatabaseReference mFirebaseUserDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseStatisticsDatabase;
    private Button btnRetry;
    private RelativeLayout rlNoConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        rlNoConnection = (RelativeLayout) findViewById(R.id.rl_noconnection);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        btnRetry =(Button) findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkConnection();
            }
        });

        txtPoints = (TextView) findViewById(R.id.toolbar_points);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseUserDatabase = mFirebaseInstance.getReference(User.FIREBASE_USER_ROOT);
        mFirebaseStatisticsDatabase = mFirebaseInstance.getReference(Statistics.FIREBASE_STATISTICS_ROOT);
        if (PrefUtils.getStringFromPrefs(this,Constants.REFERRAL_LINK,"").length() < 1){
            getShortReferralLink();
        }
        addPointsChangeListener();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                checkConnection();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new VideoFragment(), "Home");
        adapter.addFrag(new RewardsFragment(), "Rewards");
        adapter.addFrag(new LeaderBoardFragment(), "Leaderboard");
        adapter.addFrag(new HistoryFragment(), "History");
        adapter.addFrag(new AccountFragment(), "Account");
        viewPager.setAdapter(adapter);
    }

    private void checkConnection(){
        if(Utils.isNetworkAvailable(this)){
            viewPager.setVisibility(View.VISIBLE);
            rlNoConnection.setVisibility(View.GONE);
        } else {
            viewPager.setVisibility(View.GONE);
            rlNoConnection.setVisibility(View.VISIBLE);

        }
    }
    private void addPointsChangeListener() {
        // User data change listener
        mFirebaseUserDatabase.child(Utils.getUserId(MainActivity.this)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                // Check for null

                if (user == null) {
                    return;
                }

                PrefUtils.saveLongToPrefs(MainActivity.this, Constants.LAST_DAILY_REWARD,user.getLastopen());
                PrefUtils.saveStringToPrefs(MainActivity.this, Constants.USER_NAME,user.getName());

                boolean isNewDay = Utils.isNewDate(user.getLastopen(),PrefUtils.getLongFromPrefs(MainActivity.this,Constants.SERVER_TIME,user.getLastopen()));

                Log.e("KHUSHI", "KHUSHI is new Day " + isNewDay);
                initStatistics(isNewDay);

           //     PrefUtils.saveToPrefs(MainActivity.this, Constants.USER_ID, user.getUserId());
                txtPoints.setText(" " + user.getPoints() + "  ");
                Log.e("KHUSHI", "KHUSHI last open " + user.getLastopen());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("KHUSHI", "Failed to read user", error.toException());
            }
        });
    }



    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
    }

    private void initStatistics(boolean isNewDay){

        final String userId = Utils.getUserId(this);

        if (isNewDay){

            FirebaseDatabaseUtil.rewardsPoints(this,15,"Daily","Reward");
            mFirebaseUserDatabase.child(userId).child("lastopen").setValue(ServerValue.TIMESTAMP);
            PrefUtils.saveIntToPrefs(this, Constants.DICE_COUNT,0);
            PrefUtils.saveIntToPrefs(this, Constants.TTT_COUNT,0);
            mFirebaseStatisticsDatabase.child(userId).child("dice").setValue(0);
            mFirebaseStatisticsDatabase.child(userId).child("tictactoe").setValue(0);
            return;
        }


        mFirebaseStatisticsDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG,"DDDDDDDD stats "+dataSnapshot.getValue());
                Statistics stats = dataSnapshot.getValue(Statistics.class);
                if(stats == null){
                    Log.e(TAG,"DDDDDDDD stats null");
                } else {
                    PrefUtils.saveIntToPrefs(MainActivity.this, Constants.DICE_COUNT,stats.getDice());
                    PrefUtils.saveIntToPrefs(MainActivity.this, Constants.TTT_COUNT,stats.getTictactoe());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getShortReferralLink(){
        String longUrl = "https://play.google.com/store/apps/details?id=com.mobilemauj.rewards&referrer="+ Utils.encryptData(Utils.getUserId(this));
        ShortURL.makeShortUrl(longUrl, new ShortURL.ShortUrlListener() {
            @Override
            public void OnFinish(String url) {
                if(url != null && 0 < url.length()) {
                    PrefUtils.saveStringToPrefs(MainActivity.this,Constants.REFERRAL_LINK,url);
                }
            }
        });
    }

}
