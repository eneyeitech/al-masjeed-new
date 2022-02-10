package captech.muslimutility.service;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;

import androidx.legacy.content.WakefulBroadcastReceiver;

import captech.muslimutility.database.ConfigPreferences;
import captech.muslimutility.utility.Alarms;
import captech.muslimutility.utility.MindtrackLog;

public class SilentMoodAlarm extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        MindtrackLog.add("Silent Mood");

        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        mAudioManager.setRingerMode(0);
        if (ConfigPreferences.getVibrationMode(context))
            //mAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            mAudioManager.setRingerMode(1);
        Alarms.NormalAudio(context);
    }
}
