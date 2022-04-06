package captech.muslimutility.model;

public class MosquePrayerTimes {
    private String name, code, fajr, sunrise, zuhr, asr, maghrib, isha;

    public MosquePrayerTimes(String name, String code, String fajr, String sunrise, String zuhr, String asr, String maghrib, String isha) {
        this.name = name;
        this.code = code;
        this.fajr = fajr;
        this.sunrise = sunrise;
        this.zuhr = zuhr;
        this.asr = asr;
        this.maghrib = maghrib;
        this.isha = isha;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFajr() {
        return fajr;
    }

    public void setFajr(String fajr) {
        this.fajr = fajr;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getZuhr() {
        return zuhr;
    }

    public void setZuhr(String zuhr) {
        this.zuhr = zuhr;
    }

    public String getAsr() {
        return asr;
    }

    public void setAsr(String asr) {
        this.asr = asr;
    }

    public String getMaghrib() {
        return maghrib;
    }

    public void setMaghrib(String maghrib) {
        this.maghrib = maghrib;
    }

    public String getIsha() {
        return isha;
    }

    public void setIsha(String isha) {
        this.isha = isha;
    }
}

