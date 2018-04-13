package indexingTopology.util.server.query.Interface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by robert on 29/12/16.
 */
public class Route {
    public List<Attraction> attractions = new ArrayList<>();
    public int estimateTimeInMins;
    public int walkingDistanceInMeters;

    public void visitAttraction(Attraction toVisit, int walkingTime, int visitTime, int queuingTime, int distance) {
        attractions.add(toVisit);
        estimateTimeInMins += walkingTime + visitTime + queuingTime;
        walkingDistanceInMeters += distance;
    }

    public String toString() {
        String ret = String.format("time: %d, distance: %d.", estimateTimeInMins, walkingDistanceInMeters);
        for(Attraction attraction: attractions) {
            ret += " --> " + attraction.id;
        }
        return ret;
    }
}
