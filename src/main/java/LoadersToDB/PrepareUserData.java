package LoadersToDB;

import Data.UserData;

import java.sql.*;
import java.time.Instant;
import java.util.*;

public class PrepareUserData extends PrepareProcess<UserData> {

    private Map<Integer, Map<Instant, List<UserData>>> usersData = new HashMap<>();
    private Map<Integer, Instant> lastTime = new HashMap<>();

    private static final String INSERT_PATH = "insert into paths(id, timeInterval, lat, lon, distance) VALUES (?, ?, ?, ?, ?);";
    private static final String INSERT_USER = "insert into users (id) values (?);";
    private static final String SELECT_USER = "select id from users where id = ?";

    public PrepareUserData(String url, String username, String password) {
        super(url, username, password);
    }

    /**
     * Add user and his data.
     * Collected data per minute sent to database.
     *
     * @param data
     */
    @Override
    public boolean addData(UserData data) {
        Map<Instant, List<UserData>> timeMap;
        List<UserData> list;
        if (usersData.containsKey(data.userId)) {
            timeMap = usersData.get(data.userId);

            if (timeMap.containsKey(data.time)) {
                list = timeMap.get(data.time);
                list.add(data);
            } else {
                list = new LinkedList<>();
                list.add(data);
                timeMap.put(data.time, list);

                //compact and clear map
                clearMap(data.userId);
            }
        } else {
            timeMap = new HashMap<>();
            list = new LinkedList<>();

            list.add(data);
            timeMap.put(data.time, list);

            usersData.put(data.userId, timeMap);
            lastTime.put(data.userId, data.time);

            loadToDB(data.userId);
        }
        return true;
    }

    /**
     * Remove old data.
     *
     * @param id
     */
    public void clearMap(int id, int maxSize) {
        Map<Instant, List<UserData>> timeMap = usersData.get(id);
        if (timeMap.size() > maxSize) {
            Instant lastKey = lastTime.get(id);
            ClearedData clearedData = new ClearedData(id, lastKey, timeMap.remove(lastKey));
            lastTime.put(id, timeMap.entrySet().iterator().next().getKey());
            loadToDB(clearedData);
        }
    }

    /**
     * Remove old data.
     *
     * @param id
     */
    public void clearMap(int id) {
        clearMap(id, 1);
    }

    /**
     * Load user into DB table users.
     *
     * @param id
     */
    public void loadToDB(int id) {
        try (PreparedStatement statementSelect = connection.prepareStatement(SELECT_USER)) {
            statementSelect.setInt(1, id);
            if (!statementSelect.executeQuery().next()) {
                try (PreparedStatement statementInsert = connection.prepareStatement(INSERT_USER)) {
                    statementInsert.setInt(1, id);
                    statementInsert.executeUpdate();
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Load compressed user data to DB paths.
     *
     * @param data
     */
    public void loadToDB(ClearedData data) {
        try (PreparedStatement statementInsert = connection.prepareStatement(INSERT_PATH)) {
            statementInsert.setInt(1, data.userId);
            statementInsert.setTimestamp(2, Timestamp.from(data.time));
            statementInsert.setDouble(3, data.lat);
            statementInsert.setDouble(4, data.lon);
            statementInsert.setDouble(5, data.distance);
            statementInsert.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private class ClearedData extends UserData {
        public static final int EARTH_RADIUS = 6372795;
        public double distance;

        public ClearedData(int userId, Instant time, List<UserData> list) {
            super(userId, time,
                    list.stream().mapToDouble(t -> t.lat).average().getAsDouble(),
                    list.stream().mapToDouble(t -> t.lon).average().getAsDouble());
            distance = calculateDistance(list);
        }

        private double calculateDistance(List<UserData> list) {
            double result = 0;
            for (int i = 1; i < list.size(); i++) {
                result += calculateDistance(list.get(i - 1), list.get(i));
            }
            return result;
        }

        private double calculateDistance(UserData start, UserData end) {
            double lat1 = start.lat, lat2 = end.lat;
            double lon1 = start.lon, lon2 = end.lon;
            double difLat = Math.toRadians(lat2 - lat1);
            double difLon = Math.toRadians(lon2 - lon1);

            double result = Math.pow(Math.sin(difLon / 2), 2);
            result *= Math.cos(Math.toRadians(lat1));
            result *= Math.cos(Math.toRadians(lat2));
            result += Math.pow(Math.sin(difLat / 2), 2);
            result = 2 * Math.atan2(Math.sqrt(result), Math.sqrt(1 - result));
            return result * EARTH_RADIUS;
        }
    }
}
