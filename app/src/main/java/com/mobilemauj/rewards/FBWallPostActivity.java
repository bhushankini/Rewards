package com.mobilemauj.rewards;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.mobilemauj.rewards.utility.LogUtil;


public class FBWallPostActivity extends BaseActivity  {

    CallbackManager callbackManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fbwallpost);

        Button btnPost =(Button) findViewById(R.id.btnPost);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShareDialog shareDialog = new ShareDialog(FBWallPostActivity.this);
                callbackManager = CallbackManager.Factory.create();
                shareDialog.registerCallback(callbackManager, new

                        FacebookCallback<Sharer.Result>() {
                            @Override
                            public void onSuccess(Sharer.Result result) {

                                LogUtil.d( "onSuccess: "+ result.toString());
                                LogUtil.d(  "onSuccess: ");
                                Toast.makeText(FBWallPostActivity.this, "onSuccess", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancel() {
                                LogUtil.d( "onCancel: ");
                                Toast.makeText(FBWallPostActivity.this, "onCancel", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(FacebookException error) {
                                LogUtil.d( "onError: ");
                                Toast.makeText(FBWallPostActivity.this, "onError" + error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });

                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setQuote("Earn mobile recharge and gift cards")
                            .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.mobilemauj.mangela&hl=en"))
                            .build();
                    shareDialog.show(linkContent);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);


    }
}
