package com.app.rush47.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.app.rush47.models.CurrentUser;

/**
 * Wraps SharedPreferences to persist the logged-in user's session.
 * Recreated from the original decompiled UserLocalStore.
 */
public class UserLocalStore {

    private static final String PREF_NAME = "rush47_user_prefs";
    private final SharedPreferences preferences;

    public UserLocalStore(Context context) {
        preferences = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void storeUserData(CurrentUser user) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("member_id", user.getMemberid());
        editor.putString("username", user.getUsername());
        editor.putString("password", user.getPassword());
        editor.putString("email", user.getEmail());
        editor.putString("mobile", user.getMobile());
        editor.putString("token", user.getToken());
        editor.putString("first_name", user.getFirstName());
        editor.putString("last_name", user.getLastName());
        editor.putString("dob", user.getDob());
        editor.putString("gender", user.getGender());
        editor.apply();
    }

    public CurrentUser getLoggedInUser() {
        return new CurrentUser(
                preferences.getString("member_id", ""),
                preferences.getString("username", ""),
                preferences.getString("password", ""),
                preferences.getString("email", ""),
                preferences.getString("mobile", ""),
                preferences.getString("token", ""),
                preferences.getString("first_name", ""),
                preferences.getString("last_name", ""),
                preferences.getString("dob", ""),
                preferences.getString("gender", "")
        );
    }

    public boolean isLoggedIn() {
        return !getLoggedInUser().getMemberid().isEmpty();
    }

    public void clearUserData() {
        preferences.edit().clear().apply();
    }
}
