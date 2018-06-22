package main.java.model;

import main.java.db.JDBCObject;

public class Subject extends JDBCObject {
    private String subject_name;
    private int  subject_chapter_num;   //章节数目
    private String subject_refer_material;  //参考资料

    @Override
    public String toString() {
        return "Subject{" +
                "subject_name='" + subject_name + '\'' +
                ", subject_chapter_num=" + subject_chapter_num +
                ", subject_refer_material='" + subject_refer_material + '\'' +
                '}';
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public int getSubject_chapter_num() {
        return subject_chapter_num;
    }

    public void setSubject_chapter_num(int subject_chapter_num) {
        this.subject_chapter_num = subject_chapter_num;
    }

    public String getSubject_refer_material() {
        return subject_refer_material;
    }

    public void setSubject_refer_material(String subject_refer_material) {
        this.subject_refer_material = subject_refer_material;
    }
}
