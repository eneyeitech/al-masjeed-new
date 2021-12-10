package captech.muslimutility.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import captech.muslimutility.Manager.DBManager;
import captech.muslimutility.R;
import captech.muslimutility.calculator.calendar.HGDate;
import captech.muslimutility.calculator.location.LocationTracker;
import captech.muslimutility.database.ConfigPreferences;
import captech.muslimutility.model.City;
import captech.muslimutility.model.Country;
import captech.muslimutility.ui.fragments.MapFragment;

public class SelectPositionActivity extends AppCompatActivity {

    DBManager dbManager;
    SearchableSpinner countrySp, citySp;
    Button currPosBtn, setPosManualBtn, okBtn, cancelBtn;
    Context context;
    LocationTracker tracker;

    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private FrameLayout mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private String[] countries, cities;
    private List<City> cityList;
    private ProgressDialog progressDialog;
    private AlertDialog dialog;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        tracker = new LocationTracker(context);

        setContentView(R.layout.activity_select_position);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = (FrameLayout) findViewById(R.id.fullscreen_content);

        dbManager = new DBManager(this);
        dbManager.open();
        try {
            dbManager.copyDataBase();
        }catch (IOException e){

        }

        MapFragment frag = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_frag);
        if (frag != null && mVisible) {
            frag.getView().setOnTouchListener(mDelayHideTouchListener);
        }

        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        setupViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("RestrictedApi")
    private void setupViews() {


        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);

        setPosManualBtn = (Button) findViewById(R.id.get_position_manual);
        setPosManualBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        cancelBtn = (Button) findViewById(R.id.btn_current_cancel);
        okBtn = (Button) findViewById(R.id.btn_current_ok);
        currPosBtn = (Button) findViewById(R.id.btn_current_position);
        countrySp = (SearchableSpinner) findViewById(R.id.sp_country);
        citySp = (SearchableSpinner) findViewById(R.id.sp_city);

        countrySp.setTitle(getString(R.string.select_country));
        citySp.setTitle(getString(R.string.select_city));

        countrySp.setPositiveButton(getString(R.string.close));
        citySp.setPositiveButton(getString(R.string.close));

        addItemToSpinner();

        currPosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (tracker != null && tracker.hasLocation() && tracker.getLocation() != null) {
                    showDialog(new LatLng(tracker.getLocationLatitude(), tracker.getLocationLongitude()));
                } else {
                    Toast.makeText(context
                            , "Cannot detect location right now , please use manual detection"
                            , Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void addItemToSpinner() {
        final List<Country> countriesList = dbManager.getAllCountries();
        List<String> countryNamesArray = new ArrayList<>();
        List<String> countryArabicNamesArray = new ArrayList<>();
        final List<String> countriesID = new ArrayList<>();
        for (Country countryItem : countriesList) {
            countryNamesArray.add(countryItem.countryName);
            countryArabicNamesArray.add(countryItem.countryArabicName);
            countriesID.add(countryItem.countryShortCut);
        }

        //show arabic and english names of languages
        if (ConfigPreferences.getApplicationLanguage(context).equals("ar")) {
            countries = countryArabicNamesArray.toArray(new String[countryArabicNamesArray.size()]);
        } else {
            countries = countryNamesArray.toArray(new String[countryNamesArray.size()]);
        }

        //spinner adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                R.layout.spinner_view, countries);
        countrySp.setAdapter(adapter);

        //on change new item from spinner
        countrySp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String code = countriesID.get(position);
                cityList = dbManager.getAllCities(code);
                List<String> cityNames = new ArrayList<String>();
                List<String> cityArabicNames = new ArrayList<String>();
                for (City city : cityList) {
                    cityNames.add(city.Name);
                    cityArabicNames.add(city.arabicName == null ? city.Name : city.arabicName);
                }
                if (ConfigPreferences.getApplicationLanguage(context).equals("ar")) {
                    cities = cityArabicNames.toArray(new String[cityArabicNames.size()]);
                } else {
                    cities = cityNames.toArray(new String[cityNames.size()]);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                        R.layout.spinner_view, cities);
                citySp.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();

                final float lat = cityList.get(citySp.getSelectedItemPosition()).Lat;
                final float lon = cityList.get(citySp.getSelectedItemPosition()).lon;
                showDialog();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HGDate hgDate = new HGDate();
                        hgDate.toHigri();
                        ConfigPreferences.setWorldPrayerCountry(context, dbManager.getLocationInfo(lat, lon));
                        context.startActivity(new Intent(context, PrayShowActivity.class).putExtra("date", hgDate.getDay() + "-" + hgDate.getMonth() + "-" + hgDate.getYear() + "- 0"));
                        finish();
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                }).start();
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        delayedHide(0);
    }

    private void toggle() {
        if (mVisible) {
            hide();
            if (setPosManualBtn != null) {
                setPosManualBtn.setVisibility(View.VISIBLE);
            }
        } else {
            show();
            if (setPosManualBtn != null) {
                setPosManualBtn.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void showDialog(final LatLng latLng) {
        //start progress dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setCancelable(true);
        dialogBuilder.setMessage(getCompleteAddressString(latLng.latitude, latLng.longitude));
        dialogBuilder.setTitle("Is this a correct address ?");
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
                final float lat = (float) latLng.latitude;
                final float lon = (float) latLng.longitude;
                showDialog();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HGDate hgDate = new HGDate();
                        hgDate.toHigri();
                        ConfigPreferences.setWorldPrayerCountry(context, dbManager.getLocationInfo(lat, lon));
                        context.startActivity(new Intent(context, PrayShowActivity.class).putExtra("date", hgDate.getDay() + "-" + hgDate.getMonth() + "-" + hgDate.getYear() + "- 0"));
                        ((Activity) context).finish();
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                }).start();
            }
        });
        dialog = dialogBuilder.show();
    }

    @SuppressLint("LongLogTag")
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction address", "" + strReturnedAddress.toString());
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

    public void showDialog() {
        //start progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
    }

    private void hide() {
        if (actionBar != null) {
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        mVisible = true;

        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
