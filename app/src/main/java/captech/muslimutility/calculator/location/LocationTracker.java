package captech.muslimutility.calculator.location;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class LocationTracker implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    public static final long MIN_TIME_BW_UPDATES = 1000 * 10;

    private static Context context;
    private static LocationListener locationListener;
    private LocationManager locationManager;
    private Location location;
    private GoogleApiClient googleApiClient;

    private static LocationRequest locationRequest = LocationRequest.create()
            .setFastestInterval(MIN_TIME_BW_UPDATES)
            .setInterval(MIN_TIME_BW_UPDATES)
            .setSmallestDisplacement(MIN_DISTANCE_CHANGE_FOR_UPDATES)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    public LocationTracker(Context context) {
        this.context = context;
    }
    private static GoogleApiClient apiClient;
    private  static void enableGPS(final Activity activity) {

        apiClient  = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        requestGps(activity  ,apiClient);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(LocationServices.API)
                .build();

        apiClient.connect();
    }

    private static void requestGps(final Activity activity , GoogleApiClient apiClient) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(apiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:


                        Log.i("TAG_LOCATION_REQUEST", "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i("TAG_LOCATION_REQUEST", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            status.startResolutionForResult(activity, 100);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("TAG_LOCATION_REQUEST", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("TAG_LOCATION_REQUEST", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (googleApiClient != null ) {
            if (context instanceof  Activity) {
                enableGPS((Activity) context);
            }


            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient,
                    locationRequest,
                    new com.google.android.gms.location.LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            if (location != null) {
                                changeLocation(location);
                            }
                        }
                    });

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @SuppressWarnings("MissingPermission")
    public boolean updateLocation()
    {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }

        if (location==null && locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        if (location==null && locationManager != null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (location==null && locationManager != null && locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
            location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }

        if (location != null) {
            saveLocation(location);
            return true;
        }

        Log.i("TRACKER_LOG" , "gps is ON : and location is null");

        return false;
    }

    public boolean hasLocation()
    {
        return (location!=null);
    }

    public Location getLocation(){
        if (location != null) {
            Log.i("REQUEST_LOCATION", "loc : (" + location.getLatitude() + " : " + location.getLongitude()+")");
        }else{
            Log.i("REQUEST_LOCATION", "loc : null");
        }
        return location;
    }

    public  double getLocationLatitude() {
        if(location==null)return 0;
        return location.getLatitude();
    }

    public  double getLocationLongitude() {
        if(location==null)return 0;
        return location.getLongitude();
    }

    private void saveLocation(Location location) {
        Utility.saveStringPrefs(this.context, "latitude", ""+location.getLatitude());
        Utility.saveStringPrefs(this.context, "longitude", ""+location.getLongitude());
    }

    private void changeLocation(Location location)
    {
        this.location = location;
        saveLocation(location);

        if (locationListener!=null){
            locationListener.onLocationChanged(location);
        }
    }

    @Override
    public void onLocationChanged(Location location)
    {
        if (location != null) {
            changeLocation(location);
        }
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        if (locationListener!=null){
            locationListener.onProviderDisabled(provider);
        }
    }

    @Override
    public void onProviderEnabled(String provider)
    {
        if (locationListener!=null){
            locationListener.onProviderEnabled(provider);
        }

        updateLocation();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        if (locationListener!=null){
            locationListener.onStatusChanged(provider , status , extras);
        }
    }

}