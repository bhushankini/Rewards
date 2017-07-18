package com.mobilemauj.rewards.games.dice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.mobilemauj.rewards.R;
import com.mobilemauj.rewards.model.Statistics;
import com.mobilemauj.rewards.model.User;
import com.mobilemauj.rewards.model.UserTransaction;
import com.mobilemauj.rewards.utility.Constants;
import com.mobilemauj.rewards.utility.FirebaseDatabaseUtil;
import com.mobilemauj.rewards.utility.PrefUtils;
import com.mobilemauj.rewards.utility.Utils;

import java.util.Random;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class DiceActivity extends AppCompatActivity {

    private static final Random RANDOM = new Random();
    private Button rollDices;
    private ImageView imageView1, imageView2;
    private TextView txtMessage;
    private int total = 0;
    private boolean diceOneStatus = false;
    private boolean diceTwoStatus = false;
    private int gameCount = 0;

    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseUserDatabase;
    private DatabaseReference mFirebaseTransactionDatabase;
    private DatabaseReference mFirebaseStatsticsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dice);
        rollDices = (Button) findViewById(R.id.rollDices);
        imageView1 = (ImageView) findViewById(R.id.imageView1);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        txtMessage = (TextView) findViewById(R.id.txtMessage);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseUserDatabase = mFirebaseInstance.getReference(User.FIREBASE_USER_ROOT);
        mFirebaseTransactionDatabase = mFirebaseInstance.getReference(UserTransaction.FIREBASE_TRANSACTION_ROOT);
        mFirebaseStatsticsDatabase = mFirebaseInstance.getReference(Statistics.FIREBASE_STATISTICS_ROOT);

        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Roll Dice");
        }

        rollDices.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                rollDices.setEnabled(false);
                final Animation anim1 = AnimationUtils.loadAnimation(DiceActivity.this, R.anim.shake);
                final Animation anim2 = AnimationUtils.loadAnimation(DiceActivity.this, R.anim.shake);

                final Animation.AnimationListener animationListener = new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        total = 0;
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        int value = randomDiceValue();
                        total = total + value;
                        int res = getResources().getIdentifier("dice_" + value, "drawable", "com.mobilemauj.rewards");
                        if (animation == anim1) {
                            imageView1.setImageResource(res);
                            diceOneStatus = true;
                        } else if (animation == anim2) {
                            imageView2.setImageResource(res);
                            diceTwoStatus = true;
                        }

                        if(diceOneStatus && diceTwoStatus){
                            diceOneStatus = false;
                            diceTwoStatus = false;
                            rollDices.setEnabled(true);
                            gameCount(total,true);
                            total = 0;
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                };

                anim1.setAnimationListener(animationListener);
                anim2.setAnimationListener(animationListener);

                imageView1.startAnimation(anim1);
                imageView2.startAnimation(anim2);

            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private static int randomDiceValue() {
        return RANDOM.nextInt(6) + 1;
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameCount(total, false);

    }

    private void gameCount(final int points, final boolean updateCount){
        final int localCount = PrefUtils.getIntFromPrefs(this,Constants.DICE_COUNT,0);
        txtMessage.setText("You have "+(3 - localCount)+" dice rolls");
        if(localCount > 2) {
            txtMessage.setText("You already rolled 3 times today. Come back tomorrow");
            rollDices.setEnabled(false);
            return;
        }

        Log.d("TAG","KKKKK Local Count "+localCount);

            final String userId =  Utils.getUserId(this);
            mFirebaseStatsticsDatabase.child(userId).child(Statistics.FIREBASE_STATISTICS_CHILD_DICE).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        long totalPoints = (long) dataSnapshot.getValue();
                        Log.d("TAG","KKKKK totla "+totalPoints);
                        gameCount = (int) totalPoints;
                        if(gameCount >2 || localCount > 2) {
                            txtMessage.setText("You already rolled 3 times today. Come back tomorrow");
                            rollDices.setEnabled(false);
                        } else {
                            rollDices.setEnabled(true);
                        }
                        if(updateCount) {
                            mFirebaseStatsticsDatabase.child(userId).child(Statistics.FIREBASE_STATISTICS_CHILD_DICE).setValue(localCount+1);
                            mFirebaseStatsticsDatabase.child(userId).child(Statistics.FIREBASE_STATISTICS_CHILD_LAST_PLAYED).setValue(ServerValue.TIMESTAMP);
                            PrefUtils.saveIntToPrefs(DiceActivity.this,Constants.DICE_COUNT, localCount +1);
                            Log.d("TAG","KKKKK update db "+gameCount);
                            txtMessage.setText("You have "+(3 - localCount - 1)+" dice rolls");

                        //    rewardsPoints(points, "Roll Dice", "Game");
                            FirebaseDatabaseUtil.rewardsPoints(DiceActivity.this,points,"Roll Dice", "Game");
                            if(localCount+1 >2){
                                txtMessage.setText("You already rolled 3 times today. Come back tomorrow");
                                rollDices.setEnabled(false);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }
}