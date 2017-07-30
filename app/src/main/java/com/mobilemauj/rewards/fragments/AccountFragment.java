package com.mobilemauj.rewards.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.login.LoginManager;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.AppInviteDialog;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.mobilemauj.rewards.LoginActivity;
import com.mobilemauj.rewards.MyRewardsActivity;
import com.mobilemauj.rewards.R;
import com.mobilemauj.rewards.utility.Constants;
import com.mobilemauj.rewards.utility.PrefUtils;
import com.mobilemauj.rewards.utility.Utils;
import com.squareup.picasso.Picasso;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class AccountFragment extends Fragment {


    private RelativeLayout rlEmailVerification;
    private CircularImageView imgProfile;
    private TextView txtUserName;
    private TextView txtUserEmail;

    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btnLogout;
        Button btnShare;
        Button btnMyRewards;
        TextView txtSendVerificationMail;
        btnLogout = (Button)view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(getString(R.string.sign_out))
                        .setCustomImage(R.mipmap.ic_launcher)
                        .setConfirmText(getString(R.string.sign_out))
                        .setCancelText(getString(R.string.cancel))
                        .showCancelButton(true)
                        .setContentText("Are you sure you want to signout?")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                PrefUtils.removeFromPrefs(getActivity(), Constants.REFERRER_ID);
                                PrefUtils.removeFromPrefs(getActivity(), Constants.REFERRAL_LINK);
                                PrefUtils.removeFromPrefs(getActivity(), Constants.USER_ID);
                                FirebaseAuth.getInstance().signOut();
                                LoginManager.getInstance().logOut();
                                startActivity(new Intent(getActivity(),LoginActivity.class));
                                getActivity().finish();

                            }
                        })
                        .show();
            }
        });

        btnShare = (Button) view.findViewById(R.id.btnFbpost);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  share();
               // startActivity(new Intent(getActivity(),FBWallPostActivity.class));

            }
        });

        btnMyRewards = (Button) view.findViewById(R.id.btnMyRewards);
        btnMyRewards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),MyRewardsActivity.class));
            }
        });

        imgProfile = (CircularImageView) view.findViewById(R.id.img_profile);
        txtUserName = (TextView) view.findViewById(R.id.txtUserName);
        txtUserEmail= (TextView) view.findViewById(R.id.txtUserEmail);
        txtSendVerificationMail = (TextView) view.findViewById(R.id.txtSendVerificationMail);
        rlEmailVerification = (RelativeLayout) view.findViewById(R.id.rl_email_verification);
        txtSendVerificationMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(getActivity(),"Send Verification Email",Toast.LENGTH_LONG).show();
                sendEmailVerification();
            }
        });
        updateUI();

    }


    void share(){
        Intent shareIntent;

        shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,"My Rewards app");
        shareIntent.putExtra("referrer", Utils.getUserId(getActivity()));
        shareIntent.putExtra(Intent.EXTRA_TEXT,"Hey get mobile recharge and gift cards " + "https://play.google.com/store/apps/details?id=com.bpk.rewards&referrer="+ Utils.getUserId(getActivity()));
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent,"Share with"));

    }
    void shareOnFacebook(){
        ShareDialog shareDialog;
        shareDialog = new ShareDialog(getActivity());
        String referrer = Utils.encryptData(Utils.getUserId(getActivity()));
        String url ="https://play.google.com/store/apps/details?id=com.bpk.rewards&referrer="+referrer;
        //String url = "https://play.google.com/apps/testing/com.bpk.rewards&referrer="+Utils.getUserId(getActivity());
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle("My Rewards")
                .setContentDescription(
                        "\"Some more descriptve text\"")
                .setQuote("Get mobile recharge & gift cards for using this app!!")
                .setContentUrl(Uri.parse(url))
                .build();

        shareDialog.show(linkContent);
    }

    private void updateUI(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null && !currentUser.isEmailVerified()) {
            rlEmailVerification.setVisibility(View.VISIBLE);
        }
        else {
            rlEmailVerification.setVisibility(View.GONE);
        }
        if(currentUser != null) {
            txtUserEmail.setText(currentUser.getEmail());
            txtUserName.setText(currentUser.getDisplayName());

            if (currentUser.getPhotoUrl() != null)
                Picasso.with(getActivity()).load(currentUser.getPhotoUrl()).placeholder(R.drawable.user_icon).into(imgProfile);
        }
    }

    private void sendEmailVerification() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(),
                                        "Verification email sent to " + user.getEmail(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("TAG", "sendEmailVerification", task.getException());
                                Toast.makeText(getActivity(),
                                        "Failed to send verification email.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void shareonFB(final Context mContext) {
        final String one = "Invite Friends";
        final String two = "Post On Your Wall";
        final CharSequence[] options = {one, two};
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Share On FaceBook");

        builder.setSingleChoiceItems(options, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (one.equals(options[which])) {
                            //All
                            try {
                                dialog.cancel();
                                String appLinkUrl, previewImageUrl;
//
                                appLinkUrl = "https://fb.me/509886189351873&referrer=testuser";
                                previewImageUrl = "http://mobilemauj.com/rewards/images/myrewards.png";

                                if (AppInviteDialog.canShow()) {
                                    AppInviteContent content = null;

                                    content = new AppInviteContent.Builder()
                                            .setApplinkUrl(appLinkUrl)
                                            .setPreviewImageUrl(previewImageUrl)
                                            .build();
                                    AppInviteDialog.show((Activity) mContext, content);

                                }
                            } catch (Exception e) {
                                e.printStackTrace();

                            }
                        } else if (two.equals(options[which])) {
                            // Post on Wall
                            try {
                                dialog.cancel();

                                ShareDialog shareDialog = new ShareDialog(getActivity());

                                if (ShareDialog.canShow(ShareLinkContent.class)) {
                                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                            .setQuote("http://www.mobilemauj.com/")
                                            .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.mobilemauj.mangela"))
                                            .build();
                                    shareDialog.show(linkContent);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();

                            }
                        }
                    }
                }

        );

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }

        );
        AlertDialog dlg = builder.create();
        dlg.show();

    }

}