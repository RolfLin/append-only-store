package indexingTopology.util.server.rest;

/**
 * Created by billlin on 2018/4/16
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MutiUserServer extends Thread {
    private Socket client;

    public MutiUserServer(Socket c) {
        client = c;
    }

    public void run() {

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream());

            int i = 0;
            while (true) {
                String str = in.readLine();// 接受客户方输入字符串
                System.out.println(str);


                i++;
                out.println("has receive...." + i);
                out.flush();
                if (str.indexOf("Accept-Charset:") != -1) {

                    break;

                }
                if (str.equals("end"))
                    break;
            }

            client.close();
        } catch (Exception e) {
        }
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub
        System.out.println("muti user server start");
        ServerSocket server=new ServerSocket(2156);
        while(true){
            MutiUserServer mus = new MutiUserServer(server.accept());
            mus.start();
        }
    }

}