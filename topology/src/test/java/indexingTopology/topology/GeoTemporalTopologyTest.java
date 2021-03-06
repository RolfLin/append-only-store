package indexingTopology.topology;


import indexingTopology.bolt.InputStreamReceiverBolt;
import indexingTopology.bolt.InputStreamReceiverBoltServer;
import indexingTopology.bolt.GeoTemporalQueryCoordinatorBoltBolt;
import indexingTopology.bolt.QueryCoordinatorBolt;
import indexingTopology.api.client.GeoTemporalQueryClient;
import indexingTopology.api.client.GeoTemporalQueryRequest;
import indexingTopology.api.client.IngestionClientBatchMode;
import indexingTopology.api.client.QueryResponse;
import indexingTopology.config.TopologyConfig;
import indexingTopology.common.data.DataSchema;
import indexingTopology.common.data.DataTuple;
import indexingTopology.common.logics.DataTupleMapper;
import indexingTopology.common.logics.DataTuplePredicate;
import indexingTopology.util.AvailableSocketPool;
import indexingTopology.util.taxi.City;
import indexingTopology.util.taxi.ZOrderCoding;
import junit.framework.TestCase;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.generated.KillOptions;
import org.apache.storm.generated.StormTopology;
import org.junit.Test;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by robert on 16/5/17.
 */
public class GeoTemporalTopologyTest extends TestCase {

    TopologyConfig config = new TopologyConfig();

    AvailableSocketPool socketPool = new AvailableSocketPool();

    LocalCluster cluster;

    boolean setupDone = false;

    boolean tearDownDone = false;

    public void setUp() {
        if (!setupDone) {
            try {
                Runtime.getRuntime().exec("mkdir -p ./target/tmp");
            } catch (IOException e) {
                e.printStackTrace();
            }
            config.dataChunkDir = "./target/tmp";
            config.metadataDir = "./target/tmp";
            config.HDFSFlag = false;
            config.CHUNK_SIZE = 1024 * 1024;
            config.previousTime = Integer.MAX_VALUE;
            System.out.println("dataChunkDir is set to " + config.dataChunkDir);
            cluster = new LocalCluster();
            setupDone = true;
        }
    }

    public void tearDown() {
        if (!tearDownDone) {
            try {
                Runtime.getRuntime().exec("rm ./target/tmp/*");
            } catch (IOException e) {
                e.printStackTrace();
            }
            cluster.shutdown();
            tearDownDone = true;
        }
    }

    @Test
    public void testGeoRangeQuery() throws InterruptedException {
        boolean fullyExecuted = false;

        DataSchema rawSchema = new DataSchema();
        rawSchema.addIntField("id");
        rawSchema.addDoubleField("x");
        rawSchema.addDoubleField("y");
        rawSchema.addLongField("timestamp");
        rawSchema.setTemporalField("timestamp");

        DataSchema schema = rawSchema.duplicate();
        schema.addIntField("zcode");
        schema.setPrimaryIndexField("zcode");

        final double x1 = 0.0;
        final double x2 = 100.0;
        final double y1 = 0.0;
        final double y2 = 100.0;
        final int partitions = 128;

        City city = new City(x1, x2, y1, y2, partitions);
        ZOrderCoding zOrderCoding = city.getzOrderCoding();

        Integer lowerBound = 0;
        Integer upperBound = city.getMaxZCode();

        int ingestionPort = socketPool.getAvailablePort();
        int queryPort = socketPool.getAvailablePort();

        QueryCoordinatorBolt<Integer> queryCoordinatorBolt = new GeoTemporalQueryCoordinatorBoltBolt<>(lowerBound,
                upperBound, queryPort, city, config, schema);

        InputStreamReceiverBolt dataSource = new InputStreamReceiverBoltServer(rawSchema, ingestionPort, config);

        TopologyGenerator<Integer> topologyGenerator = new TopologyGenerator<>();

        DataTupleMapper dataTupleMapper = new DataTupleMapper(rawSchema, (Serializable & Function<DataTuple, DataTuple>) t -> {
            double lon = (double)schema.getValue("x", t);
            double lat = (double)schema.getValue("y", t);
            int zcode = city.getZCodeForALocation(lon, lat);
            t.add(zcode);
            t.add(System.currentTimeMillis());
            return t;
        });

        List<String> bloomFilterColumns = new ArrayList<>();
        bloomFilterColumns.add("id");

        StormTopology topology = topologyGenerator.generateIndexingTopology(schema, lowerBound, upperBound,
                false, dataSource, queryCoordinatorBolt, dataTupleMapper, bloomFilterColumns, config);

        Config conf = new Config();
        conf.setDebug(false);
        conf.setNumWorkers(1);

        conf.put(Config.WORKER_CHILDOPTS, "-Xmx2048m");
        conf.put(Config.WORKER_HEAP_MEMORY_MB, 2048);

        cluster.submitTopology("testGeoRangeQuery", conf, topology);
        IngestionClientBatchMode clientBatchMode = new IngestionClientBatchMode("localhost", ingestionPort,
                rawSchema, 1024);
        try {
            clientBatchMode.connectWithTimeout(50000);


            final int tuples = 1000 * 1000;

            for (int i = 0; i < tuples; i++) {
                int id = i % 100;
                double x = (double) (i % 1000);
                double y = (double) (i / 1000);
                long t = (long)i;
                DataTuple tuple = new DataTuple();
                tuple.add(id);
                tuple.add(x);
                tuple.add(y);
                tuple.add(t);
                try {
                    clientBatchMode.appendInBatch(tuple);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                clientBatchMode.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                // wait for the completion of insertion.
                clientBatchMode.waitFinish();
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            GeoTemporalQueryClient queryClient = new GeoTemporalQueryClient("localhost", queryPort);
            try {
                queryClient.connectWithTimeout(100000);
            } catch (IOException e) {
                e.printStackTrace();
            }


            try {

//                {// all ranges.
//                    GeoTemporalQueryRequest queryRequest = new GeoTemporalQueryRequest<>(Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Long.MIN_VALUE, Long.MAX_VALUE);
//
//                    QueryResponse response = queryClient.query(queryRequest);
//                    assertEquals(tuples, response.dataTuples.size());
//                }

                {// geo query with 10% selectivity on both dimension.
                    double qx1 = 10.0;
                    double qx2 = 19.9;
                    double qy1 = 10.0;
                    double qy2 = 19.9;

                    DataTuplePredicate predicate = t -> (double) schema.getValue("x", t) >= qx1 &&
                            (double) schema.getValue("x", t) <= qx2 &&
                            (double) schema.getValue("y", t) >= qy1 &&
                            (double) schema.getValue("y", t) <= qy2;

                    GeoTemporalQueryRequest queryRequest = new GeoTemporalQueryRequest<>(qx1, qx2, qy1, qy2, Long.MIN_VALUE, Long.MAX_VALUE, predicate);

                    QueryResponse response = queryClient.query(queryRequest);
                    assertEquals(tuples / 100 / 100, response.dataTuples.size());
                }
                fullyExecuted = true;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            queryClient.close();
            clientBatchMode.close();
            KillOptions killOptions = new KillOptions();
            killOptions.set_wait_secs(0);
            cluster.killTopologyWithOpts("testGeoRangeQuery", killOptions);

        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(fullyExecuted);
        socketPool.returnPort(ingestionPort);
        socketPool.returnPort(queryPort);
    }

    @Test
    public void testGeoRangeQueryWithBloomFilterOnVarchar() throws InterruptedException {
        boolean fullyExecuted = false;
        int ingestionPort = socketPool.getAvailablePort();
        int queryPort = socketPool.getAvailablePort();

        DataSchema rawSchema = new DataSchema();
        rawSchema.addIntField("id");
        rawSchema.addDoubleField("x");
        rawSchema.addDoubleField("y");
        rawSchema.addLongField("timestamp");;
        rawSchema.setTemporalField("timestamp");

        DataSchema schema = rawSchema.duplicate();
        schema.addIntField("zcode");
        schema.setPrimaryIndexField("zcode");

        final double x1 = 0.0;
        final double x2 = 1000.0;
        final double y1 = 0.0;
        final double y2 = 1000.0;
        final int partitions = 128;

        City city = new City(x1, x2, y1, y2, partitions);
        ZOrderCoding zOrderCoding = city.getzOrderCoding();

        Integer lowerBound = 0;
        Integer upperBound = city.getMaxZCode();

        QueryCoordinatorBolt<Integer> queryCoordinatorBolt = new GeoTemporalQueryCoordinatorBoltBolt<>(lowerBound,
                upperBound, queryPort, city, config, schema);

        InputStreamReceiverBolt dataSource = new InputStreamReceiverBoltServer(rawSchema, ingestionPort, config);

        TopologyGenerator<Integer> topologyGenerator = new TopologyGenerator<>();

        DataTupleMapper dataTupleMapper = new DataTupleMapper(rawSchema, (Serializable & Function<DataTuple, DataTuple>) t -> {
            double lon = (double)schema.getValue("x", t);
            double lat = (double)schema.getValue("y", t);
            int zcode = city.getZCodeForALocation(lon, lat);
            t.add(zcode);
            t.add(System.currentTimeMillis());
            return t;
        });

        List<String> bloomFilterColumns = new ArrayList<>();
        bloomFilterColumns.add("id");

        StormTopology topology = topologyGenerator.generateIndexingTopology(schema, lowerBound, upperBound,
                false, dataSource, queryCoordinatorBolt, dataTupleMapper, bloomFilterColumns, config);

        Config conf = new Config();
        conf.setDebug(false);
        conf.setNumWorkers(1);

        cluster.submitTopology("testGeoRangeQueryWithBloomFilterOnVarchar", conf, topology);
        IngestionClientBatchMode clientBatchMode = new IngestionClientBatchMode("localhost", ingestionPort,
                rawSchema, 1024);
        try {
            clientBatchMode.connectWithTimeout(100000);


            final int tuples = 1000 * 1000;

            int count = 0;
            for (int i = 0; i < tuples; i++) {
//                String id = Integer.toString(i % 100);
                int id = i % 100;
                if (id ==1)
                    count++;
                double x = (double) (i % 1000);
                double y = (double) (i / 1000);
                long t = (long)i;
                DataTuple tuple = new DataTuple();
                tuple.add(id);
                tuple.add(x);
                tuple.add(y);
                tuple.add(t);
                try {
                    clientBatchMode.appendInBatch(tuple);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                clientBatchMode.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                // wait for the completion of insertion.
                clientBatchMode.waitFinish();
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            GeoTemporalQueryClient queryClient = new GeoTemporalQueryClient("localhost", queryPort);
            try {
                queryClient.connectWithTimeout(50000);
            } catch (IOException e) {
                e.printStackTrace();
            }


            System.out.println("count:" + count);


            try {

                {// all ranges.
                    DataTuplePredicate predicate = t -> (double) schema.getValue("x", t) >= x1 &&
                            (double) schema.getValue("x", t) <= x2 &&
                            (double) schema.getValue("y", t) >= y1 &&
                            (double) schema.getValue("y", t) <= y2 ;
                    GeoTemporalQueryRequest queryRequest = new GeoTemporalQueryRequest<>(x1, x2, y1, y2, Long.MIN_VALUE,
                            Long.MAX_VALUE, predicate);

                    QueryResponse response = queryClient.query(queryRequest);
                    assertEquals(tuples, response.dataTuples.size());
                }

                {// geo query with 10% selectivity on both dimension.
                    double qx1 = 0.0;
                    double qx2 = 1000.0;
                    double qy1 = 0.0;
                    double qy2 = 1000.0;

                    DataTuplePredicate predicate = t -> (double) schema.getValue("x", t) >= qx1 &&
                            (double) schema.getValue("x", t) <= qx2 &&
                            (double) schema.getValue("y", t) >= qy1 &&
                            (double) schema.getValue("y", t) <= qy2 &&
                            (int)schema.getValue("id", t) == 1;

                    GeoTemporalQueryRequest queryRequest = new GeoTemporalQueryRequest<>(qx1, qx2, qy1, qy2, Long.MIN_VALUE, Long.MAX_VALUE, predicate);

                    QueryResponse response = queryClient.query(queryRequest);
                    assertEquals(tuples / 100, response.dataTuples.size());
                    for (DataTuple tuple: response.dataTuples) {
                        assertEquals(1, schema.getValue("id", tuple));
                    }
                }
                fullyExecuted = true;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            queryClient.close();
            clientBatchMode.close();
            KillOptions killOptions = new KillOptions();
            killOptions.set_wait_secs(0);
            cluster.killTopologyWithOpts("testGeoRangeQueryWithBloomFilterOnVarchar", killOptions);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(fullyExecuted);
        socketPool.returnPort(ingestionPort);
        socketPool.returnPort(queryPort);
    }
}
