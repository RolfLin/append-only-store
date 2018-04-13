package indexingTopology.metadata;

public class HDFSBoltConfig {
    /**
     * Used for maintaining persistent meta logs in HDFS. In case reconstruction
     * is needed such as when the system crashed, meta data stored in {@link HDFSBoltConfig#HDFS_META_LOG_PATH}
     * is used for reconstruction.
     */
    public static boolean RECONSTRUCT = true;
    public static final String HDFS_LOCAL_HOST = "hdfs://localhost:8866";
    public static final String HDFS_META_LOG_RELATIVE_PATH = "/user/billlin/data/";
    public static final String HDFS_META_LOG_PATH = HDFS_LOCAL_HOST + HDFS_META_LOG_RELATIVE_PATH;
}
