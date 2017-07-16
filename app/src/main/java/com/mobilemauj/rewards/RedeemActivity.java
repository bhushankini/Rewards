package com.mobilemauj.rewards;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobilemauj.rewards.model.Redeem;
import com.mobilemauj.rewards.model.Rewards;
import com.mobilemauj.rewards.model.User;
import com.mobilemauj.rewards.model.UserTransaction;
import com.mobilemauj.rewards.utility.Constants;
import com.mobilemauj.rewards.utility.PrefUtils;
import com.mobilemauj.rewards.utility.Utils;
import com.squareup.picasso.Picasso;

public class RedeemActivity extends BaseActivity {

    private Button btnRedeem;
    private TextView txtGiftCard;
    private TextView txtValue;
    private TextView txtWorth;
    private TextView txtRecipient;
    private TextView txtRegion;
    private TextView txtDescription;
    private ImageView icon;
    private LinearLayout llCircle;
    private EditText etRecipient;
    private ImageView imgBigIcon;
    private Spinner spinnerCircle;
    private DatabaseReference mFirebaseRedeemDatabase;
    private DatabaseReference mFirebaseUserDatabase;
    private DatabaseReference mFirebaseTransactionDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private RelativeLayout rlCard;
    private Rewards rewards;
    private int pointsDiff = 1;
    private String circle = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);
        rlCard = (RelativeLayout) findViewById(R.id.rl_giftcard);
        icon = (ImageView) findViewById(R.id.img_icon);
        txtGiftCard = (TextView) findViewById(R.id.txt_giftcard);
        txtValue = (TextView) findViewById(R.id.txt_value);
        txtWorth = (TextView) findViewById(R.id.txt_worth);
        txtRecipient = (TextView) findViewById(R.id.txt_recipient);
        btnRedeem = (Button) findViewById(R.id.btn_redeem);
        llCircle = (LinearLayout) findViewById(R.id.llCircle);
        etRecipient = (EditText) findViewById(R.id.etRecipient);
        imgBigIcon = (ImageView) findViewById(R.id.imgBigIcon);
        spinnerCircle = (Spinner) findViewById(R.id.spinnerCircle);
        txtDescription = (TextView)findViewById(R.id.txtDescription);
        txtRegion = (TextView) findViewById(R.id.txtRegion);
        circle = spinnerCircle.getSelectedItem().toString();

        rewards = (Rewards) getIntent().getSerializableExtra(Rewards.REWARD_EXTRAS);
        GradientDrawable myGrad = (GradientDrawable) rlCard.getBackground();
        myGrad.setColor( Color.parseColor(rewards.getBgcolor()));
        if(rewards.getIcon()>100){
            txtDescription.setText(getString(R.string.disclaimer_mobile));
            llCircle.setVisibility(View.VISIBLE);
            etRecipient.setHint(getString(R.string.mobile_hint));
            etRecipient.setInputType(InputType.TYPE_CLASS_PHONE);
            int maxLength = 10;
            etRecipient.setFilters(new InputFilter[] {
                    new InputFilter.LengthFilter(maxLength)
            });

            txtRegion.setText("("+circle+")");
        }
        else {
            txtDescription.setText(getString(R.string.disclaimer_giftcard));
            llCircle.setVisibility(View.GONE);
            etRecipient.setHint(getString(R.string.email_hint));
            etRecipient.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }

        String textColor = rewards.getTextcolor();
        txtGiftCard.setTextColor(Color.parseColor(textColor));
        txtValue.setTextColor(Color.parseColor(textColor));
        txtWorth.setTextColor(Color.parseColor(textColor));
        txtRecipient.setTextColor(Color.parseColor(textColor));
        txtRegion.setTextColor(Color.parseColor(textColor));
        txtWorth.setText(rewards.getWorth());
        txtValue.setText(""+rewards.getValue()+" points");
        txtGiftCard.setText(rewards.getBrand());
        txtRecipient.setText(getString(R.string.recipient));
        String bigUrl = Rewards.IMAGES_BASE_URL+rewards.getBrand().toLowerCase()+"_big.png";
        String smallUrl = Rewards.IMAGES_BASE_URL+rewards.getBrand().toLowerCase()+"_small.png";
        Picasso.with(this).load(bigUrl).into(imgBigIcon);
        Picasso.with(this).load(smallUrl).into(icon);
        String to = String.format(getString(R.string.recipient), PrefUtils.getStringFromPrefs(this, Constants.USER_NAME,""));
        txtRecipient.setText(to);


        btnRedeem.setOnClickListener(new View.OnClickListener() {
            String userId = Utils.getUserId(RedeemActivity.this);
            @Override
            public void onClick(View view) {
                if (!validate()) {
                    return;
                } else if(!Utils.isNetworkAvailable(RedeemActivity.this)) {
                    Toast.makeText(RedeemActivity.this,getString(R.string.no_network),Toast.LENGTH_LONG).show();
                    return;
                }

                showProgressDialog();
                mFirebaseInstance = FirebaseDatabase.getInstance();
                mFirebaseRedeemDatabase = mFirebaseInstance.getReference(Rewards.FIREBASE_REDEEM_ROOT);
                mFirebaseUserDatabase = mFirebaseInstance.getReference(User.FIREBASE_USER_ROOT);
                mFirebaseTransactionDatabase = mFirebaseInstance.getReference(UserTransaction.FIREBASE_TRANSACTION_ROOT);
                mFirebaseUserDatabase.child(userId).child("points").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            long totalPoints = (long) dataSnapshot.getValue();

                            if (rewards.getValue() <= totalPoints) {
                                btnRedeem.setEnabled(true);
                                btnRedeem.setText(getString(R.string.redeem));
                                mFirebaseUserDatabase.child(userId).child("points").setValue(totalPoints - rewards.getValue());
                                UserTransaction ut = new UserTransaction();
                                ut.setSource("Redeem");
                                ut.setPoints(-rewards.getValue());
                                ut.setType(rewards.getDisplay());
                                mFirebaseTransactionDatabase.child(userId).push().setValue(ut.toMap());

                                Redeem redeem = new Redeem();
                                redeem.setBrand(rewards.getBrand());
                                redeem.setValue(rewards.getValue());
                                if(rewards.getType() == 1)
                                    redeem.setDisplay(rewards.getDisplay()+" "+circle);
                                else
                                    redeem.setDisplay(rewards.getDisplay());
                                redeem.setRecipient(etRecipient.getText().toString().trim());
                                mFirebaseRedeemDatabase.child(userId).push().setValue(redeem.toMap());
                                Toast.makeText(RedeemActivity.this, "Your request is submitted", Toast.LENGTH_LONG).show();
                                hideProgressDialog();
                                finish();
                            } else {
                                pointsDiff = (int) (rewards.getValue() - totalPoints);
                                btnRedeem.setEnabled(false);
                                btnRedeem.setText("You need " + pointsDiff + " to claim reward");
                                Toast.makeText(RedeemActivity.this, "Your dont have enough credits", Toast.LENGTH_LONG).show();
                            }
                            hideProgressDialog();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        spinnerCircle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int pos, long id) {
                circle = parent.getItemAtPosition(pos).toString();
                txtRegion.setText("("+circle+")");
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });


        etRecipient.addTextChangedListener(new TextWatcher() {
            // the user's changes are saved here
            public void onTextChanged(CharSequence c, int start, int before, int count) {

                String to = String.format(getString(R.string.recipient),c.toString());
                txtRecipient.setText(to);
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // this space intentionally left blank
            }

            public void afterTextChanged(Editable c) {
                // this one too
            }
        });
    }

    //START

    private void hasEnoughCredits() {
        String userId = Utils.getUserId(RedeemActivity.this);
        mFirebaseUserDatabase = FirebaseDatabase.getInstance().getReference(User.FIREBASE_USER_ROOT);

        mFirebaseUserDatabase.child(userId).child("points").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    long totalPoints = (long) dataSnapshot.getValue();
                    if (rewards.getValue() <= totalPoints) {
                        btnRedeem.setEnabled(true);
                        btnRedeem.setText(getString(R.string.redeem));
                    } else {
                        pointsDiff = (int) (rewards.getValue() - totalPoints);
                        btnRedeem.setEnabled(false);
                        btnRedeem.setText("You need " + pointsDiff + " to claim reward");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //END

    private boolean validate() {
        boolean valid = true;

        String recipient = etRecipient.getText().toString();

        if(rewards.getIcon() < 100) {
        //Email
            if (recipient.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(recipient).matches()) {
                etRecipient.setError("enter a valid email address");
                valid = false;
            } else {
                etRecipient.setError(null);
            }
        } else {
            //Phone
            String regex = "\\d+";
            if (recipient.isEmpty() || recipient.length() < 10 || !recipient.matches(regex)) {
                etRecipient.setError("Enter valid phone number");
                valid = false;
            } else {
                etRecipient.setError(null);
            }
        }
        return valid;
    }

    @Override
    protected void onResume() {
        super.onResume();
        hasEnoughCredits();
    }
}