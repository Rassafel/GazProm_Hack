package LoadersToDB;

import Data.BankStatistics;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class PrepareBankStatistics extends PrepareProcess<BankStatistics> {

    public static final String INSERT = "insert into bankstats(id,timeinterval,countusers,minusers,maxusers) values(?, ?, ?, ?, ?);";

    public PrepareBankStatistics(String url, String username, String password) {
        super(url, username, password);
    }

    @Override
    public boolean addData(BankStatistics data) {
        try(PreparedStatement statement = connection.prepareStatement(INSERT)){
            statement.setInt(1, data.id);
            statement.setTimestamp(2, Timestamp.from(data.time));
            statement.setInt(3, data.countUsers);
            statement.setInt(4, data.minUsers);
            statement.setInt(5, data.maxUsers);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
        return true;
    }
}
