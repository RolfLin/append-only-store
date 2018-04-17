package indexingTopology.util.track;

public class LatLngUtils {
	
	/**
	 *计算真实距离在经纬度坐标系上的距离
	 * 
	 * @param radius 距离，单位米
	 * @return 距离在经纬度坐标系上的对应的距离 
	 */
	public static double getRadiusLatLng(double radius){
		
		double degree = (24901*1609) / 360.0;
		
		double dpmLat = 1 / degree;
		
		return radius * dpmLat * 1000;
	}
	
	/**
	 * 测试某点是否在圆内
	 * @param centerLatitude 圆心纬度
	 * @param centerLongitude 圆心经度
	 * @param testLatitude 测试点纬度
	 * @param testLongitude 测试点经度
	 * @param radiusLatLng 坐标系半径距离
	 * @return
	 */
	public static boolean isInCircle(double centerLatitude, double centerLongitude, double testLatitude, double testLongitude,double radiusLatLng){
		
		double distanceLat = Math.sqrt(Math.pow((testLatitude - centerLatitude), 2) + Math.pow(testLongitude-centerLongitude, 2));
		
		return distanceLat <= radiusLatLng;
	}
	
	
}
