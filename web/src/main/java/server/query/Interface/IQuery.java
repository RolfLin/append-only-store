package server.query.Interface;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;

/**
 * Created by robert on 28/12/16.
 */
public interface IQuery {
    Collection<Attraction> queryAllAttractions();
    Collection<Attraction> queryNearByAttractions(double x, double y, int max);
    Attraction getAttractionInfo(int id);

    /**
     * @param id attraction id
     * @return a list of predicated flows, where the first one is the current flow and the subsequent ones are the
     * predicated ones.
     */
    List<Integer> predicateFlow(int id);

    List<Integer> predicateQueuingTime(int id);

    List<Integer> retrieveFlowHistory(int id, int nthDayToReview);

    List<Integer> retrieveQueuingTimeHistory(int id, int nthDayToReview);

    Calendar getCurrentTime();

    Collection<Shop> getRecommendShops(final double x, final double y, int max);

    List<Route> getRecommendRoutes(double x, double y);

    List<Point> getPointInAnAttraction(int id);

    List<Integer> predicatePointCount(int id);

    int getAttractionInCount(int id);

    int getAttractionOutCount(int id);

    int getAttractionEnterCount(int id);

    int getAttractionEnterRate(int id);

    List<AttractionAndScore> getHotAttractionsRanking();

    void setWarningMessage(String message);

    String getWarningMessage();

    void setAdminMessage(String message);

    String getAdminMessage();

    public static class AttractionAndScore {
        public Attraction attraction;
        public int score;
        public AttractionAndScore(Attraction attraction, int score) {
            this.score = score;
            this.attraction = attraction;
        }
    }
}
