package captech.muslimutility.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import captech.muslimutility.utility.MindtrackLog;


public class TimeChangedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        MindtrackLog.add("Time Change");
        //context.startService(new Intent(context, PrayingDayCalculateHandler.class));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, PrayingDayCalculateHandler.class));
        }else {
            context.startService(new Intent(context, PrayingDayCalculateHandler.class));
        }
    }

}