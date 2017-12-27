package baroEmap;

/**
 * Created by jaypark on 2017. 9. 11..
 */

// DB에서 넘어오는 POI 데이터를 묶어주는 class
public class Poi {

    private String place_id;
    private String name;
    private double latitude;
    private double longitude;

    public Poi(String place_id, String name, double latitude, double longitude) {
        this.place_id = place_id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getPlace_id() {
        return place_id;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() { return latitude; }

    public double getLongitude() {
        return longitude;
    }
}
