package com.mobilemauj.rewards.games.tictactoe;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.mobilemauj.rewards.R;
import com.mobilemauj.rewards.model.Statistics;
import com.mobilemauj.rewards.utility.Constants;
import com.mobilemauj.rewards.utility.FirebaseDatabaseUtil;
import com.mobilemauj.rewards.utility.LogUtil;
import com.mobilemauj.rewards.utility.PrefUtils;

public class NewTicTacToeActivity extends AppCompatActivity {

    // Represents the internal state of the game
    private TicTacToeGame mGame;

    // Buttons making up the board
    private Button mBoardButtons[];
    private Button mNewGame;

    // Various text displayed
    private TextView mInfoTextView;

    // Counters for the wins and ties

    private int gameCount = 0;
    // Bools needed to see if player one goes first
    // if the gameType is to be single or local multiplayer
    // if it is player one's turn
    // and if the game is over
    private boolean mPlayerOneFirst = true;
    private boolean mIsSinglePlayer = false;
    private boolean mIsPlayerOneTurn = true;
    private boolean mGameOver = false;

    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseStatsticsDatabase;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_newtictactoe);


        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.tic_tac_toe));
        }
        // Initialize the buttons
        mBoardButtons = new Button[mGame.getBOARD_SIZE()];
        mBoardButtons[0] = (Button) findViewById(R.id.one);
        mBoardButtons[1] = (Button) findViewById(R.id.two);
        mBoardButtons[2] = (Button) findViewById(R.id.three);
        mBoardButtons[3] = (Button) findViewById(R.id.four);
        mBoardButtons[4] = (Button) findViewById(R.id.five);
        mBoardButtons[5] = (Button) findViewById(R.id.six);
        mBoardButtons[6] = (Button) findViewById(R.id.seven);
        mBoardButtons[7] = (Button) findViewById(R.id.eight);
        mBoardButtons[8] = (Button) findViewById(R.id.nine);
        addListenerOnButton();

        // setup the textviews
        mInfoTextView = (TextView) findViewById(R.id.information);

        // create a new game object
        mGame = new TicTacToeGame();

        // start a new game
      //  startNewGame(mGameType);
        //Firebase
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseStatsticsDatabase = mFirebaseInstance.getReference(Statistics.FIREBASE_STATISTICS_ROOT);

    }


    public void addListenerOnButton() {

        mNewGame = (Button) findViewById(R.id.NewGame);

        mNewGame.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //   enableNewGame(false);
                startNewGame(true);
            }
        });
    }

    // start a new game
    // clears the board and resets all buttons / text
    // sets game over to be false
    private void startNewGame(boolean isSingle) {

        this.mIsSinglePlayer = isSingle;

        mGame.clearBoard();

        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
            mBoardButtons[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.blank));
        }


        if (mIsSinglePlayer) {

            if (mPlayerOneFirst) {
                mInfoTextView.setText(R.string.first_human);
                mPlayerOneFirst = false;
            } else {
                mInfoTextView.setText(R.string.turn_computer);
                int move = mGame.getComputerMove();
                setMove(mGame.PLAYER_TWO, move);
                mPlayerOneFirst = true;
            }
        }

        mGameOver = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameCount(false, false);
    }

    // set move for the current player
    private void setMove(char player, int location) {
        mGame.setMove(player, location);
        mBoardButtons[location].setEnabled(false);
        if (player == mGame.PLAYER_ONE)
            mBoardButtons[location].setBackgroundDrawable(getResources().getDrawable(R.drawable.x));
        else
            mBoardButtons[location].setBackgroundDrawable(getResources().getDrawable(R.drawable.o));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    
    private void gameCount(final boolean updateCount, final boolean recordTxn) {
        final int localCount = PrefUtils.getIntFromPrefs(this, Constants.TTT_COUNT, 0);
        mInfoTextView.setText("You have " + (3 - localCount) + " tictactoe games");
        if (localCount > 2) {
            mInfoTextView.setText(getString(R.string.played_three_times));
            enableNewGame(false);
            return;
        }

        final String userId = PrefUtils.getStringFromPrefs(this, Constants.USER_ID, "unknownuser");
        mFirebaseStatsticsDatabase.child(userId).child(Statistics.FIREBASE_STATISTICS_CHILD_TICTACTOE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    long totalPoints = (long) dataSnapshot.getValue();
                    gameCount = (int) totalPoints;
                    if (gameCount > 2 || localCount > 2) {
                        mInfoTextView.setText(getString(R.string.played_three_times));
                        enableNewGame(false);
                    } else {
                        enableNewGame(true);
                    }
                    if (updateCount) {
                        mFirebaseStatsticsDatabase.child(userId).child(Statistics.FIREBASE_STATISTICS_CHILD_TICTACTOE).setValue(gameCount + 1);
                        mFirebaseStatsticsDatabase.child(userId).child(Statistics.FIREBASE_STATISTICS_CHILD_LAST_PLAYED).setValue(ServerValue.TIMESTAMP);
                        PrefUtils.saveIntToPrefs(NewTicTacToeActivity.this, Constants.TTT_COUNT, localCount + 1);
                        mInfoTextView.setText("You have " + (3 - localCount - 1) + " games left");
                        if (localCount + 1 > 2) {
                            mInfoTextView.setText(getString(R.string.played_three_times));
                            enableNewGame(false);
                        }
                        else {
                            enableNewGame(true);
                        }
                    }
                    if (recordTxn) {
                        //    rewardsPoints("Tic Tac Toe","Game");
                        FirebaseDatabaseUtil.rewardsPoints(NewTicTacToeActivity.this, 10, "Tic Tac Toe", "Game");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void enableNewGame(boolean status) {
        if (status) {
            mNewGame.setEnabled(true);
        } else {
            mNewGame.setEnabled(false);
        }
    }

    private class ButtonClickListener implements View.OnClickListener {
        int location;

        public ButtonClickListener(int location) {
            this.location = location;
        }

        public void onClick(View view) {
            if (!mGameOver) {
                if (mBoardButtons[location].isEnabled()) {
                    if (mIsSinglePlayer) {
                        setMove(mGame.PLAYER_ONE, location);

                        int winner = mGame.checkForWinner();

                        if (winner == 0) {
                            mInfoTextView.setText(R.string.turn_computer);
                            int move = mGame.getComputerMove();
                            setMove(mGame.PLAYER_TWO, move);
                            winner = mGame.checkForWinner();
                        }

                        if (winner == 0)
                            mInfoTextView.setText(R.string.turn_human);
                        else if (winner == 1) {
                            mInfoTextView.setText(R.string.result_tie);
                            mGameOver = true;
                            gameCount(true, false);
                        } else if (winner == 2) {
                            mInfoTextView.setText(R.string.result_human_wins);
                            mGameOver = true;
                            gameCount(true, true);
                        } else {
                            mGameOver = true;
                            gameCount(true, false);
                        }
                    }
                }
            }
        }
    }
}