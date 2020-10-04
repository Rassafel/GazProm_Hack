package ReadAndLoadToDB;

import ReadAndLoadToDB.ConnectionToDB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CalculateAvgBankStats extends ConnectionToDB {

    private static final String GROUP_BY_ID = "select id, sum(countusers), avg(minusers), avg(maxusers) from bankstats group by id";
    private static final String INSERT_AVG_STATS = "insert into avg_banks_stats(id, all_users, avg_min_users, avg_max_users)" +
            " values (?, ?, ?, ?)";
    private static final String SELECT = "select id from avg_banks_stats where id = ?";
    private static final String UPDATE = "update avg_banks_stats set (all_users, avg_min_users, avg_max_users) = (?,?,?) where id = ?;";
    private static final String CLEAR = "delete from avg_banks_stats where true";


    public CalculateAvgBankStats(String url, String username, String password) {
        super(url, username, password);
    }

    public void calculateAvgBanksStats() {
        try(Statement statement = connection.createStatement()){
            statement.executeUpdate(CLEAR);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try (Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            ResultSet resultSet = statement.executeQuery(GROUP_BY_ID);
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                try (PreparedStatement statementSelect = connection.prepareStatement(SELECT)) {
                    statementSelect.setInt(1, id);
                    ResultSet rs = statementSelect.executeQuery();
                    if (rs.next()) {
                        try(PreparedStatement statementUpdate = connection.prepareStatement(UPDATE)){
                            statementUpdate.setInt(1, resultSet.getInt(2));
                            statementUpdate.setDouble(2, resultSet.getDouble(3));
                            statementUpdate.setDouble(3, resultSet.getDouble(4));
                            statementUpdate.setInt(4, id);
                            statementUpdate.executeUpdate();
                        }
                    } else {
                        try (PreparedStatement statementInsert = connection.prepareStatement(INSERT_AVG_STATS)) {
                            statementInsert.setInt(1, id);
                            statementInsert.setInt(2, resultSet.getInt(2));
                            statementInsert.setDouble(3, resultSet.getDouble(3));
                            statementInsert.setDouble(4, resultSet.getDouble(4));
                            statementInsert.executeUpdate();
                        }
                    }
                }


            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
