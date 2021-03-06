package main.java.db;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import jdk.nashorn.internal.scripts.JD;
import main.java.model.Student;
import main.java.utils.Toast;


public class JDBCHelper {
    private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String URL = "jdbc:sqlserver://localhost:1433;DatabaseName=Test";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "tianchao1";

    private Connection connection;
    private PreparedStatement pstmt;
    private ResultSet resultSet;

    private static volatile JDBCHelper sInstance;

    private JDBCHelper() {

    }

    /**
     * 单例模式
     *
     * @return
     */
    public static JDBCHelper getInstance() {
        if (null == sInstance) {
            synchronized (JDBCHelper.class) {
                if (null == sInstance) {
                    sInstance = new JDBCHelper();
                }
            }
        }
        sInstance.initJdbc();
        return sInstance;
    }

    private void initJdbc() {
        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }


    /**
     * 增删改
     *
     * @param sql
     * @param params sql参数,填到preparedStatement的???里面
     * @return
     * @throws SQLException
     */
    public int updateByPreparedStatement(String sql, List<Object> params) throws SQLException {
        int result = -1;
        pstmt = connection.prepareStatement(sql);
        int index = 1;

        //如果list不为空,遍历list,为prepareStatment对象设置(K,V),K是index,V是list里面的对象
        if (params != null && !params.isEmpty()) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(index++, params.get(i));
                Toast.logger(params.get(i).toString());
            }
        }
        result = pstmt.executeUpdate();     //返回成功失败状态值
        return result;
    }



    public List<Map<String, Object>> findMlutiResult(String sql, List<Object> params) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        pstmt = connection.prepareStatement(sql);
        if (params != null && !params.isEmpty()) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
        }

        resultSet = pstmt.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int cols_len = metaData.getColumnCount();
        while (resultSet.next()) {
            Map<String, Object> map = new HashMap<>();
            for (int i = 0; i < cols_len; i++) {
                String cols_name = metaData.getColumnName(i + 1);
                Object cols_value = resultSet.getObject(cols_name);
                if (cols_value == null) {
                    cols_value = "";
                }
                map.put(cols_name, cols_value);
            }
            list.add(map);
        }
        return list;
    }




    /**
     * 通过反射机制查询多条记录
     *
     * @param cls
     * @return
     * @throws Exception
     */
    public <T> List<T> query(Object condition, Class<T> cls) throws Exception {
        //通过条件对象构造sql查询语句
        String sql = "";
        Class<?> baseDao = condition.getClass();
        List<Object> params = new ArrayList<>();
        Field[] fields = baseDao.getDeclaredFields();

        //通过参数对象的域构造查询条件
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            Object val = fields[i].get(condition);  //获取域的值
            if (val != null) {
                sql += fields[i].getName() + "=? and ";
                params.add(val);
            }
        }

        if ((!sql.equals("")) && sql != "") {
            sql = sql.substring(0, sql.length() - 4);
            String table = condition.getClass().getName();
            sql = "select * from " + table.substring(table.lastIndexOf(".") + 1, table.length())
                    + " where " + sql;

            System.out.println(sql);
        } else {
            //处理查询全部行的条件
            String table = condition.getClass().getName();
            sql = "select * from " + table.substring(table.lastIndexOf(".") + 1, table.length());
            System.out.println(sql);
        }


        List<T> list = new ArrayList<T>();
        int index = 1;
        pstmt = connection.prepareStatement(sql);
        if (params != null && !params.isEmpty()) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(index++, params.get(i));
            }
        }
        resultSet = pstmt.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int cols_len = metaData.getColumnCount();
        while (resultSet.next()) {
            //通过反射机制创建一个实例
            T resultObject = cls.newInstance();
            for (int i = 0; i < cols_len; i++) {
                String cols_name = metaData.getColumnName(i + 1);
                Object cols_value = resultSet.getObject(cols_name);
                if (cols_value == null) {
                    cols_value = "";
                }
                Field field = cls.getDeclaredField(cols_name);
                field.setAccessible(true); //打开javabean的访问权限
                field.set(resultObject, cols_value);
            }
            list.add(resultObject);
        }
        return list;
    }


    public int save(Object o) throws Exception {
        int reNumber = -1;
        String sql = "";
        Class<?> baseDao = o.getClass();
        List<Object> params = new ArrayList<>();    //参数对象的域的值的集合
        Field[] fields = baseDao.getDeclaredFields();   //获取传入参数对象声明的域

        //通过这样遍历的方式获取到参数对象的每个值
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            Object val = fields[i].get(o);
            if (val != null) {
                sql += fields[i].getName() + ",";
                params.add(val);
            }
        }

        //如果参数对象的域都不为空,sql语句就不为空
        if ((!sql.equals("")) && sql != "") {
            sql = sql.substring(0, sql.length() - 1);
            String table = o.getClass().getName();  //参数对象的类的名字,也就是表名
            String parasStr = "";
            for (int i = 0; i < params.size(); i++) {
                parasStr += "?,";
            }

            parasStr = parasStr.substring(0, parasStr.length() - 1);
            sql = "insert into " + table.substring(table.lastIndexOf(".") + 1, table.length())
                    + "(" + sql + ") values (" + parasStr + ")";

            System.out.println(sql);
            reNumber = updateByPreparedStatement(sql, params);
        }
        return reNumber;
    }

    public int delete(Object o) throws Exception {
        int reNumber = -1;
        String sql = "";
        Class<?> baseDao = o.getClass();
        List<Object> params = new ArrayList<>();    //参数对象的域的值的集合
        Field[] fields = baseDao.getDeclaredFields();   //获取传入参数对象声明的域

        //通过这样遍历的方式获取到参数对象的每个值
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            Object val = fields[i].get(o);
            if (val != null) {
                sql += fields[i].getName() + "=? and ";
                params.add(val);
            }
        }

        //如果参数对象的域都不为空,sql语句就不为空
        if ((!sql.equals("")) && sql != "") {
            sql = sql.substring(0, sql.length() - 4);   //删掉最后的冗余and和空格
            String table = o.getClass().getName();  //参数对象的类的名字,也就是表名
            sql = "delete from " + table.substring(table.lastIndexOf(".") + 1, table.length())
                    + " where " + sql;

            System.out.println(sql);
            reNumber = updateByPreparedStatement(sql, params);
        }
        return reNumber;
    }

    public int update(Object newValue, Object condition) throws Exception {
        int reNumber = -1;
        String sql = "";
        Class<?> baseDao = newValue.getClass();
        List<Object> params = new ArrayList<Object>();
        Field[] fields = baseDao.getDeclaredFields();// 返回Field数组
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            Object val = fields[i].get(newValue);
            if (val != null) {
                sql += fields[i].getName() + "=?,";
                params.add(val);
            }
        }

        if ((!sql.equals("")) && sql != "") {
            sql = sql.substring(0, sql.length() - 1);
            String table = newValue.getClass().getName();

            //构造where子句
            String whereStr = "";
            fields = condition.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                Object val = fields[i].get(condition);
                if (val != null) {
                    whereStr += fields[i].getName() + "=? and ";
                    params.add(val);
                }
            }
            whereStr = whereStr.substring(0, whereStr.length() - 4);

            sql = "update " + table.substring(table.lastIndexOf(".") + 1, table.length())
                    + " set " + sql + " where " + whereStr;

            System.out.println(sql);
            reNumber = updateByPreparedStatement(sql, params);
            return reNumber;
        }

        return reNumber;
    }


    /**
     * 释放数据库连接
     */
    public void releaseConn() {
        try {
            if (resultSet != null) resultSet.close();
            if (pstmt != null) pstmt.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Student student = new Student();
//        student.setStudent_name("jiang");
//        student.setStudent_special("Science");
//        student.setStudent_no("123");
//        student.setStudent_target("Tinghua");
//        student.setStudent_pw("123");
//        student.setStudent_is_manager(0);
        student.query(Student.class, new JDBCDao.QueryListener<Student>() {
            @Override
            public void onSucceed(List<Student> result) {
                for (Student s : result) {
                    System.out.println(s);
                }
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }

}
