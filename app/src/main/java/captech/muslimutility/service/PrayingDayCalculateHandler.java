package captech.muslimutility.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import androidx.annotation.RequiresApi;
import captech.muslimutility.calculator.prayer.PrayerTimeCalculator;
import captech.muslimutility.database.ConfigPreferences;
import captech.muslimutility.model.LocationInfo;
import captech.muslimutility.model.PrayerTime;
import captech.muslimutility.utility.Alarms;
import captech.muslimutility.utility.Calculators;
import captech.muslimutility.utility.NumbersLocal;

public class PrayingDayCalculateHandler extends IntentService {
    private static final int PRAYER_SIG = 110, AZKAR_SIG = 895;

    public PrayingDayCalculateHandler() {
        super(PrayingDayCalculateHandler.class.getSimpleName());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onHandleIntent(Intent intent) {
        SimpleDateFormat nsdf = new SimpleDateFormat("yyyy-MM-dd");
        String[] normaldate = nsdf.format(new Date().getTime()).split("-");
        LocationInfo locationInfo = ConfigPreferences.getLocationConfig(getApplicationContext());
        if (locationInfo == null) return;
        double[] prayers = new PrayerTimeCalculator(
                Integer.parseInt(NumbersLocal.convertNumberType(getApplicationContext(), normaldate[2].trim()))
                , Integer.parseInt(NumbersLocal.convertNumberType(getApplicationContext(), normaldate[1].trim()))
                , Integer.parseInt(NumbersLocal.convertNumberType(getApplicationContext(), normaldate[0].trim()))
                , locationInfo.latitude, locationInfo.longitude
                , locationInfo.timeZone, locationInfo.mazhab
                , locationInfo.way, locationInfo.dls
                , getApplicationContext()).calculateDailyPrayers_withSunset();

        Calendar c = Calendar.getInstance();
        int hourNow = c.get(Calendar.HOUR_OF_DAY);
        int minsNow = c.get(Calendar.MINUTE);


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
            Log.d("String_date::name", p.getName()+"");
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
         Log.d("String_date::PDCH", pray+"");
         counter++;
         if (hourNow < Calculators.extractHour(pray)) {
         break;
         } else {
         if (hourNow == Calculators.extractHour(pray)) {
         if (minsNow < Calculators.extractMinutes(pray)) {
         break;
         }
         }
         }
         }*/

        for (int i = (counter - 1); i < prayerTimes.size(); i++) {
            //alarm for every prayer
            int hr = prayerTimes.get(i).getHour();
            int min = prayerTimes.get(i).getMinute();
            if ((min - 5) < 0){
                hr = hr - 1;
                min = 60 - Math.abs(min - 5);
            } else {
                min = min - 5;
            }

            Alarms.setNotificationAlarm(getApplicationContext(), hr
                    , min, PRAYER_SIG + i, i + "");

            Log.d("String_date" , prayerTimes.get(i).getHour()+" "+prayerTimes.get(i).getMinute());

            /**if (ConfigPreferences.getAzkarMood(this) == true) {
                //alarm for morning Azkar
                if (i == 0)
                    Alarms.setAlarmForAzkar(getApplicationContext(), prayerTimes.get(i).getHour()
                            , prayerTimes.get(i).getMinute() + 30, AZKAR_SIG + i , "1");
                //alarm for night Azkar
                if (i == 3)
                    Alarms.setAlarmForAzkar(getApplicationContext(), prayerTimes.get(i).getHour()
                            , prayerTimes.get(i).getMinute()+35, AZKAR_SIG + i , "2");
            }*/

        }

        /**   for (int i = (counter - 1); i < prayers.length; i++) {
         //alarm for every prayer
         Alarms.setNotificationAlarm(getApplicationContext(), Calculators.extractHour(prayers[i])
         , Calculators.extractMinutes(prayers[i]), PRAYER_SIG + i, i + "");

         Log.d("String_date" , Calculators.extractHour(prayers[i])+" "+Calculators.extractMinutes(prayers[i]));

         if (ConfigPreferences.getAzkarMood(this) == true) {
         //alarm for morning Azkar
         if (i == 0)
         Alarms.setAlarmForAzkar(getApplicationContext(), Calculators.extractHour(prayers[i])
         , Calculators.extractMinutes(prayers[i]) + 30, AZKAR_SIG + i , "1");
         //alarm for night Azkar
         if (i == 3)
         Alarms.setAlarmForAzkar(getApplicationContext(), Calculators.extractHour(prayers[i])
         , Calculators.extractMinutes(prayers[i])+35, AZKAR_SIG + i , "2");
         }

         }*/

        //reset widget for new changes
        sendBroadcast(new Intent().setAction("prayer.information.change"));

        stopSelf();
        PrayingDayCalculateAlarm.completeWakefulIntent(intent);
    }


}