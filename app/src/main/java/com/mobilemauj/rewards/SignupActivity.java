package com.mobilemauj.rewards;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobilemauj.rewards.model.User;
import com.mobilemauj.rewards.model.UserTransaction;
import com.mobilemauj.rewards.utility.Constants;
import com.mobilemauj.rewards.utility.PrefUtils;
import com.mobilemauj.rewards.utility.Utils;

public class SignupActivity extends BaseActivity {

    private static final String TAG = "SignUpActivity";
    private Button btnSignUp;
    private TextView txtLogin;
    private TextView txtName;
    private TextView txtEmail;
    private TextView txtPassword;
    private TextView txtRePassword;
    private FirebaseAuth mAuth;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mRefererFirebaseDatabase;
    private DatabaseReference mFirebaseTransactionDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        btnSignUp = (Button) findViewById(R.id.signup_button);
        txtLogin = (TextView) findViewById(R.id.link_login);
        txtName = (TextView) findViewById(R.id.input_name);
        txtEmail = (TextView) findViewById(R.id.input_email);

        txtPassword = (TextView) findViewById(R.id.input_password);
        txtRePassword = (TextView) findViewById(R.id.input_reEnterPassword);

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validate()) {
                    return;
                }
                createAccount(txtEmail.getText().toString().trim(), txtPassword.getText().toString());
            }
        });
    }


    private boolean validate() {
        boolean valid = true;

        String name = txtName.getText().toString();
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();
        String reEnterPassword = txtRePassword.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            txtName.setError("at least 3 characters");
            valid = false;
        } else {
            txtName.setError(null);
        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtEmail.setError("enter a valid email address");
            valid = false;
        } else {
            txtEmail.setError(null);
        }


        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            txtPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            txtPassword.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            txtRePassword.setError("Password Do not match");
            valid = false;
        } else {
            txtRePassword.setError(null);
        }

        return valid;
    }


    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        showProgressDialog();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference(User.FIREBASE_USER_ROOT);
        mAuth = FirebaseAuth.getInstance();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            sendEmailVerification();
                            PrefUtils.saveStringToPrefs(SignupActivity.this, Constants.USER_ID, task.getResult().getUser().getUid());
                            //
                            addUserDetails();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:exception " + task.getException());

                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(SignupActivity.this,
                                        "User with this email already exist.", Toast.LENGTH_SHORT).show();
                                Log.w(TAG, "User with this email already exist");

                            }
                            updateUI();
                        }
                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private void rewardsReferralPoints(final String referrer) {
        if(referrer.length() > 0) {
            mFirebaseInstance = FirebaseDatabase.getInstance();
            mRefererFirebaseDatabase = mFirebaseInstance.getReference(User.FIREBASE_USER_ROOT);
            mFirebaseTransactionDatabase = mFirebaseInstance.getReference(UserTransaction.FIREBASE_TRANSACTION_ROOT);
            FirebaseDatabase.getInstance().getReference(User.FIREBASE_USER_ROOT).child(referrer).child("points").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        long totalPoints = (long) dataSnapshot.getValue();
                        mRefererFirebaseDatabase.child(referrer).child("points").setValue(totalPoints + Constants.REFERAL_POINTS);
                        UserTransaction ut = new UserTransaction();
                        ut.setSource("Referal");
                        ut.setPoints(Constants.REFERAL_POINTS);
                        ut.setType("Bonus");
                        mFirebaseTransactionDatabase.child(referrer).push().setValue(ut.toMap());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        finish();
        startActivity(new Intent(SignupActivity.this, MainActivity.class));

    }

    private void addUserDetails() {

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(txtName.getText().toString())
                .build();
        if(user !=null) {
            user.updateProfile(profileUpdates)
             .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User profile updated.");
                        updateUI();
                    }
                }
            });
        }
    }

    private void sendEmailVerification() {
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(SignupActivity.this,
                                        "Verification email sent to " + user.getEmail(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignupActivity.this,
                                        "Failed to send verification email.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void updateUI() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        hideProgressDialog();
        if (user != null) {
            PrefUtils.saveStringToPrefs(SignupActivity.this, Constants.USER_ID, user.getUid());
            User u = new User();
            u.setUserId(user.getUid());
            u.setEmail(user.getEmail());
            u.setName(txtName.getText().toString());
            u.setPoints(2 * Constants.REFERAL_POINTS);
            String referid = PrefUtils.getStringFromPrefs(SignupActivity.this, Constants.REFERRER_ID, "");
            u.setReferalId(referid);
            newUser(u);

        } else {
            PrefUtils.saveStringToPrefs(SignupActivity.this, Constants.USER_ID, null);
            Log.d(TAG, "Error in Login");
        }
    }

    private void newUser(final User user) {

        mFirebaseDatabase.child(user.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "KHUSHI111 snapshot " + dataSnapshot.getValue());
                if (dataSnapshot.getValue() != null) {
                    Log.d(TAG, "KHUSHI111 USER Exists");

                } else {
                    Log.d(TAG, "KHUSHI111 new USER create reward");

                    mFirebaseDatabase.child(user.getUserId()).setValue(user);
                    Log.e("TAG","Give welcom bonus txn");
                    UserTransaction userTxn = new UserTransaction();
                    userTxn.setSource("Welcome");
                    userTxn.setPoints(2*Constants.REFERAL_POINTS);
                    userTxn.setType("Bonus");

                    mFirebaseTransactionDatabase = mFirebaseInstance.getReference(UserTransaction.FIREBASE_TRANSACTION_ROOT);
                    mFirebaseTransactionDatabase.child(Utils.getUserId(SignupActivity.this)).push().setValue(userTxn.toMap());
                    rewardsReferralPoints(user.getReferalId());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}