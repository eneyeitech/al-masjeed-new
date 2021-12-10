package captech.muslimutility.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import captech.muslimutility.R;
import captech.muslimutility.database.ConfigPreferences;

public class SplashActivity extends AppCompatActivity {
    TextView txtGranted, txtDenied;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ConfigPreferences.IsApplicationFirstOpen(this) == true) {
            String systemLanguage = Resources.getSystem().getConfiguration().locale.getLanguage();
            ConfigPreferences.setApplicationFirstOpenDone(this);
            ConfigPreferences.setApplicationLanguage(this, systemLanguage == "ar" ? systemLanguage : "en");
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("language", systemLanguage.equals("ar") ? "0" : "1"); // value to store
            editor.commit();
        }

        String languageToLoad = ConfigPreferences.getApplicationLanguage(this);
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_splash);

        txtGranted = (TextView) findViewById(R.id.txtPermission);
        txtDenied = (TextView) findViewById(R.id.txtPermission1);

        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        String rationale = "Please provide location permission so that you can ...";
        Permissions.Options options = new Permissions.Options()
                .setRationaleDialogTitle("Info")
                .setSettingsDialogTitle("Warning");

        Permissions.check(this/*context*/, permissions, rationale, options, new PermissionHandler() {
            @Override
            public void onGranted() {
                // do your task.
                Thread timer = new Thread() {

                    @Override
                    public void run() {

                        try {
                            sleep(2500);

                            try {
                                checkAndCopy.start();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            super.run();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                    }
                };

                timer.start();
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {

            }
        });

    }

    Thread checkAndCopy = new Thread(new Runnable() {
        @Override
        public void run() {
            new File("/data/data/com.captech.muslimutility/muslim_organizer.sqlite.png");

            Intent main = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(main);
            finish();

       }
    });


}
