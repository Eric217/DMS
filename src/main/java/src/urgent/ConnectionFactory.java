package src.urgent;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.Properties;

public class ConnectionFactory {

    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PWD;

    private static final ConnectionFactory FACTORY = new ConnectionFactory();

    private Connection conn;

    static {
        Properties prop = new Properties();
        try {
            InputStream in =
                    ConnectionFactory.class.getClassLoader().getResourceAsStream(
                            "jdbc.properties");
            prop.load(in);
            in.close();
        } catch (Exception e) {
            System.out.println("XXXXXXXXXXX 配置文件读取错误 XXXXXXXXXXX");
        }
        DB_URL = prop.getProperty("url");
        DB_USER = prop.getProperty("username");
        DB_PWD = prop.getProperty("password");
    }

    private ConnectionFactory(){}

    public static ConnectionFactory shared() {
        return FACTORY;
    }

    /**
     * Driver make connection.
     * @throws SQLTimeoutException
     * @throws SQLException
     */
    public Connection makeConnection() throws SQLException {
        System.out.println("DB_URL = " + DB_URL);

        return DriverManager.getConnection(DB_URL, DB_USER, DB_PWD);
    }

    public void close() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            conn = null;
        }

    }

}
