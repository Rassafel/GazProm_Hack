package LoadersToDB;

import Data.BankData;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PrepareBankData extends PrepareProcess<BankData> {

    public static final String UPDATE = "insert into banks(id, lat, lon, region, regionType, setlementtype, setlement," +
            " fulladdress, location, replaced) values (?,?,?,?,?,?,?,?,?,?);";
    public static final String SELECT = "select id from banks where id = ?;";

    public PrepareBankData(String url, String username, String password) {
        super(url, username, password);
    }

    /**
     * Add ATM to database.
     * @param data
     */
    @Override
    public boolean addData(BankData data){
        try(PreparedStatement statementSelect = connection.prepareStatement(SELECT)){
            statementSelect.setInt(1, data.getId());
            if(!statementSelect.executeQuery().next()){
                try(PreparedStatement statementUpdate = connection.prepareStatement(UPDATE)){
                    statementUpdate.setInt(1, data.getId());
                    statementUpdate.setDouble(2, data.getGeolocation().getLat());
                    statementUpdate.setDouble(3, data.getGeolocation().getLon());
                    statementUpdate.setString(4, data.getAddress().getRegion());
                    statementUpdate.setString(5, data.getAddress().getRegionType());
                    statementUpdate.setString(6, data.getAddress().getSettlementType());
                    statementUpdate.setString(7, data.getAddress().getSettlement());
                    statementUpdate.setString(8, data.getAddress().getFullAddress());
                    statementUpdate.setString(9, data.getAddress().getLocation());
                    statementUpdate.setBoolean(10, false);
                    statementUpdate.executeUpdate();
                    return true;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }
}
