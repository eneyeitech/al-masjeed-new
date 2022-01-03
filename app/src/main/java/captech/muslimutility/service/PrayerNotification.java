package captech.muslimutility.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;
import captech.muslimutility.R;
import captech.muslimutility.calculator.calendar.HGDate;
import captech.muslimutility.database.ConfigPreferences;
import captech.muslimutility.ui.activity.MainActivity;
import captech.muslimutility.ui.activity.PrayerImageActivity;
import captech.muslimutility.utility.Alarms;
import captech.muslimutility.utility.MindtrackLog;


public class PrayerNotification extends Service {
    private String prayingName , prayerType;
    public static final String CHANNEL_ID = "#180";
    public static final String CHANNEL_NAME = "Prayer Time Notification";
    public static final String CHANNEL_DESCRIPTION = "New Implementation";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        prayingName = intent.getStringExtra("prayName");
        //not mid night
        Log.i("ACTIVITY_SRAT" , "PrayerNotification is working well");
        if (ConfigPreferences.getPrayingNotification(this) && !prayingName.equals("6"))
            showNotification();
        if (ConfigPreferences.getSilentMood(this)) changeMobileToSilentMood();
        sendBroadcast(new Intent().setAction("prayer.information.change"));
        stopSelf();
        PrayerAlarm.completeWakefulIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void changeMobileToSilentMood() {
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int ringerMode = mAudioManager.getRingerMode();
        if (ringerMode != AudioManager.RINGER_MODE_SILENT) {
            Alarms.switchToSilent(10, this);
        }
    }

    /**
     * Function to show prayer notification
     */
    public void showNotification() {

        switch (prayingName) {
            case "0":
                prayingName = this.getString(R.string.fajr_prayer);
                prayerType = PrayerImageActivity.MOSQUE_NIGHT;
                MindtrackLog.add(prayingName);
                break;
            case "1":
                prayingName = this.getString(R.string.sunrize_prayer);
                prayerType = PrayerImageActivity.MOSQUE_DAY;
                MindtrackLog.add(prayingName);
                break;
            case "2":
                HGDate hgDate = new HGDate();
                prayingName = hgDate.weekDay() != 5 ? this.getString(R.string.zuhr_prayer) : this.getString(R.string.jomma_prayer);

                prayerType = PrayerImageActivity.MOSQUE_DAY;
                MindtrackLog.add(prayingName);
                break;
            case "3":
                prayingName = this.getString(R.string.asr_prayer);
                prayerType = PrayerImageActivity.MOSQUE_DAY;
                MindtrackLog.add(prayingName);
                break;
            case "4":
                prayingName = this.getString(R.string.magreb_prayer);
                prayerType = PrayerImageActivity.MOSQUE_NIGHT;
                MindtrackLog.add(prayingName);
                break;
            case "5":
                prayingName = this.getString(R.string.asha_prayer);
                prayerType = PrayerImageActivity.MOSQUE_NIGHT;
                MindtrackLog.add(prayingName);
                break;
            case "6":
                prayingName = this.getString(R.string.mid_night);
                MindtrackLog.add(prayingName);
                break;

        }


        NotificationCompat.Builder builder;
        boolean aboveLollipopFlag = android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
        PendingIntent intent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        if (ConfigPreferences.getLedNotification(this)) {
            builder = new NotificationCompat.Builder(this, CHANNEL_ID).
                    setSmallIcon(aboveLollipopFlag ? R.drawable.notification_white : R.drawable.roundicon)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentText(prayingName)
                    .setContentTitle(getString(R.string.remember))
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setLights(0xFF00ff00, 1000, 1000)
                    .setAutoCancel(true)
                    .setColor(Color.parseColor("#FF1760AE"))
                    .setContentIntent(intent);
        } else {
            builder = new NotificationCompat.Builder(this, CHANNEL_ID).
                    setSmallIcon(aboveLollipopFlag ? R.drawable.notification_white : R.drawable.roundicon)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentText(prayingName)
                    .setContentTitle(getString(R.string.remember))
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .setColor(Color.parseColor("#FF1760AE"))
                    .setContentIntent(intent);
        }
        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(this);
        mNotificationManager.notify(0, builder.build());
        //NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //notificationManager.notify(0, builder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
