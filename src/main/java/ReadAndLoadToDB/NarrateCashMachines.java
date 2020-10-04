package ReadAndLoadToDB;

import ReadAndLoadToDB.ConnectionToDB;

import java.sql.*;

public class NarrateCashMachines extends ConnectionToDB {

    public NarrateCashMachines(String url, String username, String password) {
        super(url, username, password);
    }

    private static final String AVG_QUERY = "select sum(avg_min_users), sum(avg_max_users), count(id) from avg_banks_stats;";
    private static final String COUNT_AVG_BANK = "select id, avg_min_users, avg_max_users from avg_banks_stats;";
    private static final String NARRATE_BANKS_INSERT = "insert into %s (id,load_factor) values (?,?);";
    private static final String NARRATE_BANKS_UPDATE = "update %s set load_factor = ? where id = ?;";
    private static final String NARRATE_BANKS_SELECT = "select id from %s where id = ?;";
    private static final String CLEAR_MAX = "delete from max_atm_used where true";
    private static final String CLEAR_MIDDLE = "delete from middle_atm_used where true";
    private static final String CLEAR_MIN = "delete from min_atm_used where true";


    public static double MAX_LOAD = 1.3d;
    public static double MIN_LOAD = 0.6d;

    public void narrateCashMachines() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(CLEAR_MAX);
            statement.executeUpdate(CLEAR_MIDDLE);
            statement.executeUpdate(CLEAR_MIN);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try (Statement avg_counter = connection.createStatement()) {
            ResultSet resultSet = avg_counter.executeQuery(AVG_QUERY);
            double AVG = 0;
            if (resultSet.next()) {
                AVG = (resultSet.getDouble(1) + resultSet.getDouble(2)) / 2 / resultSet.getInt(3);
                System.out.println("AVG = " + AVG);
            }
            try (Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                ResultSet set = statement.executeQuery(COUNT_AVG_BANK);
                while (set.next()) {
                    int id = set.getInt(1);
                    double oneBankAvg = (set.getDouble(2) + set.getDouble(3)) / 2;
                    if (oneBankAvg < 0) throw new SQLException("Not correct AVG");
                    String toWhichDb;
                    if (oneBankAvg >= AVG * MAX_LOAD) {
                        toWhichDb = "max_atm_used";
                    } else if (oneBankAvg <= AVG * MIN_LOAD) {
                        toWhichDb = "min_atm_used";
                    } else {
                        toWhichDb = "middle_atm_used";
                    }
                    try (PreparedStatement statementSelect = connection.prepareStatement(String.format(NARRATE_BANKS_SELECT, toWhichDb))) {
                        statementSelect.setInt(1, id);
                        ResultSet rs = statementSelect.executeQuery();
                        if (rs.next()) {
                            try (PreparedStatement updateStatement = connection.prepareStatement(String.format(NARRATE_BANKS_UPDATE, toWhichDb))) {
                                updateStatement.setDouble(1, oneBankAvg);
                                updateStatement.setInt(2, id);
                                updateStatement.executeUpdate();
                            }
                        } else {
                            try (PreparedStatement insertStatement = connection.prepareStatement(String.format(NARRATE_BANKS_INSERT, toWhichDb))) {
                                insertStatement.setInt(1, id);
                                insertStatement.setDouble(2, oneBankAvg);
                                insertStatement.executeUpdate();
                            }
                        }
                    }
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
