package captech.muslimutility.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;
import captech.muslimutility.R;
import captech.muslimutility.database.ConfigPreferences;
import captech.muslimutility.ui.activity.AzkarActivity;
import captech.muslimutility.utility.MindtrackLog;

public class AzkarNotification extends Service {
    private String Azkar;
    public static final String CHANNEL_ID = "#180";


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            Azkar = intent.getStringExtra("Azkar");
            Log.d("Azkar", Azkar + "");
            MindtrackLog.add(Azkar.equals("1") ? getString(R.string.sabah) : getString(R.string.massa));
            if (ConfigPreferences.getAzkarMood(this)) showNotification();
            AzkarAlarm.completeWakefulIntent(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void showNotification() {
        try {
            Bitmap bigIcon = BitmapFactory.decodeResource(getResources(),
                    R.drawable.roundicon);

            boolean aboveLollipopFlag = android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

            //pending intent to open azkar
            PendingIntent intent = PendingIntent.getActivity(this, 0,
                    new Intent(this, AzkarActivity.class)
                            .putExtra("zekr_type", Azkar.equals("1") ? 2 : 3)
                            .putExtra("title", Azkar.equals("1") ? getString(R.string.sabah) : getString(R.string.massa)), 0);

            //azkar notification builder
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID).
                    setSmallIcon(R.drawable.roundicon)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentText(Azkar.equals("1") ? getString(R.string.sabah) : getString(R.string.massa))
                    .setContentTitle(getString(R.string.remember))
                    .setAutoCancel(true)
                    .setSmallIcon(aboveLollipopFlag ? R.drawable.notification_white : R.drawable.roundicon)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setColor(Color.parseColor("#FF1760AE"))
                    .setContentIntent(intent);


            NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(this);
            mNotificationManager.notify(1001001, builder.build());
            //NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            //notificationManager.notify(1001001, builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
