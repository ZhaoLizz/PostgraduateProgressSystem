package main.java.model;

public class User {
    private static volatile User sUser;
    private String student_no;
    private boolean student_is_manager;


    private User() {
    }

    public static User getInstance() {
        if (null == sUser) {
            synchronized (User.class) {
                if (null == sUser) {
                    sUser = new User();
                }
            }
        }
        return sUser;
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
