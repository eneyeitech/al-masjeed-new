package captech.muslimutility.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import captech.muslimutility.Customization.FontsOverride;
import captech.muslimutility.Manager.DBManager;
import captech.muslimutility.R;
import captech.muslimutility.SharedData.SharedDataClass;
import captech.muslimutility.adapter.ViewPagerAdapter;
import captech.muslimutility.calculator.location.LocationReader;
import captech.muslimutility.calculator.prayer.PrayerTimes;
import captech.muslimutility.calculator.quibla.QuiblaCalculator;
import captech.muslimutility.config.Config;
import captech.muslimutility.database.ConfigPreferences;
import captech.muslimutility.model.LocationInfo;
import captech.muslimutility.model.MosquePrayerTimes;
import captech.muslimutility.model.ZekerType;
import captech.muslimutility.service.FusedLocationService;
import captech.muslimutility.service.MosqueTimings;
import captech.muslimutility.service.PrayingDayCalculateHandler;
import captech.muslimutility.ui.fragments.AzkarFragment;
import captech.muslimutility.ui.fragments.CalendarFragment;
import captech.muslimutility.ui.fragments.IslamicEventsFragment;
import captech.muslimutility.ui.fragments.PrayingFragment;
import captech.muslimutility.ui.fragments.TimingsFragment;
import captech.muslimutility.ui.fragments.WeatherFragment;
import captech.muslimutility.ui.popup.CountryPrayerPopup;
import captech.muslimutility.ui.popup.DataConvertPopup;
import captech.muslimutility.utility.Alarms;
import captech.muslimutility.utility.Validations;

public class MainActivity extends AppCompatActivity implements com.google.android.gms.location.LocationListener {
    private static final int REQUEST_GPS_LOCATION = 113;
    public static LocationInfo locationInfo;
    public static int quiblaDegree;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    ViewPagerAdapter adapter;
    FusedLocationService gps;
    ProgressDialog progressDialog;
    public static List<ZekerType> zekerTypeList = new ArrayList<>();
    Location gps_loc = null, network_loc = null, final_loc = null;
    private Dialog dialog;
    Typeface Roboto_Bold, Roboto_Light, Roboto_Reg, Roboto_Thin, ProximaNovaReg, ProximaNovaBold;
    TabLayout tabLayout;
    public DBManager dbManager;
    private String mosque_code = "";
    private String donation = "";

    private List found = null;
    private TextView name, fajr, sunrise, zuhr, asr, magrib, isha, jummaah;
    private SimpleDateFormat format;

    public static final String CHANNEL_ID = "#180";
    public static final String CHANNEL_NAME = "Prayer Time Notification";
    public static final String CHANNEL_DESCRIPTION = "New Implementation";


    private int[] tabIcons = {
            R.drawable.mosqueone,
            R.drawable.calendar,
            R.drawable.hands,
            R.drawable.event,
            R.drawable.cloud
    };

    private AdView mAdView;
    PrayingFragment prayingFragment;
    TimingsFragment timingsFragment;
    CalendarFragment calendarFragment;
    AzkarFragment azkarFragment;
    IslamicEventsFragment islamicEventsFragment;
    WeatherFragment weatherFragment;
    TelephonyManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //load application language
        String languageToLoad = ConfigPreferences.getApplicationLanguage(this);
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, getResources().getString(R.string.app_id));
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        dbManager = new DBManager(this);
        dbManager.open();
        try {
            dbManager.copyDataBase();
        } catch (IOException e) {

        }



//        zekerTypeList = dbManager.getAllAzkarTypes();

        ProximaNovaReg = Typeface.createFromAsset(this.getAssets(), "fonts/ProximaNovaReg.ttf");
        ProximaNovaBold = Typeface.createFromAsset(this.getAssets(), "fonts/ProximaNovaBold.ttf");
        Roboto_Bold = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Bold.ttf");
        Roboto_Light = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Light.ttf");
        Roboto_Reg = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Regular.ttf");
        Roboto_Thin = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Thin.ttf");

        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/ProximaNovaReg.ttf");
        manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        network_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (gps_loc != null) {
            final_loc = gps_loc;
            SharedDataClass.longitude = final_loc.getLongitude();
            SharedDataClass.latitude = final_loc.getLatitude();

        } else if (network_loc != null) {
            final_loc = network_loc;
            SharedDataClass.longitude = final_loc.getLongitude();
            SharedDataClass.latitude = final_loc.getLatitude();

        } else {
            SharedDataClass.longitude = 33.692959;
            SharedDataClass.latitude = 73.042072;
        }

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> listAddresses = geocoder.getFromLocation(SharedDataClass.latitude, SharedDataClass.longitude, 1);
            if (null != listAddresses && listAddresses.size() > 0) {
                SharedDataClass.locationArea = listAddresses.get(0).getAddressLine(0);
                SharedDataClass.locationCity = listAddresses.get(0).getAddressLine(1);
                SharedDataClass.locationCountry = listAddresses.get(0).getAddressLine(2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Abdulmumin
        format = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        locationInfo = ConfigPreferences.getLocationConfig(this);
        quiblaDegree = ConfigPreferences.getQuibla(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(5);
        setupViewPager(mViewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        setupTabIcons();

        if (ConfigPreferences.getPrayingNotification(this))
            Alarms.setNotificationAlarmMainPrayer(this);


        //clickable application title
        TextView applicationTitle = (TextView) findViewById(R.id.title);
        applicationTitle.setTypeface(ProximaNovaBold);
        applicationTitle.setText(getString(R.string.main));
        applicationTitle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition(), false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.getTabAt(position).select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        if (ConfigPreferences.getLocationConfig(this) == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_GPS_LOCATION);
            } else {
                getLocation();
            }
        }

        NotificationManager notificationManager = null;
        Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.yusuf_islam);  //Here is FILE_NAME is the name of file that you want to play

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );

            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SPEECH)
                    .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            /**NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID,
                    getApplicationContext().getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_HIGH);*/
            Log.d("Audio","1");
            // Configure the notification channel.
            channel.setDescription(CHANNEL_DESCRIPTION);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setSound(sound, attributes); // This is IMPORTANT
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        } else {
            Log.d("Audio","2");
            notificationManager =
                    (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        }

        /**if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            startActivity(intent);
        }*/



        new AzkarTypes().execute();

    }

    public void showNotification(MenuItem item) {
        boolean aboveLollipopFlag = android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(aboveLollipopFlag ? R.drawable.notification_white : R.drawable.roundicon)
                        .setContentTitle("Hello, attention!")
                        .setContentText("Here's the notification you were looking for!")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(this);
        mNotificationManager.notify(1, mBuilder.build());
    }

    public void fetchMosqueTimings() {

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory factory = JacksonFactory.getDefaultInstance();
        final Sheets sheetsService = new Sheets.Builder(transport, factory, null)
                .setApplicationName("My Awesome App")
                .build();
        final String spreadsheetId = Config.spreadsheet_id;

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = preferences.edit();

        //Body of your click handler
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                //you call here
                String range = "Sheet1!A2:J";
                ValueRange result = null;
                try {
                    result = sheetsService.spreadsheets().values()
                            .get(spreadsheetId, range)
                            .setKey(Config.google_api_key)
                            .execute();
                    List<List<Object>> values = result.getValues() ;
                    int numRows = result.getValues() != null ? result.getValues().size() : 0;
                    Log.d("SUCCESS.", "rows retrived " + numRows);
                    System.out.println("Name, Code, Fajr, Sunrise, Zuhr, Asr, Maghrib, Isha");
                    Log.d("SUCCESS.", "Name, Code, Fajr, Sunrise, Zuhr, Asr, Maghrib, Isha");

                    String mc = preferences.getString("mosquecode", mosque_code);
                    for (List row : values) {
                        // Print columns A and E, which correspond to indices 0 and 4.
                        String code = (String) row.get(1);

                        if(code.toLowerCase().equals(mc.toLowerCase())) {
                            found = row;
                            donation = (String) row.get(9);
                        }

                    }
                    if(found != null) {
                        MosqueTimings.setTimings(found);

                        //Update
                        name = (TextView) findViewById(R.id.mosque_name);
                        fajr = (TextView) findViewById(R.id.fajrTime);
                        sunrise = (TextView) findViewById(R.id.sunriseTime);
                        zuhr = (TextView) findViewById(R.id.zuhrTime);
                        asr = (TextView) findViewById(R.id.asrTime);
                        magrib = (TextView) findViewById(R.id.magribTime);
                        isha = (TextView) findViewById(R.id.ishaTime);

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
                        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                        String currentDate = sdf.format(new Date());

                        String fStr = "",sStr="",zStr="",aStr="",mStr="",iStr="", frStr;

                        DateFormat formatter = new SimpleDateFormat("yyyy.MM.d hh:mm a");

                        final Date fajrDate, sunriseDate, zuhrDate, asrDate, magribDate, ishaDate, fridayDate;
                        fStr = currentDate + " " + found.get(2);
                        sStr = currentDate + " " + found.get(3);
                        zStr = currentDate + " " + found.get(4);
                        aStr = currentDate + " " + found.get(5);
                        mStr = currentDate + " " + found.get(6);
                        iStr = currentDate + " " + found.get(7);
                        frStr = currentDate + " " + found.get(8);
                        //sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                        //SharedPreferences.Editor editor = sharedpreferences.edit();

                        editor.putString("ifajr", fStr);
                        editor.putString("isunset", sStr);
                        editor.putString("izohr", zStr);
                        editor.putString("iasr", aStr);
                        editor.putString("imaghrib", mStr);
                        editor.putString("iisha", iStr);
                        editor.putString("ifriday", frStr);

                        try {
                            fajrDate = (Date)formatter.parse(fStr);
                            sunriseDate = (Date)formatter.parse(sStr);
                            zuhrDate = (Date)formatter.parse(zStr);
                            asrDate = (Date)formatter.parse(aStr);
                            magribDate = (Date)formatter.parse(mStr);
                            ishaDate = (Date)formatter.parse(iStr);
                            fridayDate = (Date)formatter.parse(frStr);

                            Log.d("MA::Fajr",String.valueOf(fajrDate));
                            Log.d("MA::Fajr",String.valueOf(format.format(fajrDate)));
                            Log.d("MA::Sunrise",String.valueOf(sunriseDate));
                            Log.d("MA::Sunrise",String.valueOf(format.format(sunriseDate)));
                            Log.d("MA::Zuhr",String.valueOf(zuhrDate));
                            Log.d("MA::Zuhr",String.valueOf(format.format(zuhrDate)));
                            Log.d("MA::Asr",String.valueOf(asrDate));
                            Log.d("MA::Asr",String.valueOf(format.format(asrDate)));
                            Log.d("MA::Magrib",String.valueOf(magribDate));
                            Log.d("MA::Magrib",String.valueOf(format.format(magribDate)));
                            Log.d("MA::Isha",String.valueOf(ishaDate));
                            Log.d("MA::Isha",iStr);
                            Log.d("MA::Isha",String.valueOf(format.format(ishaDate)));
                            Log.d("MA::Friday",frStr);
                            Log.d("MA::Friday",String.valueOf(format.format(fridayDate)));
                            Log.d("Donation Text :: ", donation);

                            Calendar cal = Calendar.getInstance();

                            boolean friday = cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY;

                            Log.d("Is Friday :: ", friday + "");

                            editor.putString("mosquename", String.valueOf( found.get(0)));
                            editor.putString("fajr", format.format(fajrDate));
                            editor.putString("sunset", format.format(sunriseDate));
                            editor.putString("zohr", format.format(zuhrDate));
                            editor.putString("asr", format.format(asrDate));
                            editor.putString("maghrib", format.format(magribDate));
                            editor.putString("isha", format.format(ishaDate));
                            editor.putString("friday", format.format(fridayDate));
                            editor.putString("mosquecode",mosque_code);
                            editor.putString("donation",donation);

                            /**editor.putString("mosquename", String.valueOf( found.get(0)));
                            editor.putString("fajr", format.format(fajrDate));
                            editor.putString("sunset", format.format(sunriseDate));
                            editor.putString("zohr", format.format(zuhrDate));
                            editor.putString("asr", format.format(asrDate));
                            editor.putString("maghrib", format.format(magribDate));
                            editor.putString("isha", format.format(ishaDate));
                            editor.putString("mosquecode",mosque_code);*/

                            editor.apply();
                            editor.commit();

                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

                                    /**name.setText((CharSequence) found.get(0));
                                    fajr.setText(String.valueOf(format.format(fajrDate)));
                                    sunrise.setText(String.valueOf(format.format(sunriseDate)));
                                    zuhr.setText(String.valueOf(format.format(zuhrDate)));
                                    asr.setText(String.valueOf(format.format(asrDate)));
                                    magrib.setText(String.valueOf(format.format(magribDate)));
                                    isha.setText(String.valueOf(format.format(ishaDate)));*/

                                    FragmentManager fragmentManager = getSupportFragmentManager();
                                    fragmentManager.beginTransaction()
                                            .replace(R.id.fragment_timings, TimingsFragment.class, null, "tag")
                                            .setReorderingAllowed(true)
                                            .addToBackStack(null)
                                            .commit();

                                    TimingsFragment fragment = (TimingsFragment) fragmentManager.findFragmentByTag("timings");

                                }
                            });
                            Log.d("Love::2", "rows retrived " + numRows);
                            getApplicationContext().startService(new Intent(getApplicationContext(), PrayingDayCalculateHandler.class));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                Log.d("Love::1.", "My Love");
                                //getApplicationContext().startForegroundService(new Intent(getApplicationContext(), PrayingDayCalculateHandler.class));
                            }else {

                            }



                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            Toast.makeText(getApplicationContext(),"No Network",Toast.LENGTH_LONG);
                        }
                    });
                    e.printStackTrace();


                }


            }
        });
        thread.start();


        /**SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        name.setText(preferences.getString("mosquename", String.valueOf(found.get(0))));
        fajr.setText(preferences.getString("fajr", String.valueOf(found.get(2))));
        sunrise.setText(preferences.getString("sunset", String.valueOf(found.get(3))));
        zuhr.setText(preferences.getString("zohr", String.valueOf(found.get(4))));
        asr.setText(preferences.getString("asr", String.valueOf(found.get(5))));
        magrib.setText(preferences.getString("maghrib", String.valueOf(found.get(6))));
        isha.setText(preferences.getString("isha", String.valueOf(found.get(7))));*/
    }

    /**
     * Open a web page of a specified URL
     *Abdulmumin
     */
    public void addMosqueWebPage(MenuItem item) {
        Uri webpage = Uri.parse("https://almasjeed.com/add-mosques/");
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * Open a web page of a specified URL
     *Abdulmumin
     */
    public void updateMosqueTimeWebPage(MenuItem item) {
        Uri webpage = Uri.parse("https://almasjeed.com/mosque-prayer-time-update/");
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * Open a web page of a specified URL
     *Abdulmumin
     */
    public void shareAppWebPage(MenuItem item) {
        Uri webpage = Uri.parse("https://almasjeed.com/download/");
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    //Abdulmumin added

    public void insertMosqueCode() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Insert Mosque Code");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = preferences.edit();

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mosque_code = input.getText().toString();
                editor.putString("mosquecode",mosque_code);
                editor.apply();
                editor.commit();

                    fetchMosqueTimings();

                    


            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void showDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setCancelable(true);
        dialogBuilder.setMessage(getString(R.string.location_dialog_message));
        dialogBuilder.setTitle(android.R.string.dialog_alert_title);
        dialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        dialogBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //MosqueTimings.setTimings(null);// Abdulmumin include
                getLocation();
            }
        });

        dialog = dialogBuilder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, CompassActivity.class));
            return true;
        } else if (id == R.id.action_refresh_timings) {
            fetchMosqueTimings();
        } else if (id == R.id.action_add_mosque) {
            insertMosqueCode();
            return true;
        } /**else if (id == R.id.action_location) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_GPS_LOCATION);
            } else {
                showDialog();
                return true;
            }

        }*/ else if (id == R.id.action_convert_date) {
            new DataConvertPopup(this);
        } else if (id == R.id.settings) {
            startActivityForResult(new Intent(this, SettingsActivity.class), 16);
        } else if (id == R.id.mosques) {
            //check gps enable or not
            if (Validations.gpsEnabled(this)) {
                if (Validations.isNetworkAvailable(this)) {
                    startActivity(new Intent(this, MosquesActivity.class));
                }
            }

        } else if (id == R.id.worldpraye) {
            new CountryPrayerPopup(this, true, false);
       /** } else if (id == R.id.action_rate_app) {
            String url = "https://play.google.com/store/apps/details?id=com.captech.muslimutility";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);*/
        } else if (id == R.id.action_about_app) {
            //start about activity
            startActivity(new Intent(this, AboutActivity.class));
        } else if (id == R.id.action_share) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "#" + getString(R.string.app_name) + "\n https://almasjeed.com/download/");
            startActivity(Intent.createChooser(sharingIntent, "Share using"));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_GPS_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    finish();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Validations.REQUEST_CODE && resultCode == 0) {
            getLocation();
        } else if (requestCode == 16) {
        }

    }

    public void getLocation() {
        if (Validations.gpsEnabledInLocation(this, true, true)) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.detecting_location));
            progressDialog.show();
            gps = new FusedLocationService(this, this);
        }
    }

    Location currLocation = null;

    @Override
    public void onLocationChanged(Location location) {
        if (location != null && currLocation == null) {
            currLocation = location;
            gps.setFusedLatitude(location.getLatitude());
            gps.setFusedLongitude(location.getLongitude());
            if (gps.getFusedLatitude() != 0 && gps.getFusedLongitude() != 0) {
                LocationInfo locationInfo = dbManager.getLocationInfo((float) gps.getFusedLatitude(), (float) gps.getFusedLongitude());
                Calendar calendar = Calendar.getInstance();
                LocationReader lr = new LocationReader(this);
                lr.read(gps.getFusedLatitude(), gps.getFusedLongitude());
                int dst = calendar.getTimeZone().getDSTSavings();
                locationInfo.dls = dst;
                switch (PrayerTimes.getDefaultMazhab(manager.getSimCountryIso().toUpperCase())) {
                    case PTC_MAZHAB_HANAFI:
                        locationInfo.mazhab = 1;
                        break;
                    case PTC_MAZHAB_SHAFEI:
                        locationInfo.mazhab = 0;
                        break;
                }
                switch (PrayerTimes.getDefaultWay(manager.getSimCountryIso().toUpperCase())) {
                    case PTC_WAY_EGYPT:
                        locationInfo.way = 0;
                        break;
                    case PTC_WAY_UMQURA:
                        locationInfo.way = 3;
                        break;

                    case PTC_WAY_MWL:
                        locationInfo.way = 4;
                        break;

                    case PTC_WAY_KARACHI:
                        locationInfo.way = 1;
                        break;

                    case PTC_WAY_ISNA:
                        locationInfo.way = 2;
                        break;
                }
                ConfigPreferences.setLocationConfig(this, locationInfo);
                ConfigPreferences.setQuibla(this, (int) QuiblaCalculator.doCalculate((float) location.getLatitude(), (float) location.getLongitude()));
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("mazhab", locationInfo.mazhab + ""); // value to store
                editor.putString("calculations", locationInfo.way + "");
                editor.commit();
                progressDialog.cancel();
                gps.stopFusedLocation();
                Intent intent = getIntent();
                sendBroadcast(new Intent().setAction("prayer.information.change"));
                finish();
                startActivity(intent);
                ConfigPreferences.setPrayingNotification(this, true);
                Alarms.startCalculatePrayingBroadcast(this);
            }
        } else {
            new CountryPrayerPopup(this, true, true);
        }
    }


    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    //return new PrayingFragment();
                    return new TimingsFragment();
                case 1:
                    return new CalendarFragment();
                case 2:
                    return new AzkarFragment();
                case 3:
                    return new IslamicEventsFragment();
                default:
                    return new WeatherFragment();
            }
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.praying_tab);
                case 1:
                    return getString(R.string.calender_tab);
                case 2:
                    return getString(R.string.azkar_tab);
                case 3:
                    return getString(R.string.islamic_tab);
                case 4:
                    return getString(R.string.weather_tab);

            }
            return null;
        }
    }

    private class AzkarTypes extends AsyncTask<Void, Void, List<ZekerType>> {

        @Override
        protected List<ZekerType> doInBackground(Void... voids) {
            zekerTypeList = new ArrayList<>();
            return dbManager.getAllAzkarTypes();
        }

        @Override
        protected void onPostExecute(List<ZekerType> zekerTypes) {
            super.onPostExecute(zekerTypes);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        prayingFragment = new PrayingFragment();
        timingsFragment = new TimingsFragment();
        calendarFragment = new CalendarFragment();
        azkarFragment = new AzkarFragment();
        islamicEventsFragment = new IslamicEventsFragment();
        weatherFragment = new WeatherFragment();
       // adapter.addFragment(prayingFragment, "Azan");
        adapter.addFragment(timingsFragment, "Azan");
        adapter.addFragment(calendarFragment, "Calendar");
        adapter.addFragment(azkarFragment, "Azkar");
        adapter.addFragment(islamicEventsFragment, "Events");
        //adapter.addFragment(weatherFragment, "Weather");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
        //tabLayout.getTabAt(4).setIcon(tabIcons[4]);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gps_loc = null;
        network_loc = null;
    }

}


