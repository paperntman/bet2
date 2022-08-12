package main;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;

public class Config {
    private static Dotenv dotenv = Dotenv.load();
    public static String get(String key){
        return dotenv.get(key.toUpperCase());
    }

    public static String getDB(String key){
        try{

            String host = "ec2-44-196-223-128.compute-1.amazonaws.com";
            String database = "d1baaddau2fm97";
            String user = "qkdvzhzhtuzbca";
            String password = "627158973009f8fe4b299076b18700f4472cb2cc5dff78e752af6c24df6f46b4";
            int port = 5432;

            Connection connection = connection = DriverManager.getConnection(String.format("jdbc:postgresql://%s:%d/%s", host, port, database), user, password);
            final Statement statement = connection.createStatement();
            final ResultSet resultSet = statement.executeQuery("SELECT * FROM information WHERE KEY='" + key + "'");
            if(resultSet.next()) return resultSet.getString("value");
        }catch (SQLException e){
            e.printStackTrace();
        }
        return "";
    }
}
