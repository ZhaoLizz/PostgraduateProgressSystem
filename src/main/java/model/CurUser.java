package main.java.model;

/**
 * 单例对象用于存放当前登录用户的实例
 */
public class CurUser {
    private static volatile CurUser sCurUser;
    private String student_no;
    private boolean student_is_manager = false;


    private CurUser() {
    }

    public static CurUser getInstance() {
        if (null == sCurUser) {
            synchronized (CurUser.class) {
                if (null == sCurUser) {
                    sCurUser = new CurUser();
                }
            }
        }
        return sCurUser;
    }

    public String getStudent_no() {
        return student_no;
    }

    public void setStudent_no(String student_no) {
        this.student_no = student_no;
    }

    public boolean isStudent_is_manager() {
        return student_is_manager;
    }

    public void setStudent_is_manager(boolean student_is_manager) {
        this.student_is_manager = student_is_manager;
    }
}
