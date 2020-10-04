package Generators;

import Data.Location;
import Data.UserData;
import InputStreams.StreamData;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class GenerateUserData extends StreamData<UserData> {

    public static Location MAX = new Location(57.2, 41);
    public static Location MIN = new Location(54.6, 35);
    public static int secondsInMinute = 60;

    private UserData last = null;

    @Override
    public Stream<UserData> getStream(final int id, Instant start, Instant end) {
        return getList(id, start, end).stream();
    }

    public List<UserData> getList(int id, Instant start, Instant end){
        List<UserData> list = new LinkedList<>();

        Instant cur = start;
        while(cur.isBefore(end)){
            list.addAll(getMinuteData(id, cur));
            cur = cur.plusSeconds(60);
        }

        return list;
    }

    private List<UserData> getMinuteData(int id, final Instant time){
        List<UserData> list = new LinkedList<>();
        int i = 0;
        if(last == null){
            i++;
            double lon = (Math.random() * (MAX.getLon() - MIN.getLon())) + MIN.getLon();
            double lat = (Math.random() * (MAX.getLat() - MIN.getLat())) + MIN.getLat();
            last = new UserData(id, time, lat, lon);
            list.add(last);
        }
        for (;i < secondsInMinute; i++) {
            double dLon = Math.random() > 0.5 ? -0.00001 : 0.00001;
            double dLat = Math.random() > 0.5 ? -0.00001 : 0.00001;
            last = new UserData(id, time, last.lat + dLat, last.lon + dLon);
            list.add(last);
        }
        return list;
    }
}
