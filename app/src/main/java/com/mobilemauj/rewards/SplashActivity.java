package com.mobilemauj.rewards;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobilemauj.rewards.interfaces.ServerTimeAsyncResponse;
import com.mobilemauj.rewards.utility.Constants;
import com.mobilemauj.rewards.utility.LogUtil;
import com.mobilemauj.rewards.utility.PrefUtils;
import com.mobilemauj.rewards.utility.Utils;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SplashActivity extends Activity implements ServerTimeAsyncResponse {
    private String country;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        country = PrefUtils.getStringFromPrefs(this, Constants.USER_COUNTRY,"");
        if(country.length()==0){
            country = Utils.getCountryCode(this);
            PrefUtils.saveStringToPrefs(this,Constants.USER_COUNTRY,country);
        }
        mAuth = FirebaseAuth.getInstance();
        if(Utils.isNetworkAvailable(this)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    GetServerFromTime task = new GetServerFromTime(SplashActivity.this);
                    task.execute();
                }
            }, 1500);
        } else {
            Toast.makeText(this, "No Internet",Toast.LENGTH_LONG).show();
        }

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

    @Override
    public void processFinish(String result) {
        try {
            LogUtil.d("save time "+result);
            PrefUtils.saveLongToPrefs(this, Constants.SERVER_TIME, Long.parseLong(result));
            gotoMain();
        } catch (Exception e){
            Toast.makeText(this,"Server Error. Try Again",Toast.LENGTH_LONG).show();
        }

    }
}


class GetServerFromTime extends AsyncTask<String, Void, String> {

    private ServerTimeAsyncResponse delegate = null;//Call back interface

    public GetServerFromTime(ServerTimeAsyncResponse asyncResponse) {
        delegate = asyncResponse;//Assigning call back interfacethrough constructor
    }

    @Override
    protected String doInBackground(String... urls) {
        // we use the OkHttp library from https://github.com/square/okhttp
        OkHttpClient client = new OkHttpClient();
        Request request =
                new Request.Builder()
                        .url("http://mobilemauj.com/mangela/servertime.php")
                        .build();
        Response response;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }
}

