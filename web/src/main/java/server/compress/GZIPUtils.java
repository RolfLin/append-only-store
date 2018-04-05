package server.compress;



import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Create by zelin on 18-3-7
 **/

public class GZIPUtils {

    public static String CompressToBase64(String string){
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
            GZIPOutputStream gos = new GZIPOutputStream(os);
            gos.write(string.getBytes());
            gos.close();
            byte[] compressed = os.toByteArray();
            os.close();


            String result = Base64.encodeBase64String(compressed);
            return result;
        } catch (IOException e) {
            e.printStackTrace();




        }
        catch (Exception ex){


        }
        return "";
    }


    public static String DecompressToBase64(String textToDecode){
        //String textToDecode = "H4sIAAAAAAAAAPNIzcnJBwCCidH3BQAAAA==\n";
        try {
            byte[] compressed = Base64.decodeBase64(textToDecode);
            final int BUFFER_SIZE = 32;
            ByteArrayInputStream inputStream = new ByteArrayInputStream(compressed);


            GZIPInputStream gis  = new GZIPInputStream(inputStream, BUFFER_SIZE);


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] data = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = gis.read(data)) != -1) {
                baos.write(data, 0, bytesRead);
            }


            return baos.toString("UTF-8");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception ex){


        }
        return "";
    }
}

