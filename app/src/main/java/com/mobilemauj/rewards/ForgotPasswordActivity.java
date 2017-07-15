package com.mobilemauj.rewards;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobilemauj.rewards.model.User;

/**
 * Created by bkini on 6/27/17.
 */

public class ForgotPasswordActivity extends BaseActivity {

    private Button btnSubmit;
    private TextView txtEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        btnSubmit =(Button) findViewById(R.id.signup_button);
        txtEmail = (TextView) findViewById(R.id.input_email);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressDialog();
                if (!validate()) {
                    return;
                }
                sendForgotPassword();
            }
        });
    }

    private void sendForgotPassword(){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(User.FIREBASE_USER_ROOT);
        ref.orderByChild("email").equalTo(txtEmail.getText().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() > 0) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(txtEmail.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        String msg = String.format(getString(R.string.email_sent), txtEmail.getText().toString());
                                        Toast.makeText(ForgotPasswordActivity.this, msg, Toast.LENGTH_SHORT).show();
                                        hideProgressDialog();
                                        finish();
                                    }
                                }
                            });
                } else {
                    hideProgressDialog();

                    String msg = String.format(getString(R.string.email_not_registered), txtEmail.getText().toString());
                    txtEmail.setError(msg);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean validate() {
        boolean valid = true;
        String email = txtEmail.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtEmail.setError("enter a valid email address");
            valid = false;
        } else {
            txtEmail.setError(null);
        }
        return valid;
    }
}