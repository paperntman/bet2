package main;

import java.sql.*;

public class CurrencyManager {

    private final String guildID;
    Connection connection;

    public CurrencyManager(String guildID) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");

        String host = "ec2-44-196-223-128.compute-1.amazonaws.com";
        String database = "d1baaddau2fm97";
        String user = "qkdvzhzhtuzbca";
        String password = "627158973009f8fe4b299076b18700f4472cb2cc5dff78e752af6c24df6f46b4";
        this.guildID = guildID;
        long port = 5432;
        connection = DriverManager.getConnection(String.format("jdbc:postgresql://%s:%d/%s", host, port, database), user, password);
        connection.setAutoCommit(false);
    }

    public boolean setup(){
        try{
            Statement statement = connection.createStatement();
            final ResultSet resultSet = statement.executeQuery("SELECT tablename FROM pg_catalog.pg_tables WHERE tablename='"+"d"+guildID+"';");
            boolean contains = false;
            while (resultSet.next()){
                if (resultSet.getString("tablename").equalsIgnoreCase("d"+guildID)) contains=true;
            }
            if(!contains) {
                statement.execute("CREATE TABLE d"+guildID+" ( USER_ID VARCHAR(18), POINT NUMERIC);");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public long getPoint(String id){
        try{
            Statement statement = connection.createStatement();
            final ResultSet resultSet = statement.executeQuery(String.format("SELECT * FROM d%s WHERE USER_ID='%s'", guildID, id));
            while (resultSet.next()) {
                final String setString = resultSet.getString("USER_ID");
                if (setString.equalsIgnoreCase(id)) {
                    return resultSet.getInt("POINT");
                }
            }
            statement.executeUpdate(String.format("INSERT INTO d%s VALUES (%s, 0)", guildID, id));
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void setPoint(String id, long point) {
        try{
            final Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format("SELECT FROM d%s WHERE USER_ID='%s'", guildID, id));
            if(!resultSet.next()) statement.executeUpdate(String.format("INSERT INTO d%s VALUES (%s, %d)", guildID, id, point));
            else {
                statement.executeUpdate(String.format("UPDATE d%s SET POINT=%d WHERE USER_ID='%s'", guildID, point, id));
            }
            connection.commit();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void addPoint(String id, long point) {
        setPoint(id, getPoint(id)+point);
    }





}
