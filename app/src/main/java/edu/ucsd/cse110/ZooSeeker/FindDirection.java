package edu.ucsd.cse110.ZooSeeker;

import static edu.ucsd.cse110.ZooSeeker.DirectionActivity.currLocation;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.IDToNameMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.nameToParentIDMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.vertexInfoMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.graphInfoMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.edgeInfoMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.sortedID;

import android.location.Location;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import java.util.List;

public class FindDirection {

    //print the path decided by mode chose
    public static String printPath(String start, String goal, boolean detailed){
        //Construct result string
        GraphPath<String, IdentifiedWeightedEdge> path =
                DijkstraShortestPath.findPathBetween(graphInfoMap, start, goal);
        String rlt = "You are close to "
                + findNearestLocationName(currLocation) + ". \n";
        // print different info decided by mode chose
        if(detailed) rlt = printDetail(path, rlt);
        else rlt = printBrief(path, rlt);

        return rlt;
    }

    // print brief information
    public static String printBrief(GraphPath<String,
            IdentifiedWeightedEdge> path, String rlt) {
        List<IdentifiedWeightedEdge> edgeList = path.getEdgeList();
        List<String> verList = path.getVertexList();
        double last = 0;
        // use a for loop to construct brief info from edgeList one by one
        for (int x = 0; x < edgeList.size(); x++) {
            double weight = graphInfoMap.getEdgeWeight(edgeList.get(x));
            String street = edgeInfoMap.get(edgeList.get(x).getId()).street;
            if(x < edgeList.size() - 1 &&
                    street.equals(edgeInfoMap.get
                            (edgeList.get(x + 1).getId()).street)){
                last = last + weight;
                continue;
            }
            String s = "\n- Walk " + (int)(weight + last)
                    + " ft along " + street + " to "
                    + IDToNameMap.get(verList.get(x+1)) +  ". \n";
            rlt = rlt.concat(s);
            last = 0 ;
        }
        return rlt;
    }

    public static String printDetail(GraphPath<String,
            IdentifiedWeightedEdge> path, String rlt) {
        List<String> verList = path.getVertexList();
        List<IdentifiedWeightedEdge> edgeList = path.getEdgeList();
        // use a for loop to construct detail info from edgeList one by one
        for (int x = 0; x < edgeList.size(); x++) {
            String s = "\n- Proceed on "
                    + (int)graphInfoMap.getEdgeWeight(edgeList.get(x))
                    + " feet along " +
                    edgeInfoMap.get(edgeList.get(x).getId()).street + " towards "
                    + IDToNameMap.get(verList.get(x+1)) +  ". \n";
            rlt = rlt.concat(s);
        }
        return rlt;
    }

    //print two point's distance
    public static String printDistance(String start, String goal){
        GraphPath<String, IdentifiedWeightedEdge> path =
                DijkstraShortestPath.findPathBetween(graphInfoMap, start, goal);
        //get the total weight between two points
        double weight = 0;
        for (IdentifiedWeightedEdge e : path.getEdgeList()) {
            weight += graphInfoMap.getEdgeWeight(e);
        }

        //construct the result string
        String rlt = IDToNameMap.get(goal) + " Exhibit, " + (int)weight + " feet away.";

        return rlt;
    }

    //function used to find nearest location id of input location
    public static String findNearestLocationID(Location location) {
        ZooData.VertexInfo nearestLocation = null;
        double min = Integer.MAX_VALUE;

        //loop to get the shortest one from all the node
        for( String key : vertexInfoMap.keySet() ){
            ZooData.VertexInfo exhibitInfo = vertexInfoMap.get(key);
            Location endPoint = new Location(exhibitInfo.id);
            endPoint.setLatitude(exhibitInfo.lat);
            endPoint.setLongitude(exhibitInfo.lng);

            //calculate shortest one and record, return it
            double distance = location.distanceTo(endPoint);
            if(distance < min){
                min = distance;
                nearestLocation = exhibitInfo;
            }
        }

        //if location is a group exhibits , return its parent id
        if (nearestLocation.hasGroup())
            return nearestLocation.parent_id;
        return nearestLocation.id;
    }

    //function used to find nearest exhibit name of input location
    public static String findNearestLocationName(Location location) {
        String result = "";
        double min = Integer.MAX_VALUE;

        //loop to get the shortest one from all the node
        for( String key : vertexInfoMap.keySet() ){
            ZooData.VertexInfo exhibitInfo = vertexInfoMap.get(key);
            Location endPoint = new Location(exhibitInfo.id);
            endPoint.setLatitude(exhibitInfo.lat);
            endPoint.setLongitude(exhibitInfo.lng);

            //calculate shortest one and record, return it
            double distance = location.distanceTo(endPoint);
            if(distance < min){
                min = distance;
                result = exhibitInfo.name;
            }
        }

        return result;
    }

    //function used to find nearest exhibit id of input location
    public static String findNearestExhibitID(Location location) {
        String result = "";
        double min = Integer.MAX_VALUE;

        //loop through selected IDs to find the nearest exhibit
        for( String id : sortedID ){
            ZooData.VertexInfo exhibitInfo = vertexInfoMap.get(id);
            Location endPoint = new Location(exhibitInfo.id);
            endPoint.setLatitude(exhibitInfo.lat);
            endPoint.setLongitude(exhibitInfo.lng);

            //calculate shortest one and record, return it
            double distance=location.distanceTo(endPoint);
            if(distance < min){
                min = distance;
                System.out.println(result);
                result = exhibitInfo.name;
            }
        }
        //Convert exhibit name to id
        result = nameToParentIDMap.get(result);
        return result;
    }
}