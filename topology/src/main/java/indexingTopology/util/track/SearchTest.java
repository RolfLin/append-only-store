package indexingTopology.util.track;

import java.util.Scanner;

/**
 * Created by billlin on 2017/12/17
 */
public class SearchTest {
    public static void main(String[] args) {
        String permissionParams = null;
        Scanner scanner =new Scanner(System.in);
        while (scanner.hasNext()){
            String  querySelect = scanner.next();
            long startTime = System.currentTimeMillis() - 30 * 1000;
            long endTime = System.currentTimeMillis();
            switch (querySelect){
                case "1" : {
                    String businessParams = "{\"city\":\"4406\",\"devbtype\":11,\"devid\":\"8518\",\"startTime\":" + startTime + ",\"endTime\":" + endTime + ",\"longitude\":110.123,\"latitude\":23.123,\"radius\":10.123,}";
                    System.out.println(businessParams);
                    TrackSearchWs trackSearchWs = new TrackSearchWs();
                    String queryResult = trackSearchWs.services(permissionParams, businessParams);
                    System.out.println(queryResult);
                    break;
                }
                case "2" : {
                    String businessParamsPaged = "{\"city\":\"4403\",\"devbtype\":1,\"devid\":\"75736331\",\"startTime\":" + startTime + ",\"endTime\":" + endTime + ",\"page\":1,\"rows\":10}";
                    TrackPagedSearchWs trackPagedSearchWs = new TrackPagedSearchWs();
                    String queryResultPaged = trackPagedSearchWs.services(permissionParams, businessParamsPaged);
                    System.out.println(queryResultPaged);
                    break;
                }
                case "3" : {
                    PosNonSpacialSearchWs posNonSpacialSearchWs = new PosNonSpacialSearchWs();
                    String result = posNonSpacialSearchWs.services(null, null);
                    System.out.println(result);
                    break;
                }
                case "4" : {
                    String searcRectangle = "{\"type\":\"rectangle\",\"searchType\":\"special\",\"jzlx\":2,\"workstate\":2,\"groupId\":\"workstate\"," +
                            "\"leftTop\":\"10,1000\",\"rightBottom\":\"1000,10\"}";
                    PosSpacialSearchWs posSpacialSearchWs = new PosSpacialSearchWs();
//                    String result = posSpacialSearchWs.service(null, searcRectangle, startTime, endTime, null);
//                    System.out.println(result);
                    break;
                }

                case "5" : {
                    String searchCircle = "{\"type\":\"circle\",\"leftTop\":null,\"rightBottom\":null,\"geoStr\":null,\"longitude\":110,\"latitude\":20,\"radius\":10}";
                    PosSpacialSearchWs posSpacialSearchWs = new PosSpacialSearchWs();
//                    String result = posSpacialSearchWs.service(null, searchCircle, startTime, endTime, null);
//                    System.out.prinletln(result);
                    break;
                }
                case "6" : {
//                    String searchPolygon = "{\"type\":\"polygon\",\"leftTop\":null,\"rightBottom\":null,\"geoStr\":[\"70 70\",\"85 80\",\"90 75\",\"85 70\",\"70 70\"],\"lon\":null,\"lat\":null,\"radius\":null}";
//                    String searchPolygon = "{\"type\":\"polygon\",\"leftTop\":null,\"rightBottom\":null,\"geoSt" +
//                            "r\":[\"80 60\",\"120 60\",\"120 80\",\"80 80\"],\"lon\":null,\"lat\":null,\"radius\":null}";
                    String searchPolygon = "{\"type\":\"polygon\",\"searchType\":\"null\",\"jzlx\":2,\"workstate\":2,\"leftTop\":null,\"rightBottom\":null,\"geoSt" +
                            "r\":[\"100 10\",\"120 10\",\"120 30\",\"100 30\"],\"lon\":null,\"lat\":null,\"radius\":null}";
                    PosSpacialSearchWs posSpacialSearchWs = new PosSpacialSearchWs();
//                    String result = posSpacialSearchWs.service(null, searchPolygon, startTime, endTime, null);
//                    System.out.println(result);
                    break;
                }
                case "７" : {
//                    String searchPolygon = "{\"type\":\"polygon\",\"leftTop\":null,\"rightBottom\":null,\"geoStr\":[\"70 70\",\"85 80\",\"90 75\",\"85 70\",\"70 70\"],\"lon\":null,\"lat\":null,\"radius\":null}";
//                    String searchPolygon = "{\"type\":\"polygon\",\"leftTop\":null,\"rightBottom\":null,\"geoSt" +
//                            "r\":[\"80 60\",\"120 60\",\"120 80\",\"80 80\"],\"lon\":null,\"lat\":null,\"radius\":null}";
                    String searchRectangle = "{\"type\":\"rectangle\",\"searchType\":\"special\",\"jzlx\":2,\"workstate\":2,\"leftTop\":\"10,1000\",\"rightBottom\":\"1000,10\"," +
                            "\"geoStr\":null,\"longitude\":null,\"latitude\":null,\"radius\":null}";
                    PosSpacialSearchWs posSpacialSearchWs = new PosSpacialSearchWs();
//                    String result = posSpacialSearchWs.service(null, searchRectangle);
//                    System.out.println(result);
                    break;
                }
                case "8" : { // test track with options
//                    String searchPolygon = "{\"type\":\"polygon\",\"leftTop\":null,\"rightBottom\":null,\"geoStr\":[\"70 70\",\"85 80\",\"90 75\",\"85 70\",\"70 70\"],\"lon\":null,\"lat\":null,\"radius\":null}";
//                    String searchPolygon = "{\"type\":\"polygon\",\"leftTop\":null,\"rightBottom\":null,\"geoSt" +
//                            "r\":[\"80 60\",\"120 60\",\"120 80\",\"80 80\"],\"lon\":null,\"lat\":null,\"radius\":null}";
                    String searchRectangle = "{\"type\":\"rectangle\",\"searchType\":\"special\",\"jzlx\":2,\"workstate\":2,\"leftTop\":\"10,1000\",\"rightBottom\":\"1000,10\"," +
                            "\"geoStr\":null,\"longitude\":null,\"latitude\":null,\"radius\":null}";
                    String searchStr = "{\"type\":\"line\",\"devid\":\"81905\",\"startTime\":\"2017-02-01 00:00:00\",\"endTime\":\"2017-02-07 00:00:00\",\"city\":[\"4406\"],\"devbtype\":22,\"function\":\"null\",\"options\":[\"devid\"]}";
                    TrackNew trackNew = new TrackNew();
                    String result = trackNew.service(null,searchStr);
                    System.out.println(result);
                    break;
                }
                default : break;
            }
        }
    }
}
