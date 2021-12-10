package captech.muslimutility.ui.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import captech.muslimutility.R;
import captech.muslimutility.SharedData.SharedDataClass;
import captech.muslimutility.adapter.WeatherAdapter;
import captech.muslimutility.database.ConfigPreferences;
import captech.muslimutility.model.LocationInfo;
import captech.muslimutility.model.Weather;
import captech.muslimutility.model.WeatherSave;
import captech.muslimutility.utility.NumbersLocal;
import captech.muslimutility.utility.WeatherIcon;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherFragment extends Fragment {
    final String sampleurl = "http://api.openweathermap.org/data/2.5/forecast?lat="+SharedDataClass.latitude+"&lon="+
            SharedDataClass.longitude+"&lang=en&appid=ac6f2688dbfdc24772be777529947e27";
    private final String URL = "http://api.openweathermap.org/data/2.5/forecast?";
    private final String API_ID = "&appid=ac6f2688dbfdc24772be777529947e27";
    private RecyclerView weatherRecyclerView;
    List<captech.muslimutility.model.Weather> weathers = new ArrayList<>();
    private WeatherAdapter adapter;
    private List<captech.muslimutility.model.Weather> weatherList;
    private TextView today, todayDescription, dayA, humidity, windSpeed,
            dayB, dayC, dayD, dayA_temp, dayB_temp, dayC_temp, dayD_temp,
            locationName;
    private ImageView imageToday, dayA_Image, dayB_Image, dayC_Image, dayD_image, refresh;
    private ProgressBar seeking;
    private boolean firstEntry = true;
    private Snackbar snackbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        init(view);
        ViewAllServiceCenters();
        return view;

    }

    private void init(View view) {
        //init views in the weather view
        today = (TextView) view.findViewById(R.id.textView29);
        todayDescription = (TextView) view.findViewById(R.id.textView20);
        imageToday = (ImageView) view.findViewById(R.id.imageView6);
        humidity = (TextView) view.findViewById(R.id.humidity);
        windSpeed = (TextView) view.findViewById(R.id.windSpeed);
        seeking = (ProgressBar) view.findViewById(R.id.seeking);
        refresh = (ImageView) view.findViewById(R.id.refresh);
        dayA = (TextView) view.findViewById(R.id.day1);
        dayB = (TextView) view.findViewById(R.id.day2);
        dayC = (TextView) view.findViewById(R.id.day3);
        dayD = (TextView) view.findViewById(R.id.day4);
        locationName = (TextView) view.findViewById(R.id.tv_city_name);
        dayA_temp = (TextView) view.findViewById(R.id.day1Temp);
        dayB_temp = (TextView) view.findViewById(R.id.day2temp);
        dayC_temp = (TextView) view.findViewById(R.id.day3temp);
        dayD_temp = (TextView) view.findViewById(R.id.day4temp);
        dayA_Image = (ImageView) view.findViewById(R.id.day1Imgae);
        dayB_Image = (ImageView) view.findViewById(R.id.day2Image);
        dayC_Image = (ImageView) view.findViewById(R.id.day3Image);
        dayD_image = (ImageView) view.findViewById(R.id.day4Image);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new WeatherUpdate(true).execute();
            }
        });

        //Recycler view init
        weatherList = new ArrayList<>();
        adapter = new WeatherAdapter(weatherList, getContext());
        weatherRecyclerView = (RecyclerView) view.findViewById(R.id.weather);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        weatherRecyclerView.setLayoutManager(layoutManager);
        weatherRecyclerView.setItemAnimator(new DefaultItemAnimator());
        weatherRecyclerView.setAdapter(adapter);
        LocationInfo locationInfo = ConfigPreferences.getLocationConfig(getContext());
        if (locationInfo != null) {
            locationName.setText(getString(R.string.near) + " " + (ConfigPreferences.getApplicationLanguage(getContext()).equals("en") ? locationInfo.city : locationInfo.city_ar));
        }

        //load offline saved weather data if exists
        WeatherSave weatherSave = ConfigPreferences.getTodayListWeather(getContext());
        if (weatherSave != null && weatherSave.weathers.size() != 0) {
            today.setText(NumbersLocal.convertNumberType(getContext(), weatherSave.weathers.get(0).tempMini + "°"));
            humidity.setText(NumbersLocal.convertNumberType(getContext(), weatherSave.weathers.get(0).humidity) + " %");
            windSpeed.setText(NumbersLocal.convertNumberType(getContext(), weatherSave.weathers.get(0).windSpeed + " " + getString(R.string.wind_meager)));
            todayDescription.setText(weatherSave.weathers.get(0).desc);
            imageToday.setImageResource(WeatherIcon.get_icon_id_white(weatherSave.weathers.get(0).image));
            weatherList.addAll(weatherSave.weathers);
            adapter.notifyDataSetChanged();
            WeatherSave weekWeather = ConfigPreferences.getWeekListWeather(getContext());
            if (weekWeather.weathers.size() > 0) {
                showDate(weekWeather.weathers, 0, dayA_temp, dayA_Image, dayA);
                showDate(weekWeather.weathers, 1, dayB_temp, dayB_Image, dayB);
                showDate(weekWeather.weathers, 2, dayC_temp, dayC_Image, dayC);
                showDate(weekWeather.weathers, 3, dayD_temp, dayD_image, dayD);
            }
        }

        //call weather api to get new weather
        new WeatherUpdate(false).execute();

    }

    private class WeatherUpdate extends AsyncTask<Void, Void, List<Weather>> {

        private boolean showSnackbar;

        public WeatherUpdate(boolean showSnackbar) {
            this.showSnackbar = showSnackbar;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            seeking.setVisibility(View.VISIBLE);
            refresh.setVisibility(View.GONE);
        }

        @Override
        protected List<Weather> doInBackground(Void... voids) {
            try {
                ConfigPreferences.getLocationConfig(getContext());
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url("http://api.openweathermap.org/data/2.5/forecast?lat=33.568727&lon=73.024699&lang=en&appid=ac6f2688dbfdc24772be777529947e27").build();

                Log.i("URL_WITHER" , URL + "lat=" + 33.568727 + "&lon=" + 73.024699+"&lang="+ Locale.getDefault().getLanguage() + API_ID);

                Response response = client.newCall(request).execute();
                String jsonData = response.body().string();
                if (jsonData != null) {
                    JSONObject Jobject = new JSONObject(jsonData);
                    JSONArray Jarray = Jobject.getJSONArray("list");
                    for (int i = 0; i < Jarray.length(); i++) {
                        JSONObject object = Jarray.getJSONObject(i);
                        JSONObject main = object.getJSONObject("main");
                        JSONArray weather = object.getJSONArray("weather");
                        String desc = weather.getJSONObject(0).getString("description");
                        Log.i("URL_WITHER" , "desc : "+desc);
                        String icon = weather.getJSONObject(0).getString("icon");
                        String date = object.getString("dt_txt");
                        String temp = main.getString("temp");
                        String temp_min = main.getString("temp_min");
                        String temp_max = main.getString("temp_max");
                        String humidity = main.getString("humidity");
                        JSONObject wind = object.getJSONObject("wind");
                        String windSpeed = wind.getString("speed");

                        weathers.add(new captech.muslimutility.model.Weather
                                (date, Math.round(Float.valueOf(temp) - 272.15f) + "",
                                        Math.round(Float.valueOf(temp_min) - 272.15f) + "",
                                        Math.round(Float.valueOf(temp_max) - 272.15f) + "",
                                        icon, desc, humidity, windSpeed));
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return weathers;
        }

        @Override
        protected void onPostExecute(List<Weather> weathers) {
            super.onPostExecute(weathers);
            try {
                seeking.setVisibility(View.GONE);
                refresh.setVisibility(View.VISIBLE);

                if (showSnackbar) {
                    snackbar = Snackbar
                            .make(refresh, R.string.weather_updated, Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                        }
                    });
                    View snackView = snackbar.getView();
                    Button snackViewButton = (Button) snackView.findViewById(R.id.snackbar_action);
                    snackViewButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.watermelon));
                    snackbar.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            snackbar.dismiss();
                        }
                    } , 30000);
                }
                List<Weather> todayList = new ArrayList<>();
                List<Weather> allDays = new ArrayList<>();
                String previousDayName = "";
                int min = 0;
                int max = 0;
                String weatherIcon = "";
                for (captech.muslimutility.model.Weather weather : weathers) {
                    String[] splits = weather.dayName.split(" ");
                    if (NumbersLocal.convertNumberTypeToEnglish(getContext(), getDataNow()).equals(splits[0].trim())) {

                        todayList.add(weather);
                    } else {
                        if (firstEntry) {
                            previousDayName = splits[0].trim();
                            firstEntry = false;
                        }
                        //degrees of the week
                        if (splits[0].trim().equals(previousDayName)) {
                            if (weather.dayName.contains("12:00:00")) {
                                max = Integer.parseInt(weather.tempMini);
                                weatherIcon = weather.image;
                            } else if (weather.dayName.contains("21:00:00")) {
                                min = Integer.parseInt(weather.tempMini);
                            }
                            //check to add day
                            if (min != 0 && max != 0) {
                                weather.tempMax = max + "";
                                weather.tempMini = min + "";
                                allDays.add(new Weather(previousDayName,
                                        min + "",
                                        max + "",
                                        weatherIcon));
                                max = min = 0;
                            }
                        } else {
                            previousDayName = splits[0].trim();
                            max = min = 0;
                        }
                    }
                }

                //update or add weather of the week
                if (todayList.size() > 0) {
                    weatherList.clear();
                    ConfigPreferences.setTodayListWeather(getActivity(), todayList);
                    weatherList.addAll(todayList);
                    adapter.notifyDataSetChanged();
                    WeatherSave weatherSave = ConfigPreferences.getTodayListWeather(getContext());
                    today.setText(NumbersLocal.convertNumberType(getContext(), weatherSave.weathers.get(0).tempMini + "°"));
                    humidity.setText(NumbersLocal.convertNumberType(getContext(), weatherSave.weathers.get(0).humidity) + " %");
                    windSpeed.setText(NumbersLocal.convertNumberType(getContext(), weatherSave.weathers.get(0).windSpeed + " " + getString(R.string.wind_meager)));
                    todayDescription.setText(weatherSave.weathers.get(0).desc);
                    imageToday.setImageResource(WeatherIcon.get_icon_id_white(weatherSave.weathers.get(0).image));
                }

                //show and save locally  weather of the week
                if (allDays.size() > 0) {
                    ConfigPreferences.setWeekListWeather(getActivity(), allDays);
                    showDate(allDays, 0, dayA_temp, dayA_Image, dayA);
                    showDate(allDays, 1, dayB_temp, dayB_Image, dayB);
                    showDate(allDays, 2, dayC_temp, dayC_Image, dayC);
                    showDate(allDays, 3, dayD_temp, dayD_image, dayD);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    public void ViewAllServiceCenters() {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        weathers.clear();
        JsonObjectRequest req2 = new JsonObjectRequest(sampleurl, null, new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray arry = response.getJSONArray("list");
                    for (int i = 0; i < arry.length(); i++) {
                        JSONObject object = arry.getJSONObject(i);
                        JSONObject main = object.getJSONObject("main");
                        JSONArray weather = object.getJSONArray("weather");
                        String desc = weather.getJSONObject(0).getString("description");
                        Log.i("URL_WITHER" , "desc : "+desc);
                        String icon = weather.getJSONObject(0).getString("icon");
                        String date = object.getString("dt_txt");
                        String temp = main.getString("temp");
                        String temp_min = main.getString("temp_min");
                        String temp_max = main.getString("temp_max");
                        String humidity = main.getString("humidity");
                        JSONObject wind = object.getJSONObject("wind");
                        String windSpeed = wind.getString("speed");

                        weathers.add(new captech.muslimutility.model.Weather
                                (date, Math.round(Float.valueOf(temp) - 272.15f) + "",
                                        Math.round(Float.valueOf(temp_min) - 272.15f) + "",
                                        Math.round(Float.valueOf(temp_max) - 272.15f) + "",
                                        icon, desc, humidity, windSpeed));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(req2);
    }

    public String getDataNow() {

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(c.getTime());
    }

    public void showDate(List<captech.muslimutility.model.Weather> weathers, int position
            , TextView temp, ImageView image, TextView day) {
        try {
            captech.muslimutility.model.Weather weather = weathers.get(position);
            String[] time = weather.dayName.split(" ");
            String[] date = time[0].split("-");
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
            Date d = new Date();
            d.setYear(Integer.parseInt(date[0]));
            d.setMonth(Integer.parseInt(date[1]) - 1);
            d.setDate(Integer.parseInt(date[2]) - 1);
            String dayOfTheWeek = sdf.format(d);
            day.setText(dayOfTheWeek);
            image.setImageResource(WeatherIcon.get_icon_id_white(weather.image));
            temp.setText(NumbersLocal.convertNumberType(getContext(), "°" + weather.tempMax + " | " + weather.tempMini + "°"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
