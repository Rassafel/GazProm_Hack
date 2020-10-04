package Data;

import java.time.Instant;

public class UserData {
    public int userId;
    public Instant time;
    public double lat;
    public double lon;

    public UserData(int userId, Instant time, double lat, double lon) {
        this.userId = userId;
        this.time = time;
        this.lat = lat;
        this.lon = lon;
    }
}
