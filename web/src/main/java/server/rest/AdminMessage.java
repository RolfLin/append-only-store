package server.rest;



import indexingTopology.util.track.PosSpacialSearchWs;
import server.compress.GZIPUtils;
import server.query.naive.NaiveQueryImpl;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * Created by robert on 28/12/16.
 */
@Path("admin_message")
public class AdminMessage {
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public String setWainingMessage(
            @FormParam("message") String message) {
        NaiveQueryImpl.instance().setAdminMessage(message);
        return "Success!";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getWarningMessage(@DefaultValue("null") @QueryParam("message") String message) throws IOException {
        String str = message.replace('[','{');
        str = str.replace(']','}');

//        System.out.println(str);
//        TrackSpacialSearchWs trackSpacialSearch = new TrackSpacialSearchWs();
//        String result = trackSpacialSearch.services(null,str);

//        TrackNew trackNew = new TrackNew();
//        String result = trackNew.service(null,str);

//        TrackSearchWs trackSearchWs = new TrackSearchWs();
//        String result = trackSearchWs.services(null,str);

        PosSpacialSearchWs posSpacialSearchWs = new PosSpacialSearchWs();
        String result = posSpacialSearchWs.service(null, str);
//        System.out.println(result);
//        System.out.println(GZIPUtils.uncompress("����".getBytes("UTF-8")));
//        System.out.println(str);
//        if (message.equals("null")) {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("message", NaiveQueryImpl.instance().getAdminMessage());
//            return jsonObject.toString();

//        } else {
        NaiveQueryImpl.instance().setAdminMessage(message);
        System.out.println(result);
//        System.out.println(GZIPUtils.CompressToBase64(result));
//        System.out.println("ssssss" + result.length());
        String bytes = GZIPUtils.CompressToBase64(result);
//        System.out.println("llllll" + bytes.length());
//        System.out.println(GZIPUtils.DecompressToBase64(bytes));
        System.out.println("");
//        String s = "eyJyZXN1bHQiOlt7Im51bXMiOjIwOTIuMH1dLCJzdWNjZXNzIjp0cnVlLCJlcnJvckNvZGUiOm51bGwsImVycm9yTXNnIjpudWxsfQ==";
//        System.out.println("this  is ss       " + GZIPUtils.uncompress(GZIPUtils.compress("eyJyZXN1bHQiOlt7Im51bXMiOjIwOTIuMH1dLCJzdWNjZXNzIjp0cnVlLCJlcnJvckNvZGUiOm51bGwsImVycm9yTXNnIjpudWxsfQ==")));
//            JSONObject jsonObject = JSON.parseObject(result);
//            jsonObject.put("response",result);
//        System.out.println(result.length());
//        JSONObject jsonObject = JSONObject.parseObject(result);
//        return GZIPUtils.compress(result.getBytes());
//        }
//        String s = "H4sIAAAAAAAAAKtWKkotLs0pUbKKrlbKK80tVrIyMrKw1DOojdVRKi5NTk4tBgqVFJWm6iilFhXlFznnp6QqWeWV5uRABXyL0yH8WgBaOAygTAAAAA==";
//        System.out.println(GZIPUtils.DecompressToBase64(s));
        return bytes;
//        return GZIPUtils.uncompress(GZIPUtils.compress(result));
    }
}
