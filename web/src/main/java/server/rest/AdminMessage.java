//package server.rest;
//
//
//
//import com.alibaba.fastjson.JSONObject;
//import indexingTopology.util.track.PosSpacialSearchWs;
//import server.query.naive.NaiveQueryImpl;
//
//import javax.ws.rs.*;
//import javax.ws.rs.core.MediaType;
//
//
//@Path("admin_message")
//public class AdminMessage {
////    @POST
////    @Produces(MediaType.TEXT_PLAIN)
////    @Consumes(MediaType.TEXT_PLAIN)
////    public String setWainingMessage(
////            @FormParam("message") String message) {
////        NaiveQueryImpl.instance().setAdminMessage(message);
////        return "Success!";
////    }
//
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public JSONObject getWarningMessage(@DefaultValue("null") @QueryParam("message") String message) {
////        String str = message;
////        str = str.replace(']','}');
//        StringBuilder sb = new StringBuilder(message);
//        sb.replace(0,1,"{");
//        sb.replace(message.length()-1,message.length(),"}");
//        String sbStr = sb.toString();
//        sbStr = sbStr.replace('(','{');
//        sbStr = sbStr.replace(')','}');
//
//
//        System.out.println(sbStr);
////        TrackSpacialSearchWs trackSpacialSearch = new TrackSpacialSearchWs();
////        String result = trackSpacialSearch.services(null,str);
//
////        TrackNew trackNew = new TrackNew();
////        String result = trackNew.service(null,str);
//
////        TrackSearchWs trackSearchWs = new TrackSearchWs();
////        String result = trackSearchWs.services(null,str);
//
//        PosSpacialSearchWs posSpacialSearchWs = new PosSpacialSearchWs();
//        String result = posSpacialSearchWs.service(null, sbStr,1L,1L,null);
////        System.out.println(str);
////        if (message.equals("null")) {
////            JSONObject jsonObject = new JSONObject();
////            jsonObject.put("message", NaiveQueryImpl.instance().getAdminMessage());
////            return jsonObject.toString();
////        } else
////        NaiveQueryImpl.instance().setAdminMessage(message);
////            JSONObject jsonObject = new JSONObject();
////            jsonObject.put("response", result);
//        System.out.println(result);
//        JSONObject jsonObject = JSONObject.parseObject(result);
//        return jsonObject;
////        }
//    }
//}
