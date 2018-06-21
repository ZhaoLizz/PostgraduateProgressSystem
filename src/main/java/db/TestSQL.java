package main.java.db;

import java.sql.*;

public class TestSQL {

    public static void main(String[] args) {
        String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

        String dbURL = "jdbc:sqlserver://localhost:1433;DatabaseName=Test";

        String userName = "sa";
        String userPwd = "tianchao1";

        Connection dbConn = null;

        try {
            Class.forName(driverName);
            dbConn = DriverManager.getConnection(dbURL, userName, userPwd);
            System.out.println("连接成功");

            System.out.println("查询");
            Statement stmt = dbConn.createStatement();
            String sql = "SELECT * FROM STUDENT";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String student_no = rs.getString("student_no");
                System.out.println(student_no);
            }

            rs.close();
            stmt.close();
            dbConn.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
