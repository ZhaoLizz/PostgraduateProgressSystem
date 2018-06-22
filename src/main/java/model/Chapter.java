package main.java.model;

import main.java.db.JDBCObject;

public class Chapter extends JDBCObject {
    private String subject_name;
    private String chapter_name;
    private Integer chatper_index;

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

    public int getChatper_index() {
        return chatper_index;
    }

    public void setChatper_index(int chatper_index) {
        this.chatper_index = chatper_index;
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "subject_name='" + subject_name + '\'' +
                ", chapter_name='" + chapter_name + '\'' +
                ", chatper_index=" + chatper_index +
                '}';
    }
}
