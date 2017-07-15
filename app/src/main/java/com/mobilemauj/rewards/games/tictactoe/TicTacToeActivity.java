package com.mobilemauj.rewards.games.tictactoe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobilemauj.rewards.R;
import com.mobilemauj.rewards.model.Statistics;
import com.mobilemauj.rewards.model.User;
import com.mobilemauj.rewards.model.UserTransaction;
import com.mobilemauj.rewards.utility.Constants;
import com.mobilemauj.rewards.utility.PrefUtils;
import com.mobilemauj.rewards.utility.Utils;
import java.util.Random;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class TicTacToeActivity extends AppCompatActivity {

    int c[][];
    int i, j;
    Button b[][];
    TextView txtMessage;
    AI ai;
    private int gameCount = 0;
    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseUserDatabase;
    private DatabaseReference mFirebaseTransactionDatabase;
    private DatabaseReference mFirebaseStatsticsDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tictactoe);
        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.tic_tac_toe));
        }
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseUserDatabase = mFirebaseInstance.getReference(User.FIREBASE_USER_ROOT);
        mFirebaseTransactionDatabase = mFirebaseInstance.getReference(UserTransaction.FIREBASE_TRANSACTION_ROOT);
        mFirebaseStatsticsDatabase = mFirebaseInstance.getReference(Statistics.FIREBASE_STATISTICS_ROOT);

        setBoard();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
        gameCount(false,false);
    }

    // Set up the game board.
    private void setBoard() {

                ai = new AI();
                b = new Button[4][4];
                c = new int[4][4];

                txtMessage = (TextView) findViewById(R.id.dialogue);

                b[1][3] = (Button) findViewById(R.id.one);
                b[1][2] = (Button) findViewById(R.id.two);
                b[1][1] = (Button) findViewById(R.id.three);


                b[2][3] = (Button) findViewById(R.id.four);
                b[2][2] = (Button) findViewById(R.id.five);
                b[2][1] = (Button) findViewById(R.id.six);


                b[3][3] = (Button) findViewById(R.id.seven);
                b[3][2] = (Button) findViewById(R.id.eight);
                b[3][1] = (Button) findViewById(R.id.nine);

                for (i = 1; i <= 3; i++) {
                    for (j = 1; j <= 3; j++)
                        c[i][j] = 2;
                }


                txtMessage.setText("Click a button to start.");


                // add the click listeners for each button
                for (i = 1; i <= 3; i++) {
                    for (j = 1; j <= 3; j++) {
                        b[i][j].setOnClickListener(new MyClickListener(i, j));
                        if (!b[i][j].isEnabled()) {
                            b[i][j].setBackgroundResource(android.R.drawable.btn_default);
                            b[i][j].setText(" ");
                            b[i][j].setEnabled(true);
                        }
                    }
                }

        int count = PrefUtils.getIntFromPrefs(this,Constants.TTT_COUNT,0);
        if(count >2){
            enableGame(false);
            txtMessage.setText(getString(R.string.played_three_times));
        }
    }


    class MyClickListener implements View.OnClickListener {
        int x;
        int y;


        public MyClickListener(int x, int y) {
            this.x = x;
            this.y = y;
        }


        public void onClick(View view) {
            if (b[x][y].isEnabled()) {
                b[x][y].setEnabled(false);
                //b[x][y].setText("O");
                Log.d("TAG","Played O");
                b[x][y].setBackgroundResource(R.drawable.o);
                c[x][y] = 0;
                txtMessage.setText("You Turn");
                if (!checkBoard()) {
                    ai.takeTurn();
                }
            }
        }
    }


    private class AI {
        public void takeTurn() {
            if(c[1][1]==2 &&
                    ((c[1][2]==0 && c[1][3]==0) ||
                            (c[2][2]==0 && c[3][3]==0) ||
                            (c[2][1]==0 && c[3][1]==0))) {
                markSquare(1,1);
            } else if (c[1][2]==2 &&
                    ((c[2][2]==0 && c[3][2]==0) ||
                            (c[1][1]==0 && c[1][3]==0))) {
                markSquare(1,2);
            } else if(c[1][3]==2 &&
                    ((c[1][1]==0 && c[1][2]==0) ||
                            (c[3][1]==0 && c[2][2]==0) ||
                            (c[2][3]==0 && c[3][3]==0))) {
                markSquare(1,3);
            } else if(c[2][1]==2 &&
                    ((c[2][2]==0 && c[2][3]==0) ||
                            (c[1][1]==0 && c[3][1]==0))){
                markSquare(2,1);
            } else if(c[2][2]==2 &&
                    ((c[1][1]==0 && c[3][3]==0) ||
                            (c[1][2]==0 && c[3][2]==0) ||
                            (c[3][1]==0 && c[1][3]==0) ||
                            (c[2][1]==0 && c[2][3]==0))) {
                markSquare(2,2);
            } else if(c[2][3]==2 &&
                    ((c[2][1]==0 && c[2][2]==0) ||
                            (c[1][3]==0 && c[3][3]==0))) {
                markSquare(2,3);
            } else if(c[3][1]==2 &&
                    ((c[1][1]==0 && c[2][1]==0) ||
                            (c[3][2]==0 && c[3][3]==0) ||
                            (c[2][2]==0 && c[1][3]==0))){
                markSquare(3,1);
            } else if(c[3][2]==2 &&
                    ((c[1][2]==0 && c[2][2]==0) ||
                            (c[3][1]==0 && c[3][3]==0))) {
                markSquare(3,2);
            }else if( c[3][3]==2 &&
                    ((c[1][1]==0 && c[2][2]==0) ||
                            (c[1][3]==0 && c[2][3]==0) ||
                            (c[3][1]==0 && c[3][2]==0))) {
                markSquare(3,3);
            } else {
                Random rand = new Random();

                int a = rand.nextInt(4);
                int b = rand.nextInt(4);
                while(a==0 || b==0 || c[a][b]!=2) {
                    a = rand.nextInt(4);
                    b = rand.nextInt(4);
                }
                markSquare(a,b);
            }
        }


        private void markSquare(int x, int y) {
            b[x][y].setEnabled(false);
            //    b[x][y].setText("X");
            b[x][y].setBackgroundResource(R.drawable.x);
            Log.d("TAG","Played X");
            c[x][y] = 1;
            checkBoard();
        }
    }

    // check the board to see if someone has won
    private boolean checkBoard() {
        boolean gameOver = false;
        if ((c[1][1] == 0 && c[2][2] == 0 && c[3][3] == 0)
                || (c[1][3] == 0 && c[2][2] == 0 && c[3][1] == 0)
                || (c[1][2] == 0 && c[2][2] == 0 && c[3][2] == 0)
                || (c[1][3] == 0 && c[2][3] == 0 && c[3][3] == 0)
                || (c[1][1] == 0 && c[1][2] == 0 && c[1][3] == 0)
                || (c[2][1] == 0 && c[2][2] == 0 && c[2][3] == 0)
                || (c[3][1] == 0 && c[3][2] == 0 && c[3][3] == 0)
                || (c[1][1] == 0 && c[2][1] == 0 && c[3][1] == 0)) {
         //   txtMessage.setText("Game over. You win!");
        //    showRewardsDialog("Congrats","You got 10 coins", SweetAlertDialog.NORMAL_TYPE);
           // rewardsPoints("Tic Tac Toe","Game");
            gameCount(true,true);
            gameOver = true;
        } else if ((c[1][1] == 1 && c[2][2] == 1 && c[3][3] == 1)
                || (c[1][3] == 1 && c[2][2] == 1 && c[3][1] == 1)
                || (c[1][2] == 1 && c[2][2] == 1 && c[3][2] == 1)
                || (c[1][3] == 1 && c[2][3] == 1 && c[3][3] == 1)
                || (c[1][1] == 1 && c[1][2] == 1 && c[1][3] == 1)
                || (c[2][1] == 1 && c[2][2] == 1 && c[2][3] == 1)
                || (c[3][1] == 1 && c[3][2] == 1 && c[3][3] == 1)
                || (c[1][1] == 1 && c[2][1] == 1 && c[3][1] == 1)) {
          //  txtMessage.setText("Game over. You lost!");
            gameCount(true,false);
            showRewardsDialog("Oh No!!","You lost. Try again later", SweetAlertDialog.ERROR_TYPE);
            gameOver = true;
        } else {
            boolean empty = false;
            for(i=1; i<=3; i++) {
                for(j=1; j<=3; j++) {
                    if(c[i][j]==2) {
                        empty = true;
                        break;
                    }
                }
            }
            if(!empty) {
                gameOver = true;
                gameCount(true,false);
                txtMessage.setText("Game over. It's a draw!");
                showRewardsDialog("It's a TIE","TIE... Try again later", SweetAlertDialog.ERROR_TYPE);
            }
        }
        return gameOver;
    }

    private void showRewardsDialog(String title, String message ,int type){
        new SweetAlertDialog(this, type)
                .setTitleText(title)
                .setCustomImage(R.mipmap.ic_launcher)
                .setContentText(message)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        setBoard();
                        sweetAlertDialog.dismissWithAnimation();
                    }
                })
                .show();

    }

    private void rewardsPoints(final String source, final String type) {
        final String userId = Utils.getUserId(this);
        mFirebaseUserDatabase.child(userId).child("points").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    long totalPoints = (long) dataSnapshot.getValue();
                    mFirebaseUserDatabase.child(userId).child("points").setValue(totalPoints + 10);
                    UserTransaction ut = new UserTransaction();
                    ut.setSource(source);
                    ut.setPoints(10);
                    ut.setType(type);
                    mFirebaseTransactionDatabase.child(userId).push().setValue(ut.toMap());
                    showRewardsDialog("Congrats","You got 10 coins", SweetAlertDialog.NORMAL_TYPE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void gameCount(final boolean updateCount,final boolean recordTxn){
        final int localCount = PrefUtils.getIntFromPrefs(this, Constants.TTT_COUNT,0);
        txtMessage.setText("You have "+(3 - localCount)+" tictactoe games");
        if(localCount>2) {
            txtMessage.setText(getString(R.string.played_three_times));
            enableGame(false);
            return;
        }

        final String userId = PrefUtils.getStringFromPrefs(this, Constants.USER_ID, "unknownuser");
        mFirebaseStatsticsDatabase.child(userId).child(Statistics.FIREBASE_STATISTICS_CHILD_TICTACTOE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    long totalPoints = (long) dataSnapshot.getValue();
                    gameCount = (int) totalPoints;
                    if(gameCount >2 || localCount > 2) {
                        txtMessage.setText(getString(R.string.played_three_times));
                        enableGame(false);
                    } else {
                        enableGame(true);
                    }
                    if(updateCount) {
                        mFirebaseStatsticsDatabase.child(userId).child(Statistics.FIREBASE_STATISTICS_CHILD_TICTACTOE).setValue(gameCount+1);
                        PrefUtils.saveIntToPrefs(TicTacToeActivity.this, Constants.TTT_COUNT, localCount +1);
                        txtMessage.setText("You have "+(3 - localCount - 1)+" games left");
                        if(localCount+1 >2){
                            txtMessage.setText(getString(R.string.played_three_times));
                            enableGame(false);
                        }
                    }
                    if(recordTxn){
                          rewardsPoints("Tic Tac Toe","Game");
                   //     FirebaseDatabaseUtil.rewardsPoints(TicTacToeActivity.this,10,"Tic Tac Toe","Game");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void enableGame(boolean status) {
        for (i = 1; i <= 3; i++) {
            for (j = 1; j <= 3; j++) {
                b[i][j].setBackgroundResource(android.R.drawable.btn_default);
                b[i][j].setEnabled(status);
            }
        }
    }

}
