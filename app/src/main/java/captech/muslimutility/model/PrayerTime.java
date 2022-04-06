package captech.muslimutility.model;

public class PrayerTime implements Comparable{
    private String name;
    private int hour;
    private int minute;

    /**
     *
     * @param name fajr
     * @param timeString 05:20:00
     */
    public PrayerTime(String name, String timeString){
        this.name = name;

        hour = Integer.parseInt(timeString.substring(0,2));
        minute = Integer.parseInt(timeString.substring(3,5));

    }

    public String getName() {
        return name;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    @Override
    public int compareTo(Object o) {
        PrayerTime p = (PrayerTime) o;
        if(hour > p.getHour()){
            return 1;
        } else if(hour < p.getHour()){
            return -1;
        } else {
            if(minute > p.getMinute()){
                return 1;
            } else if(minute < p.getMinute()) {
                return -1;
            }else{
                return 0;
            }
        }
    }
}
