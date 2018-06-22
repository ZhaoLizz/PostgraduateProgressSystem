package main.java.model;

import main.java.db.JDBCObject;

public class Progress extends JDBCObject {
    private String student_no;
    private String subject_name;
    private String chapter_name;

    @Override
    public String toString() {
        return "Progress{" +
                "student_no='" + student_no + '\'' +
                ", subject_name='" + subject_name + '\'' +
                ", chapter_name='" + chapter_name + '\'' +
                '}';
    }

    public String getStudent_no() {
        return student_no;
    }

    public void setStudent_no(String student_no) {
        this.student_no = student_no;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public String getChapter_name() {
        return chapter_name;
    }

    public void setChapter_name(String chapter_name) {
        this.chapter_name = chapter_name;
    }
}
