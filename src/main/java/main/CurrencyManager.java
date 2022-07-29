package main;

import java.sql.*;

public class CurrencyManager {

    private String host;
    private String database;
    private String user;
    private String password;
    private String guildID;
    private int port;

    public CurrencyManager(String guildID) throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");

        this.host = "ec2-44-196-223-128.compute-1.amazonaws.com";
        this.database = "d1baaddau2fm97";
        this.user = "qkdvzhzhtuzbca";
        this.password = "627158973009f8fe4b299076b18700f4472cb2cc5dff78e752af6c24df6f46b4";
        this.guildID = guildID;
        this.port = 5432;
    }

    public boolean setup(){
        try(Connection connection = DriverManager.getConnection(String.format("jdbc:postgresql://%s:%d/%s", host, port, database), user, password)){
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

    public int getPoint(String id){
        try(Connection connection = DriverManager.getConnection(String.format("jdbc:postgresql://%s:%d/%s", host, port, database), user, password)){
            Statement statement = connection.createStatement();
            final ResultSet resultSet = statement.executeQuery(String.format("SELECT * FROM d%s WHERE USER_ID='%s'", guildID, id));
            while (resultSet.next()) {
                final String setString = resultSet.getString("USER_ID");
                if (setString.equalsIgnoreCase(id)) {
                    return resultSet.getInt("POINT");
                }
            }
            statement.execute(String.format("INSERT INTO d%s VALUES (%s, 0)", guildID, id));
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void setPoint(String id, int point) {
        try(Connection connection = DriverManager.getConnection(String.format("jdbc:postgresql://%s:%d/%s", host, port, database), user, password)){
            final Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format("SELECT FROM d%s WHERE USER_ID='%s'", guildID, id));
            if(!resultSet.next()) statement.execute(String.format("INSERT INTO d%s VALUES (%s, %d)", guildID, id, point));
            else statement.executeUpdate(String.format("UPDATE d%s SET POINT=%d WHERE USER_ID='%s'", guildID, point, id));
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void addPoint(String id, int point) {
        setPoint(id, getPoint(id)+point);
    }

    public static String get(String key){
        try(Connection connection = DriverManager.getConnection(String.format("jdbc:postgresql://%s:%d/%s", "ec2-44-196-223-128.compute-1.amazonaws.com", 5432, "d1baaddau2fm97"), "qkdvzhzhtuzbca", "627158973009f8fe4b299076b18700f4472cb2cc5dff78e752af6c24df6f46b4")){
            final Statement statement = connection.createStatement();
            final ResultSet resultSet = statement.executeQuery("SELECT * FROM information WHERE KEY='" + key + "'");
            if(resultSet.next()) return resultSet.getString("value");
        }catch (SQLException e){
            e.printStackTrace();
        }
        return "";
    }


}
