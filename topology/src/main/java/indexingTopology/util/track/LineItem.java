package indexingTopology.util.track;

import java.io.Serializable;

/**
 * Created by billlin on 2018/3/2
 */
public class LineItem implements Line, Serializable{

    private int devbtype;
    private String city;
    private String devid;

    public LineItem(String city, int devbtype, String devid) {
        this.devbtype = devbtype;
        this.city = city;
        this.devid = devid;
    }

    @Override
    public boolean checkConform(Object city, Object devbtype, Object devid) {
        if(city == null || devbtype == null ||devid == null ){
            return false;
        }
        String cityStr = (String) city;
        int devbtypeInt = (int) devbtype;
        String devidStr = (String) devid;
        if (this.city.equals(cityStr) && this.devbtype == devbtypeInt && this.devid.equals(devidStr)) {
            return true;
        }
        else
            return false;
    }
}
