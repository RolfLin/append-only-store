package indexingTopology.util.shape;

import java.io.Serializable;

/**
 * Create by zelin on 17-12-5
 **/
public class Circle implements Shape, Serializable{

    private double longitude;
    private double latitude;
    private double radius;
    private int jzlx[];
    private int workstate[];

    public Circle(double longitude, double latitude, double radius) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.radius = radius;
    }

    public Circle(double longitude, double latitude, double radius, int jzlx[], int workstate[]) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.radius = radius;
        this.jzlx = jzlx;
        this.workstate = workstate;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public boolean checkIn(Point point) {
        double pointX = point.x, pointY = point.y;
        double len = Math.sqrt(Math.pow(pointX - longitude, 2) + Math.pow(pointY - latitude, 2));
        if(radius >= len) {
            return true;
        }
        return false;
    }

    public boolean SpecialCheckIn(Point point) {
        double pointX = point.x, pointY = point.y;
        double len = Math.sqrt(Math.pow(pointX - longitude, 2) + Math.pow(pointY - latitude, 2));
        if(radius >= len) {
            boolean b1 = false, b2 = false;
            for(int i = 0; i < this.jzlx.length; i++){
                if(this.jzlx[i] == point.jzlx){
                    b1 = true;
                    break;
                }
            }
            for(int i = 0; i < this.workstate.length; i++){
                if(this.workstate[i] == point.workstate){
                    b2 = true;
                    break;
                }
            }
            if( (b1 && b2) || (b1 & this.workstate[0] == 0) || (this.workstate[0] == 0 & b2)){
                return true;
            }
            else return false;
        }
        return false;
    }

    @Override
    public Rectangle getExternalRectangle() {
        Point leftTop = new Point(longitude - radius, latitude + radius);
        Point rightBottom = new Point(longitude + radius, latitude - radius);
        return new Rectangle(leftTop, rightBottom);
    }
}
