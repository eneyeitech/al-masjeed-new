package captech.muslimutility.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import captech.muslimutility.Manager.DBManager;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;
import captech.muslimutility.R;
import captech.muslimutility.calculator.calendar.HGDate;
import captech.muslimutility.calculator.quibla.QuiblaCalculator;
import captech.muslimutility.database.ConfigPreferences;
import captech.muslimutility.model.FragsInfo;
import captech.muslimutility.model.LocationInfo;
import captech.muslimutility.service.DetectLocationListener;
import captech.muslimutility.service.DetectLocationManualListener;
import captech.muslimutility.ui.fragments.ManualLocationFragment;
import captech.muslimutility.ui.fragments.MapFragment;
import captech.muslimutility.utility.Alarms;

public class SelectLocationTabsActivity extends AppCompatActivity implements MaterialTabListener, DetectLocationListener, DetectLocationManualListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private List<FragsInfo> frags;

    Button selectPlaceBtn;
    ImageButton tabSelectorBtn;

    private MaterialTabHost tabHost;
    private ActionBar actionBar;
    private LocationManager locationManager;
    Context context;
    DBManager dbManager;
    private ProgressDialog progressDialog;
    private AlertDialog.Builder dialogBuilder;
    private Dialog dialog;
    private LatLng latLng , latLngManual;
    private boolean isManual = true;
    private String address;
    private boolean isFromLocationBtn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        if (getIntent().hasExtra("IsFromLocationBtn")) {
            isFromLocationBtn = getIntent().getExtras().getBoolean("IsFromLocationBtn",false);
            Log.i("IsFromLocationBtn" , "yes "+isFromLocationBtn);
        } else {
            throw new IllegalArgumentException("Activity cannot find  extras " + "IsFromLocationBtn");
        }

        setContentView(R.layout.activity_select_location_tabs);

        dbManager = new DBManager(this);
        dbManager.open();
        try {
            dbManager.copyDataBase();
        }catch (IOException e){

        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        setupViews();
    }


    @SuppressLint("RestrictedApi")
    public void setupViews(){

        tabSelectorBtn = (ImageButton) findViewById(R.id.tab_img_btn);
        tabSelectorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleTabs();
            }
        });
        selectPlaceBtn = (Button) findViewById(R.id.select_place_btn);
        selectPlaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetAddressAsync async = new GetAddressAsync();
                if (isManual){
                    showData(latLngManual);
                }else {
                    async.execute(latLng);
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        frags = new LinkedList<>();
        if (isInternetOn(context)){
            isManual = false;
            tabSelectorBtn.setVisibility(View.VISIBLE);
            MapFragment fragment = new MapFragment();
            fragment.addListener(this);
            frags.add(new FragsInfo("Map" , fragment ));
        }else{
            isManual = true;
            tabSelectorBtn.setVisibility(View.GONE);
        }

        ManualLocationFragment fragment = new ManualLocationFragment();
        fragment.addListener(this);
        frags.add(new FragsInfo("Manual",fragment));

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager() , frags);
        tabHost = (MaterialTabHost) this.findViewById(R.id.tabs);

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.beginFakeDrag();

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onPageSelected(int position) {
                tabHost.setSelectedNavigationItem(position);
                switch (position){
                    case 0:
                        isManual = false;
                        tabSelectorBtn.setImageDrawable(getDrawable(R.mipmap.ic_manual_tab));
                        break;

                    case 1:
                        isManual = true;
                        tabSelectorBtn.setImageDrawable(getDrawable(R.mipmap.ic_map_tab));
                        break;
                }
            }
        });

        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            tabHost.addTab(
                    tabHost.newTab()
                            .setText(frags.get(i).getName())
                            .setTabListener(this)
            );
        }

    }

    private void toggleTabs() {
        if (isManual){
            mViewPager.setCurrentItem(0);
            isManual = false;
        }else{
            mViewPager.setCurrentItem(1);
            isManual = true;
        }
    }

    public static boolean isInternetOn(Context context) {
        boolean isMobile = false, isWifi = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] infoAvailableNetworks = cm.getAllNetworkInfo();

        if (infoAvailableNetworks != null) {
            for (NetworkInfo network : infoAvailableNetworks) {

                if (network.getType() == ConnectivityManager.TYPE_WIFI) {
                    if (network.isConnected() && network.isAvailable())
                        isWifi = true;
                }
                if (network.getType() == ConnectivityManager.TYPE_MOBILE) {
                    if (network.isConnected() && network.isAvailable())
                        isMobile = true;
                }
            }
        }

        return isMobile || isWifi;

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


    @Override
    public void onTabSelected(MaterialTab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab tab) {

    }

    @Override
    public void onTabUnselected(MaterialTab tab) {

    }

    @Override
    public void onDetectLocationListener(LatLng latLng) {
        this.latLng = latLng;
    }

    @Override
    public void onDetectLocationManualListener(LatLng latLng) {

        this.latLngManual = latLng;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        List<FragsInfo> frags;
        public SectionsPagerAdapter(FragmentManager fm , List<FragsInfo> frags) {
            super(fm);
            this.frags = frags;
        }

        @Override
        public Fragment getItem(int position) {
            return frags.get(position).getFrag();
        }

        @Override
        public int getCount() {
            return frags.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return frags.get(position).getName();
        }
    }

    public class GetAddressAsync extends AsyncTask<LatLng, Void, Void> {
        @Override
        protected Void doInBackground(LatLng... latLngs) {
            showDialog(latLngs[0]);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            if (dialogBuilder != null) {
                dialog = dialogBuilder.show();
            }

            if (address == null || address.isEmpty()){
                if (dialog != null) {
                    dialog.dismiss();
                }
                showData(latLng);
            }
        }
    }

    public void showDialog(final LatLng latLng){
        address = null;
        dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setCancelable(true);
        address = getCompleteAddressString(latLng.latitude , latLng.longitude);
        dialogBuilder.setMessage(address);
        dialogBuilder.setTitle(R.string.is_this_correct_address);
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
                showData(latLng);
            }
        });

    }

    public void showData(LatLng latLng){
        if (latLng != null) {
            final float lat = (float) latLng.latitude;
            final float lon = (float) latLng.longitude;
            Toast.makeText(context, "lat : " + lat + "lng : " + lon, Toast.LENGTH_LONG).show();
            showDialog();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HGDate hgDate = new HGDate();
                    hgDate.toHigri();
                    ConfigPreferences.setWorldPrayerCountry(context, dbManager.getLocationInfo(lat, lon));
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    if (!isFromLocationBtn) {
                        context.startActivity(new Intent(context, PrayShowActivity.class).putExtra("date", hgDate.getDay() + "-" + hgDate.getMonth() + "-" + hgDate.getYear() + "- 0"));
                        ((Activity) context).finish();
                    }else{
                        LocationInfo locationInfo = dbManager.getLocationInfo(lat, lon);
                        ConfigPreferences.setLocationConfig(context, locationInfo);
                        ConfigPreferences.setQuibla(context, (int) QuiblaCalculator.doCalculate(lat,lon));
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("mazhab", locationInfo.mazhab + ""); // value to store
                        editor.putString("calculations", locationInfo.way + "");
                        editor.commit();
                        Intent intent = new Intent(context , MainActivity.class);
                        sendBroadcast(new Intent().setAction("prayer.information.change"));
                        ((Activity) context).finish();
                        startActivity(intent);
                        ConfigPreferences.setPrayingNotification(context, true);
                        Alarms.startCalculatePrayingBroadcast(context);
                    }

                }
            }).start();
        }
    }


    public void showDialog(){
        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
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

}