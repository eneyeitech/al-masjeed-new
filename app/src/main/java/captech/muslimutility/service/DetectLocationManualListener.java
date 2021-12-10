package captech.muslimutility.service;

import com.google.android.gms.maps.model.LatLng;

public abstract interface DetectLocationManualListener {
    public abstract void onDetectLocationManualListener(LatLng latLng);
}
