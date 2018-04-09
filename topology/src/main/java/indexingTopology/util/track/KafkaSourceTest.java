package indexingTopology.util.track;

import indexingTopology.api.client.IngestionKafkaBatchMode;
import indexingTopology.config.TopologyConfig;
import indexingTopology.util.FrequencyRestrictor;
import indexingTopology.util.Json.JsonTest;
import indexingTopology.util.taxi.Car;
import indexingTopology.util.taxi.TrajectoryGenerator;
import indexingTopology.util.taxi.TrajectoryMovingGenerator;
import org.apache.storm.metric.internal.RateTracker;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by billlin on 2017/12/30
 */
public class KafkaSourceTest {

    @Option(name = "--ingest-rate-limit", aliases = {"-r"}, usage = "max ingestion rate")
    private int MaxIngestRate = 10;

    @Option(name = "--ingest-server-ip", usage = "the ingest server ip")
    private String IngestServerIp = "localhost";

    static final double x1 = 111.012928;
    static final double x2 = 115.023983;
    static final double y1 = 21.292677;
    static final double y2 = 25.614865;

    public void sourceProducer(){
        long start = System.currentTimeMillis();
        TrajectoryGenerator generator = new TrajectoryMovingGenerator(x1, x2, y1, y2, 10000, 60 * 10);
        JsonTest jsonTest = new JsonTest();
        String regEx = "[`~!@#$%^&*()+=|{}';'\\[\\]<>/?~！@#�%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher("[\"10.21.25.203:9092\",\"10.21.25.204:9092\",\"10.21.25.205:9092\"]");
        String currentKafkahost = m.replaceAll("").trim();
//        IngestionKafkaBatchMode kafkaBatchMode = new IngestionKafkaBatchMode("10.21.25.203:9092,10.21.25.203:9092,10.21.25.203:9092", "gpis");
        IngestionKafkaBatchMode kafkaBatchMode = new IngestionKafkaBatchMode(IngestServerIp + ":9092", "gpis");
        kafkaBatchMode.ingestProducer();
        int batchSize = 100;
        FrequencyRestrictor restrictor = new FrequencyRestrictor(MaxIngestRate, 2);
        RateTracker rateTracker = new RateTracker(1000, 5);
        Thread emittingThread = null;
        emittingThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    for (int i = 0; i < batchSize; i++) {
                        restrictor.getPermission();
                        rateTracker.notify(1);
                        Car car = generator.generate();
                        Double lon = Math.random() * 100;
                        Double lat = Math.random() * 100;
                        int devbtype = (int) (Math.random() * 100);
                        final int id = new Random().nextInt(100);
                        final String idString = "" + id;
                        Date dateOld = new Date(System.currentTimeMillis()); // 根据long类型的毫秒数生命一个date类型的时间
                        String sDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateOld); // 把date类型的时间转换为string
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = formatter.parse(sDateTime); // 把String类型转换为Date类型
                        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
//                            System.out.println(devbtype);
//                            String Msg = "{\"lon\":"+ car.x + ",\"lat\":" + car.y + ",\"devbtype\":"+ devbtype +",\"devid\":\"asd\",\"city\":\"4401\",\"locationtime\":" + System.currentTimeMillis() +  "}";
                        if (i  < 1) {
                            String Msg = "{\"devbtype\":" + 31 + ",\"devstype\":" + 31 + ",\"devid\":\"" + 83696 + "\",\"city\":\"4401\",\"longitude\":" + car.x + ",\"latitude\":" + car.y
                                    + ",\"altitude\":\"0\"," +
                                    "\"speed\":\"0\",\"direction\":\"0\",\"locationtime\":\"" + currentTime + "\",\"workstate\":\"1\",\"clzl\":\"\",\"hphm\":\"\",\"jzlx\":\"7\",\"jybh\":\"100011\"," +
                                    "\"jymc\":\"陈国基陈国基陈国基陈国基陈国基陈国基陈国基陈国基陈国基陈国基\",\"lxdh\":\"13576123212\",\"dth\":\"\",\"reserve1\":\"1\",\"reserve2\":\"\",\"reserve3\":\"\",\"ssdwdm\":\"440100000000\"," +
                                    "\"ssdwmc\":\"a\",\"teamno\":\"44010001\"}";
//                            String Msg = jsonTest.CheckJingyiJson(1);
////                            String Msg = "{\"asd\":\"\",\"reserve1\":\"1\",\"reserve2\":\"\",\"reserve3\":\"\",\"ssdwdm\":\"440100000000\"," +
////                                    "\"ssdwmc\":\"字数字数字数字数字数字数字数字数字数字数字数字数字数字数字数字数字数字数字数字数字数字数字数字数字数字数字数字数字数\",\"teamno\":\"44010001\",}";
//                            kafkaBatchMode.send(i, Msg);
                        } else {
                            String Msg = "{\"devbtype\":" + 11 + ",\"devstype\":" + 123 + ",\"devid\":\"" + car.id + "\",\"city\":\"4406\",\"longitude\":" + car.x + ",\"latitude\":" + car.y
                                    + ",\"altitude\":\"0\"," +
                                    "\"speed\":\"0\",\"direction\":\"0\",\"locationtime\":\"" + currentTime + "\",\"workstate\":\"1\",\"clzl\":\"\",\"hphm\":\"\",\"jzlx\":\"7\",\"jybh\":\"100011\"," +
                                    "\"jymc\":\"陈国基陈国基陈国基陈国基陈国基陈国基陈国基陈国基陈国基陈国基\",\"lxdh\":\"13576123212\",\"dth\":\"\",\"reserve1\":\"1\",\"reserve2\":\"\",\"reserve3\":\"\",\"ssdwdm\":\"440100000000\"," +
                                    "\"ssdwmc\":\"a\",\"teamno\":\"44010001\"}";
//                            String Msg = "{\"devbtype\":" + 10 + ",\"devstyaasdpe\":\"123\",\"devid\":\"0x0101\",\"city\":\"4401\",\"longitude\":"+ 80.8888888888 + ",\"latitude\":" + 80.8888888888 + ",\"altitude\":2000.0," +
//                                    "\"speed\":50.0,\"direction\":40.0,\"locationtime\":\""+ currentTime +"\",\"workstate\":1,\"clzl\":\"巡逻车\",\"hphm\":\"粤A39824\",\"jzlx\":1,\"jybh\":\"100011\"," +
//                                    "\"jymc\":\"陈国基\",\"lxdh\":\"13576123212\",\"dth\":\"SG0000000352\",\"reserve1\":null,\"reserve2\":\"\",\"reserve3\":\"\",\"ssdwdm\":\"440100000000\"," +
//                                    "\"ssdwmc\":\"广州市\",\"teamno\":\"44010001\"}";
//                            String   Msg = "{\"devbtype\":" + 10 + ",\"devstype\":\"123\"}";
//                            System.out.println(currentTime);
                        System.out.println(Msg);
                            kafkaBatchMode.send(i, Msg);
                        }
                        //                        this.producer.send(new ProducerRecord<String, String>("consumer",
                        //                                String.valueOf(i), "{\"employees\":[{\"firstName\":\"John\",\"lastName\":\"Doe\"},{\"firstName\":\"Anna\",\"lastName\":\"Smith\"},{\"firstName\":\"Peter\",\"lastName\":\"Jones\"}]}"));
                        //                        String.format("{\"type\":\"test\", \"t\":%d, \"k\":%d}", System.currentTimeMillis(), i)));

                        // every so often send to a different topicxing
                        //                if (i % 1000 == 0) {
                        //                    producer.send(new ProducerRecord<String, String>("test", String.format("{\"type\":\"marker\", \"t\":%d, \"k\":%d}", System.currentTimeMillis(), i)));
                        //                    producer.send(new ProducerRecord<String, String>("hello", String.format("{\"type\":\"marker\", \"t\":%d, \"k\":%d}", System.currentTimeMillis(), i)));

                        //                        System.out.println("Sent msg number " + totalNumber);
                        //                }
                    }
                    kafkaBatchMode.flush();
                    //            producer.close();
//                    System.out.println("Kafka Producer send msg over,cost time:" + (System.currentTimeMillis() - start) + "ms");
//                    Thread.sleep(500000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        emittingThread.start();

        new Thread(() -> {
            while(true) {
                try {
                    Thread.sleep(1000);
                    System.out.println(String.format("%.1f tuples / s.", rateTracker.reportRate()));
                    System.out.println(System.currentTimeMillis());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }).start();
    }

    public static void main(String[] args) {
        KafkaSourceTest kafkaSourceTest = new KafkaSourceTest();
        CmdLineParser parser = new CmdLineParser(kafkaSourceTest);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            e.printStackTrace();
            parser.printUsage(System.out);
        }

        kafkaSourceTest.sourceProducer();
    }
}
