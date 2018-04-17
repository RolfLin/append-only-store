package indexingTopology.util.server.query.Interface;

/**
 * Created by robert on 29/12/16.
 */
public class Point {
    public int attractionId;
    public double x;
    public double y;
    public String name;

    public Point(int attractionId, double x, double y, String name) {
        this.attractionId = attractionId;
        this.x = x;
        this.y = y;
        this.name = name;
    }

}
