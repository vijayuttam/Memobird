package com.intretech.library.keep;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.compat.BuildConfig;

public class Keep {
    private SharedPreferences pref;

    private static final String ASKED_FOR_PERMISSION = "ASKED_FOR_PERMISSION";

    Keep(Context context) {
        this.pref = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
    }

    public static Keep with(Context context) {
        return new Keep(context);
    }

    public void askedForPermission() {
        pref.edit().putBoolean(ASKED_FOR_PERMISSION, true).commit();
    }

    public boolean neverAskedForPermissionYet() {
        return !pref.getBoolean(ASKED_FOR_PERMISSION, false);
    }

}
