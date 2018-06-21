package main.java.db;

public class JDBCDao {
    public interface SaveListerner{
        void onSucceed();

        void onFailed(Exception e);
    }

    public interface DeleteListener {
        void onSucceed();

        void onFailed(Exception e);
    }





}
