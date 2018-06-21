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

import main.java.model.Student;


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
            System.out.println("数据库连接成功");
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
            }
        }
        result = pstmt.executeUpdate();     //返回成功失败状态值
        return result;
    }


    /**
     * 查询单条数据
     * 多条数据时只返回最后一行
     *
     * @param sql
     * @param params
     * @return 列名-值 映射
     * @throws SQLException
     */
    public Map<String, Object> findSingleResult(String sql, List<Object> params) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        pstmt = connection.prepareStatement(sql);
        if (params != null && !params.isEmpty()) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
        }
        resultSet = pstmt.executeQuery();   //返回查询结果
        ResultSetMetaData metaData = resultSet.getMetaData();   //ResultMetaData用于处理列数据
        int cloLen = metaData.getColumnCount(); //列的数量

        while (resultSet.next()) {
            for (int i = 1; i <= cloLen; i++) {
                String cols_name = metaData.getColumnName(i);   //列名
                Object cols_value = resultSet.getObject(cols_name);//列对应的数据
                if (cols_value == null) {
                    cols_value = "";
                }
                map.put(cols_name, cols_value);
            }
        }
        return map;
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
     * 利用反射机制查询单条记录
     *
     * @param sql
     * @param params
     * @param cls
     * @param <T>
     * @return 查询到的数据作为一个对象返回
     * @throws Exception
     */
    public <T> T findSingleRefResult(String sql, List<Object> params, Class<T> cls) throws Exception {
        T resultObject = null;
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
            //通过反射创建一个实例
            //查询出每一列的列名,然后根据反射来给泛型对象的域设定值,最后返回这个泛型对象
            resultObject = cls.newInstance();
            for (int i = 0; i < cols_len; i++) {
                String cols_name = metaData.getColumnName(i + 1);
                Object cols_value = resultSet.getObject(cols_name);
                if (cols_value == null) {
                    cols_value = "";
                }
                Field field = cls.getDeclaredField(cols_name);  //获取泛型类中定义的域
                field.setAccessible(true);
                field.set(resultObject, cols_value); //给某个域设值
            }
        }
        return resultObject;
    }


    /**
     * 通过反射机制查询多条记录
     *
     * @param sql
     * @param params
     * @param cls
     * @return
     * @throws Exception
     */
    public <T> List<T> findMlutiRefResult(String sql, List<Object> params,
                                          Class<T> cls) throws Exception {
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
                sql += fields[i].getName() + " = ?, ";
                params.add(val);
            }
        }

        //如果参数对象的域都不为空,sql语句就不为空
        if ((!sql.equals("")) && sql != "") {
            sql = sql.substring(0, sql.length() - 1);
            String table = o.getClass().getName();  //参数对象的类的名字,也就是表名
//            sql = "DELETE FROM " + table.substring(table.lastIndexOf(".") + 1, table.length())
//                    + " WHRER " + sql;

//            sql = "delete from Student where student_no = ? , student_pw = ? , student_name = ? , student_target = ? , student_special = ? , student_is_manager = ? ";
            sql = "delete from Student where student_no = ?";

            System.out.println(sql);
            reNumber = updateByPreparedStatement(sql, params);
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
        JDBCHelper helper = main.java.db.JDBCHelper.getInstance();
        String sql = "SELECT * FROM Student";
        try {
//            Map<String, Object> result = helper.findSingleResult(sql, null);
//            System.out.println(result.get("student_no"));

            Student student = helper.findSingleRefResult(sql, null, Student.class);
            System.out.println(student.getStudent_no());

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
