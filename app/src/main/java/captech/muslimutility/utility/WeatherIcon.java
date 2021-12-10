package captech.muslimutility.utility;

import captech.muslimutility.R;

public class WeatherIcon {

    public static int get_icon_id_white(String icon) {
        //clear sky
        if (icon.equals("01d"))
            return R.drawable.w_day_clear_w;
        else if (icon.equals("01n"))
            return R.drawable.w_night_clear_w;
            //few clouds
        else if (icon.equals("02d"))
            return R.drawable.w_day_cloud_w;
        else if (icon.equals("02n"))
            return R.drawable.w_night_cloud_w;
            //scattered clouds
        else if (icon.equals("03d") || icon.equals("03n") || icon.equals("04d") || icon.equals("04n"))
            return R.drawable.w_cloud_w;
        else if (icon.equals("09d") || icon.equals("09n"))
            return R.drawable.w_rain_w;
            //rain
        else if (icon.equals("10d"))
            return R.drawable.w_day_rain_w;
        else if (icon.equals("10n"))
            return R.drawable.w_night_rain_w;
            //thunderstorm
        else if (icon.equals("11d") || icon.equals("11n"))
            return R.drawable.w_thunder_w;
            //snow
        else if (icon.equals("13d") || icon.equals("13n"))
            return R.drawable.w_snow_w;
            //mist
        else if (icon.equals("50d") || icon.equals("50n"))
            return R.drawable.w_mist_w;

        return R.drawable.w_cloud_w;

    }

}
