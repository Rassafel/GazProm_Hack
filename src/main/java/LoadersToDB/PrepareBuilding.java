package LoadersToDB;

import Data.Building;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PrepareBuilding extends PrepareProcess<Building> {

    public static final String INSERT = "insert into buildings (lat,lon,name,address,type,priority) values(?,?,?,?,?,?);";

    public PrepareBuilding(String url, String username, String password) {
        super(url, username, password);
    }

    @Override
    public boolean addData(Building building) {
        try (PreparedStatement statement = connection.prepareStatement(INSERT)) {
            statement.setDouble(1, building.getGeoData().getLat());
            statement.setDouble(2, building.getGeoData().getLon());
            statement.setString(3, building.getName());
            statement.setString(4, building.getAddress());
            statement.setString(5, building.getType());
            statement.setShort(6, building.getPriority());
            statement.executeUpdate();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }


}
