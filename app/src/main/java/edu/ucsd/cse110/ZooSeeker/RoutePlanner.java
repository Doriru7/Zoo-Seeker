package edu.ucsd.cse110.ZooSeeker;

import static edu.ucsd.cse110.ZooSeeker.DirectionActivity.currLocation;
import static edu.ucsd.cse110.ZooSeeker.FindDirection.*;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.graphInfoMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.vertexInfoMap;

import android.util.Pair;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoutePlanner {
    private List<String> route;
    private List<String> distance;
    private String start;

    public static List<String> sortedExhibitID;

    public DijkstraShortestPath path;

    public RoutePlanner(List<String> plan, Boolean rePlan) {
        route = new ArrayList<>();
        distance = new ArrayList<>();
        sortedExhibitID = new ArrayList<>();

        if(!rePlan) {
            this.start = findGate(vertexInfoMap);
        }
        else {
            this.start = findNearestLocationID(currLocation);
        }

        this.path = new DijkstraShortestPath(graphInfoMap);
        buildRoute(plan, start);
    }

    public static String findGate(Map<String, ZooData.VertexInfo> vertexInfoMap){
        ZooData.VertexInfo exhibitInfo;
        for (String key : vertexInfoMap.keySet()) {
            exhibitInfo = vertexInfoMap.get(key);
            //if this vertex is an exhibit, add it to animalExhibitList
            if (exhibitInfo.kind == ZooData.VertexInfo.Kind.GATE) {
                return exhibitInfo.id;
            }
        }
        return "";
    }

    public void buildRoute(List<String> userPlan, String current) {

        double lastDis = 0;
        while(userPlan.size() != 0){
            double min = Double.MAX_VALUE;
            String shortest = "";
            for(String id : userPlan){
                if(min > path.getPathWeight(current,id)){
                    shortest = id;
                    min = path.getPathWeight(current,id);
                }
            }
            route.add(shortest);

            distance.add(Double.toString(min + lastDis) + " feet");
            lastDis = min + lastDis;
            userPlan.remove(shortest);
            current = shortest;
        }

    }

    public List<String> getRoute() {
        return this.route;
    }

    public List<String> getDistance(){
        return this.distance;
    }

    public void buildRouteButReturnExhibitID(List<Pair<String,String>> parentExhibitIDPair) {
        String current = findGate(vertexInfoMap);   //set start point to gate
        while(parentExhibitIDPair.size() != 0){  //find closest exhibit
            double min = Double.MAX_VALUE;
            String shortest = "";
            Pair<String, String> pairOfShortest = null;
            for(Pair<String, String> pair : parentExhibitIDPair){
                if(min > path.getPathWeight(current,pair.first)){
                    shortest = pair.second;
                    pairOfShortest = pair;
                    min = path.getPathWeight(current,pair.first);
                }
            }
            sortedExhibitID.add(shortest);  //add the closest exhibit to the returning list

            parentExhibitIDPair.remove(pairOfShortest); //remove closest exhibit from input list
            current = pairOfShortest.first;
        }
    }
}