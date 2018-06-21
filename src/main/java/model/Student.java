package main.java.model;

import main.java.db.JDBCDao;
import main.java.db.JDBCObject;

public class Student extends JDBCObject {
    private String student_no;
    private String student_pw;
    private String student_name;
    private String student_target;
    private String student_special;
    private int student_is_manager = 0;

    @Override
    public String toString() {
        return "Student{}";
    }

    public String getStudent_no() {
        return student_no;
    }

    public void setStudent_no(String student_no) {
        this.student_no = student_no;
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public String getStudent_pw() {
        return student_pw;
    }

    public void setStudent_pw(String student_pw) {
        this.student_pw = student_pw;
    }

    public String getStudent_target() {
        return student_target;
    }

    public void setStudent_target(String student_target) {
        this.student_target = student_target;
    }

    public String getStudent_special() {
        return student_special;
    }

    public void setStudent_special(String student_special) {
        this.student_special = student_special;
    }

    public int getStudent_is_manager() {
        return student_is_manager;
    }

    public void setStudent_is_manager(int student_is_manager) {
        this.student_is_manager = student_is_manager;
    }

}
