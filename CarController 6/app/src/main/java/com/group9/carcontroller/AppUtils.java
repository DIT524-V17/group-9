package com.group9.carcontroller;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppUtils {

    public static final String KEY_PWD = "password";


    public static int checkLogin(Context ctx, String pwd) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
    String ppwd = pref.getString(KEY_PWD, "");


        if (ppwd.equals(pwd)){
            return 1;
        } else {
            return 2;
        }

    }


}
