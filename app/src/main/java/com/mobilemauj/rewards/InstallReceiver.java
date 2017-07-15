package com.mobilemauj.rewards;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.mobilemauj.rewards.utility.Constants;
import com.mobilemauj.rewards.utility.PrefUtils;
import com.mobilemauj.rewards.utility.Utils;

public class InstallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if(action != null && action.equals("com.android.vending.INSTALL_REFERRER")){
            try {
                final String referrer = intent.getStringExtra("referrer");
                Log.e("InstallReceiver", "referrer == "+referrer);
                String data = Utils.decryptData(referrer);
                PrefUtils.saveStringToPrefs(context, Constants.REFERRER_ID, data);
            } catch (Exception e) {
                Log.e("InstallReceiver", "Error "+e);
            }
        }
    }
}
//am broadcast -a com.android.vending.INSTALL_REFERRER -n com.mobilemauj.rewards/.InstallReceiver --es "referrer" "mi[OKRvQr8buJjIhHob5uGu7RRC4"
//https://play.google.com/store/apps/details?id=com.mobilemauj.rewardss&referrer=ABCDEFGHIJKLMNO
//PT    IfXtTDhgWqDIyRSrK2Rx2S7773
//pAsMEu9uKDeuIJuPPqweNXmJb3Z2

//  keytool -exportcert -alias release -keystore keystore.jks
//keytool -exportcert -alias release -keystore keystore.jks | openssl sha1 -binary | openssl base64