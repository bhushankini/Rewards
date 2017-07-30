package com.mobilemauj.rewards.utility;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Utils {


    public static String localTime(long utcTime) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy hh:mm a"); //this format changeable
        dateFormatter.setTimeZone(TimeZone.getDefault());
        return dateFormatter.format(utcTime);
    }

    public static String localDate(long utcTime) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy"); //this format changeable
        dateFormatter.setTimeZone(TimeZone.getDefault());
        return dateFormatter.format(utcTime);
    }

    public static long getCurrentDateTime() {
        return new Date().getTime();
    }


    public static boolean isNewDate(long lastOpenTime, long serverTime) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        //fmt.setTimeZone(TimeZone.getTimeZone("IST"));
        Date d1 = new Date(lastOpenTime);
        Date d2 = new Date(serverTime * 1000);
        return !fmt.format(d1).equals(fmt.format(d2));
    }

    public static String getUserId(Context context) {
        return PrefUtils.getStringFromPrefs(context, Constants.USER_ID, "GUEST");
    }

    private static String getCountryCode(Context context) {
        String countryCode = "";
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            countryCode = tm.getNetworkCountryIso();
            if (countryCode.length() > 0) {
                return countryCode.toUpperCase();
            }
        }
        countryCode = Locale.getDefault().getCountry();
        if (countryCode.length() > 0) {
            return countryCode.toUpperCase();
        }
        return countryCode;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String encryptData(String input) {

        String result = "";
        for (int i = 0; i < input.length(); i++) {
            result = result + String.valueOf((char) (input.charAt(i) + 1));
        }
        return result;
    }

    public static String decryptData(String input) {
        String result = "";
        for (int i = 0; i < input.length(); i++) {
            result = result + String.valueOf((char) (input.charAt(i) - 1));
        }
        return result;
    }

    private static Drawable getCoinIcon(Context context) {
        String country = PrefUtils.getStringFromPrefs(context, Constants.USER_COUNTRY, "in").toLowerCase();
      //  return context.getResources().getDrawable(context.getResources()
      //          .getIdentifier("ic_coin_" + country, "mipmap", context.getPackageName()));
        return context.getResources().getDrawable(context.getResources()
                .getIdentifier("ic_coin_" + "in", "mipmap", context.getPackageName()));

    }

}
