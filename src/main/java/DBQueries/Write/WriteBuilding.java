package DBQueries.Write;

import Data.Building;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

public class WriteBuilding extends WriteProcessToDB<Building> {

    private static final Logger LOG = Logger.getLogger(WriteBuilding.class.getName());

    private static final String SELECT = "select id from buildings where lat=? and lon=?;";
    private static final String INSERT = "insert into buildings (lat,lon,name,address,type,priority) values(?,?,?,?,?,?);";
    private static final String UPDATE = "update buildings set(name,address,type,priority) = (?,?,?,?) where lat=? and lon=?;";

    public WriteBuilding(String url, String username, String password) {
        super(url, username, password);
    }

    @Override
    protected Logger log() {
        return LOG;
    }

    @Override
    protected boolean select(Building data) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(SELECT);
        statement.setDouble(1, data.getGeoData().getLat());
        statement.setDouble(2, data.getGeoData().getLon());
        return statement.executeQuery().next();
    }

    @Override
    protected void update(Building data) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(UPDATE);
        statement.setString(1, data.getName());
        statement.setString(2, data.getAddress());
        statement.setString(3, data.getType());
        statement.setShort(4, data.getPriority());
        statement.setDouble(5, data.getGeoData().getLat());
        statement.setDouble(6, data.getGeoData().getLon());
        statement.executeUpdate();
    }

    @Override
    protected void insert(Building data) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(INSERT);
        statement.setDouble(1, data.getGeoData().getLat());
        statement.setDouble(2, data.getGeoData().getLon());
        statement.setString(3, data.getName());
        statement.setString(4, data.getAddress());
        statement.setString(5, data.getType());
        statement.setShort(6, data.getPriority());
        statement.executeUpdate();
    }
}
