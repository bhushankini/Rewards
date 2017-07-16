package com.mobilemauj.rewards;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mRefererFirebaseDatabase;
    private DatabaseReference mFirebaseTransactionDatabase;
    private CallbackManager mCallbackManager;
    private EditText mEmailField;
    private EditText mPasswordField;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        mEmailField = (EditText) findViewById(R.id.email_edittext);
        mPasswordField = (EditText) findViewById(R.id.password_edittext);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference(User.FIREBASE_USER_ROOT);

        // Buttons
        findViewById(R.id.signin_button).setOnClickListener(this);
        //   findViewById(R.id.signup_button).setOnClickListener(this);
        //   findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.txtForgotPassword).setOnClickListener(this);
        findViewById(R.id.txtCreateAccount).setOnClickListener(this);
        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
// [START initialize_fblogin]
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.button_facebook_login);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        });
        // [END initialize_fblogin]

    }
    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Log.d(TAG,"USER PROVIDER "+ currentUser.isEmailVerified());
        updateUI(currentUser);
    }
    // [END on_start_check_user]


    // [START on_activity_result]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
    // [END on_activity_result]

    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_facebook]


    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user!=null /*&& user.isEmailVerified()*/) {
                                updateUI(user);
                            }
                            else {
                                Toast.makeText(LoginActivity.this,"Check your mail and verify your email",Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }


    private void updateUI(FirebaseUser user) {

        hideProgressDialog();
        if (user != null) {
            Log.d(TAG, "Login Success " + user.getDisplayName());
            PrefUtils.saveStringToPrefs(LoginActivity.this, Constants.USER_ID, user.getUid());
            User u = new User();
            u.setUserId(user.getUid());
            u.setEmail(user.getEmail());
            u.setName(user.getDisplayName());
            if(user.getPhotoUrl()!=null)
                u.setPhotoUrl(user.getPhotoUrl().toString());
            u.setCountry(PrefUtils.getStringFromPrefs(this,Constants.USER_COUNTRY,""));
            newUser(u);
            user.getDisplayName();
            Log.d(TAG, "Login SuccessUser  " + user);


        } else {
            PrefUtils.saveStringToPrefs(LoginActivity.this, Constants.USER_ID, null);
            Log.d(TAG, "Error in Login");
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.txtCreateAccount) {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        } else if (i == R.id.signin_button) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        }  else if (i == R.id.txtForgotPassword) {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        }
    }


    private void newUser(final User user) {
        Log.d(TAG, "KHUSHI111 new User ");
        final String referrer = PrefUtils.getStringFromPrefs(LoginActivity.this,Constants.REFERRER_ID,"");
        mFirebaseTransactionDatabase = mFirebaseInstance.getReference(UserTransaction.FIREBASE_TRANSACTION_ROOT);
        mFirebaseDatabase.child(user.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "KHUSHI111 snapshot " + dataSnapshot.getValue());
                if (dataSnapshot.getValue() != null) {
                    Log.d(TAG, "KHUSHI111 USER Exists ");
                    //       mFirebaseDatabase.child(user.getUserId()).setValue(user);
                    gotoMain();

                } else {

                    Log.d(TAG, "KHUSHI111 new USER create reward referrer is "+referrer);
                    if(referrer.length() > 0) {
                        user.setReferalId(referrer);
                    }
                    user.setPoints(2 * Constants.REFERAL_POINTS);
                    mFirebaseDatabase.child(user.getUserId()).setValue(user);
                    Log.e("TAG","Give welcom bonus txn");
                    UserTransaction userTxn = new UserTransaction();
                    userTxn.setSource("Welcome");
                    userTxn.setPoints(2*Constants.REFERAL_POINTS);
                    userTxn.setType("Bonus");
                    mFirebaseTransactionDatabase.child(Utils.getUserId(LoginActivity.this)).push().setValue(userTxn.toMap());
                    rewardsReferralPoints(referrer);
                }

                //   finish();
                //   startActivity(new Intent(LoginActivity.this,MainActivity.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void gotoMain(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!= null ){

            startActivity(new Intent(this, MainActivity.class));
        }
        else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
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
        gotoMain();
    }
}