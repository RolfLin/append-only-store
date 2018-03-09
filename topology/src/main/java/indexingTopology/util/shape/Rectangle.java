package indexingTopology.util.shape;

import java.io.Serializable;

/**
 * Create by zelin on 17-12-5
 **/
public class Rectangle implements Shape, Serializable{

    private double leftTopX, leftTopY;
    private double rightBottomX, rightBottomY;
    private int jzlx;
    private int workstate;

    public Rectangle(Point leftTop, Point rightBottom) {
        this.leftTopX = Math.min(leftTop.x, rightBottom.x);
        this.leftTopY = Math.max(leftTop.y, rightBottom.y);
        this.rightBottomX = Math.max(leftTop.x, rightBottom.x);
        this.rightBottomY = Math.min(leftTop.y, rightBottom.y);
    }

    public Rectangle(Point leftTop, Point rightBottom, int jzlx, int workstate) {
        this.leftTopX = leftTop.x;
        this.leftTopY = leftTop.y;
        this.rightBottomX = rightBottom.x;
        this.rightBottomY = rightBottom.y;
        this.jzlx = jzlx;
        this.workstate = workstate;
    }

    public double getLeftTopX() {
        return leftTopX;
    }

    public double getLeftTopY() {
        return leftTopY;
    }

    public double getRightBottomX() {
        return rightBottomX;
    }

    public double getRightBottomY() {
        return rightBottomY;
    }

    public int getJzlx() { return jzlx; }

    @Override
    public boolean checkIn(Point point) {
        double pointX = point.x, pointY = point.y;
        if((pointX <= rightBottomX && pointX >= leftTopX) && (pointY <= leftTopY && pointY >= rightBottomY)) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param point 查询的范围
     * @return
     */
    public boolean specialCheckIn(Point point) {
        double pointX = point.x, pointY = point.y;
        if((pointX <= rightBottomX && pointX >= leftTopX) && (pointY <= leftTopY && pointY >= rightBottomY)) {
            if ((point.jzlx == this.jzlx && point.workstate == this.workstate) || (this.jzlx == 0 && point.workstate == this.workstate)
                    || (point.jzlx == this.jzlx && this.workstate == 0)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Rectangle getExternalRectangle() {
        return this;
    }

}
