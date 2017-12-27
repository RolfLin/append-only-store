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

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * Created by billlin on 2017/12/15
 */
public class TrackPagedSearchWs implements Serializable{
    private String city;
    private int devbtype;
    private String devid;
    private long startTime;
    private long endTime;
    private int page;
    private int rows;
    private String errorCode;
    private String errorMsg;

    public TrackPagedSearchWs(){

//        this.city = (String)businessParams.get("city");
//        this.devbtype = (int)businessParams.get("devbtype");
//        this.devid = (String)businessParams.get("devid");
//        this.startTime = (long)businessParams.get("startTime");
//        this.endTime = (long)businessParams.get("endTime");
    }

    public String services(String permissionParams, String businessParams) {
        JSONObject queryResponse = new JSONObject();
        try{
            JSONObject jsonObject = JSONObject.parseObject(businessParams);
            try{
                getQueryJson(jsonObject); // query failed,json format is error
            }
            catch (JSONException e){
                queryResponse.put("result", null);
                queryResponse.put("errorCode", "1002");
                queryResponse.put("errorMsg", "参数值无效或者缺失必填参数");
                String result = JSONObject.toJSONString(queryResponse, SerializerFeature.WriteMapNullValue);
                return result;
            }
            catch (NullPointerException e){
                queryResponse.put("result", null);
                queryResponse.put("errorCode", "1002");
                queryResponse.put("errorMsg", "参数值无效或者缺失必填参数");
                String result = JSONObject.toJSONString(queryResponse, SerializerFeature.WriteMapNullValue);
                return result;
            }
            catch (IllegalArgumentException e){
                queryResponse.put("result", null);
                queryResponse.put("errorCode", "1");
                queryResponse.put("errorMsg", "参数值无效或者缺失必填参数");
                String result = JSONObject.toJSONString(queryResponse, SerializerFeature.WriteMapNullValue);
                return result;
            }
        }catch (JSONException e){// query failed, json value invalid
            errorCode = "1001";
//            errorMsg = Error(errorCode);
            queryResponse.put("result", null);
            queryResponse.put("errorCode", errorCode);
            queryResponse.put("errorMsg", "参数解析失败，参数格式存在问题");
            String result = JSONObject.toJSONString(queryResponse, SerializerFeature.WriteMapNullValue);
            return result;
        }

        // query success
        GeoTemporalQueryClient queryClient = new GeoTemporalQueryClient("localhost", 10001);
        try {
            queryClient.connectWithTimeout(10000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        DataSchema schema = getDataSchema();
        DataTuplePredicate predicate;
//        System.out.println("city : " + city);
//        System.out.println("devbtype : " + devbtype);
//        System.out.println("devid : " + devid);
//        System.out.println("startTime : " + startTime);
//        System.out.println("endTime : " + endTime);
        predicate = t -> CheckEqual((String)schema.getValue("city", t),(int)schema.getValue("devbtype", t),(String)schema.getValue("devid", t));
        GeoTemporalQueryRequest queryRequest = new GeoTemporalQueryRequest<>(Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE,
                startTime,
                endTime, predicate, null, null, null, null);
        try {
            QueryResponse response = queryClient.query(queryRequest);
            DataSchema outputSchema = response.getSchema();
            System.out.println("datatuples : " + response.dataTuples.size());
            List<DataTuple> tuples = response.getTuples();

            int totalPage = tuples.size()/rows;

            queryResponse.put("success", true);
            JSONArray queryResult = new JSONArray();
            if(tuples.size() > 0 && tuples.size() > rows){
                for (int i = rows * (page - 1); i < rows * page; i++) {
                    if(i >= tuples.size()){
                        break;
                    }
                    queryResult.add(schema.getJsonFromDataTupleWithoutZcode(tuples.get(i)));
                }
            }
            JSONObject result = new JSONObject();
            result.put("total", tuples.size());
            result.put("page",page);
            result.put("sortName",null);
            result.put("sortOrder",null);
            result.put("city",city);
            result.put("devbtype",devbtype);
            result.put("devid",devid);
            result.put("startTime",startTime);
            result.put("endTime",endTime);
            result.put("startRowKey",null);
            result.put("stopRowKey",null);
            result.put("rows",queryResult);
            queryResponse.put("result", result);
            queryResponse.put("errorCode", null);
            queryResponse.put("errorMsg", null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            queryClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = JSONObject.toJSONString(queryResponse, SerializerFeature.WriteMapNullValue);
        return result;
    }

    public boolean getQueryJson(JSONObject businessParams) throws JSONException, NullPointerException{
//        try {
            this.city = (String)businessParams.get("city");
            this.devbtype = (int)businessParams.get("devbtype");
            this.devid = (String)businessParams.get("devid");
            this.startTime = (long)businessParams.get("startTime");
            this.endTime = (long)businessParams.get("endTime");
            this.page = (int)businessParams.get("page");
            this.rows = (int)businessParams.get("rows");
            if(city == null || devid == null || businessParams.size() > 7){
                throw new IllegalArgumentException();
            }
        return true;
    }

    public boolean CheckEqual(String city, int devbtype, String devid) {
        if (this.city.equals(city) && this.devbtype == devbtype && this.devid.equals(devid)) {
            return true;
        }
        else
            return false;
    }

    static private DataSchema getDataSchema() {
        DataSchema schema = new DataSchema();

        schema.addIntField("devbtype");
        schema.addVarcharField("devstype", 32);
        schema.addVarcharField("devid", 32);
        schema.addVarcharField("city", 32);
        schema.addDoubleField("longitude");
        schema.addDoubleField("latitude");
        schema.addDoubleField("altitude");
        schema.addDoubleField("speed");
        schema.addDoubleField("direction");
        schema.addLongField("locationtime");
        schema.addIntField("workstate");
        schema.addVarcharField("clzl", 32);
        schema.addVarcharField("hphm", 32);
        schema.addIntField("jzlx");
        schema.addVarcharField("jybh", 32);
        schema.addVarcharField("jymc", 32);
        schema.addVarcharField("lxdh", 32);
        schema.addVarcharField("ssdwdm", 32);
        schema.addVarcharField("ssdwmc", 32);
        schema.addVarcharField("teamno", 32);
        schema.addVarcharField("dth", 32);
        schema.addVarcharField("reserve1", 32);
        schema.addVarcharField("reserve2", 32);
        schema.addVarcharField("reserve3", 32);
        schema.setTemporalField("locationtime");
        schema.addIntField("zcode");
        schema.setPrimaryIndexField("zcode");
        return schema;
    }




//    static private DataSchema getDataSchema() {
//        DataSchema schema = new DataSchema();
//        schema.addDoubleField("lon");
//        schema.addDoubleField("lat");
//        schema.addIntField("devbtype");
//        schema.addVarcharField("devid", 8);
////        schema.addVarcharField("id", 32);
//        schema.addVarcharField("city",32);
//        schema.addLongField("locationtime");
//        schema.setTemporalField("locationtime");
////        schema.addLongField("timestamp");
//        schema.addIntField("zcode");
//        schema.setPrimaryIndexField("zcode");
//
//        return schema;
//    }

}