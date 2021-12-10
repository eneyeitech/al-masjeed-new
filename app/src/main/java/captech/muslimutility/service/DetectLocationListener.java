package captech.muslimutility.service;

import com.google.android.gms.maps.model.LatLng;

public abstract interface DetectLocationListener {
    public abstract void onDetectLocationListener(LatLng latLng);
}
