package indexingTopology.util.track;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import indexingTopology.api.client.GeoTemporalQueryClient;
import indexingTopology.api.client.GeoTemporalQueryRequest;
import indexingTopology.api.client.QueryResponse;

import indexingTopology.common.aggregator.AggregateField;
import indexingTopology.common.aggregator.Aggregator;
import indexingTopology.common.aggregator.Count;
import indexingTopology.common.data.DataSchema;
import indexingTopology.common.data.DataTuple;
import indexingTopology.common.logics.DataTupleEquivalentPredicateHint;
import indexingTopology.common.logics.DataTuplePredicate;
import indexingTopology.util.shape.Circle;
import indexingTopology.util.shape.Point;
import indexingTopology.util.shape.Polygon;
import indexingTopology.util.shape.Rectangle;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Create by zelin on 17-12-15
 **/
public class PosSpacialSearchWs {

    private String QueryServerIp = "localhost";
    private Point leftTop, rightBottom;
    private Point[] geoStr;
    private Point circle;
    private double radius;
    private Point externalLeftTop, externalRightBottom;
    private String hdfsIP = "68.28.8.91";
    private boolean shapeCheckIn = true;

    public String service(String permissionsParams, String businessParams) {
        DataSchema schema = getDataSchema();
        DataSchema outputSchema = schema;
        List<DataTuple> finalTuples = new ArrayList<>();
        ArrayList<String> finalOptions = new ArrayList<>();
        ArrayList<String> finalSetType = new ArrayList<>();
        JSONObject queryResponse = new JSONObject();
        JSONArray queryResult = new JSONArray();
        boolean firstQuery = true;
        try{
            JSONObject jsonObject = JSONObject.parseObject(businessParams);
            do{
                String type = jsonObject.getString("type"); // 查询类型

                if(type.equals("line") == true) {
                    TrackNew trackNew = new TrackNew();
                    String result = trackNew.service(null,businessParams);
                    System.out.println("go to trakSearch: " + result);
                    return result;
                }
                /**
                 * search the specified column
                 */

                boolean optionFlag = true;
                JSONArray optionArray = jsonObject.getJSONArray("options");
                ArrayList<String> options = new ArrayList<>();
                for(int i = 0; i < optionArray.size(); i++){
                    System.out.println(optionArray.get(i));
                    String currentOption = String.valueOf(optionArray.get(i));
                    options.add(currentOption);
                    if(!currentOption.equals("null") && !schema.getFieldNames().contains(currentOption)){
                        optionFlag = false;
                    }
                }
                ArrayList<String> tmpOptions = new ArrayList<>();
                if(firstQuery == false){
                    if(!options.get(0).equals("null") && !finalOptions.get(0).equals("null")){// display all row
                        for(String option : options){
                            if(finalOptions.contains(option)){
                                tmpOptions.add(option);
                            }
                        }
                        finalOptions = tmpOptions;
                        if(finalOptions.size() == 0){
                            queryResult = new JSONArray();
                            queryResponse.put("success", true);
                            queryResponse.put("result", queryResult);
                            String result = JSONObject.toJSONString(queryResponse, SerializerFeature.WriteMapNullValue);
                            return result;
                        }
                    }else if(finalOptions.get(0).equals("null")){
                        finalOptions = options;
                    }
                }else{
                    finalOptions = options;
                }



                /**
                 * specified conditions search
                 */

                JSONArray jzlxArray = jsonObject.getJSONArray("jzlx");
                int jzlx[] = new int[jzlxArray.size()]; // 车辆类型
                JSONArray workstateArray = jsonObject.getJSONArray("workstate");
                int workstate[] = new int[workstateArray.size()]; // 工作状态
                for(int i = 0; i < jzlxArray.size(); i++){
                    jzlx[i] = (int)jzlxArray.get(i);
                }
                for(int i = 0; i < workstateArray.size(); i++){
                    workstate[i] = (int)workstateArray.get(i);
                }


//            int jzlx = jsonObject.getInteger("jzlx"); //车辆类型
//            int workstate = jsonObject.getInteger("workstate"); //工作状态
                /**
                 * search statistics
                 */
                String groupId = jsonObject.getString("function");
                String setType = jsonObject.getString("set");
                boolean setTypeFlag = true;
                if(firstQuery == false){
                    if((!setType.equals("union") && !setType.equals("intersection")) || !finalSetType.contains(setType)){
                        setTypeFlag = false;
                    }
                }else{
                    finalSetType.add(setType);
                    if(setType.equals("null") && !jsonObject.getJSONObject("subquery").toString().equals("{}")){
                        setTypeFlag = false;
                    }
                    if((setType.equals("union") || setType.equals("intersection")) && jsonObject.getJSONObject("subquery").toString().equals("{}")){
                        setTypeFlag = false;
                    }
                }

                if(setTypeFlag == false || optionFlag == false){
                    queryResponse.put("success", false);
//                            queryResponse.put("result", null);
                    queryResponse.put("errorCode", 1002);
                    queryResponse.put("errorMsg", "参数值无效或缺失必填参数");
                    System.out.println(queryResponse);
                    return queryResponse.toString();
                }


//            long startTime = jsonObject.getLong("startTime");
//            long endTime = jsonObject.getLong("endTime");


                String startTimeStr = jsonObject.getString("startTime");
                String endTimeStr = jsonObject.getString("endTime");
                if (startTimeStr.equals("null") && endTimeStr.equals("null")) {
                    startTimeStr = "2018-02-01 00:00:00";
                    endTimeStr = "2018-02-07 00:00:00";
                } else if (endTimeStr.equals("null") && !startTimeStr.equals("null")) {
                    endTimeStr = startTimeStr;
                }
                SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date dt1 = null;
                Date dt2 = null;
                try {
                    dt1 = sdf.parse(startTimeStr);
                    dt2 = sdf.parse(endTimeStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //继续转换得到毫秒数的long型
                long startTime = dt1.getTime();
                long endTime = dt2.getTime();
                if (startTime == endTime) {
                    endTime = startTime + 3600 * 24 * 1000;
                }
//            long startTime = System.currentTimeMillis() - 600 * 1000;
//            long endTime = System.currentTimeMillis();
                Pattern p = null;
                boolean flag = true;
                //        System.out.println(geoArray.toString());
                DataTuplePredicate predicate = null, localPredicate = null, finalPredicate = null;
                Aggregator<Integer> aggregator = new Aggregator<>(schema, null, new AggregateField(new Count(), "*")
                );
                outputSchema = aggregator.getOutputDataSchema();
                DataTupleEquivalentPredicateHint predicateHint = null;

//                ArrayList<Circle> circleArrayList = new ArrayList<>();
//                ArrayList<Rectangle> rectangleArrayList = new ArrayList<>();
//                ArrayList<Predicate> predicateArrayList = new ArrayList<>();
                type = jsonObject.getString("type");
                switch (type) {
                    case "rectangle" : {
                        p = Pattern.compile("^\\-?[0-9]+\\.?[0-9]*+\\,\\-?[0-9]+\\.?[0-9]*");
                        String rectLeftTop = jsonObject.get("leftTop").toString();
                        boolean b1 = p.matcher(rectLeftTop).matches();
                        String rectRightBottom = jsonObject.get("rightBottom").toString();
                        boolean b2 = p.matcher(rectRightBottom).matches();
                        if (!b1 || !b2) {
                            flag = false;
                            break;
                        }
                        Rectangle rectangle;
                        if (jzlx[0] != 0 || workstate[0] != 0) { // Query conditions
                            System.out.println("Query with workstate or jzlx");
                            rectangle = initSpecialRectangel(rectLeftTop, rectRightBottom, jzlx, workstate);
                        }else {
                            System.out.println("No conditions");
                            rectangle = initRectangel(rectLeftTop, rectRightBottom);
                        }

//                        rectangleArrayList.add(rectangle);// add to rectangle array list
                        //                    System.out.println(rectangle.getJzlx());
                        externalLeftTop = new Point(rectangle.getExternalRectangle().getLeftTopX(), rectangle.getExternalRectangle().getLeftTopY());
                        externalRightBottom = new Point(rectangle.getExternalRectangle().getRightBottomX(), rectangle.getExternalRectangle().getRightBottomY());

                        if (externalLeftTop.x > externalRightBottom.x || externalLeftTop.y < externalRightBottom.y) {
                            queryResponse.put("success", false);
//                            queryResponse.put("result", null);
                            queryResponse.put("errorCode", 1002);
                            queryResponse.put("errorMsg", "参数值无效或缺失必填参数");
                            System.out.println(queryResponse);
                            return queryResponse.toString();
                        }
                        if (jzlx[0] != 0 || workstate[0] != 0) {
                            System.out.println("SpecialCheckIn with workstate or jzlx");
                            predicate = t -> rectangle.specialCheckIn(new Point((Double)schema.getValue("longitude", t),(Double)schema.getValue("latitude", t), (Integer) schema.getValue("jzlx", t), (Integer) schema.getValue("workstate", t)));
                        }else {
                            System.out.println("No SpecialCheckIn");
                            predicate = t -> rectangle.checkIn(new Point((Double)schema.getValue("longitude", t),(Double)schema.getValue("latitude", t)));
                        }
//                        predicateArrayList.add(predicate);// add to predicate array list
                        break;
                    }
                    case "polygon" : {
                        JSONArray geoArray = null;
                        if (jsonObject.getJSONArray("geoStr") != null) {
                            geoArray = jsonObject.getJSONArray("geoStr");
                        }else {
                            flag = false;
                            break;
                        }
                        Polygon polygon = initPolygon(geoArray);
                        externalLeftTop = new Point(polygon.getExternalRectangle().getLeftTopX(), polygon.getExternalRectangle().getLeftTopY());
                        externalRightBottom = new Point(polygon.getExternalRectangle().getRightBottomX(), polygon.getExternalRectangle().getRightBottomY());
                        localPredicate = t -> polygon.checkIn(new Point((Double)schema.getValue("longitude", t),(Double)schema.getValue("latitude", t)));
                        break;
                    }
                    case "circle" : {
                        p = Pattern.compile("^\\-?[0-9]+\\.?[0-9]*");
                        String longitude = jsonObject.get("longitude").toString();
                        boolean b1 = p.matcher(longitude).matches();
                        String latitude = jsonObject.get("latitude").toString();
                        boolean b2 = p.matcher(latitude).matches();
                        String circleradius = jsonObject.get("radius").toString();
                        boolean b3 = p.matcher(circleradius).matches();
                        if (!b1 || !b2 || !b3) {
                            flag = false;
                            break;
                        }
                        double circleRadius = Double.parseDouble(circleradius);
                        if (groupId.equals("hour") || groupId.equals("min")) {
                            circleRadius += 10;
                        }
                        Circle circle;
                        if (jzlx[0] != 0 || workstate[0] != 0) {
                            System.out.println("Query with workstate or jzlx");
                            circle = initSpecialCircle(longitude, latitude, circleRadius, jzlx, workstate);
                        }else {
                            circle = initCircle(longitude, latitude, circleRadius);
                            System.out.println("No conditions");
                        }

//                        circleArrayList.add(circle);// add to circle array list

                        externalLeftTop = new Point(circle.getExternalRectangle().getLeftTopX(), circle.getExternalRectangle().getLeftTopY());
                        externalRightBottom = new Point(circle.getExternalRectangle().getRightBottomX(), circle.getExternalRectangle().getRightBottomY());

                        if (jzlx[0] != 0 || workstate[0] != 0) {
                            System.out.println("SpecialCheckIn with workstate or jzlx");
                            predicate = t -> circle.SpecialCheckIn(new Point((Double)schema.getValue("longitude", t),(Double)schema.getValue("latitude", t),(Integer) schema.getValue("jzlx", t), (Integer) schema.getValue("workstate", t)));
                        }else {
                            predicate = t -> circle.checkIn(new Point((Double)schema.getValue("longitude", t),(Double)schema.getValue("latitude", t)));
                            System.out.println("No SpecialCheckIn");
                        }
        //                        predicateArrayList.add(predicate);// add to predicate array list
                        break;
                     }
                       default: return null;
                 }


//            System.out.println(circleArrayList.size() + " " + rectangleArrayList.size());
//            Circle circleTotal = initCircle("1.1","1.1",1.1);
//            circleTotal.setCircleArrayList(circleArrayList);
//            circleTotal.setRectangleArrayList(rectangleArrayList);
//            if (jzlx[0] != 0 || workstate[0] != 0){
//                System.out.println("SpecialCheckIn with workstate or jzlx");
//                predicate = t ->circleTotal.shapeListCheckIn(new Point((Double)schema.getValue("longitude", t),(Double)schema.getValue("latitude", t),(Integer) schema.getValue("jzlx", t), (Integer) schema.getValue("workstate", t)),true);
////                predicate = t -> circleTotal.SpecialCheckIn(new Point((Double)schema.getValue("longitude", t),(Double)schema.getValue("latitude", t),(Integer) schema.getValue("jzlx", t), (Integer) schema.getValue("workstate", t)));
//
//            }else {
//                System.out.println("No SpecialCheckIn");
//                predicate = t ->circleTotal.shapeListCheckIn(new Point((Double)schema.getValue("longitude", t),(Double)schema.getValue("latitude", t)),false);
////                predicate = t -> circleTotal.SpecialCheckIn(new Point((Double)schema.getValue("longitude", t),(Double)schema.getValue("latitude", t),(Integer) schema.getValue("jzlx", t), (Integer) schema.getValue("workstate", t)));
//            }

//            if (id != null) {
//                final DataTuplePredicate tempPredicate = localPredicate;
//                final DataSchema localSchema = schema;
//                final String tempId = id;
//                predicate = t -> ((tempPredicate == null) || tempPredicate.test(t)) && ((String)localSchema.getValue("devid", t)).equals(tempId);
//                finalPredicate = predicate;
//
//                predicateHint = new DataTupleEquivalentPredicateHint("devid", id);
//            } else {
//                finalPredicate = localPredicate;
//            }

                if (flag == true) {
                    final double xLow = externalLeftTop.x;
                    final double xHigh = externalRightBottom.x;
                    final double yLow = Math.min(externalRightBottom.y, externalLeftTop.y);
                    final double yHigh = Math.max(externalRightBottom.y, externalLeftTop.y);
                    GeoTemporalQueryClient queryClient = new GeoTemporalQueryClient(QueryServerIp, 10001);
                    try {
                        queryClient.connectWithTimeout(10000);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //统计查询Aggregator
                    aggregator = null;
                    if (!groupId.equals("null")) {
                        aggregator = new Aggregator<>(schema, "hphm", new AggregateField(new Count(), "nums"));
                    }
                    GeoTemporalQueryRequest queryRequest = new GeoTemporalQueryRequest<>(xLow, xHigh, yLow, yHigh,
                            startTime,
                            endTime, predicate,null,aggregator, null, null);
                    System.out.println("xLow:" + xLow + " " + xHigh + " " +yLow + " " + yHigh);
                    System.out.println("start: " + startTime + " end: " + endTime);
                    try {
                        //统计查询
                        QueryResponse response = queryClient.query(queryRequest);
    //                    System.out.println(response.toString());
                        List<DataTuple> tuples = response.getTuples();
                        System.out.println("tmpTuples size : " + tuples.size());
                        if(setType.equals("intersection")){// intersection set
                            if(firstQuery == false){ // compare with parent query result
                                List<DataTuple> tmpTuples = new ArrayList<>();
                                for(DataTuple tuple : tuples){
                                    if (finalTuples.contains(tuple)){
                                        tmpTuples.add(tuple);
                                    }
                                }
                                finalTuples = tmpTuples;
                            }else{
                                finalTuples = tuples;
                                firstQuery = false;
                            }
                        } else {// union set
                            if(firstQuery == false){
                                for(DataTuple tuple : tuples){
                                    if (!finalTuples.contains(tuple)){
                                        finalTuples.add(tuple);
                                    }
                                }
                            }else {
                                finalTuples = tuples;
                                firstQuery = false;
                            }
                        }
    //                    System.out.println(tuples.size());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        queryClient.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    JSONObject jsonFromTuple = null;
                    if (groupId.equals("hour") || groupId.equals("min")) {
                        queryResult = new JSONArray(); // empty the query result
                        float aveTime;
                        if (groupId.equals("min")) {
                            aveTime = (endTime - startTime) / (1000 * 60);
                        } else {
                            aveTime = (endTime - startTime) / (1000 * 60 * 60);
                        }
                        if (aveTime == 0) aveTime = 1;
                        float nums = finalTuples.size() / aveTime;
                        jsonFromTuple = new JSONObject();
                        jsonFromTuple.put("nums", nums);
                        queryResult.add(jsonFromTuple);
                    }else {
                        queryResult = new JSONArray(); // empty the query result
                        for (DataTuple tuple : finalTuples){
                            if (!groupId.equals("null")) {
                                jsonFromTuple = new JSONObject();
                                jsonFromTuple.put(groupId, tuple.get(0));
                                jsonFromTuple.put("nums", tuple.get(1));
                            }else {
                                jsonFromTuple = schema.getJsonFromDataTupleWithoutZcode(tuple,finalOptions);
                            }
                            queryResult.add(jsonFromTuple);
                        }
                    }
                }else{
                    queryResponse.put("success", false);
                    queryResponse.put("result", null);
                    queryResponse.put("errorCode","1001");
                    queryResponse.put("errorMsg", "参数解析失败，参数格式存在问题");
                    String result = JSONObject.toJSONString(queryResponse, SerializerFeature.WriteMapNullValue);
                    return result;
                }
                jsonObject = jsonObject.getJSONObject("subquery");// sub query
            }while(!jsonObject.toString().equals("{}"));
//                    System.out.println(jsonFromTuple);
            System.out.println("Amount : " + finalTuples.size() + " tuples.");
            queryResponse.put("success", true);
            queryResponse.put("result", queryResult);
            queryResponse.put("errorCode", null);
            queryResponse.put("errorMsg", null);
            String result = JSONObject.toJSONString(queryResponse, SerializerFeature.WriteMapNullValue);
            return result;
        }catch (NullPointerException e){
            e.printStackTrace();
            queryResponse.put("success", false);
            queryResponse.put("result", null);
            queryResponse.put("errorCode", 1002);
            queryResponse.put("errorMsg", "参数值无效或缺失必填参数22");
            String result = JSONObject.toJSONString(queryResponse, SerializerFeature.WriteMapNullValue);
            return result;
        }catch (JSONException e){
            e.printStackTrace();
            queryResponse.put("success", false);
            queryResponse.put("result", null);
            queryResponse.put("errorCode", 1002);
            queryResponse.put("errorMsg", "参数值无效或缺失必填参数33");
            String result = JSONObject.toJSONString(queryResponse, SerializerFeature.WriteMapNullValue);
            return result;
        }

    }

    Polygon initPolygon(JSONArray geoArray) {
        int size = geoArray.size();
        geoStr = new Point[size];
        for (int i = 0; i < size; i++) {
            String[] strings = geoArray.get(i).toString().split(" ");
            geoStr[i] = new Point(Double.parseDouble(strings[0]), Double.parseDouble(strings[1]));
        }
        Polygon.Builder polygonBuilder = Polygon.Builder();
        for (Point point : geoStr) {
            polygonBuilder.addVertex(point);
        }
        polygonBuilder.addVertex(geoStr[0]);
        return polygonBuilder.build();
    }

    Circle initCircle(String longitude, String latitude, double radius) {
        double circlelon = Double.parseDouble(longitude);
        double circlelat = Double.parseDouble(latitude);
        Circle circle = new Circle(circlelon, circlelat, radius);
        return circle;
    }

    Circle initSpecialCircle(String longitude, String latitude, double radius, int jzlx[], int workstate[]) {
        double circlelon = Double.parseDouble(longitude);
        double circlelat = Double.parseDouble(latitude);
        Circle circle = new Circle(circlelon, circlelat, radius, jzlx, workstate);
        return circle;
    }

    Rectangle initRectangel(String leftTop, String rightBottom) {
        double leftTop_x = Double.parseDouble(leftTop.split(",")[0]);
        double leftTop_y = Double.parseDouble(leftTop.split(",")[1]);
        double rightBottom_x = Double.parseDouble(rightBottom.split(",")[0]);
        double rightBottom_y = Double.parseDouble(rightBottom.split(",")[1]);
        Point rectLeftTop = new Point(leftTop_x, leftTop_y);
        Point rectRightBottom = new Point(rightBottom_x, rightBottom_y);
        Rectangle rectangle = new Rectangle(rectLeftTop, rectRightBottom);
        return rectangle;
    }

    Rectangle initSpecialRectangel(String leftTop, String rightBottom, int[] jzlx, int[] workstate) {
        double leftTop_x = Double.parseDouble(leftTop.split(",")[0]);
        double leftTop_y = Double.parseDouble(leftTop.split(",")[1]);
        double rightBottom_x = Double.parseDouble(rightBottom.split(",")[0]);
        double rightBottom_y = Double.parseDouble(rightBottom.split(",")[1]);
        Point rectLeftTop = new Point(leftTop_x, leftTop_y);
        Point rectRightBottom = new Point(rightBottom_x, rightBottom_y);
        Rectangle rectangle = new Rectangle(rectLeftTop, rectRightBottom, jzlx, workstate);
        return rectangle;
    }

    boolean ShapeSubQuery(boolean currentShapeCheckIn){
        shapeCheckIn = shapeCheckIn & currentShapeCheckIn;
        return shapeCheckIn;
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
        schema.setTemporalField("locationtime");
        schema.addIntField("zcode");
        schema.setPrimaryIndexField("zcode");
        return schema;
    }




    public static void main(String[] args) {

        String searchTest = "{\"type\":\"rectangle\",\"leftTop\":\"50,100\",\"rightBottom\":\"150,10\",\"geoStr\":null,\"longitude\":null,\"latitude\":null,\"radius\":null}";
        String searchTest2 = "{\"type\":\"circle\",\"leftTop\":null,\"rightBottom\":null,\"geoStr\":null,\"longitude\":100,\"latitude\":70,\"radius\":10}";
        String searchTest3 = "{\"type\":\"polygon\",\"leftTop\":null,\"rightBottom\":null,\"geoStr\":[\"1 3\",\"2 8\",\"5 4\",\"5 9\",\"7 5\"],\"longitude\":null,\"latitude\":null,\"radius\":null}";
        String businessParams = "{\"type\":\"polygon\",\"leftTop\":null,\"rightBottom\":null,\"geoStr\":[\"1 3\",\"2 8\",\"5 4\",\"5 9\",\"7 5\"],\"longitude\":null,\"latitude\":null,\"radius\":null}";
        PosSpacialSearchWs posSpacialSearchWs = new PosSpacialSearchWs();
        String result = posSpacialSearchWs.service(null, searchTest2);
        System.out.println(result);
    }
}
