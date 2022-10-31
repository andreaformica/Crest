/**
 *
 */
package hep.crest.data.repositories.externals;

/**
 * @author formica
 *
 */
public final class SqlRequests {

    /**
     * Where condition on HASH.
     */
    private static final String WHERE_HASH = " WHERE HASH=? ";
    /**
     * Insert.
     */
    private static final String INSERT_INTO = "INSERT INTO ";
    /**
     * Insert.
     */
    private static final String UPDATE = "UPDATE ";

    /**
     * Private ctor.
     */
    private SqlRequests() {
    }

    /**
     * @param tablename the String
     * @return String
     */
    public static final String getExistsHashQuery(String tablename) {
        return "select HASH from " + tablename + WHERE_HASH;
    }

    /**
     * @param tablename the String
     * @return String
     */
    public static final String getStreamerInfoQuery(String tablename) {
        return "select STREAMER_INFO "
               + " from " + tablename + WHERE_HASH;
    }

    /**
     * @param tablename the String
     * @return String
     */
    public static final String getDataQuery(String tablename) {
        return "select DATA "
               + " from " + tablename + WHERE_HASH;
    }

    /**
     * @param tablename the String
     * @return String
     */
    public static final String getInsertDataQuery(String tablename) {
        return INSERT_INTO + tablename
               + "(HASH, DATA) "
               + " VALUES (?,?)";
    }


    /**
     * @param tablename the String
     * @return String
     */
    public static final String getInfoDataQuery(String tablename) {
        return "select STREAMER_INFO "
               + " from " + tablename + WHERE_HASH;
    }

    /**
     * @param tablename the String
     * @return String
     */
    public static final String getInsertInfoQuery(String tablename) {
        return INSERT_INTO + tablename
               + "(HASH, STREAMER_INFO) "
               + " VALUES (?,?)";
    }

    /**
     * @param tablename the String
     * @return String
     */
    public static final String getUpdateInfoQuery(String tablename) {
        return UPDATE + tablename
               + " set STREAMER_INFO=? "
               + WHERE_HASH;
    }

    /**
     * @param tablename the String
     * @return String
     */
    public static final String getDeleteQuery(String tablename) {
        return "DELETE FROM " + tablename + WHERE_HASH;
    }

    /**
     * Get payload info query using range.
     *
     * @param iovtablename
     * @param payloadtablename
     * @return
     */
    public static final String getRangeIovPayloadQuery(String iovtablename, String payloadtablename) {
        return "select iv.TAG_NAME, iv.SINCE, iv.INSERTION_TIME, iv.PAYLOAD_HASH, "
               + " pyld.VERSION, pyld.OBJECT_TYPE, " + " pyld.DATA_SIZE from " + iovtablename + " iv "
               + " LEFT JOIN " + payloadtablename + " pyld " + " ON iv.PAYLOAD_HASH=pyld.HASH "
               + " where iv.TAG_NAME=? AND iv.SINCE>=COALESCE(" + "  (SELECT max(iov2.SINCE) FROM "
               + iovtablename + " iov2 "
               + "  WHERE iov2.TAG_NAME=? AND iov2.SINCE<=? AND iov2.INSERTION_TIME<=? ),0)"
               + " AND iv.SINCE<=? AND iv.INSERTION_TIME<=? "
               + " ORDER BY iv.SINCE ASC, iv.INSERTION_TIME DESC";
    }
}
