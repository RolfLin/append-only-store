package indexingTopology.util.server.query.naive;//package indexingTopology.util.server.query.naive;
//
//import server.query.Interface.Attraction;
//import server.query.Interface.Point;
//
//import java.util.*;
//
///**
// * Created by robert on 28/12/16.
// */
//public class FlowGenerator {
//
//    public Map<Integer, Attraction> attractions;
//
//    public Map<Integer, List<Point>> attractionIdToPoints;
//
//    public FlowGenerator(Map<Integer, Attraction> attractions, Map<Integer, List<Point>> attractionIdToPoints) {
//        this.attractions = attractions;
//        this.attractionIdToPoints = attractionIdToPoints;
//    }
//
//    public List<Integer> historyFlow(int id, Calendar currentTime, int nthDayToReview) {
//        currentTime.add(Calendar.DAY_OF_MONTH, - nthDayToReview);
//        int year = currentTime.get(Calendar.YEAR);
//        int month = currentTime.get(Calendar.MONTH);
//        int day = currentTime.get(Calendar.DAY_OF_MONTH);
//        Random random = new Random(id + year + month + day);
//        Attraction attraction = attractions.get(id);
//        int start = attraction.startTimeHour * 60 + attraction.startTimeMin;
//        int end = attraction.endTimeHour * 60 + attraction.endTimeMin;
//
//
//
//        final int peakValue = 1000 + random.nextInt(500);
//        final int lowValue = random.nextInt(100);
//
//        final int peakTimeStart = 13 * 60;
//        final int peakTimeEnd = 15 * 60;
//
//        List<Integer> ret = new ArrayList<>();
//
//        int logicalEnd = nthDayToReview != 0 ? end : currentTime.get(Calendar.HOUR_OF_DAY) * 60 +
//                currentTime.get(Calendar.MINUTE);
//
//        for(int i = start; i <= logicalEnd; i += 30) {
//            Random localRandom = new Random(id + year + month + day + i);
//            int value;
//            if(i < peakTimeStart) {
//                value = lowValue + (int)((double)(i - start) / (peakTimeStart - start) * (peakValue - lowValue));
//                value += localRandom.nextInt(500) - 250;
//            } else if (i > peakTimeEnd) {
//                value = lowValue + (int)((double)(end - i) / (end - peakTimeEnd) * (peakValue - lowValue));
//                value += localRandom.nextInt(500) - 250;
//            } else {
//                value = peakValue + localRandom.nextInt(100) - 50;
//            }
//            ret.add(Math.max(0, value));
//        }
//
//        return ret;
//    }
//
//    public List<Integer> predicteFlow(int id, Calendar currentTime, int nSteps, int step) {
//        int year = currentTime.get(Calendar.YEAR);
//        int month = currentTime.get(Calendar.MONTH);
//        int day = currentTime.get(Calendar.DAY_OF_MONTH);
//        int startHour = currentTime.get(Calendar.HOUR_OF_DAY);
//        int startMin = currentTime.get(Calendar.MINUTE);
//        Random random = new Random(id + year + month + day);
//        Attraction attraction = attractions.get(id);
//        int start = attraction.startTimeHour * 60 + attraction.startTimeMin;
//        int end = attraction.endTimeHour * 60 + attraction.endTimeMin;
//
//        final int peakValue = 1000 + random.nextInt(500);
//        final int lowValue = random.nextInt(100);
//
//        final int peakTimeStart = 13 * 60;
//        final int peakTimeEnd = 15 * 60;
//
//        List<Integer> ret = new ArrayList<>();
//        for(int i = startHour * 60 + startMin + step; i <= startHour * 60 + startMin + nSteps * step; i += step) {
//            Random localRandom = new Random(id + year + month + day + i);
//            int value;
//            if(i < peakTimeStart) {
//                value = lowValue + (int)((double)(i - start) / (peakTimeStart - start) * (peakValue - lowValue));
//                value += localRandom.nextInt(500) - 250;
//            } else if (i > peakTimeEnd) {
//                value = lowValue + (int)((double)(end - i) / (end - peakTimeEnd) * (peakValue - lowValue));
//                value += localRandom.nextInt(500) - 250;
//            } else {
//                value = peakValue + localRandom.nextInt(100) - 50;
//            }
//            ret.add(Math.max(0, value));
//            if (step == 0) {
//                break;
//            }
//        }
//
//        return ret;
//    }
//
//
//    public List<Integer> historyQueuingTime(int id, Calendar currentTime, int nthDayToReview) {
//        currentTime.add(Calendar.DAY_OF_MONTH, - nthDayToReview);
//        int year = currentTime.get(Calendar.YEAR);
//        int month = currentTime.get(Calendar.MONTH);
//        int day = currentTime.get(Calendar.DAY_OF_MONTH);
//        Random random = new Random(id + year + month + day);
//        Attraction attraction = attractions.get(id);
//        int start = attraction.startTimeHour * 60 + attraction.startTimeMin;
//        int end = attraction.endTimeHour * 60 + attraction.endTimeMin;
//
//        final int peakValue = 1000 + random.nextInt(500);
//        final int lowValue = random.nextInt(100);
//
//        final int peakTimeStart = 13 * 60;
//        final int peakTimeEnd = 15 * 60;
//
//        final int maxWaitingTime = 45;
//        final int minWaitingTime = 5;
//
//        List<Integer> ret = new ArrayList<>();
//        int logicalEnd = nthDayToReview != 0 ? end : currentTime.get(Calendar.HOUR_OF_DAY) * 60 +
//                currentTime.get(Calendar.MINUTE);
//        for(int i = start; i <= logicalEnd; i += 30) {
//            Random localRandom = new Random(id + year + month + day + i);
//            int value;
//            if(i < peakTimeStart) {
//                value = lowValue + (int)((double)(i - start) / (peakTimeStart - start) * (peakValue - lowValue));
//                value += localRandom.nextInt(500) - 250;
//            } else if (i > peakTimeEnd) {
//                value = lowValue + (int)((double)(end - i) / (end - peakTimeEnd) * (peakValue - lowValue));
//                value += localRandom.nextInt(500) - 250;
//            } else {
//                value = peakValue + localRandom.nextInt(100) - 50;
//            }
//            int waitingTime = minWaitingTime + (int)((value - lowValue) / (double) (peakValue - lowValue) * (maxWaitingTime - minWaitingTime));
//            waitingTime += localRandom.nextInt(10) - 5;
//            ret.add(Math.max(0, waitingTime));
//        }
//
//        return ret;
//    }
//
//    public List<Integer> predicateQueuingTime(int id, Calendar currentTime, int nSteps, int step) {
//        int year = currentTime.get(Calendar.YEAR);
//        int month = currentTime.get(Calendar.MONTH);
//        int day = currentTime.get(Calendar.DAY_OF_MONTH);
//        int startHour = currentTime.get(Calendar.HOUR_OF_DAY);
//        int startMin = currentTime.get(Calendar.MINUTE);
//        Random random = new Random(id + year + month + day);
//        Attraction attraction = attractions.get(id);
//        int start = attraction.startTimeHour * 60 + attraction.startTimeMin;
//        int end = attraction.endTimeHour * 60 + attraction.endTimeMin;
//
//        final int peakValue = 1000 + random.nextInt(500);
//        final int lowValue = random.nextInt(100);
//
//        final int peakTimeStart = 13 * 60;
//        final int peakTimeEnd = 15 * 60;
//
//        final int maxWaitingTime = 45;
//        final int minWaitingTime = 5;
//
//        List<Integer> ret = new ArrayList<>();
//        for(int i = startHour * 60 + startMin + step; i <=  startHour * 60 + startMin + nSteps * step; i += step) {
//            Random localRandom = new Random(id + year + month + day + i);
//            int value;
//            if(i < peakTimeStart) {
//                value = lowValue + (int)((double)(i - start) / (peakTimeStart - start) * (peakValue - lowValue));
//                value += localRandom.nextInt(500) - 250;
//            } else if (i > peakTimeEnd) {
//                value = lowValue + (int)((double)(end - i) / (end - peakTimeEnd) * (peakValue - lowValue));
//                value += localRandom.nextInt(500) - 250;
//            } else {
//                value = peakValue + localRandom.nextInt(100) - 50;
//            }
//            int waitingTime = minWaitingTime + (int)((value - lowValue) / (double) (peakValue - lowValue) * (maxWaitingTime - minWaitingTime));
//            waitingTime += localRandom.nextInt(10) - 5;
//            ret.add(Math.max(0, waitingTime));
//            if(step == 0)
//                break;
//        }
//        return ret;
//    }
//
//    public List<Integer> predicatePointCounts(int attractionid, Calendar calendar) {
//        int totalCount = predicteFlow(attractionid, calendar, 1, 0).get(0);
//        List<Point> points = attractionIdToPoints.get(attractionid);
//        int sumCount = 0;
//        final int numberOfCounts = points.size();
//        Random random = new Random(attractionid + calendar.get(Calendar.DAY_OF_YEAR) + calendar.get(Calendar.MINUTE) +
//                calendar.get(Calendar.HOUR_OF_DAY));
//        List<Integer> counts = new ArrayList<>();
//        final int fluctuation = totalCount / numberOfCounts / 4;
//        for(int i = 0; i < points.size(); i++) {
//            int count;
//            if (i != points.size() - 1) {
//                count = totalCount / numberOfCounts + random.nextInt(fluctuation) - fluctuation / 2;
//                sumCount += Math.max(0, count);
//            } else {
//                count = totalCount - sumCount;
//            }
//            counts.add(Math.max(0, count));
//        }
//        return counts;
//    }
//
//    static public void main(String[] args) {
//
//        FlowGenerator flowGenerator = new FlowGenerator(NaiveQueryImpl.instance().attractions, NaiveQueryImpl.instance().attractionIdToPoints);
//        System.out.println(flowGenerator.historyFlow(0, NaiveQueryImpl.instance().date, 1));
//        System.out.println(flowGenerator.historyFlow(0, NaiveQueryImpl.instance().date, 2));
//        System.out.println(flowGenerator.predicteFlow(0, NaiveQueryImpl.instance().date, 10, 10));
//        System.out.println(flowGenerator.predicteFlow(0, NaiveQueryImpl.instance().date, 10, 10));
//
//        System.out.println(flowGenerator.historyQueuingTime(0, NaiveQueryImpl.instance().date, 0));
//        System.out.println(flowGenerator.historyQueuingTime(0, NaiveQueryImpl.instance().date, 1));
//        System.out.println(flowGenerator.historyQueuingTime(0, NaiveQueryImpl.instance().date, 2));
//
//        System.out.println(flowGenerator.predicateQueuingTime(0, NaiveQueryImpl.instance().date, 10, 10));
//        NaiveQueryImpl.instance().date.add(Calendar.MINUTE, 10);
//        System.out.println(flowGenerator.predicateQueuingTime(0, NaiveQueryImpl.instance().date, 10, 10));
//
//        System.out.println("Counts:");
//
//        System.out.println(flowGenerator.predicatePointCounts(0, NaiveQueryImpl.instance().date));
//        System.out.println(flowGenerator.predicatePointCounts(1, NaiveQueryImpl.instance().date));
//        System.out.println(flowGenerator.predicatePointCounts(2, NaiveQueryImpl.instance().date));
//    }
//}
