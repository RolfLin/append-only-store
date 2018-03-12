package indexingTopology.util.track;

import indexingTopology.util.shape.Point;
import indexingTopology.util.shape.Rectangle;
import indexingTopology.util.shape.Shape;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by billlin on 2018/3/2
 */
public class LineItem implements Line, Serializable{

    private int devbtype;
    private String city[];
    private ArrayList<String> options;

    private String devid;

    public int getDevbtype() {
        return devbtype;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public String[] getCity() {
        return city;
    }

    public String getDevid() {
        return devid;
    }

    public LineItem(String[] city, int devbtype, String devid, ArrayList<String> options) {
        this.devbtype = devbtype;
        this.city = city;
        this.devid = devid;
        this.options = options;
    }


    @Override
    public boolean checkConform(Object city, Object devbtype, Object devid) {
        if(city == null || devbtype == null ||devid == null ){
            return false;
        }
        String cityStr = (String) city;
        int devbtypeInt = (int) devbtype;
        String devidStr = (String) devid;
        if (this.devbtype == devbtypeInt && this.devid.equals(devidStr)) {
            for(int i = 0;i < this.city.length; i++){
                if(this.city[i].equals(cityStr)){
                    return true;
                }
            }
            return false;
//            return true;
        }
        else
            return false;
    }
}
