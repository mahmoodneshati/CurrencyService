package util;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import core.Currency;
import core.trigger.CurrencyThresholdTrigger;
import core.trigger.TriggerCaller;

import java.beans.PropertyVetoException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Properties;


/**
 * Created by neshati on 1/24/2017.
 * Behpardaz
 */
public class DBHelper {

    private static  int MIN_POOL_SIZE = 5;
    private static  int Acquire_Increment = 5;
    private static  int MAX_POOL_SIZE = 20;
    ComboPooledDataSource cpds = new ComboPooledDataSource();
    Properties prop = new Properties();



    private String user = "sa";
    private String password = "sdp@b3h";
    private String dbName = "BPJ_SDP_MS_Currency";
    private String host = "172.16.4.199";
    private String port = "1433";
    private String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private String connectionString = "jdbc:sqlserver://" +
            host +
            "\\SQLEXPRESS:" +
            port +
            ";databaseName=" +
            dbName +
            ";" ;
/*
            +
            "user=" +
            user +
            ";" +
            "password=" +
            password;
*/

    private void init(){
        try {
            InputStream input = new FileInputStream("config.properties");
            prop.load(input);
            MIN_POOL_SIZE = Integer.parseInt(prop.getProperty("MIN_POOL_SIZE"));
            Acquire_Increment = Integer.parseInt(prop.getProperty("Acquire_Increment"));
            MAX_POOL_SIZE = Integer.parseInt(prop.getProperty("MAX_POOL_SIZE"));
            user= prop.getProperty("user");
            password= prop.getProperty("password");
            dbName = prop.getProperty("dbName");
            host = prop.getProperty("host");
            port = prop.getProperty("port");
            driverName = prop.getProperty("driverName");

            connectionString = "jdbc:sqlserver://" +
                    host +
                    "\\SQLEXPRESS:" +
                    port +
                    ";databaseName=" +
                    dbName +
                    ";" ;

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void initConnectionPooling(){
        try {
            cpds.setDriverClass(driverName); //loads the jdbc driver
            cpds.setJdbcUrl(connectionString);
            cpds.setUser(user);
            cpds.setPassword(password);
            cpds.setMinPoolSize(MIN_POOL_SIZE);
            cpds.setAcquireIncrement(Acquire_Increment);
            cpds.setMaxPoolSize(MAX_POOL_SIZE);
        }
        catch (PropertyVetoException e) {
            e.printStackTrace();
        }

    }
    private Connection getConnection() {
        try {

            return cpds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static DBHelper dbHelper;

    public static DBHelper getInstance() {
        if (dbHelper == null) {
            dbHelper = new DBHelper();
            dbHelper.initConnectionPooling();
        }
        return dbHelper;
    }

    private DBHelper() {

    }


    public int insertCurrency(Currency currency) {
        if (currency.englishName == null)
            return -1;
        Connection conn = getConnection();

        try {
            PreparedStatement statement
                    = conn.prepareStatement("INSERT INTO currencyValue  VALUES(?,?,?)");
            Calendar cal = Calendar.getInstance();
            java.sql.Timestamp timestamp = new java.sql.Timestamp(cal.getTimeInMillis());
            statement.setString(1, currency.englishName);
            statement.setDouble(2, currency.price);
            statement.setTimestamp(3, timestamp);
            int result = statement.executeUpdate();
            statement.close();
            conn.close();
            return result;
        } catch (Exception e) {
            try {
                assert conn != null;
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return 0;
    }

    public int insertCurrencyTreshhold(CurrencyThresholdTrigger threshold) {
        Connection conn = getConnection();
        try {
            PreparedStatement statement
                    = conn.prepareStatement("INSERT INTO currencythreshold  VALUES(?,?,?,?)");
            Calendar cal = Calendar.getInstance();
            java.sql.Timestamp timestamp = new java.sql.Timestamp(cal.getTimeInMillis());
            statement.setString(1, threshold.currencyType);
            statement.setDouble(2, threshold.value);
            statement.setInt(3, threshold.goUpper);
            statement.setTimestamp(4, timestamp);
            int result = statement.executeUpdate();
            statement.close();
            conn.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int deleteCurrencyTreshhold(CurrencyThresholdTrigger threshold) {
        Connection conn = getConnection();
        try {
            PreparedStatement statement
                    = conn.prepareStatement("DELETE TOP (1) FROM currencythreshold " +
                    "WHERE currencyType = ? AND VALUE = ? AND goUpper = ?");

            statement.setString(1, threshold.currencyType);
            statement.setDouble(2, threshold.value);
            statement.setInt(3, threshold.goUpper);
            int result = statement.executeUpdate();
            statement.close();
            conn.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static void main(String[] args) {
/*
        DBHelper.getInstance().insertCurrencyTreshhold(new CurrencyThreshold("USD", 1, 1));
        DBHelper.getInstance().deleteCurrencyTreshhold(new CurrencyThreshold("USD", 1, 1));
*/
    }


    public HashMap<String, Currency> loadLastPrice() {
        HashMap<String, Currency> out = new HashMap();
        String sql = "SELECT id, currencyName, value FROM currencyValue " +
                "WHERE (id IN (SELECT MAX(id) FROM currencyValue GROUP BY currencyName))";
        Connection connection = getConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String currencyName = resultSet.getString("currencyName");
                double value = resultSet.getDouble("value");
                Currency currency = new Currency(null, currencyName, value);
                out.put(currencyName, currency);
            }
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }

    public ArrayList<TriggerCaller> getValidCurrencyTresholdsUpper(String englishName,
                                                              Double oldPrice,
                                                              Double newPrice) {
        Connection conn = getConnection();
        ArrayList<TriggerCaller> out = new ArrayList<>();
        try {
            String sql = "SELECT DISTINCT " +
                    "      [currencyType]\n" +
                    "      ,[VALUE]\n" +
                    "      ,[goUpper]\n" +
                    "  FROM [currencythreshold]\n" +
                    "  WHERE [goUpper] =  "+ CurrencyThresholdTrigger.GOUP + " AND (VALUE BETWEEN ? AND ?) AND [currencyType] = ?";
            PreparedStatement statement
                    = conn.prepareStatement(sql);

            statement.setDouble(1, oldPrice);
            statement.setDouble(2, newPrice);
            statement.setString(3, englishName);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                CurrencyThresholdTrigger ct = new CurrencyThresholdTrigger(
                        resultSet.getString("currencyType"),
                        resultSet.getDouble("value"),
                        resultSet.getInt("goUpper"),null);
                out.add(ct);
            }
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;


    }

    public ArrayList<TriggerCaller> getValidCurrencyTresholdsLower(String englishName,
                                                                   Double oldPrice,
                                                                   Double newPrice) {
        Connection conn = getConnection();
        ArrayList<TriggerCaller> out = new ArrayList<>();
        try {
            String sql = "SELECT DISTINCT " +
                    "      [currencyType]\n" +
                    "      ,[VALUE]\n" +
                    "      ,[goUpper]\n" +
                    "  FROM [currencythreshold]\n" +
                    "  WHERE [goUpper] =  "+ CurrencyThresholdTrigger.GODOWN + " AND (VALUE BETWEEN ? AND ?) AND [currencyType] = ?";
            PreparedStatement statement
                    = conn.prepareStatement(sql);

            statement.setDouble(1, newPrice);
            statement.setDouble(2, oldPrice);
            statement.setString(3, englishName);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                CurrencyThresholdTrigger ct = new CurrencyThresholdTrigger(
                        resultSet.getString("currencyType"),
                        resultSet.getDouble("value"),
                        resultSet.getInt("goUpper"),null);
                out.add(ct);
            }
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;


    }


    public double[] getLastWeekMeanSDTEV(String englishName) {
        Connection conn = getConnection();
        double outValue[] = new double[2];


        try {
            String sql = "SELECT " +
                    "      avg([value]) as avg " +
                    " ,stdev([value]) as std " +
                    "  FROM currencyValue " +
                    "  WHERE ins_datetime >= DATEADD(day,-7, GETDATE())" +
                    "  and currencyName = '"+"?"+"'";

            assert conn != null;
            PreparedStatement statement
                    = conn.prepareStatement(sql);

            statement.setString(1, englishName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                outValue[0] = resultSet.getDouble("avg");
                outValue[1] = resultSet.getDouble("std");
            }
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return outValue;
    }
}
