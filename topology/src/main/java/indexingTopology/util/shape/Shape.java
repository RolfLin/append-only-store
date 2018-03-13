package indexingTopology.util.shape;

import java.util.ArrayList;

/**
 * Create by zelin on 17-12-5
 **/
public interface Shape {
    boolean checkIn (Point point);
    Rectangle getExternalRectangle();
    boolean shapeListCheckIn(Point point,boolean checkSpecial);
}
