package captech.muslimutility.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import captech.muslimutility.utility.MindtrackLog;

public class StartUpBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            MindtrackLog.add("Boot Complete");
            context.startService(new Intent(context, PrayingDayCalculateHandler.class));
        }
    }
}
