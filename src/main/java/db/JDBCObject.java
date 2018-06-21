package main.java.db;

public class JDBCObject {
    public void save(JDBCDao.SaveListerner listener) {
        JDBCHelper helper = JDBCHelper.getInstance();
        try {
            int result = helper.save(this);
            if (result > 0) {
                listener.onSucceed();
            } else {
                listener.onFailed(new Exception("save failed!"));
            }
        } catch (Exception e) {
            listener.onFailed(e);
        }
    }

    public void delete(JDBCDao.DeleteListener listener) {
        JDBCHelper helper = JDBCHelper.getInstance();
        try {
            int result = helper.delete(this);
            if (result > 0) {
                listener.onSucceed();
            } else {
                listener.onFailed(new Exception("delete failed!"));
            }
        } catch (Exception e) {
            listener.onFailed(e);
        }
    }
}
