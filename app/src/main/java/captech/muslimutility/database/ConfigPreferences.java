package captech.muslimutility.database;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import java.util.List;
import captech.muslimutility.model.LocationInfo;
import captech.muslimutility.model.Weather;
import captech.muslimutility.model.WeatherSave;

public class ConfigPreferences {
    private static final String MAIN_CONFIG = "application_settings";
    public static final String LOCATION_INFO = "location_information",
            QUIBLA_DEGREE = "quibla_degree",
            TODAY_WETHER = "today_weather", WEEK_WETHER = "week_weather",
            APP_LANGUAGE = "app_language", PRAY_NOTIFY = "pray_notify",
            SILENT_MOOD = "silent_mood", LED_MOOD = "led_mood", VIBRATION = "vibration_mood",
            TWENTYFOUR = "twenty_four", AZKAR_MOOD = "azkar_mood",
            COUNTRY_POPUP = "country_popup", APP_FIRST_OPEN = "application_first_open";

    public static void setLocationConfig(Context context, LocationInfo locationConfig) {
        SharedPreferences.Editor editor = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(locationConfig);
        editor.putString(LOCATION_INFO, json);
        editor.commit();
    }

    public static LocationInfo getLocationConfig(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(LOCATION_INFO, "");
        LocationInfo locationInfo = gson.fromJson(json, LocationInfo.class);
        return locationInfo;
    }

    public static void setTodayListWeather(Context context, List<Weather> weather) {
        SharedPreferences.Editor editor = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(new WeatherSave(weather));
        editor.putString(TODAY_WETHER, json);
        editor.commit();
    }

    public static WeatherSave getTodayListWeather(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(TODAY_WETHER, "");
        WeatherSave weathers = gson.fromJson(json, WeatherSave.class);
        return weathers;
    }

    public static void setWeekListWeather(Context context, List<Weather> weather) {
        SharedPreferences.Editor editor = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(new WeatherSave(weather));
        editor.putString(WEEK_WETHER, json);
        editor.commit();
    }

    public static WeatherSave getWeekListWeather(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(WEEK_WETHER, "");
        WeatherSave weathers = gson.fromJson(json, WeatherSave.class);
        return weathers;
    }

    public static void setQuibla(Context context, int degree) {
        SharedPreferences.Editor editor = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE).edit();
        editor.putInt(QUIBLA_DEGREE, degree);
        editor.commit();
    }

    public static int getQuibla(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE);
        int degree = sharedPreferences.getInt(QUIBLA_DEGREE, -1);
        return degree;
    }

    public static void setApplicationLanguage(Context context, String language) {
        SharedPreferences.Editor editor = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE).edit();
        editor.putString(APP_LANGUAGE, language);
        editor.commit();
    }

    public static String getApplicationLanguage(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE);
        String language = sharedPreferences.getString(APP_LANGUAGE, "en");
        return language;
    }

    public static void setPrayingNotification(Context context, boolean notification) {
        SharedPreferences.Editor editor = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE).edit();
        editor.putBoolean(PRAY_NOTIFY, notification);
        editor.commit();
    }

    public static boolean getPrayingNotification(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(PRAY_NOTIFY, false);
    }

    public static void setSilentMood(Context context, boolean silent) {
        SharedPreferences.Editor editor = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE).edit();
        editor.putBoolean(SILENT_MOOD, silent);
        editor.commit();
    }

    public static boolean getSilentMood(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SILENT_MOOD, true);
    }

    public static void setLedNotification(Context context, boolean led) {
        SharedPreferences.Editor editor = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE).edit();
        editor.putBoolean(LED_MOOD, led);
        editor.commit();
    }

    public static boolean getLedNotification(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(LED_MOOD, true);
    }

    public static void setVibrationMode(Context context, boolean vibrationFlag) {
        SharedPreferences.Editor editor = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE).edit();
        editor.putBoolean(VIBRATION, vibrationFlag);
        editor.commit();
    }

    public static boolean getVibrationMode(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(VIBRATION, true);
    }

    public static void setTwentyFourMode(Context context, boolean twentyFourFlag) {

        SharedPreferences.Editor editor = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE).edit();
        editor.putBoolean(TWENTYFOUR, twentyFourFlag);
        editor.commit();
    }

    public static boolean getTwentyFourMode(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(TWENTYFOUR, false);
    }

    public static void setAzkarMood(Context context, boolean azkarMood) {
        SharedPreferences.Editor editor = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE).edit();
        editor.putBoolean(AZKAR_MOOD, azkarMood);
        editor.commit();
    }

    public static boolean getAzkarMood(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(AZKAR_MOOD, true);

    }

    public static void setWorldPrayerCountry(Context context, LocationInfo locationInfo) {
        SharedPreferences.Editor editor = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(locationInfo);
        editor.putString(COUNTRY_POPUP, json);
        editor.commit();

    }

    public static LocationInfo getWorldPrayerCountry(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(COUNTRY_POPUP, "");
        LocationInfo locationInfo = gson.fromJson(json, LocationInfo.class);
        return locationInfo;
    }

    public static void setApplicationFirstOpenDone(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE).edit();
        editor.putBoolean(APP_FIRST_OPEN, false);
        editor.commit();
    }

    public static boolean IsApplicationFirstOpen(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (MAIN_CONFIG, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(APP_FIRST_OPEN, true);
    }


}
