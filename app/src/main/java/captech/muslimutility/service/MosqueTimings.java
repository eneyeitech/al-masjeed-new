package captech.muslimutility.service;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.api.client.auth.oauth2.Credential;
        import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
        import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
        import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
        import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
        import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
        import com.google.api.client.http.javanet.NetHttpTransport;
        import com.google.api.client.json.JsonFactory;
        import com.google.api.client.json.jackson2.JacksonFactory;
        import com.google.api.client.util.store.FileDataStoreFactory;
        import com.google.api.services.sheets.v4.Sheets;
        import com.google.api.services.sheets.v4.SheetsScopes;
        import com.google.api.services.sheets.v4.model.ValueRange;

        import java.io.FileNotFoundException;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
        import java.util.List;

import captech.muslimutility.config.Config;
import captech.muslimutility.ui.widget.PrayerWidget;

public class MosqueTimings {


    private static List<Object> timings = null;
    private static List<Object> mosqueDefault = new ArrayList<>();


    public static List<Object> getTimings() {
        if(timings == null){
            mosqueDefault.add("Default Mosque");
            mosqueDefault.add("A0");
            mosqueDefault.add("05:20 AM");
            mosqueDefault.add("06:20 AM");
            mosqueDefault.add("13:14 PM");
            mosqueDefault.add("15:35 PM");
            mosqueDefault.add("18:10 PM");
            mosqueDefault.add("19:19 PM");

            return mosqueDefault;
        }
       return timings;
    }

    public static void setTimings(List<Object> t){
        timings = t;

        Log.d("SUCCESS--TIMINGS::1", "" + t.get(0) + "," + t.get(1) + "," + t.get(2) + "," + t.get(3) + "," + t.get(4) + "," + t.get(5) + "," + t.get(6)+ "," + t.get(7));


    }

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */


    /**
     * Prints the names and majors of students in a sample spreadsheet:
     * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
     */
    public static void main(String... args)  {

    }
}