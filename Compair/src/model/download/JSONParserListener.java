package model.download;

/**
 * Created by alextelek on 24/11/14.
 */
public interface JSONParserListener<T> {

    /**
     * This method will be called, when the
     * JSONParse class finished the parsing on
     * a JSON file
     * @param result the parsed json data (HashMap)
     */
    public void onJSONParseFinished(String type, T result);
}
