package captech.muslimutility.utility;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import captech.muslimutility.service.AzkarAlarm;
import captech.muslimutility.service.PrayerAlarm;
import captech.muslimutility.service.PrayingDayCalculateAlarm;
import captech.muslimutility.service.RingingAlarm;
import captech.muslimutility.service.SilentMoodAlarm;

public class Alarms {

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {

        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void setNotificationAlarm(Context context, int hour, int min, int id, String extra) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Bundle details = new Bundle();
        details.putString("prayName", extra);
        Intent alarmReceiver = new Intent(context, PrayerAlarm.class);
        alarmReceiver.putExtras(details);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, alarmReceiver, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        /**if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // kitkat...
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        } else {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingIntent);
        }*/
    }

    public static void setAlarmForAzkar(Context context, int hour, int min, int id , String type) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Bundle details = new Bundle();
        details.putString("Azkar", type);
        Intent alarmReceiver = new Intent(context, AzkarAlarm.class);
        alarmReceiver.putExtras(details);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, alarmReceiver, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // kitkat...
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        } else {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingIntent);
        }
    }

    public static void startCalculatePrayingBroadcast(Context context) {
        context.sendBroadcast(new Intent("com.captech.muslimutility.calculatepraying"));
    }

    public static void setNotificationAlarmMainPrayer(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmReceiver = new Intent(context, PrayingDayCalculateAlarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1111, alarmReceiver, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // kitkat...
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        } else {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingIntent);
        }
    }

    public static void NormalAudio(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmReceiver = new Intent(context, RingingAlarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 11022, alarmReceiver, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 900000, pendingIntent);
    }

    public static void switchToSilent(int minutes, Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmReceiver = new Intent(context, SilentMoodAlarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1159, alarmReceiver, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + TimeUnit.MINUTES.toMillis(minutes), pendingIntent);
    }

}
