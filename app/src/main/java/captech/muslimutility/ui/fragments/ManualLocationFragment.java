package captech.muslimutility.ui.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import captech.muslimutility.Manager.DBManager;
import captech.muslimutility.R;
import captech.muslimutility.calculator.calendar.HGDate;
import captech.muslimutility.database.ConfigPreferences;
import captech.muslimutility.model.City;
import captech.muslimutility.model.Country;
import captech.muslimutility.service.DetectLocationManualListener;
import captech.muslimutility.ui.activity.PrayShowActivity;

public class ManualLocationFragment extends Fragment {

    View v;
    Context context;
    SearchableSpinner countrySp , citySp;
    Button okBtn , cancelBtn;
    private String[] countries, cities;
    private List<City> cityList;
    DBManager dbManager;
    private ProgressDialog progressDialog;
    DetectLocationManualListener listener;

    private LatLng latLng;
    public void addListener(DetectLocationManualListener listener){
        this.listener = listener;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_select_location_manual , container , false);
        context = getActivity();
        latLng = new LatLng(0 , 0);

        dbManager = new DBManager(getActivity());
        dbManager.open();
        try {
            dbManager.copyDataBase();
        }catch (IOException e){

        }

        setupViews();

        return v;
    }

    private void setupViews() {

        cancelBtn = (Button) v.findViewById(R.id.btn_current_cancel);
        okBtn = (Button) v.findViewById(R.id.btn_current_ok);
        countrySp = (SearchableSpinner) v.findViewById(R.id.sp_country);
        citySp = (SearchableSpinner) v.findViewById(R.id.sp_city);

        countrySp.setTitle(getString(R.string.select_country));
        citySp.setTitle(getString(R.string.select_city));

        countrySp.setPositiveButton(getString(R.string.close));
        citySp.setPositiveButton(getString(R.string.close));

        addItemToSpinner();

    }

    public void addItemToSpinner(){
        final List<Country> countriesList = dbManager.getAllCountries();
        List<String> countryNamesArray = new ArrayList<>();
        List<String> countryArabicNamesArray = new ArrayList<>();
        final List<String> countriesID = new ArrayList<>();
        for (Country countryItem : countriesList) {
            countryNamesArray.add(countryItem.countryName);
            countryArabicNamesArray.add(countryItem.countryArabicName);
            countriesID.add(countryItem.countryShortCut);
        }

        if (ConfigPreferences.getApplicationLanguage(context).equals("ar")) {
            countries = countryArabicNamesArray.toArray(new String[countryArabicNamesArray.size()]);
        } else {
            countries = countryNamesArray.toArray(new String[countryNamesArray.size()]);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                R.layout.spinner_view, countries);
        countrySp.setAdapter(adapter);


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
                addLATLNG();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        citySp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                addLATLNG();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final float lat= cityList.get(citySp.getSelectedItemPosition()).Lat;
                final float lon = cityList.get(citySp.getSelectedItemPosition()).lon;
                showDialog();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HGDate hgDate = new HGDate();
                        hgDate.toHigri();
                        ConfigPreferences.setWorldPrayerCountry(context, dbManager.getLocationInfo(lat, lon));
                        context.startActivity(new Intent(context, PrayShowActivity.class).putExtra("date", hgDate.getDay() + "-" + hgDate.getMonth() + "-" + hgDate.getYear() + "- 0"));
                        ((Activity)context).finish();
                        if (progressDialog != null){
                            progressDialog.dismiss();
                        }
                    }
                }).start();
            }
        });
    }

    private void addLATLNG() {
        double lat= cityList.get(citySp.getSelectedItemPosition()).Lat;
        double lon = cityList.get(citySp.getSelectedItemPosition()).lon;
        if (listener != null) {
            listener.onDetectLocationManualListener(new LatLng(lat, lon));
        }
    }

    public void showDialog(){
        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
    }
}
