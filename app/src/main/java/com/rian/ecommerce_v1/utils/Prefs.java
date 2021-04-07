package com.rian.ecommerce_v1.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import net.grandcentrix.tray.TrayPreferences;

public class Prefs extends TrayPreferences {
    public static final String PREF_IS_LOGEDIN = "is.login";
    public static final String PREF_STORE_PROFILE = "pref.store.profile";
    public static final String PREF_STORE_PROFILE_TOKO = "pref.store.profiletoko";

    public Prefs(@NonNull Context context) {
        super(context, "myAppPreferencesModule", 1);
    }
}
