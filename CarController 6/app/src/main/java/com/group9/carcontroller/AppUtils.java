package com.group9.carcontroller;

/**
 * This class contains the key to the
 * password and makes sure the typed
 * password is the same as the
 * actual password
 *
 * @author Isak Magnusson
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppUtils {

    //Key to the password
    public static final String KEY_PWD = "password";


    /*
    * The method which is used in LoginActivity
    * to make sure the password entered by the user
    * is the same as the saved password
    */
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
