package DBQueries.ReadWrite;

import DBQueries.ConnectionToDB;

import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;

public class CalculateRating extends ConnectionToDB {

    private static final Logger LOG = Logger.getLogger(CalculateRating.class.getName());

    private static final String SELECT_DATA = "select banks.id, paths.id, paths.timeInterval from paths, banks " +
            "where ? <= paths.timeinterval AND ? < paths.timeinterval and calculateDistance(paths.lat, banks.lat, paths.lon, banks.lon) < ?;";

    private static final String SELECT_ALL_BANKS = "select id from banks;";

    private static final String SELECT_BANK = "select id from bankstats where id = ? AND timeinterval = ?;";
    private static final String UPDATE = "update bankstats set (countusers,minusers,maxusers) = (?,?,?) where " +
            "id = ? AND timeinterval = ?;";

    private static final String INSERT = "insert into bankstats(id,timeinterval,countusers,minusers,maxusers) values(?, ?, ?, ?, ?);";

    /**
     * Time interval in minutes.
     */
    private static int interval = 12;

    /**
     * ATM search radius.
     */
    private static double distance = 50;

    public CalculateRating(String url, String username, String password) {
        super(url, username, password);
    }

    /**
     * Rating all data.
     */
    public void updateData() {
        LOG.info("Start calculate users and load to bankStats\n");
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("select min(timeinterval), max(timeinterval) from paths;");
            if (resultSet.next()) {
                final Instant minStamp = resultSet.getTimestamp(1).toInstant();
                final Instant maxStamp = resultSet.getTimestamp(2).toInstant().plusSeconds(1);
                Instant last = minStamp;
                while (last.isBefore(maxStamp)) {
                    Instant current = last.plusSeconds(interval * 60);
                    updateData(last, current);
                    last = current;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Rating data from start timestamp to end timestamp.
     * @param start
     * @param end
     */
    private void updateData(Instant start, Instant end) {
        Map<Integer, Map<Instant, List<Integer>>> map = new HashMap<>();
        try (PreparedStatement statementSelectData = connection.prepareStatement(SELECT_DATA)) {
            statementSelectData.setTimestamp(1, Timestamp.from(start));
            statementSelectData.setTimestamp(2, Timestamp.from(end));
            statementSelectData.setDouble(3, distance);

            ResultSet resultSet = statementSelectData.executeQuery();
            while (resultSet.next()) {
                int bankId = resultSet.getInt(1);
                int userId = resultSet.getInt(2);
                Instant time = resultSet.getTimestamp(3).toInstant();
                if (map.containsKey(bankId)) {
                    Map<Instant, List<Integer>> instantListMap = map.get(bankId);
                    if (instantListMap.containsKey(time)) {
                        instantListMap.get(time).add(userId);
                    } else {
                        List<Integer> list = new LinkedList<>();
                        list.add(userId);
                        instantListMap.put(time, list);
                    }
                } else {
                    Map<Instant, List<Integer>> instantListMap = new HashMap<>();
                    List<Integer> list = new LinkedList<>();
                    list.add(userId);
                    instantListMap.put(time, list);
                    map.put(bankId, instantListMap);
                }
            }

            Map<Integer, List<Integer>> listMap = new HashMap<>();

            try(PreparedStatement statementSelectBanks = connection.prepareStatement(SELECT_ALL_BANKS)){
                ResultSet rs = statementSelectBanks.executeQuery();
                while(rs.next()){
                    int key = rs.getInt(1);
                    if(map.containsKey(key)){
                        listMap.put(key, calculateRating(map.get(key)));
                    } else{
                        List<Integer> list = new ArrayList<>(3);
                        list.add(0);
                        list.add(0);
                        list.add(0);
                        listMap.put(key, list);
                    }
                }
            }

            for (Map.Entry<Integer, List<Integer>> set : listMap.entrySet()) {
                loadToDB(set.getKey(), end, set.getValue());
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Calculate rating, from queue.
     * @param timeMap
     * @return
     */
    private List<Integer> calculateRating(Map<Instant, List<Integer>> timeMap) {
        List<Integer> counts = new ArrayList<>(3);
        Set<Integer> set = new HashSet<>();
        timeMap.values().forEach(set::addAll);
        counts.add(set.size());

        List<Integer> max = new LinkedList<>();
        List<Integer> min = new LinkedList<>();

        timeMap.values().forEach(t -> max.add(t.stream().max(Integer::compareTo).get()));
        timeMap.values().forEach(t -> min.add(t.stream().min(Integer::compareTo).get()));

        counts.add(min.stream().min(Integer::compareTo).get());
        counts.add(max.stream().max(Integer::compareTo).get());
        return counts;
    }

    /**
     * Write to database statistics in interval.
     * @param key
     * @param time
     * @param list
     */
    private void loadToDB(int key, Instant time, List<Integer> list) {
        Timestamp stamp = Timestamp.from(time);
        try (PreparedStatement statementSelect = connection.prepareStatement(SELECT_BANK)) {
            statementSelect.setInt(1, key);
            statementSelect.setTimestamp(2, stamp);
            if (statementSelect.executeQuery().next()) {
                try (PreparedStatement statementUpdate = connection.prepareStatement(UPDATE)) {
                    statementUpdate.setInt(1, list.get(0));
                    statementUpdate.setInt(2, list.get(1));
                    statementUpdate.setInt(3, list.get(2));
                    statementUpdate.setInt(4, key);
                    statementUpdate.setTimestamp(5, stamp);
                    statementUpdate.executeUpdate();
                }
            } else {
                try (PreparedStatement statementInsert = connection.prepareStatement(INSERT)) {
                    statementInsert.setInt(1, key);
                    statementInsert.setTimestamp(2, stamp);
                    statementInsert.setInt(3, list.get(0));
                    statementInsert.setInt(4, list.get(1));
                    statementInsert.setInt(5, list.get(2));
                    statementInsert.executeUpdate();
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}