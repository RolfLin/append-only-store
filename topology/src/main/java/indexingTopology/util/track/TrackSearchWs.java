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
import indexingTopology.util.shape.Point;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by billlin on 2018/3/3
 */
public class TrackSearchWs {

    private String QueryServerIp = "localhost";
    private Point externalLeftTop, externalRightBottom;

    public String services(String permissionsParams, String businessParams) {
        DataSchema schema = getDataSchema();
        try{
            JSONObject jsonObject = JSONObject.parseObject(businessParams);
            int devbtype = Integer.parseInt(jsonObject.get("devbtype").toString());
            String devid = jsonObject.get("devid").toString();
            String city = jsonObject.get("city").toString();
            long startTime = Long.parseLong(jsonObject.get("startTime").toString());
            long endTime = Long.parseLong(jsonObject.get("endTime").toString());

            DataTuplePredicate predicate = null;
            DataTuplePredicate postPredicate = null;
            LineItem lineItem = new LineItem(city, devbtype, devid);
            predicate = t -> lineItem.checkConform(schema.getValue("city", t),schema.getValue("devbtype", t),schema.getValue("devid", t));

            JSONObject queryResponse = new JSONObject();

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
                List<DataTuple> tuples = response.getTuples();
                queryResult = new JSONArray();
                for (DataTuple tuple : tuples) {
                    JSONObject jsonFromTuple = schema.getJsonFromDataTupleWithoutZcode(tuple);
                    queryResult.add(jsonFromTuple);
    //                        System.out.println(jsonFromTuple);
                }
                        System.out.println("Amount : " + tuples.size());
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
