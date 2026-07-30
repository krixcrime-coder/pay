package com.app.rush47.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;

import java.util.Locale;

/**
 * Persists the user's chosen app language (English / Hindi, per the
 * Choose Language screen) and wraps a Context so string resources
 * resolve from values-hi/ when Hindi is selected.
 *
 * Usage - override in every Activity (or once in a BaseActivity all
 * activities extend):
 *
 *   @Override
 *   protected void attachBaseContext(Context newBase) {
 *       super.attachBaseContext(LocaleHelper.wrap(newBase));
 *   }
 */
public final class LocaleHelper {

    private static final String PREF_NAME = "rush47_locale_prefs";
    private static final String KEY_LANGUAGE = "app_language";

    private LocaleHelper() {}

    public static String getLanguage(Context context) {
        SharedPreferences prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_LANGUAGE, "en");
    }

    public static void setLocale(Context context, String languageCode) {
        SharedPreferences prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply();
    }

    public static Context wrap(Context context) {
        String languageCode = getLanguage(context);
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(locale);

        return context.createConfigurationContext(configuration);
    }
}
