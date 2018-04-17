package indexingTopology.util.server.rest;

/**
 * Created by billlin on 2018/4/15
 */

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static com.google.common.base.Preconditions.checkArgument;

public class JerseyAsyncTest {
    private static Logger log = LoggerFactory.getLogger(JerseyAsyncTest.class);

    static Client client = ClientBuilder.newClient();
    // 分页查询次数
    static int pageSize = 10;
    // 每次分发数量
    static int requestCount = 20;

//    /**
//     * @see http请求不会阻塞，但是回调函数会阻塞进程,借助CountDownLatch实现批量异步请求
//     * @see 批量请求依赖ExecutorService实现多进程并发
//     *
//     * @see 耗时 2S
//     */
    @Test
    public void batch_dispatcher_multiThread() throws Exception {
        long st = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(pageSize);
        while (pageSize > 0) {
            System.out.println("分页查询--------------" + pageSize);
            Callable<String> task = new MyCallable();
            executor.submit(task);
            pageSize--;
        }
        executor.shutdown();
        // Wait until all threads are finish
        while (!executor.isTerminated()) {
            // System.out.println(DateUtil.getCurrentDate() + "...waiting...");
        }
        long et = System.currentTimeMillis();
        System.out.println(et-st);
    }

//    /**
//     * 阻塞请求
//     *
//     * @see 耗时4S
//     * @throws Exception
//     */
    @Test
    public void batch_dispatcher_sequential() throws Exception {
        long st = System.currentTimeMillis();
        while (pageSize > 0) {
            System.out.println("分页查询--------------" + pageSize);

            asyncRequest();
            pageSize--;
        }
        long et = System.currentTimeMillis();
        System.out.println(et-st);
    }

    class MyCallable implements Callable<String> {
        @Override
        public String call() throws Exception {
            asyncRequest();
            return null;
        }
    }

    private void asyncRequest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(requestCount);

        List<Future<Response>> futureResponseList = new ArrayList<Future<Response>>();
        for (int j = 0; j < requestCount; j++) {
            AsyncInvoker asyncInvoker = getAsyncInvoker();
            AsyncCallback callback = new AsyncCallback(asyncInvoker, 10, latch);
            Future<Response> futureResponse = asyncInvoker.get(callback);
            futureResponseList.add(futureResponse);
        }

        for (Future<Response> future : futureResponseList) {
            System.out.println("request--------------第" + pageSize + "页");
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("loop-------------第" + pageSize + "页");
        }
        latch.await();
    }

    private static AsyncInvoker getAsyncInvoker() {
        String PATH = "http://musicbrainz.org/ws/1/artist?limit=1&query=fashio";
        WebTarget target = client.target(PATH);
        AsyncInvoker asyncInvoker = target.request(MediaType.APPLICATION_JSON).async();
        return asyncInvoker;
    }

    private class AsyncCallback implements InvocationCallback<Response> {
        private AsyncInvoker invoker;
        private int retries;
        CountDownLatch latch;

        AsyncCallback(AsyncInvoker invoker, int retries, CountDownLatch latch) {
            checkArgument(retries > 0);
            this.invoker = invoker;
            this.retries = retries;
            this.latch = latch;
        }

        @Override
        public void completed(Response response) {
            try {
                // System.out.println("Response status code " + response.getStatus() + " received.");
                // Thread.sleep(5000);
                System.out.println("-----------------complete- :" + response.getEntity().toString().length());
            } catch (Exception e) {
                e.printStackTrace();
            }
            latch.countDown();
        }

        @Override
        public void failed(Throwable throwable) {
            System.out.println("*****failed--------------");
            if (retries > 0) {
                retry();
            } else {
                latch.countDown();
                this.failed(throwable);
            }
        }

        private void retry() {
            System.out.println("*****retry--------------");
            retries--;
            invoker.get(this);
        }
    }
}