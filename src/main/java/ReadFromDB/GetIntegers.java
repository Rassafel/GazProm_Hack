package ReadFromDB;

import ReadAndLoadToDB.ConnectionToDB;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class GetIntegers extends ConnectionToDB {

    public String SELECT = "select id from banks;";

    public GetIntegers(String url, String username, String password) {
        super(url, username, password);
    }

    public List<Integer> getList(){
        List<Integer> list = new LinkedList<>();
        try(Statement statement = connection.createStatement()){
            ResultSet rs = statement.executeQuery(SELECT);
            while(rs.next()){
                list.add(rs.getInt(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }

    public int[] getArray(){
        return getList().stream().mapToInt(t -> t).toArray();
    }
}
