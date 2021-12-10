package captech.muslimutility.ui.popup;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;

import captech.muslimutility.Manager.DBManager;
import captech.muslimutility.ui.activity.SelectLocationTabsActivity;

public class CountryPrayerPopup {
    private Context context;
    public static String locationInformation;
    public static float lat, log;
    DBManager dbManager;
    private boolean manualLocationMood = false;

    public CountryPrayerPopup(Context context, boolean manualLocationMood , boolean fromLocationBtn) {
        this.context = context;
        this.manualLocationMood = manualLocationMood;

        Log.i("IsFromLocationBtn" , "pop   "+fromLocationBtn);

        dbManager = new DBManager(context);
        dbManager.open();
        try {
            dbManager.copyDataBase();
        }catch (IOException e){

        }

        Intent intent = new Intent(context , SelectLocationTabsActivity.class);
        intent.putExtra("IsFromLocationBtn" , fromLocationBtn);
        context.startActivity(intent);
    }

}
