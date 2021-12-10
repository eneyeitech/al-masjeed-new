package captech.muslimutility.calculator.location;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utility {

    public static String getStringPrefs (Context context, String prefsKey , String defaultValue){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(prefsKey, defaultValue);
    }

    public static void saveStringPrefs (Context context, String prefsKey, String prefsValue){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString(prefsKey, prefsValue).apply();
    }

}

