package captech.muslimutility.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.legacy.content.WakefulBroadcastReceiver;
import android.util.Log;

public class AzkarAlarm extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle b = intent.getExtras();
        Log.d("Azkar" , b.getString("Azkar")+"") ;
        Intent serviceNotification = new Intent(context, AzkarNotification.class).putExtra("Azkar" , b.getString("Azkar"));
        startWakefulService(context, serviceNotification);
    }
}
