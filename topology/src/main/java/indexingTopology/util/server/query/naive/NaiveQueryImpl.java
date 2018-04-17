package indexingTopology.util.server.query.naive;//package indexingTopology.util.server.query.naive;
//
//import indexingTopology.util.server.query.Interface.Attraction;
//import indexingTopology.util.server.query.Interface.IQuery;
//import indexingTopology.util.server.Utils.Utils;
//import indexingTopology.util.server.config.Config;
//import indexingTopology.util.server.query.Interface.Point;
//import indexingTopology.util.server.query.Interface.Shop;
//
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
///**
// * A naive implementation of the IQuery interface
// */
//public class NaiveQueryImpl implements IQuery {
//
//    Map<Integer, Attraction> attractions = new HashMap<>();
//
//    Map<Integer, Shop> shops = new HashMap<>();
//
//    Map<Integer, List<Point>> attractionIdToPoints = new HashMap<>();
//
//    final private static NaiveQueryImpl instance = new NaiveQueryImpl();
//
//    private FlowGenerator flowGenerator;
//
//    Calendar date = Calendar.getInstance();
//
//    private String warningMessage = "Please take care of your belongings. If you see any suspicious person or " +
//            "article, please call 999.";
//
//    private String adminMessage = "There will be an intensely increased flow between 14:00 and 15:00.";
//
//    public static NaiveQueryImpl instance() {
//            return instance;
//    }
//
//    private NaiveQueryImpl() {
//        loadData();
//        flowGenerator = new FlowGenerator(attractions, attractionIdToPoints);
//    }
//
//    private void addPointToAttraction(Point point) {
//        if(!attractionIdToPoints.containsKey(point.attractionId)) {
//            attractionIdToPoints.put(point.attractionId, new ArrayList<Point>());
//        }
//        attractionIdToPoints.get(point.attractionId).add(point);
//    }
//
//    /**
//     * Currently, the data is initialized by hard code.
//     */
//    private void loadData() {
//        attractions.put(0, new Attraction(0, 1.254028, 103.823806, "Universal Studio", 9, 30, 18, 0, 4.9,
//                "Universal Studio Singapore is a theme park located within Resorts World Sentosa on Sentosa Island, " +
//                        "Singapore.",
//                "sentosa-demo-image/universalStudio.jpg"));
//        attractions.put(1, new Attraction(1, 1.258549, 103.819314, "Adventure Cove Waterpark", 10, 0, 20, 0, 4.8,
//                "At Adventure Cove Waterpark, you can take high-speed water slides, laze the day away drifting on a " +
//                        "lazy river, snorkel with 20,000 tropical fish over a colourful reef, wade among rays and " +
//                        "even come face to face with sharks!", "sentosa-demo-image/waterPark.jpg"));
//        attractions.put(2, new Attraction(2, 1.253336, 103.818853, "Sentosa Merlion", 8, 0, 22, 0, 4.7,
//                "The Merlion is the national personification of Singapore. Its name combines \"mer\", meaning the " +
//                        "sea, and \"lion\". The fish body represents Singapore's origin as a fishing village when it " +
//                        "was called Temasek, which means \"sea town\" in Javanese.",
//                "sentosa-demo-image/sentosaMerlion.jpg"));
//        DateFormat format = new SimpleDateFormat("yyyy-MM-dd,HH:mm");
//        try {
//            date.setTime(format.parse("2016-12-30,14:00"));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        shops.put(0, new Shop(0, 1.257554, 103.822677, 9.5, "Seafood Republic", "sentosa-demo-image/seafood_republic.jpg"));
//        shops.put(1, new Shop(1, 1.256118, 103.822013, 9.8, "Universal Studio Store", "sentosa-demo-image/universal_studio_store.jpg"));
//        shops.put(2, new Shop(2, 1.254835, 103.821406, 9.6, "Big Bird's Emporium", "sentosa-demo-image/big_bird.jpg"));
//        shops.put(3, new Shop(3, 1.256670, 103.820387, 9.9, "Candylicious", "sentosa-demo-image/candylicious.jpg"));
//
//        addPointToAttraction(new Point(0, 1.253823, 103.822153, "Battlestar Galactica"));
//        addPointToAttraction(new Point(0, 1.253195, 103.825276, "Swellview Fairground"));
//        addPointToAttraction(new Point(0, 1.255713, 103.823083, "Madagascar Ride"));
//
//        addPointToAttraction(new Point(1, 1.258609, 103.819423, "Adventure Cove Waterpark"));
//
//        addPointToAttraction(new Point(2, 1.253336, 103.818853, "Sentosa Merlion"));
//        addPointToAttraction(new Point(2, 1.251313, 103.817089, "Wings Of Time"));
//        addPointToAttraction(new Point(2, 1.255364, 103.812566, "Siloso Beach"));
//
//
//    }
//
//    @Override
//    public Collection<Attraction> queryAllAttractions() {
//        return attractions.values();
//    }
//
//    @Override
//    public Collection<Attraction> queryNearByAttractions(final double x, final double y, int max) {
//        List<Attraction> ret = new ArrayList<>();
//        ret.addAll(attractions.values());
////        Collections.sort(ret, new Comparator<Attraction>() {
////            @Override
////            public int compare(Attraction o1, Attraction o2) {
////                double d1 = Math.sqrt((x - o1.x) * (x - o1.x) + (y - o1.y) * (y - o1.y));
////                double d2 = Math.sqrt((x - o2.x) * (x - o2.x) + (y - o2.y) * (y - o2.y));
////                return Double.compare(d1, d2);
////            }
////        });
//        sortAttractionBasedOnDistance(ret, x, y);
//        return ret;
//    }
//
//    private void sortAttractionBasedOnDistance(List<Attraction> attractions, final double x, final double y) {
//        Collections.sort(attractions, new Comparator<Attraction>() {
//            @Override
//            public int compare(Attraction o1, Attraction o2) {
//                double d1 = Math.sqrt((x - o1.x) * (x - o1.x) + (y - o1.y) * (y - o1.y));
//                double d2 = Math.sqrt((x - o2.x) * (x - o2.x) + (y - o2.y) * (y - o2.y));
//                return Double.compare(d1, d2);
//            }
//        });
//    }
//
//    @Override
//    public Attraction getAttractionInfo(int id) {
//        return attractions.get(id);
//    }
//
//    @Override
//    public List<Integer> predicateFlow(int id) {
//        return flowGenerator.predicteFlow(id, date, Config
//                        .NumberOfPredicates, Config.PredicateStepInMins);
//    }
//
//    @Override
//    public List<Integer> predicateQueuingTime(int id) {
//        return flowGenerator.predicateQueuingTime(id, date, Config
//                        .NumberOfPredicates, Config.PredicateStepInMins);
//    }
//
//    @Override
//    public List<Integer> retrieveFlowHistory(int id, int nthDayToReview) {
//        Calendar historicalDay = Calendar.getInstance();
//        historicalDay.setTime(date.getTime());
//        return flowGenerator.historyFlow(id, historicalDay, nthDayToReview);
//    }
//
//    @Override
//    public List<Integer> retrieveQueuingTimeHistory(int id, int nthDayToReview) {
//        Calendar historicalDay = Calendar.getInstance();
//        historicalDay.setTime(date.getTime());
//        return flowGenerator.historyQueuingTime(id, historicalDay, nthDayToReview);
//    }
//
//    @Override
//    public Calendar getCurrentTime() {
//        return date;
//    }
//
//    @Override
//    public Collection<Shop> getRecommendShops(final double x, final double y, int max) {
//        List<Shop> allShops = new ArrayList<>();
//
//        // sort the shops based on the distance in increasing order.
//        allShops.addAll(shops.values());
//        Collections.sort(allShops, new Comparator<Shop>() {
//            @Override
//            public int compare(Shop o1, Shop o2) {
//                double d1 = Math.sqrt((x - o1.x) * (x - o1.x) + (y - o1.y) * (y - o1.y));
//                double d2 = Math.sqrt((x - o2.x) * (x - o2.x) + (y - o2.y) * (y - o2.y));
//                return Double.compare(d1, d2);
//            }
//        });
//
//        List<Shop> nearbyShops = allShops.subList(0, Math.min(max, allShops.size()));
//
//        Collections.sort(nearbyShops, new Comparator<Shop>() {
//            @Override
//            public int compare(Shop o1, Shop o2) {
//                return Double.compare(o2.rating, o1.rating);
//            }
//        });
//
//        return nearbyShops;
//    }
//
//    @Override
//    public List<Route> getRecommendRoutes(double x, double y) {
//        List<Route> routes = new ArrayList<>();
//        routes.add(getRouteWithMinimizedWalkingDistance(x, y));
//        routes.add(getRouteWithMinimizedTime(x, y));
////        Collections.sort(routes, new Comparator<Route>() {
////            @Override
////            public int compare(Route o1, Route o2) {
////                return Integer.compare(o1.estimateTimeInMins, o2.estimateTimeInMins);
////            }
////        });
//        return routes;
//    }
//
//    @Override
//    public List<Point> getPointInAnAttraction(int id) {
//        return attractionIdToPoints.get(id);
//    }
//
//    @Override
//    public List<Integer> predicatePointCount(int id) {
//        return flowGenerator.predicatePointCounts(id, date);
//    }
//
//    @Override
//    public int getAttractionInCount(int id) {
//        return flowGenerator.predicteFlow(id, date, 1, 0).get(0);
//    }
//
//    @Override
//    public int getAttractionOutCount(int id) {
//        Random random = new Random(id + date.get(Calendar.DAY_OF_YEAR) + date.get(Calendar.HOUR_OF_DAY) + date.get(Calendar.MINUTE));
//        int flow = flowGenerator.predicteFlow(id, date, 1, 0).get(0);
//        flow = flow / 5 + random.nextInt(flow / 10);
//        return flow;
//    }
//
//    @Override
//    public int getAttractionEnterCount(int id) {
//        Random random = new Random(id + date.get(Calendar.DAY_OF_YEAR) + date.get(Calendar.HOUR_OF_DAY) + date.get(Calendar.MINUTE));
//        int flow = flowGenerator.predicteFlow(id, date, 1, 0).get(0);
//        random.nextInt();
//        flow = flow / 5 + random.nextInt(flow / 10);
//        return flow;
//    }
//
//    @Override
//    public int getAttractionEnterRate(int id) {
//        Random random = new Random(id + date.get(Calendar.DAY_OF_YEAR) + date.get(Calendar.HOUR_OF_DAY) + date.get(Calendar.MINUTE));
//        int flow = flowGenerator.predicteFlow(id, date, 1, 0).get(0);
//        random.nextInt();
//        flow = flow / 5 + random.nextInt(flow / 10);
//        return flow;
//    }
//
//    @Override
//    public List<AttractionAndScore> getHotAttractionsRanking() {
//        List<Attraction> attractions = new ArrayList<>();
//        attractions.addAll(queryAllAttractions());
//        List<AttractionAndScore> ret = new ArrayList<>();
//        int max = 0;
//        for (Attraction attraction: attractions) {
//            final int flow = flowGenerator.predicteFlow(attraction.id, date, 1, 0).get(0);
//            ret.add(new AttractionAndScore(attraction, flow));
//            max = Math.max(max, flow);
//        }
//
//        double normalizationFactor = 100.0 / max;
//
//        //normalization
//
//        for (AttractionAndScore attractionAndScore: ret) {
//            attractionAndScore.score *= normalizationFactor;
//        }
//
//
//        Collections.sort(ret, new Comparator<AttractionAndScore>() {
//            @Override
//            public int compare(AttractionAndScore o1, AttractionAndScore o2) {
//                return Integer.compare(o2.score, o1.score);
//            }
//        });
//        return ret;
//    }
//
//    @Override
//    public void setWarningMessage(String message) {
//        warningMessage = message;
//    }
//
//    @Override
//    public String getWarningMessage() {
//        return warningMessage;
//    }
//
//    @Override
//    public void setAdminMessage(String message) {
//        adminMessage = message;
//    }
//
//    @Override
//    public String getAdminMessage() {
//        return adminMessage;
//    }
//
//    private Route getRouteWithMinimizedWalkingDistance(double x, double y) {
//        Route route = new Route();
//        List<Attraction> unvisited = new ArrayList<>();
//        unvisited.addAll(attractions.values());
//        int futureTimeInMin = 0;
//        while(!unvisited.isEmpty()) {
//            sortAttractionBasedOnDistance(unvisited, x, y);
//            Attraction toVisit = unvisited.get(0);
//            final int walkingDistance = getDistance(x, y, toVisit.x, toVisit.y);
//            final int walkingTime = walkingDistance / Utils.walkingMeterPerMin;
//            final int queuingTime = predicateQueueTime(toVisit.id, futureTimeInMin + walkingTime);
//            final int visitTime = Config.VisitTimeInSecs;
//            route.visitAttraction(toVisit, walkingTime, visitTime, queuingTime, walkingDistance);
//            unvisited.remove(0);
//            futureTimeInMin += walkingTime + queuingTime + visitTime;
//        }
//        return route;
//    }
//
//    private Route getRouteWithMinimizedTime(double x, double y) {
//        Route route = new Route();
//        List<Attraction> unvisited = new ArrayList<>();
//        unvisited.addAll(attractions.values());
//        int futureTimeInMin = 0;
//        while (!unvisited.isEmpty()) {
//            Attraction bestToVisit = null;
//            int bestTimeBudget = Integer.MAX_VALUE;
//            int bestWalkingTime = 0;
//            int bestQueueTime = 0;
//            int bestWalkingDistance = 0;
//            for (Attraction toVisit: unvisited) {
//                final int walkingDistance = getDistance(x, y, toVisit.x, toVisit.y);
//                final int walkingTime = walkingDistance / Utils.walkingMeterPerMin;
//                final int queueTime = predicateQueueTime(toVisit.id, futureTimeInMin + walkingTime);
//                final int visitTime = Config.VisitTimeInSecs;
//                final int timeBudget = walkingTime + queueTime + visitTime;
//                if (timeBudget <  bestTimeBudget) {
//                    bestToVisit = toVisit;
//                    bestTimeBudget = timeBudget;
//                    bestQueueTime = queueTime;
//                    bestWalkingTime = walkingTime;
//                    bestWalkingDistance = walkingDistance;
//                }
//            }
//            unvisited.remove(bestToVisit);
//            futureTimeInMin += bestTimeBudget;
//            route.visitAttraction(bestToVisit, bestWalkingTime, Config.VisitTimeInSecs, bestQueueTime, bestWalkingDistance);
//        }
//        return route;
//    }
//
//    private int predicateQueueTime(int id, int futureMin) {
//        final int queueTime = flowGenerator.predicateQueuingTime(id, date, 1, futureMin).get(0);
//        return queueTime;
//    }
//
//    private int getDistance(double x1, double y1, double x2, double y2) {
//        return (int)(Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)) * Utils.coordinatorToMeterFactor);
//    }
//
//    static public void main(String[] args) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        System.out.println(dateFormat.format(NaiveQueryImpl.instance().date.getTime()));
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(NaiveQueryImpl.instance().date.getTime());
//        calendar.add(Calendar.DAY_OF_MONTH, - 100);
//        System.out.println(dateFormat.format(calendar.getTime()));
//        System.out.println(dateFormat.format(NaiveQueryImpl.instance().date.getTime()));
//
//        System.out.println(NaiveQueryImpl.instance.getRecommendShops(0, 0, 10));
//
//        System.out.println(NaiveQueryImpl.instance.getRecommendRoutes(1.258609, 103.819424));
//
//        System.out.println(String.format("%d %d %d %d",
//                NaiveQueryImpl.instance.getAttractionEnterCount(0),
//                NaiveQueryImpl.instance.getAttractionInCount(0),
//                NaiveQueryImpl.instance.getAttractionOutCount(0),
//                NaiveQueryImpl.instance.getAttractionEnterRate(0))
//                );
//
//        System.out.println(String.format("%d %d %d %d",
//                NaiveQueryImpl.instance.getAttractionEnterCount(1),
//                NaiveQueryImpl.instance.getAttractionInCount(1),
//                NaiveQueryImpl.instance.getAttractionOutCount(1),
//                NaiveQueryImpl.instance.getAttractionEnterRate(1))
//        );
//
//        System.out.println(String.format("%d %d %d %d",
//                NaiveQueryImpl.instance.getAttractionEnterCount(2),
//                NaiveQueryImpl.instance.getAttractionInCount(2),
//                NaiveQueryImpl.instance.getAttractionOutCount(2),
//                NaiveQueryImpl.instance.getAttractionEnterRate(2))
//        );
//    }
//}
