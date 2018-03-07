package indexingTopology.util.track;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import indexingTopology.api.client.GeoTemporalQueryClient;
import indexingTopology.api.client.GeoTemporalQueryRequest;
import indexingTopology.api.client.QueryResponse;
import indexingTopology.common.data.DataSchema;
import indexingTopology.common.data.DataTuple;
import indexingTopology.common.logics.DataTuplePredicate;
import indexingTopology.util.shape.Circle;
import indexingTopology.util.shape.Circle2;
import indexingTopology.util.shape.Point;
import indexingTopology.util.shape.TrackItem;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by billlin on 2018/3/3
 */
public class TrackNew {

    private String QueryServerIp = "localhost";
    private Point externalLeftTop, externalRightBottom;
//    private int devbtype;
//    private String devid;
//    private double longitude;
//    private double latitude;
//    private double radius;
//    private long startTime;
//    private long endTime;

    public String service(String permissionsParams, String businessParams) {
        DataSchema schema = getDataSchema();
        try{
            JSONObject jsonObject = JSONObject.parseObject(businessParams);
            String type = jsonObject.get("type").toString();
            int devbtype = Integer.parseInt(jsonObject.get("devbtype").toString());
            String devid = jsonObject.get("devid").toString();
            String city = jsonObject.get("city").toString();
//            double longitude = Double.parseDouble(jsonObject.get("longitude").toString());
//            double latitude = Double.parseDouble(jsonObject.get("latitude").toString());
//            double radius = Double.parseDouble(jsonObject.get("radius").toString());
//            long startTime = Long.parseLong(jsonObject.get("startTime").toString());
//            long endTime = Long.parseLong(jsonObject.get("endTime").toString());
            String startTimeStr = jsonObject.getString("startTime");
            String endTimeStr = jsonObject.getString("endTime");
            if (startTimeStr.equals("null") && endTimeStr.equals("null")) {
                startTimeStr = "2017-02-01 00:00:00";
                endTimeStr = "2017-02-07 00:00:00";
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

            String functionStr = jsonObject.get("function").toString();

            if(type == null || type.equals("line") == false){
                JSONObject queryResponse = new JSONObject();
                queryResponse.put("success", false);
                queryResponse.put("result", null);
                queryResponse.put("errorCode", 1003);
                queryResponse.put("errorMsg", "查询类型不正确");
                String result = JSONObject.toJSONString(queryResponse, SerializerFeature.WriteMapNullValue);
                return result;
            }
            DataTuplePredicate predicate = null;
            DataTuplePredicate postPredicate = null;
//            Circle circle = new Circle(longitude, latitude, radius);
//            Circle2 circle2 = new Circle2(longitude, latitude, radius);
//            TrackItem trackItem = new TrackItem(longitude, latitude, radius);
            LineItem lineItem = new LineItem(city, devbtype, devid);
//            externalLeftTop = new Point(circle.getExternalRectangle().getLeftTopX(), circle.getExternalRectangle().getLeftTopY());
//            externalRightBottom = new Point(circle.getExternalRectangle().getRightBottomX(), circle.getExternalRectangle().getRightBottomY());
//            int a = 0;
//            postPredicate = t -> trackItem.checkIn(new Point((Double) schema.getValue("longitude", t), (Double) schema.getValue("latitude", t)));
            predicate = t -> lineItem.checkConform(schema.getValue("city", t),schema.getValue("devbtype", t),schema.getValue("devid", t));

            JSONObject queryResponse = new JSONObject();
//            final double xLow = externalLeftTop.x;
//            final double xHigh = externalRightBottom.x;
//            final double yLow = Math.min(externalRightBottom.y, externalLeftTop.y);
//            final double yHigh = Math.max(externalRightBottom.y, externalLeftTop.y);

            JSONArray queryResult = null;
            GeoTemporalQueryClient queryClient = new GeoTemporalQueryClient(QueryServerIp, 10001);
            try {
                queryClient.connectWithTimeout(10000);
            } catch (IOException e) {
                e.printStackTrace();
            }

            GeoTemporalQueryRequest queryRequest = new GeoTemporalQueryRequest<>(Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE,
                    startTime,
                    endTime, predicate, null, null, null, null);
            try {
                QueryResponse response = queryClient.query(queryRequest);
                System.out.println(response.toString());
                List<DataTuple> tuples = response.getTuples();
                queryResult = new JSONArray();
                for (DataTuple tuple : tuples) {
                    JSONObject jsonFromTuple = schema.getJsonFromDataTupleWithoutZcode(tuple);
                    queryResult.add(jsonFromTuple);
    //                        System.out.println(jsonFromTuple);
                }
    //                    System.out.println(tuples.size() + " tuples.");
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
            queryResponse.put("success", true);
            queryResponse.put("result", queryResult);
            queryResponse.put("errorCode", null);
            queryResponse.put("errorMsg", null);
//            String result = JSONObject.toJSONString(queryResponse);
            String result = JSONObject.toJSONString(queryResponse, SerializerFeature.WriteMapNullValue);
            return result;
        }catch (NullPointerException e){
            e.printStackTrace();
            JSONObject queryResponse = new JSONObject();
            queryResponse.put("success", false);
            queryResponse.put("result", null);
            queryResponse.put("errorCode", 1002);
            queryResponse.put("errorMsg", "参数值无效或缺失必填参数");
            String result = JSONObject.toJSONString(queryResponse, SerializerFeature.WriteMapNullValue);
            return result;
        }catch (JSONException e){
            e.printStackTrace();
            e.printStackTrace();
            JSONObject queryResponse = new JSONObject();
            queryResponse.put("success", false);
            queryResponse.put("result", null);
            queryResponse.put("errorCode", 1002);
            queryResponse.put("errorMsg", "参数值无效或缺失必填参数");
            String result = JSONObject.toJSONString(queryResponse, SerializerFeature.WriteMapNullValue);
            return result;
        }
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
}
