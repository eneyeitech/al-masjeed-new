package captech.muslimutility.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import captech.muslimutility.Customization.FontsOverride;
import captech.muslimutility.R;
import captech.muslimutility.service.CopyDatabase;
import captech.muslimutility.utility.Alarms;

public class DataActivity extends AppCompatActivity {
    private ProgressBar copyingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        copyingBar = (ProgressBar) findViewById(R.id.progressBar);
        int color = 0xFF00FF00;
        copyingBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        copyingBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        if(!Alarms.isMyServiceRunning(this , CopyDatabase.class))
            startService(new Intent(this , CopyDatabase.class));
        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/ProximaNovaReg.ttf");
    }


    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(copyingBoBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                copyingBoBroadcastReceiver, new IntentFilter("coping_main_database"));
    }

    private BroadcastReceiver copyingBoBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            copyingBar.setMax(intent.getIntExtra("file_size", 0));
            copyingBar.setProgress(intent.getIntExtra("coping_size", 0));
            if (intent.getIntExtra("finish", 0) == 1) {
                copyingBar.setVisibility(View.GONE);
                Intent main = new Intent(DataActivity.this, MainActivity.class);
                startActivity(main);
                finish();
            }
        }
    };
}
