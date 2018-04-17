package indexingTopology.util.server.rest;//package indexingTopology.util.server.rest;
//
//
//
//
//import com.alibaba.fastjson.JSONObject;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//import indexingTopology.util.track.PosNonSpacialSearchWs;
//import indexingTopology.util.track.PosSpacialSearchWs;
//import indexingTopology.util.track.TrackPagedSearchWs;
//import indexingTopology.util.track.TrackSearchWs;
//
//
//import javax.ws.rs.*;
//import javax.ws.rs.core.MediaType;
//
//
//
//
//import static com.google.common.base.Preconditions.checkArgument;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.Callable;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//
//import javax.ws.rs.client.AsyncInvoker;
//import javax.ws.rs.client.Client;
//import javax.ws.rs.client.ClientBuilder;
//import javax.ws.rs.client.InvocationCallback;
//import javax.ws.rs.client.WebTarget;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * Created by billlin on 2018/4/15
// */
//
//@Path("admin")
//public class MyJerSeyTest {
//
//
//        @POST
//        @Produces(MediaType.TEXT_PLAIN)
//        @Consumes(MediaType.TEXT_PLAIN)
//        public String setWainingMessage(
//                @FormParam("message") String message) {
//            System.out.println(message);
//            return "Success!";
//        }
//
//        @GET
//        @Produces(MediaType.APPLICATION_JSON)
//        public String getWarningMessage(@DefaultValue("null") @QueryParam("permissionParams") String permissionParams,@QueryParam("businessParams") String businessParams ) {
////        String str = message;
////        str = str.replace(']','}');
//            StringBuilder messageBuilder = null;
//            String jsonStr = null;
//            String result = null;
//            messageBuilder = new StringBuilder(businessParams);
//            messageBuilder.replace(0,1,"{");
//            messageBuilder.replace(businessParams.length()-1,businessParams.length(),"}");
//            jsonStr = messageBuilder.toString();
//            jsonStr = jsonStr.replace('(','{');
//            jsonStr = jsonStr.replace(')','}');
//
//            long searchStartTime = System.currentTimeMillis();
//
//            JSONObject messageJson = JSONObject.parseObject(jsonStr);
//            if(messageJson.getString("type") != null){
//                System.out.println("posSpacialSearchWs");
//                messageJson.getString("type");
//                long endStartTime = System.currentTimeMillis();
//                long usedTime = endStartTime - searchStartTime;
//                System.out.println("search type time : " + usedTime);
//                PosSpacialSearchWs posSpacialSearchWs = new PosSpacialSearchWs();
//                result = posSpacialSearchWs.service(null, jsonStr,1L,1L,null);
//            }else{
//                if(messageJson.getString("page") != null){
//                    System.out.println("trackPagedSearchWs");
//                    long endStartTime = System.currentTimeMillis();
//                    long usedTime = endStartTime - searchStartTime;
//                    System.out.println("search type time : " + usedTime);
//                    TrackPagedSearchWs trackPagedSearchWs = new TrackPagedSearchWs();
//                    result = trackPagedSearchWs.services(null, jsonStr);
//                } else{
//                    if(messageJson.getString("devid") != null){
//                        System.out.println("trackSearchWs");
//                        long endStartTime = System.currentTimeMillis();
//                        long usedTime = endStartTime - searchStartTime;
//                        System.out.println("search type time : " + usedTime);
//                        TrackSearchWs trackSearchWs = new TrackSearchWs();
//                        result = trackSearchWs.services(null, jsonStr);
//                    }else{
//                        System.out.println("posNonSpacialSearchWs");
//                        long endStartTime = System.currentTimeMillis();
//                        long usedTime = endStartTime - searchStartTime;
//                        System.out.println("search type time : " + usedTime);
//                        PosNonSpacialSearchWs posNonSpacialSearchWs = new PosNonSpacialSearchWs();
//                        result = posNonSpacialSearchWs.services(null,null);
//                    }
//                }
//            }
////            try {
////                messageJson.getString("type");
////                System.out.println("type : " + messageJson.getString("type"));
////                PosSpacialSearchWs posSpacialSearchWs = new PosSpacialSearchWs();
////                result = posSpacialSearchWs.service(null, jsonStr,1L,1L,null);
////            }catch (NullPointerException e1) {
////                try {
////                    messageJson.getString("page");
////                    TrackPagedSearchWs trackPagedSearchWs = new TrackPagedSearchWs();
////                    result = trackPagedSearchWs.services(null, jsonStr);
////                }catch (NullPointerException e2) {
////                    TrackSearchWs trackSearchWs = new TrackSearchWs();
////                    result = trackSearchWs.services(null, jsonStr);
////                }
////            }
//
//
//
//
//            System.out.println(jsonStr);
////        TrackSpacialSearchWs trackSpacialSearch = new TrackSpacialSearchWs();
////        String result = trackSpacialSearch.services(null,str);
//
////        TrackNew trackNew = new TrackNew();
////        String result = trackNew.service(null,str);
//
////        TrackSearchWs trackSearchWs = new TrackSearchWs();
////        String result = trackSearchWs.services(null,str);
//
//
//
//
////        System.out.println(str);
////        if (message.equals("null")) {
////            JSONObject jsonObject = new JSONObject();
////            jsonObject.put("message", NaiveQueryImpl.instance().getAdminMessage());
////            return jsonObject.toString();
////        } else
////        NaiveQueryImpl.instance().setAdminMessage(message);
////            JSONObject jsonObject = new JSONObject();
////            jsonObject.put("response", result);
////        System.out.println(result);
//            JSONObject jsonObject = JSONObject.parseObject(result);
//
//
//            JsonObject returnData = new JsonParser().parse(result).getAsJsonObject();
//
//            long endStartTime = System.currentTimeMillis();
//            long usedTime = endStartTime - searchStartTime;
//            System.out.println("elapsed time : " + usedTime + "\n");
//            return result;
////        }
//        }
//
//
//    private static Logger log = LoggerFactory.getLogger(JerseyAsyncTest.class);
//
//    static Client client = ClientBuilder.newClient();
//    // 分页查询次数
//    static int pageSize = 10;
//    // 每次分发数量
//    static int requestCount = 20;
//
//    //    /**
////     * @see http请求不会阻塞，但是回调函数会阻塞进程,借助CountDownLatch实现批量异步请求
////     * @see 批量请求依赖ExecutorService实现多进程并发
////     *
////     * @see 耗时 2S
////     */
//    @Test
//    public void batch_dispatcher_multiThread() throws Exception {
//        long st = System.currentTimeMillis();
//        ExecutorService executor = Executors.newFixedThreadPool(pageSize);
//        while (pageSize > 0) {
//            System.out.println("分页查询--------------" + pageSize);
//            Callable<String> task = new MyJerSeyTest.MyCallable();
//            executor.submit(task);
//            pageSize--;
//        }
//        executor.shutdown();
//        // Wait until all threads are finish
//        while (!executor.isTerminated()) {
//            // System.out.println(DateUtil.getCurrentDate() + "...waiting...");
//        }
//        long et = System.currentTimeMillis();
//        System.out.println(et-st);
//    }
//
//    //    /**
////     * 阻塞请求
////     *
////     * @see 耗时4S
////     * @throws Exception
////     */
//    @Test
//    public void batch_dispatcher_sequential() throws Exception {
//        long st = System.currentTimeMillis();
//        while (pageSize > 0) {
//            System.out.println("分页查询--------------" + pageSize);
//
//            asyncRequest();
//            pageSize--;
//        }
//        long et = System.currentTimeMillis();
//        System.out.println(et-st);
//    }
//
//    class MyCallable implements Callable<String> {
//        @Override
//        public String call() throws Exception {
//            asyncRequest();
//            return null;
//        }
//    }
//
//
//
//
//
//
//    private void asyncRequest() throws InterruptedException {
//
//
//
//
//
//
//        CountDownLatch latch = new CountDownLatch(requestCount);
//
//        List<Future<Response>> futureResponseList = new ArrayList<Future<Response>>();
//        for (int j = 0; j < requestCount; j++) {
//            AsyncInvoker asyncInvoker = getAsyncInvoker();
//            MyJerSeyTest.AsyncCallback callback = new MyJerSeyTest.AsyncCallback(asyncInvoker, 10, latch);
//            Future<Response> futureResponse = asyncInvoker.get(callback);
//            futureResponseList.add(futureResponse);
//        }
//
//        for (Future<Response> future : futureResponseList) {
//            System.out.println("request--------------第" + pageSize + "页");
//            try {
//                future.get();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            System.out.println("loop-------------第" + pageSize + "页");
//        }
//        latch.await();
//    }
//
//    private static AsyncInvoker getAsyncInvoker() {
//        String PATH = "http://musicbrainz.org/ws/1/artist?limit=1&query=fashion";
//        WebTarget target = client.target(PATH);
//        AsyncInvoker asyncInvoker = target.request(MediaType.APPLICATION_JSON).async();
//        return asyncInvoker;
//    }
//
//    private class AsyncCallback implements InvocationCallback<Response> {
//        private AsyncInvoker invoker;
//        private int retries;
//        CountDownLatch latch;
//
//        AsyncCallback(AsyncInvoker invoker, int retries, CountDownLatch latch) {
//            checkArgument(retries > 0);
//            this.invoker = invoker;
//            this.retries = retries;
//            this.latch = latch;
//        }
//
//        @Override
//        public void completed(Response response) {
//            try {
//                // System.out.println("Response status code " + response.getStatus() + " received.");
//                // Thread.sleep(5000);
//                System.out.println("-----------------complete- :" + response.getEntity().toString().length());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            latch.countDown();
//        }
//
//        @Override
//        public void failed(Throwable throwable) {
//            System.out.println("*****failed--------------");
//            if (retries > 0) {
//                retry();
//            } else {
//                latch.countDown();
//                this.failed(throwable);
//            }
//        }
//
//        private void retry() {
//            System.out.println("*****retry--------------");
//            retries--;
//            invoker.get(this);
//        }
//    }
//
//
//}
