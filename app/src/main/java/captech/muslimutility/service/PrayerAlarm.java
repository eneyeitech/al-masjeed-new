package captech.muslimutility.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationManagerCompat;
import androidx.legacy.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class PrayerAlarm extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle b = intent.getExtras();
        startWakefulService(context , new Intent(context ,
                PrayerNotification.class).putExtra("prayName" ,
                b.getString("prayName")));

        final Context appContext = context;

        Log.i("ACTIVITY_SRAT" , "PrayerAlarm is working well");
        TimerTask task = new TimerTask() {
            public void run() {
                NotificationManagerCompat.from(appContext).cancelAll();
            }
        };
        Timer timer = new Timer("Stop Prayer Timer");

        long delay = 300000L;
        timer.schedule(task, delay);
    }
}
