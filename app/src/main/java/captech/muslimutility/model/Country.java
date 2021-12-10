package captech.muslimutility.model;

public class Country {
    public String countryName, countryArabicName, countryShortCut;

    public Country(String countryName, String countryArabicName, String countryShortCut) {
        this.countryName = countryName;
        this.countryArabicName = countryArabicName;
        this.countryShortCut = countryShortCut;
    }

    public Country() {
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryArabicName() {
        return countryArabicName;
    }

    public void setCountryArabicName(String countryArabicName) {
        this.countryArabicName = countryArabicName;
    }

    public String getCountryShortCut() {
        return countryShortCut;
    }

    public void setCountryShortCut(String countryShortCut) {
        this.countryShortCut = countryShortCut;
    }
}
