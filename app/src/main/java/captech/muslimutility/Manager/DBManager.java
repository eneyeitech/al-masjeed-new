package captech.muslimutility.Manager;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import captech.muslimutility.model.City;
import captech.muslimutility.model.Country;
import captech.muslimutility.model.LocationInfo;
import captech.muslimutility.model.Zeker;
import captech.muslimutility.model.ZekerType;

public class DBManager {

    public static final String DATABASE_NAME = "muslim_organizer.sqlite";
    static final int DATABASE_VERSION = 2;
    final Context context;
    SQLiteDatabase db;
    DatabaseHelper DBHelper;

    public DBManager(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private class DatabaseHelper extends SQLiteOpenHelper {


        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
            db.disableWriteAheadLogging();
        }

    }

    // ---opens the database---
    public DBManager open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public long copyDataBase() throws IOException {
        String DB_PATH = context.getDatabasePath(DATABASE_NAME).getPath()
                .toString();

        // Open your local db as the input stream
        InputStream myInput = context.getAssets().open(DATABASE_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH;

        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
        return length;
    }

    public LocationInfo getLocationInfo(float latitude, float longitude) {

        String sql = "select  b.En_Name , b.Ar_Name , b.iso3 , a.city ," +
                " b.Continent_Code ,  b.number  , b.mazhab , b.way , b.dls , a.time_zone ," +
                " (latitude - " + latitude + ")*(latitude - " + latitude + ")+(longitude - " + longitude + ")" +
                "*(longitude - " + longitude + ") as ed , a.Ar_Name  from cityd a , countries b where" +
                " b.code = a.country order by ed asc limit 1;";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();

        LocationInfo locationInfo = new LocationInfo(latitude, longitude,
                cursor.getString(0), cursor.getString(1), cursor.getString(2),
                cursor.getString(3), cursor.getString(4), cursor.getInt(5),
                cursor.getInt(6), cursor.getInt(7), cursor.getInt(8), cursor.getInt(9),
                cursor.getString(11) == null ? cursor.getString(3) : cursor.getString(11));

        cursor.close();
        return locationInfo;
    }

    public List<ZekerType> getAllAzkarTypes() {
        List<ZekerType> zekerTypeList = new ArrayList<>();
        String sql = "select a.ZekrTypeID , a.ZekrTypeName , count( b.TypeID ) from AzkarTypes a ," +
                "  azkar b where a.ZekrTypeID = b.TypeID group by a.ZekrTypeName order by  a.ZekrTypeID;";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            zekerTypeList.add(new ZekerType(cursor.getInt(0), cursor.getString(1), cursor.getInt(2)));
            cursor.moveToNext();
        }
        cursor.close();
        return zekerTypeList;
    }

    public List<Zeker> getAllAzkarOfType(int type) {
        List<Zeker> zekerList = new ArrayList<>();
        String[] selection = {String.valueOf(type)};
        String sql = "select * from Azkar where TypeID = ?";
        Cursor cursor = db.rawQuery(sql, selection);
        if (cursor.moveToFirst()){
            do {
                Zeker zeker = new Zeker();
                zeker.setContent(cursor.getString(2));
                zeker.setFadl(cursor.getString(5));
                zeker.setNumberOfRepeat(cursor.getInt(3));
                zekerList.add(zeker);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return zekerList;
    }

    public List<Country> getAllCountries() {
        List<Country> countries = new ArrayList<>();
        String sql = "select * from Countries";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                Country country = new Country();
                country.setCountryName(cursor.getString(2));
                country.setCountryArabicName(cursor.getString(9));
                country.setCountryShortCut(cursor.getString(0));
                countries.add(country);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return countries;
    }

    public List<City> getAllCities(String code) {
        List<City> cities = new ArrayList<>();
        String sql = "select * from cityd where country = '" + code + "' ;";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            cities.add(new City(cursor.getString(1),cursor.getString(6) , cursor.getFloat(2), cursor.getFloat(3)));
            cursor.moveToNext();
        }
        cursor.close();

        return cities;
    }

}


