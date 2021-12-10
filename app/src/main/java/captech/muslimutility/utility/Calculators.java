package captech.muslimutility.utility;

import android.content.Context;

import captech.muslimutility.R;
import captech.muslimutility.database.ConfigPreferences;

public class Calculators {

    public static int extractMinutes(double time) {

        String preparedNumber = (time + "").substring(0, 3);
        double timeDouble = new Double(time);
        int hour = (int) timeDouble;
        double minsDouble = (60 * (timeDouble - hour)) / 100;

        preparedNumber = (minsDouble + "").substring(0, (minsDouble + "").length() < 4 ? 3 : 4);
        double mins = new Double(preparedNumber);
        String minsFinal = String.valueOf(mins).replace("0.", "");

        return Integer.parseInt((minsFinal.length() == 1 ? minsFinal + "0" : minsFinal).trim()) ;

    }

    public static int extractHour(double time) {
        String preparedNumber = (time + "").substring(0, 3);
        double timeDouble = Double.parseDouble(preparedNumber);
        return (int) timeDouble;
    }

    public static String extractPrayTime(Context context, double time) {

        boolean pmFlag = false;
        String preparedNumber = (time + "").substring(0, 3);
        double timeDouble = new Double(time);
        int hour = (int) timeDouble;
        double minsDouble = (60 * (timeDouble - hour)) / 100;

        preparedNumber = (minsDouble + "").substring(0, (minsDouble + "").length() < 4 ? 3 : 4);
        double mins = new Double(preparedNumber);
        String minsFinal = String.valueOf(mins).replace("0.", "");
        if (ConfigPreferences.getTwentyFourMode(context) != true) {
            if (hour > 12) {
                hour -= 12;
                pmFlag = true;
            }
        }
        return NumbersLocal.convertNumberType(context, hour + ":" +
                (minsFinal.length() == 1 ? minsFinal + "0" : minsFinal) + " " +
                ((hour >= 12 || pmFlag) ? context.getString(R.string.pm) : context.getString(R.string.am)));
    }

}
