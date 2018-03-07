package indexingTopology.topology.kafka;

import com.alibaba.fastjson.JSONObject;
import indexingTopology.api.client.GeoTemporalQueryClient;
import indexingTopology.api.client.GeoTemporalQueryRequest;
import indexingTopology.api.client.IngestionKafkaBatchMode;
import indexingTopology.api.client.QueryResponse;
import indexingTopology.bolt.*;
import indexingTopology.common.data.DataSchema;
import indexingTopology.common.data.DataTuple;
import indexingTopology.common.logics.DataTupleEquivalentPredicateHint;
import indexingTopology.common.logics.DataTupleMapper;
import indexingTopology.common.logics.DataTuplePredicate;
import indexingTopology.config.TopologyConfig;
import indexingTopology.bolt.InputStreamKafkaReceiverBoltServer;
import indexingTopology.topology.TopologyGenerator;
import indexingTopology.util.AvailableSocketPool;
import indexingTopology.util.Json.JsonTest;
import indexingTopology.util.shape.*;
import indexingTopology.util.taxi.Car;
import indexingTopology.util.taxi.City;
import indexingTopology.util.taxi.TrajectoryGenerator;
import indexingTopology.util.taxi.TrajectoryMovingGenerator;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.StormTopology;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by billlin on 2017/11/25
 */
public class KafkaTopology {

    /**
     * general configuration
     */
    @Option(name = "--help", aliases = {"-h"}, usage = "help")
    private boolean Help = false;

//    @Option(name = "--mode", aliases = {"-m"}, usage = "submit|ingest|query")
//    private String Mode = "Not Given";

    /**
     * topology configuration
     */
    @Option(name = "--topology-name", aliases = "-t", usage = "topology name")
    private String TopologyName = "waterwheel";

    @Option(name = "--config-file", aliases = {"-f"}, usage = "conf.yaml to override default configs")
    private String confFile = "conf/conf.yaml";

    @Option(name = "--node", aliases = {"-n"}, usage = "number of nodes used in the topology")
    private int NumberOfNodes = 1;

    @Option(name = "--local", usage = "run the topology in local cluster")
    private boolean LocalMode = true;

    /**
     * query api configuration
     */
    @Option(name = "--query-server-ip", usage = "the query server ip")
    private String QueryServerIp = "localhost";

    @Option(name = "--selectivity", usage = "the selectivity on the key domain")
    private double Selectivity = 1;

    @Option(name = "--temporal", usage = "recent time in seconds of interest")
    private int RecentSecondsOfInterest = 100;

    @Option(name = "--queries", usage = "number of queries to perform")
//    private int NumberOfQueries = Integer.MAX_VALUE;
    private int NumberOfQueries = 20;




    static final double x1 = 111.012928;
    static final double x2 = 115.023983;
    static final double y1 = 21.292677;
    static final double y2 = 25.614865;
    static final int partitions = 128;
    int totalNumber = 0;
    AvailableSocketPool socketPool = new AvailableSocketPool();
    int queryPort = socketPool.getAvailablePort();
    TopologyConfig config;

    public void excuteQuery(){
        DataSchema schema = getDataSchema();
        String searchTest = "{\"type\":\"rectangle\",\"leftTop\":\"80,80\",\"rightBottom\":\"90,70\",\"geoStr\":null,\"lon\":null,\"lat\":null,\"radius\":null}";
        String searchTest2 = "{\"type\":\"circle\",\"leftTop\":null,\"rightBottom\":null,\"geoStr\":null,\"lon\":85,\"lat\":75,\"radius\":5}";
        String searchTest3 = "{\"type\":\"polygon\",\"leftTop\":null,\"rightBottom\":null,\"geoStr\":[\"80  75\",\"85  80\",\"90  75\",\"85  70\"],\"lon\":null,\"lat\":null,\"radius\":null}";
        JSONObject jsonObject = JSONObject.parseObject(searchTest);
        ShapeChecking shapeChecking = new ShapeChecking(jsonObject);
        ArrayList arrayList= shapeChecking.split();
        Point leftTop;
        Point rightBottom;
        if(shapeChecking.getError() != null){
            System.out.println("Error! ErrorCode :  " + shapeChecking.getError());
            return;
        }
        DataTuplePredicate predicate;
        if(jsonObject.get("type").equals("rectangle")){
            Rectangle rectangle = new Rectangle(new Point(Double.valueOf(String.valueOf(arrayList.get(0))),(Double.valueOf(String.valueOf(arrayList.get(1))))),new Point(Double.valueOf(String.valueOf(arrayList.get(2))),(Double.valueOf(String.valueOf(arrayList.get(3))))));
            predicate = t -> rectangle.checkIn(new Point((Double)schema.getValue("lon", t),(Double)schema.getValue("lat", t)));
            leftTop = new Point(rectangle.getExternalRectangle().getLeftTopX(), rectangle.getExternalRectangle().getLeftTopY());
            rightBottom = new Point(rectangle.getExternalRectangle().getRightBottomX(), rectangle.getExternalRectangle().getRightBottomY());
        }
        else if(jsonObject.get("type").equals("circle")){
            Circle circle = new Circle((Double.valueOf(String.valueOf(arrayList.get(0)))),(Double.valueOf(String.valueOf(arrayList.get(1)))),(Double.valueOf(String.valueOf(arrayList.get(2)))));
            predicate = t -> circle.checkIn(new Point((Double)schema.getValue("lon", t),(Double)schema.getValue("lat", t)));
            leftTop = new Point(circle.getExternalRectangle().getLeftTopX(), circle.getExternalRectangle().getLeftTopY());
            rightBottom = new Point(circle.getExternalRectangle().getRightBottomX(), circle.getExternalRectangle().getRightBottomY());
        }
        else if(jsonObject.get("type").equals("polygon")){
            Polygon.Builder builder = Polygon.Builder();
            for(int i = 0;i < arrayList.size();i++){
                builder.addVertex(new Point((Double.valueOf(String.valueOf( arrayList.get(i++)))),(Double.valueOf(String.valueOf( arrayList.get(i))))));
            }
            Polygon polygon = builder.build();
            predicate = t -> polygon.checkIn(new Point((Double)schema.getValue("lon", t),(Double)schema.getValue("lat", t)));
            leftTop = new Point(polygon.getExternalRectangle().getLeftTopX(), polygon.getExternalRectangle().getLeftTopY());
            rightBottom = new Point(polygon.getExternalRectangle().getRightBottomX(), polygon.getExternalRectangle().getRightBottomY());

        }
        else{
            predicate = null;
            throw new IllegalArgumentException("Illegal arguments of checking shape.");
        }
        double selectivityOnOneDimension = Math.sqrt(Selectivity);
        GeoTemporalQueryClient queryClient = new GeoTemporalQueryClient(QueryServerIp, queryPort);
        Thread queryThread = new Thread(() -> {
            Random random = new Random();

            int executed = 0;
            long totalQueryTime = 0;

            while (true) {

                try {
                    queryClient.connectWithTimeout(10000);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                final int id = new Random().nextInt(100000);
                final String idString = "" + id;
                DataTupleEquivalentPredicateHint equivalentPredicateHint = new DataTupleEquivalentPredicateHint("id", idString);


                double x = x1 + (x2 - x1) * (1 - selectivityOnOneDimension) * random.nextDouble();
                double y = y1 + (y2 - y1) * (1 - selectivityOnOneDimension) * random.nextDouble();

                final double xLow = leftTop.x;
                final double xHigh =rightBottom.x;
                final double yLow = rightBottom.y;
                final double yHigh =leftTop.y;

                GeoTemporalQueryRequest queryRequest = new GeoTemporalQueryRequest<>(xLow, xHigh, yLow, yHigh,
                        System.currentTimeMillis() - RecentSecondsOfInterest * 1000,
                        System.currentTimeMillis(), predicate, null, null, null, null);
                long start = System.currentTimeMillis();
                try {
                    DateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
                    Calendar cal = Calendar.getInstance();
                    System.out.println("[" + dateFormat.format(cal.getTime()) + "]: A query will be issued.");
                    QueryResponse response = queryClient.query(queryRequest);
                    System.out.println("A query finished.");
                    long end = System.currentTimeMillis();
                    totalQueryTime += end - start;
                    DataSchema outputSchema = response.getSchema();
                    System.out.println(outputSchema.getFieldNames());
                    System.out.println("datatuples : " + response.dataTuples.size());
                    List<DataTuple> tuples = response.getTuples();
                    for (int i = 0; i < tuples.size(); i++) {
                        System.out.println(tuples.get(i).toValues());
                    }
                    System.out.println(String.format("Query time: %d ms", end - start));

                    if (executed++ >= NumberOfQueries) {
                        System.out.println("Average Query Latency: " + totalQueryTime / (double)executed);
                        break;
                    }

                } catch (SocketTimeoutException e) {
                    Thread.interrupted();
                } catch (IOException e) {
                    if (Thread.currentThread().interrupted()) {
                        Thread.interrupted();
                    }
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    queryClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // interval time
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                queryClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        queryThread.start();


    }

    public void excuteIngestion(){
        int total = 50;
        Thread emittingThread;
        long start = System.currentTimeMillis();
        JsonTest jsonTest = new JsonTest();
        String regEx = "[`~!@#$%^&*()+=|{}';'\\[\\]<>/?~！@#�%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(config.kafkaHost.toString());
        String currentKafkahost = m.replaceAll("").trim();
        IngestionKafkaBatchMode kafkaBatchMode = new IngestionKafkaBatchMode(currentKafkahost, config.topic);
        kafkaBatchMode.ingestProducer();
        TrajectoryGenerator generator = new TrajectoryMovingGenerator(x1, x2, y1, y2, 100000, 45.0);

        //            producer = new KafkaProducer<>(props);
        emittingThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    for (int i = 0; i < total; i++) {
                        Car car = generator.generate();
                        totalNumber++;
                        Double lon = Math.random() * 100;
                        Double lat = Math.random() * 100;
                        int devbtype = (int)(Math.random() * 100);
                        final int id = new Random().nextInt(100);
                        final String idString = "" + id;
                        Date dateOld = new Date(System.currentTimeMillis()); // 根据long类型的毫秒数生命一个date类型的时间
                        String sDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateOld); // 把date类型的时间转换为string
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = formatter.parse(sDateTime); // 把String类型转换为Date类型
                        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
                        System.out.println(devbtype);
//                            String Msg = "{\"lon\":"+ car.x + ",\"lat\":" + car.y + ",\"devbtype\":"+ devbtype +",\"devid\":\"asd\",\"city\":\"4401\",\"locationtime\":" + System.currentTimeMillis() +  "}";
                        if(i == 0) {
                            String Msg = jsonTest.CheckJingyiJson(2);
                            kafkaBatchMode.send(i, Msg);
                        }else{
                            String Msg = "{\"devbtype\":" + devbtype + ",\"devstype\":\"123\",\"devid\":\"0x0101\",\"city\":\"4401\",\"longitude\":"+ car.x + ",\"latitude\":" + car.y + ",\"altitude\":2000.0," +
                                    "\"speed\":50.0,\"direction\":40.0,\"locationtime\":\"" + currentTime + "\",\"workstate\":1,\"clzl\":\"巡逻车\",\"hphm\":\"粤A39824\",\"jzlx\":1,\"jybh\":\"100011\"," +
                                    "\"jymc\":\"陈国基\",\"lxdh\":\"13576123212\",\"dth\":\"SG0000000352\",\"reserve1\":null,\"reserve2\":\"\",\"reserve3\":\"\",\"ssdwdm\":\"440100000000\"," +
                                    "\"ssdwmc\":\"【】%测试*\",\"teamno\":\"44010001\"}";
                            kafkaBatchMode.send(i, Msg);
                        }
                        //                        this.producer.send(new ProducerRecord<String, String>("consumer",
                        //                                String.valueOf(i), "{\"employees\":[{\"firstName\":\"John\",\"lastName\":\"Doe\"},{\"firstName\":\"Anna\",\"lastName\":\"Smith\"},{\"firstName\":\"Peter\",\"lastName\":\"Jones\"}]}"));
                        //                        String.format("{\"type\":\"test\", \"t\":%d, \"k\":%d}", System.currentTimeMillis(), i)));

                        // every so often send to a different topic
                        //                if (i % 1000 == 0) {
                        //                    producer.send(new ProducerRecord<String, String>("test", String.format("{\"type\":\"marker\", \"t\":%d, \"k\":%d}", System.currentTimeMillis(), i)));
                        //                    producer.send(new ProducerRecord<String, String>("hello", String.format("{\"type\":\"marker\", \"t\":%d, \"k\":%d}", System.currentTimeMillis(), i)));

                        //                        System.out.println("Sent msg number " + totalNumber);
                        //                }
                    }
                    kafkaBatchMode.flush();
                    //            producer.close();
                    System.out.println("Kafka Producer send msg over,cost time:" + (System.currentTimeMillis() - start) + "ms");
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        emittingThread.start();
    }

    public void  submitTopology(){
        DataSchema rawSchema = getRawDataSchema();
        DataSchema schema = getDataSchema();
        config = new TopologyConfig();
        final boolean enableLoadBalance = false;

        City city = new City(x1, x2, y1, y2, partitions);

        Integer lowerBound = 0;
        Integer upperBound = city.getMaxZCode();
//        Integer upperBound = 5000;

        config.override(confFile);
        System.out.println("Topology is overridden by " + confFile);
        System.out.println(config.getCriticalSettings());

        //change this one
//        InputStreamReceiverBolt dataSource = new InputStreamReceiverBoltServer(rawSchema, 10000, config);


        InputStreamKafkaReceiverBoltServer dataSource = new InputStreamKafkaReceiverBoltServer(rawSchema,10000,config,config.topic);

//        QueryCoordinatorBolt<Integer> coordinator = new QueryCoordinatorWithQueryReceiverServerBolt<>(minIndex, maxIndex, queryPort,
//                config, schema);
        QueryCoordinatorBolt<Integer> coordinator = new GeoTemporalQueryCoordinatorBoltBolt<>(lowerBound,
                upperBound, 10001, city, config, schema);

        DataTupleMapper dataTupleMapper = new DataTupleMapper(rawSchema, (Serializable & Function<DataTuple, DataTuple>) t -> {
            Object longitude = schema.getValue("longitude", t);
            Object latitude = schema.getValue("latitude", t);
            int zcode = 0;
            if(longitude != null && latitude != null){
                zcode = city.getZCodeForALocation((Double)longitude,(Double)latitude);
            }
            t.add(zcode);
            return t;
        });



//        StormTopology topology = topologyGenerator.generateIndexingTopology(schema, lowerBound, upperBound,
//                enableLoadBalance, dataSource, queryCoordinatorBolt, dataTupleMapper, bloomFilterColumns, config);
        List<String> bloomFilterColumns = new ArrayList<>();
        bloomFilterColumns.add("devid");
//        bloomFilterColumns.add("longitude");

        TopologyGenerator<Integer> topologyGenerator = new TopologyGenerator<>();
        topologyGenerator.setNumberOfNodes(NumberOfNodes);

        StormTopology topology = topologyGenerator.generateIndexingTopology(schema, lowerBound, upperBound, enableLoadBalance, dataSource,
                coordinator,dataTupleMapper, bloomFilterColumns , config);

        Config conf = new Config();
        conf.setDebug(false);
        conf.setNumWorkers(NumberOfNodes);

        conf.put(Config.WORKER_CHILDOPTS, "-Xmx1024m");
        conf.put(Config.WORKER_HEAP_MEMORY_MB, 1024);
        conf.put(Config.STORM_MESSAGING_NETTY_MAX_SLEEP_MS, 1);

        // use ResourceAwareScheduler with some magic configurations to ensure that QueryCoordinator and Sink
        // are executed on the nimbus node.
        conf.setTopologyStrategy(waterwheel.scheduler.FFDStrategyByCPU.class);

//        LocalCluster localCluster = new LocalCluster();
//        localCluster.submitTopology(TopologyName, conf, topology);

        if (config.HDFSFlag == false) {
            LocalCluster localCluster = new LocalCluster();
            localCluster.submitTopology(TopologyName, conf, topology);
        } else {
            try {
                StormSubmitter.submitTopology(TopologyName, conf, topology);
            } catch (AlreadyAliveException e) {
                e.printStackTrace();
            } catch (InvalidTopologyException e) {
                e.printStackTrace();
            } catch (AuthorizationException e) {
                e.printStackTrace();
            }
            System.out.println("Topology is successfully submitted to the cluster!");
            System.out.println(config.getCriticalSettings());
        }

    }
    public static void main(String[] args) throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {

        KafkaTopology kafkaTopology = new KafkaTopology();

        CmdLineParser parser = new CmdLineParser(kafkaTopology);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            e.printStackTrace();
            parser.printUsage(System.out);
        }

        if (kafkaTopology.Help) {
            parser.printUsage(System.out);
            return;
        }

        kafkaTopology.submitTopology();
//        try {
//            Thread.sleep(1000);
//            kafkaTopology.excuteIngestion();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }

    static private DataSchema getRawDataSchema() {
        DataSchema schema = new DataSchema();

        schema.addIntField("devbtype");
        schema.addVarcharField("devstype", 64);
        schema.addVarcharField("devid", 64);
        schema.addVarcharField("city", 64);
        schema.addDoubleField("longitude");
        schema.addDoubleField("latitude");
        schema.addDoubleField("altitude");
        schema.addDoubleField("speed");
        schema.addDoubleField("direction");
        schema.addLongField("locationtime");
        schema.addIntField("workstate");
        schema.addVarcharField("clzl", 64);
        schema.addVarcharField("hphm", 64);
        schema.addIntField("jzlx");
        schema.addVarcharField("jybh", 64);
        schema.addVarcharField("jymc", 64);
        schema.addVarcharField("lxdh", 64);
        schema.addVarcharField("ssdwdm", 64);
        schema.addVarcharField("ssdwmc", 64);
        schema.addVarcharField("teamno", 64);
        schema.addVarcharField("dth", 64);
        schema.addVarcharField("reserve1", 64);
        schema.addVarcharField("reserve2", 64);
        schema.addVarcharField("reserve3", 64);
        schema.setTemporalField("locationtime");
        return schema;
    }

    static private DataSchema getDataSchema() {
        DataSchema schema = new DataSchema();

        schema.addIntField("devbtype");
        schema.addVarcharField("devstype", 64);
        schema.addVarcharField("devid", 64);
        schema.addVarcharField("city", 64);
        schema.addDoubleField("longitude");
        schema.addDoubleField("latitude");
        schema.addDoubleField("altitude");
        schema.addDoubleField("speed");
        schema.addDoubleField("direction");
        schema.addLongField("locationtime");
        schema.addIntField("workstate");
        schema.addVarcharField("clzl", 64);
        schema.addVarcharField("hphm", 64);
        schema.addIntField("jzlx");
        schema.addVarcharField("jybh", 64);
        schema.addVarcharField("jymc", 64);
        schema.addVarcharField("lxdh", 64);
        schema.addVarcharField("ssdwdm", 64);
        schema.addVarcharField("ssdwmc", 64);
        schema.addVarcharField("teamno", 64);
        schema.addVarcharField("dth", 64);
        schema.addVarcharField("reserve1", 64);
        schema.addVarcharField("reserve2", 64);
        schema.addVarcharField("reserve3", 64);
        schema.addIntField("zcode");

        schema.setTemporalField("locationtime");
        schema.setPrimaryIndexField("zcode");
        return schema;
    }

}
