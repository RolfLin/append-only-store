package indexingTopology.util.track;

import indexingTopology.api.client.IngestionKafkaBatchMode;
import indexingTopology.config.TopologyConfig;
import indexingTopology.util.FrequencyRestrictor;
import indexingTopology.util.Json.JsonTest;
import indexingTopology.util.taxi.Car;
import indexingTopology.util.taxi.TrajectoryGenerator;
import indexingTopology.util.taxi.TrajectoryMovingGenerator;
import org.apache.storm.metric.internal.RateTracker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by billlin on 2017/12/30
 */
public class KafkaSourceTest {
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
        IngestionKafkaBatchMode kafkaBatchMode = new IngestionKafkaBatchMode("localhost:9092", "gpis");
        kafkaBatchMode.ingestProducer();
        LocationFile locationFile = new LocationFile();
        ArrayList<String> carDetailList = locationFile.read2("20170201.txt");
        ArrayList<String> carLocationList = locationFile.read2("201701-06localtion.txt");
        ArrayList<String> carPlateList = new ArrayList<String>();
        for(String record : carDetailList){
            carPlateList.add(record.split("//")[2]);
        }
        ArrayList<String> nameList = locationFile.getNameList(carPlateList);
        // generator the name by carID
//        ArrayList<String>

        FrequencyRestrictor restrictor = new FrequencyRestrictor(10, 5);
        RateTracker rateTracker = new RateTracker(1000, 5);

//        int batchSize = 2000;
        int batchSize = 10;

        Thread emittingThread = null;
        emittingThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    for (int i = 0; i < batchSize; i++) {
//                        restrictor.getPermission();
//                        rateTracker.notify(1);
                        Car car = generator.generate();
                        Double lon = Math.random() * 100;
                        Double lat = Math.random() * 100;
                        int devbtype = (int) (Math.random() * 10);
                        int devid = (int) (Math.random() * 10);
                        int city = (int) (Math.random() * 10);
                        final int id = new Random().nextInt(100);
                        final String idString = "" + id;
                        Date dateOld = new Date(System.currentTimeMillis()); // 根据long类型的毫秒数生命一个date类型的时间
                        String sDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateOld); // 把date类型的时间转换为string
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = formatter.parse(sDateTime); // 把String类型转换为Date类型
                        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
//                        if (i < ( batchSize / 5)) {
                        if (i < 1) {
                                Random random = new Random();
//                                int randomValue = random.nextInt(3) + 1;
                                int randomValue = 1;
                                String Msg = "{\"devbtype\":" + 31 + "," +
                                        "\"devstype\":\"" + getRandomCarDetial(carDetailList.get(1),6) + "\"," +
                                        "\"devid\":\"" + "83696" +
//                                    "\",\"city\":\"" + getRandomCity() +    // exchange the city value
                                        "\",\"city\":\"" + "4401" +   //exchange the city value
                                        "\",\"longitude\":" + getRandomCarLocation(carLocationList.get(1),4) +
                                        ",\"latitude\":" + getRandomCarLocation(carLocationList.get(1),5)+ "," +
//                                    "\"altitude\":\"" + getRandomCarLocation(carLocationList.get(randomValue),6) + "\"," +
                                        "\"speed\":\"" + getRandomCarLocation(carLocationList.get(1),7) + "\","+
                                        "\"direction\":\"" + getRandomCarLocation(carLocationList.get(1),8) +
                                        "\",\"locationtime\":\"" + getRandomCarLocation(carLocationList.get(1),3) +
                                        "\",\"workstate\":\"" + getRandomWorkstate() + "\"," +
                                        "\"hphm\":\"" + getRandomCarDetial(carDetailList.get(1),2) +
                                        "\",\"jzlx\":\"" + getRandomJzlx() +
                                        "\",\"jybh\":\"" + getRandomCarDetial(carDetailList.get(1),8) + "\"," +
                                        "\"jymc\":\"" + nameList.get(1) +
                                        "\",\"reserve1\":\"" + getRandomReserve1() +
                                        "\",\"ssdwdm\":\"" + getRandomCarDetial(carDetailList.get(1),3)+
                                        "\",\"ssdwmc\":\"a\"," +
                                        "\"teamno\":\"" + getRandomCarDetial(carDetailList.get(1),8) + "\"}";
//                                System.out.println(Msg);
//                                kafkaBatchMode.send(i, Msg);
                            System.out.println();
                            System.out.println();
                            System.out.println();
                            System.out.println();
                            } else {
                                Random random = new Random();
                                int randomValue = random.nextInt(5000) + 1;
//                            String Msg = "{\"devbtype\":" + 11 + ",\"devstype\":" + 123 + ",\"devid\":\"75736331\",\"city\":\"4406\",\"longitude\":" + car.x + ",\"latitude\":" + car.y
//                                    + ",\"altitude\":\"0\"," +
//                                    "\"speed\":\"0\",\"direction\":\"0\",\"locationtime\":\"" + currentTime + "\",\"workstate\":\"" + (random.nextInt(5) + 1) +"\",\"clzl\":\"\",\"hphm\":\"\",\"jzlx\":\"" + (random.nextInt(8) + 1) +"\",\"jybh\":\"100011\"," +
//                                    "\"jymc\":\"" + getRandomName() + "\",\"lxdh\":\"13576123212\",\"dth\":\"\",\"reserve1\":\"1\",\"reserve2\":\"\",\"reserve3\":\"\",\"ssdwdm\":\"440100000000\"," +
//                                    "\"ssdwmc\":\"a\",\"teamno\":\"44010001\"}";
                                String Msg = "{\"devbtype\":" + getRandomDevbtype() + "," +
                                        "\"devstype\":\"" + getRandomCarDetial(carDetailList.get(randomValue),6) + "\"," +
                                        "\"devid\":\"" + cutCarDevid(carDetailList.get(randomValue), 1) +
//                                    "\",\"city\":\"" + getRandomCity() +    // exchange the city value
                                        "\",\"city\":\"" + getCityIDByName(carLocationList.get(randomValue),11) +   //exchange the city value
                                        "\",\"longitude\":" + getRandomCarLocation(carLocationList.get(randomValue),4) +
                                        ",\"latitude\":" + getRandomCarLocation(carLocationList.get(randomValue),5)+ "," +
//                                    "\"altitude\":\"" + getRandomCarLocation(carLocationList.get(randomValue),6) + "\"," +
                                        "\"speed\":\"" + getRandomCarLocation(carLocationList.get(randomValue),7) + "\","+
                                        "\"direction\":\"" + getRandomCarLocation(carLocationList.get(randomValue),8) +
                                        "\",\"locationtime\":\"" + getRandomCarLocation(carLocationList.get(randomValue),3) +
                                        "\",\"workstate\":\"" + getRandomWorkstate() + "\"," +
                                        "\"hphm\":\"" + getRandomCarDetial(carDetailList.get(randomValue),2) +
                                        "\",\"jzlx\":\"" + getRandomJzlx() +
                                        "\",\"jybh\":\"" + getRandomCarDetial(carDetailList.get(randomValue),8) + "\"," +
                                        "\"jymc\":\"" + nameList.get(randomValue) +
                                        "\",\"reserve1\":\"" + getRandomReserve1() +
                                        "\",\"ssdwdm\":\"" + getRandomCarDetial(carDetailList.get(randomValue),3)+
                                        "\",\"ssdwmc\":\"a\"," +
                                        "\"teamno\":\"" + getRandomCarDetial(carDetailList.get(randomValue),8) + "\"}";

//                            String Msg = "{\"devbtype\":" + devbtype + ",\"devstype\":" + 123 + ",\"devid\":\"" + devid + "\",\"city\":\"" + city + "\",\"longitude\":" + 113.123123 + ",\"latitude\":" + car.x
//                                    + ",\"altitude\":" + car.y + "," +
//                                    "\"speed\":\"0\",\"direction\":\"0\",\"locationtime\":\"" + currentTime + "\",\"workstate\":\"1\",\"clzl\":\"\",\"hphm\":\"\",\"jzlx\":\"7\",\"jybh\":\"100011\"," +
//                                    "\"jymc\":\"陈国基\",\"lxdh\":\"13576123212\",\"dth\":\"\",\"reserve1\":\"1\",\"reserve2\":\"\",\"reserve3\":\"\",\"ssdwdm\":\"440100000000\"," +
//                                    "\"ssdwmc\":\"a\",\"teamno\":\"44010001\"}";

                                //                            String Msg = "{\"devbtype\":" + 10 + ",\"devstyaasdpe\":\"123\",\"devid\":\"0x0101\",\"city\":\"4401\",\"longitude\":"+ 80.8888888888 + ",\"latitude\":" + 80.8888888888 + ",\"altitude\":2000.0," +
//                                    "\"speed\":50.0,\"direction\":40.0,\"locationtime\":\""+ currentTime +"\",\"workstate\":1,\"clzl\":\"巡逻车\",\"hphm\":\"粤A39824\",\"jzlx\":1,\"jybh\":\"100011\"," +
//                                    "\"jymc\":\"陈国基\",\"lxdh\":\"13576123212\",\"dth\":\"SG0000000352\",\"reserve1\":null,\"reserve2\":\"\",\"reserve3\":\"\",\"ssdwdm\":\"440100000000\"," +
//                                    "\"ssdwmc\":\"广州市\",\"teamno\":\"44010001\"}";
//                            String   Msg = "{\"devbtype\":" + 10 + ",\"devstype\":\"123\"}";

                                System.out.println(System.currentTimeMillis());
//                            System.out.println(currentTime);
                                System.out.println(Msg);
                                kafkaBatchMode.send(i, Msg);
                            }
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
//                    }
                    kafkaBatchMode.flush();
                    //            producer.close();
                    System.out.println("Kafka Producer send msg over,cost time:" + (System.currentTimeMillis() - start) + "ms");

                    Thread.sleep(1800 * 1000);
//                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        emittingThread.start();

//        new Thread(() -> {
//            while(true) {
//                try {
//                    Thread.sleep(1000);
//                    System.out.println(String.format("%.1f tuples / s.", rateTracker.reportRate()));
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                    break;
//                }
//            }
//        }).start();
    }

    String getRandomCarDetial(String locationList,int num){
        String detail[] = locationList.split("//");
        return detail[num];
    }

    String getRandomCarLocation(String locationList,int num){
        String location[] = locationList.split("//");
        return location[num-1]; // ben
    }

    String cutCarDevid(String locationList,int num){
        String carDevid = getRandomCarDetial(locationList, num);
        String subCarDevid = carDevid.substring(5);
        return subCarDevid; // ben
    }

    String getCityIDByName(String locationList, int num){
        String cityName = getRandomCarLocation(locationList, num);
        String cityID = "";
        switch (cityName){
            case "番禺区" :{
                cityID = "4401";
                break;
            }
            case "海珠区" :{
                cityID = "4402";
                break;
            }
            case "天河区" :{
                cityID = "4403";
                break;
            }
            case "越秀区" :{
                cityID = "4404";
                break;
            }
            case "荔湾区" :{
                cityID = "4405";
                break;
            }
            case "白云区" :{
                cityID = "4406";
                break;
            }
            case "黄埔区" :{
                cityID = "4407";
                break;
            }
            case "花都区" :{
                cityID = "4408";
                break;
            }
            case "南沙区" :{
                cityID = "4409";
                break;
            }
            case "增城区" :{
                cityID = "4410";
                break;
            }
            case "从化区" :{
                cityID = "4411";
                break;
            }
            default: {
                cityID = "未知";
                break;
            }
        }
        return cityID;
    }

    String getRandomName() {
        String name[] = {"陈国基","李国伟","王小明","李国强","刘江","张三","李四"};
        Random random = new Random();
        int num = random.nextInt(7);
        return name[num];
    }

    String getRandomHmhp() {
        String name[] = {"","100012","100013","100014","100015","100016","100017"};
        Random random = new Random();
        int num = random.nextInt(7);
        return name[num];
    }

    int getRandomDevbtype() {
        int devbtype[] = {11,12,13,14,21,22,23,24,31,32,33,34,4,5,9};
        Random random = new Random();
        int num = random.nextInt(15);
        return devbtype[num];
    }

    String getRandomDevid() {
        String name[] = {"83696","78216","81905"};
//        String name[] = {"1364883696","1364878216","1364881905","1364882716","1364882347","1364884081","1364880892"};
        Random random = new Random();
        int num = random.nextInt(3);
        return name[num];
    }

    String getRandomCity() {
        String city[] = {"4401", "4402", "4403", "4404", "4405", "4406", "4407", "4408", "4409", "4410", "4411"};
        Random random = new Random();
        int num = random.nextInt(11);
        return city[num];
    }

    int getRandomWorkstate() {
        int workstate[] = {1,2,3,4,9};
        Random random = new Random();
        int num = random.nextInt(5);
        return workstate[num];
    }

    int getRandomJzlx() {
        int jzlx[] = {1,2,3,4,5,6,7,9};
        Random random = new Random();
        int num = random.nextInt(8);
        return jzlx[num];
    }

    String getRandomReserve1() {
        String reserve1[] = {"1", "2", "3", "4", "5", "6", "99"};
        Random random = new Random();
        int num = random.nextInt(7);
        return reserve1[num];
    }

    public static void main(String[] args) {
        KafkaSourceTest kafkaSourceTest = new KafkaSourceTest();
        kafkaSourceTest.sourceProducer();
    }
}
