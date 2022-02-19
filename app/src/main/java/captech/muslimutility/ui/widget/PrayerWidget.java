package captech.muslimutility.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import captech.muslimutility.R;
import captech.muslimutility.calculator.calendar.HGDate;
import captech.muslimutility.calculator.prayer.PrayerTimeCalculator;
import captech.muslimutility.database.ConfigPreferences;
import captech.muslimutility.model.LocationInfo;
import captech.muslimutility.model.PrayerTime;
import captech.muslimutility.service.MosqueTimings;
import captech.muslimutility.ui.activity.MainActivity;
import captech.muslimutility.utility.Calculators;
import captech.muslimutility.utility.Dates;
import captech.muslimutility.utility.NumbersLocal;

public class PrayerWidget extends AppWidgetProvider {
    private static String PRAYER_CHANGE = "prayer.information.change";
    private double[] prayers, nextDayPrayers;

    @Override
    public void onUpdate(Context context, final AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        String languageToLoad = ConfigPreferences.getApplicationLanguage(context);

        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());

        List<Object> t = MosqueTimings.getTimings();
        Log.d("SUCCESS--Widget-1", "" + t.get(0) + "," + t.get(1) + "," + t.get(2) + "," + t.get(3) + "," + t.get(4) + "," + t.get(5) + "," + t.get(6) + "," + t.get(7));
        if(t == null) { // Abdulmumin include

            final int count = appWidgetIds.length;
            for (int i = 0; i < count; i++) {
                final int widgetId = appWidgetIds[i];
                final RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                        R.layout.widget_prayer);

                HGDate georgianDate = new HGDate();
                HGDate islamicDate = new HGDate(georgianDate);
                islamicDate.toHigri();
                String hDay = georgianDate.getDay() + "";
                String hMonth = georgianDate.getMonth() - 1 + "";
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM , dd");
                String[] myformat = sdf.format(new Date().getTime()).split(",");
                String week = myformat[0];

                //set current dates
                remoteViews.setTextViewText(R.id.textView5, NumbersLocal.convertNumberType(context, islamicDate.getDay() + ""));
                remoteViews.setTextViewText(R.id.textView6, Dates.islamicMonthName(context, islamicDate.getMonth() - 1));
                remoteViews.setTextViewText(R.id.textView3, NumbersLocal.convertNumberType(context, (hDay + "").trim()));
                remoteViews.setTextViewText(R.id.textView4, Dates.gregorianMonthName(context, Integer.parseInt(hMonth.trim())).trim());
                remoteViews.setTextViewText(R.id.textView, week);

                //get saved location information
                LocationInfo locationInfo = ConfigPreferences.getLocationConfig(context);
                if (locationInfo != null) {
                    remoteViews.setTextViewText(R.id.textView2, (context.getResources().getConfiguration()
                            .locale.getDisplayLanguage().equals("العربية")
                            ? locationInfo.name_english : locationInfo.name));

                    remoteViews.setTextViewText(R.id.textView32, context.getString(R.string.near) + " " + (context.getResources().getConfiguration()
                            .locale.getDisplayLanguage().equals("العربية")
                            ? locationInfo.city_ar : locationInfo.city));

                    SimpleDateFormat nsdf = new SimpleDateFormat("yyyy-MM-dd");
                    String[] normaldate = nsdf.format(new Date().getTime()).split("-");

                    prayers = new PrayerTimeCalculator(
                            Integer.parseInt(NumbersLocal.convertNumberType(context, normaldate[2].trim()))
                            , Integer.parseInt(NumbersLocal.convertNumberType(context, normaldate[1].trim()))
                            , Integer.parseInt(NumbersLocal.convertNumberType(context, normaldate[0].trim()))
                            , locationInfo.latitude, locationInfo.longitude
                            , locationInfo.timeZone, locationInfo.mazhab
                            , locationInfo.way, locationInfo.dls
                            , context).calculateDailyPrayers_withSunset();

                    HGDate nextDay = new HGDate();
                    nextDay.nextDay();
                    nextDayPrayers = new PrayerTimeCalculator(
                            Integer.parseInt(NumbersLocal.convertNumberType(context, nextDay.getDay() + ""))
                            , Integer.parseInt(NumbersLocal.convertNumberType(context, nextDay.getMonth() + ""))
                            , Integer.parseInt(NumbersLocal.convertNumberType(context, nextDay.getYear() + ""))
                            , locationInfo.latitude, locationInfo.longitude
                            , locationInfo.timeZone, locationInfo.mazhab
                            , locationInfo.way, locationInfo.dls
                            , context).calculateDailyPrayers_withSunset();

                    Calendar c = Calendar.getInstance();
                    int hourNow = c.get(Calendar.HOUR_OF_DAY);
                    int minsNow = c.get(Calendar.MINUTE);
                    //int counter = 0;

                    Map<String, Object> sx = MosqueTimings.getMosqueTiming();
                    String sc = (String) sx.get("fajr");
                    Log.d("String_date::maghrib1", sc.substring(11,19) + "");
                    Log.d("String_date::maghrib2", Integer.parseInt(sc.substring(14,16)) + " SIZE "+prayers.length+" hournow "+hourNow+" minutenow " + minsNow);

                    //Sun Jan 02 19:19:00 GMT+01:00 2022
                    List<PrayerTime> prayerTimes = new ArrayList<>();
                    for(Map.Entry<String, Object> mp : sx.entrySet()){
                        String tString = (String) mp.getValue();
                        prayerTimes.add(new PrayerTime(mp.getKey(), tString.substring(11,19)));
                    }

                    prayerTimes.add(new PrayerTime("mid", "24:00"));

                    Collections.sort(prayerTimes);


                    int counter = 0;
                    for(PrayerTime p: prayerTimes){
                        Log.d("String_date::widget", p.getName()+"");
                        counter++;
                        if (hourNow < p.getHour()) {
                            break;
                        } else {
                            if (hourNow == p.getHour()) {
                                if (minsNow < p.getMinute()) {
                                    break;
                                }
                            }
                        }
                    }

                    /**for (double pray : prayers) {
                        counter++;
                        if (houreNow < Calculators.extractHour(pray)) {
                            break;
                        } else {
                            if (houreNow == Calculators.extractHour(pray)) {
                                if (minsNow < Calculators.extractMinutes(pray)) {
                                    break;
                                }
                            }
                        }
                    }*/


                    //switch to check the next prayer
                    switch (counter) {
                        case 1:
                            remoteViews.setTextViewText(R.id.textView7, NumbersLocal.convertNumberType(context
                                    , context.getString(R.string.fajr_prayer) + " " + Calculators.extractPrayTime(context, prayers[0])));
                            break;
                        case 2:
                            remoteViews.setTextViewText(R.id.textView7, NumbersLocal.convertNumberType(context
                                    , context.getString(R.string.sunrize_prayer) + " " + Calculators.extractPrayTime(context, prayers[1])));
                            break;
                        case 3:
                            Calendar cal = Calendar.getInstance();
                            boolean isFriday = cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY;
                            // changes the

                            remoteViews.setTextViewText(R.id.textView7, NumbersLocal.convertNumberType(context
                                    , (!isFriday ? context.getString(R.string.zuhr_prayer) : context.getString(R.string.jomma_prayer)) + " " + Calculators.extractPrayTime(context, prayers[2])));
                            /**remoteViews.setTextViewText(R.id.textView7, NumbersLocal.convertNumberType(context
                             , (georgianDate.weekDay() != 5 ? context.getString(R.string.zuhr_prayer) : context.getString(R.string.jomma_prayer)) + " " + Calculators.extractPrayTime(context, prayers[2])));*/
                            break;
                        case 4:
                            remoteViews.setTextViewText(R.id.textView7, NumbersLocal.convertNumberType(context
                                    , context.getString(R.string.asr_prayer) + " " + Calculators.extractPrayTime(context, prayers[3])));
                            break;
                        case 5:
                            remoteViews.setTextViewText(R.id.textView7, NumbersLocal.convertNumberType(context
                                    , context.getString(R.string.magreb_prayer) + " " + Calculators.extractPrayTime(context, prayers[4])));
                            break;
                        case 6:
                            remoteViews.setTextViewText(R.id.textView7, NumbersLocal.convertNumberType(context
                                    , context.getString(R.string.asha_prayer) + " " + Calculators.extractPrayTime(context, prayers[5])));
                            break;
                        case 7:
                            remoteViews.setTextViewText(R.id.textView7, NumbersLocal.convertNumberType(context
                                    , context.getString(R.string.fajr_prayer) + " " + Calculators.extractPrayTime(context, nextDayPrayers[0])));
                            break;
                    }


                    //Intent open application when press in widget
                    PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
                    remoteViews.setOnClickPendingIntent(R.id.relativeLayout, configPendingIntent);

                    appWidgetManager.updateAppWidget(widgetId, remoteViews);
                }
            }
        }else{// Abdulmumin include
            Log.d("SUCCESS--Widget", "" + t.get(0) + "," + t.get(1) + "," + t.get(2) + "," + t.get(3) + "," + t.get(4) + "," + t.get(5) + "," + t.get(6) + "," + t.get(7));
        }

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (action.equals(PRAYER_CHANGE) || action.equals(Intent.ACTION_DATE_CHANGED)) {
            AppWidgetManager gm = AppWidgetManager.getInstance(context);
            int[] ids = gm.getAppWidgetIds(new ComponentName(context, PrayerWidget.class));
            this.onUpdate(context, gm, ids);
        } else {
            super.onReceive(context, intent);
        }
    }

}