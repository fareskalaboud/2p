package model.download;

/**
 * This is a useful callback interface. The activity can extend
 * the listener, and when the download is finished, the method will be
 * overridden
 * Created by alextelek on 24/11/14.
 */
public interface DownloadDataListener<T> {

    /**
     * This method will be called, when the AsyncTask
     * has finished the execution
     * @param result the object what the download
     *               returns (JSONArray)
     */
    public void onDownloadFinished(T result);
}
