package main.java.utils;

/**
 * 存放一些常量
 */
public class Statics {
    //连接数据库用到的常量
    public static final String DBURL = "jdbc:sqlserver://localhost;database=TextbookAdmin";
    public static final String USERNAME = "bao";
    public static final String PASSWORD = "123456";

    //教师身份标记
    public static final String SIMPLE_TEACHER = "0";
    public static final String ADMIN_TEACHER = "1";
    //当前用户的用户名,在查询每个老师所管理的班级时会用到
    public static String CURR_USERNAME;

    //用户身份标记
    public static final String TYPE_TEACHRE = "teacher";
    public static final String TYPE_ADMIN = "admin";
    public static final String TYPE_CLASS = "class";
    public static String TYPE_CURR;




}
