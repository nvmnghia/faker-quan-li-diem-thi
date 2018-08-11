package util;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import config.Config;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataUtl {

    /**
     * DB
     */

    private static Connection connection = null;

    public static Connection getDBConnection() throws SQLException {
        if (connection == null) {
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setUseUnicode(true);
            dataSource.setEncoding("utf-8");
            dataSource.setUser(Config.DB.USERNAME);
            dataSource.setPassword(Config.DB.PASSWORD);

            connection = dataSource.getConnection();
        }

        return connection;
    }

    public static Statement getDBStatement() throws SQLException {
        if (statement == null) {
            statement = getDBConnection().createStatement();
        }

        return statement;
    }

    private static Statement statement = null;

    public static ResultSet queryDB(String dbName, String query) throws SQLException {
        Statement stm = getDBStatement();

        stm.executeQuery("USE `" + dbName + "`");
        return stm.executeQuery(query);
    }

    public static int updateDB(String dbName, String query) throws SQLException {
        Statement stm = getDBStatement();

        stm.executeQuery("USE `" + dbName + "`");
        return stm.executeUpdate(query);
    }

    public static int insertAndGetID(String dbName, String insertQuery) throws SQLException {
        Statement stm = getDBStatement();

        stm.executeQuery("USE `" + dbName + "`");
        return stm.executeUpdate(insertQuery, Statement.RETURN_GENERATED_KEYS);
    }

    public static void batchInsert(String dbName, List<String> insertQueries) throws SQLException {
        Statement stm = getDBStatement();

        stm.executeQuery("USE " + dbName);

        for (String query : insertQueries) {
            stm.addBatch(query);
        }

        stm.executeBatch();
    }

    public static int getAutoIncrementID(PreparedStatement pstm) throws SQLException {
        ResultSet rs = pstm.getGeneratedKeys();
        rs.next();
        return rs.getInt(1);
    }

    public static List<Integer> getAutoIncrementIDs(PreparedStatement pstm) throws SQLException {
        List<Integer> IDs = new ArrayList<>();
        ResultSet rs = pstm.getGeneratedKeys();

        while (rs.next()) {
            IDs.add(rs.getInt(1));
        }

        return IDs;
    }

    public static int getMaxIDOfTable(String dbName, String tableName) throws SQLException {
        ResultSet rs = DataUtl.queryDB(dbName, "SELECT id FROM " + tableName + " ORDER BY id DESC LIMIT 1");
        rs.next();

        int maxIDOfMergedDB = rs.getInt(1);
        System.out.println("Max ID of " + dbName + "." + tableName + ": " + maxIDOfMergedDB);

        return maxIDOfMergedDB;
    }

    public static void truncate(String dbName, String tableName) throws SQLException {
        DataUtl.queryDB(Config.DB.NAME, "SET FOREIGN_KEY_CHECKS = 0");

        Statement stm = getDBStatement();
        stm.executeQuery("USE `" + dbName + "`");
        stm.executeUpdate("TRUNCATE " + tableName);

        DataUtl.queryDB(Config.DB.NAME, "SET FOREIGN_KEY_CHECKS = 1");
    }
}
