package edu.ucsd.cse110.ZooSeeker;

import static edu.ucsd.cse110.ZooSeeker.FindDirection.*;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.*;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DirectionActivity extends AppCompatActivity {
    public static Location currLocation;

    //exhibits to be visit
    private boolean isResume;
    private String gate;

    //current nearest exhibit's ID
    private String currentLocationID;
    //current focus exhibit in plan
    private String focus;
    //index for finding current focus exhibit
    private int focusIndex;
    //List of remaining plan to be visited
    List<String> remainingPlan;
    //List of exhibits in plan that have been visited
    List<String> visitedExhibits;

    private TextView directionText;
    private TextView distanceText;
    private String nextExhibitDistance;
    private String directionToNextExhibit;

    private boolean detailed;
    private float latitude;
    private float longitude;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        //find gate id
        gate = ZooData.findGate(vertexInfoMap);

        //initialize currLocation
        {
            currLocation = new Location("entrance_exit_gate");
            currLocation.setLatitude(32.73561);
            currLocation.setLongitude(-117.14936);
        }

        detailed = true;

        //retain progress
        {
            Bundle extras = getIntent().getExtras();
            if (extras != null)
                isResume = extras.getBoolean("isResume");
            else
                isResume = false;
        }

        //when the sortedID is created
        if(sortedID != null) {

            currentLocationID = gate;   //set current location to gate
            focusIndex = 0;
            focus = sortedID.get(focusIndex);   //set focus to first exhibit in sortedID

            if (isResume) {
                //load latitude, longitude, focusIndex, focus
                load();
            }

            //store path info from cur to nxt into firstDirection
            String firstDirection = FindDirection.printPath(currentLocationID, focus, detailed);

            directionText = findViewById(R.id.direction_inf);
            directionText.setText(firstDirection);

            distanceText = findViewById(R.id.distance_inf);
            nextExhibitDistance = FindDirection.printDistance(currentLocationID, focus);
            distanceText.setText(nextExhibitDistance);

            //get a deep copy of sortedID - Non of the exhibits in plan has been visited
            remainingPlan = sortedID.stream()
                    .collect(Collectors.toList());

            //add exit gate to the sorted plan
            sortedID.add(ZooData.findGate(vertexInfoMap));

            //Non of the exhibits in plan has been visited
            visitedExhibits = new ArrayList<>();

            //make direction scrollable
            directionText.setMovementMethod(new ScrollingMovementMethod());
        }

    }

    public void NextClicked(View view) {

        if(focusIndex < sortedID.size() - 1) {
            focusIndex++;

            //update instructions to the next exhibit (distance, directions) on UI
            updateDirectionInfo();
        }
        else {

            resumeBtn.setVisibility(View.INVISIBLE);
            finish();

        }

    }

    public void stepBackClicked(View view) {

        if(focusIndex > 0) {
            focusIndex--;

            //update instructions to the next exhibit (distance, directions) on UI
            updateDirectionInfo();
        }

    }

    //Adapted from DylanLukes' example code
    public void mockClicked(View view) {

        var inputType = EditorInfo.TYPE_CLASS_NUMBER
                | EditorInfo.TYPE_NUMBER_FLAG_SIGNED
                | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL;

        final EditText latInput = new EditText(this);
        latInput.setInputType(inputType);
        latInput.setHint("Latitude");
        latInput.setText("32.");

        final EditText lngInput = new EditText(this);
        lngInput.setInputType(inputType);
        lngInput.setHint("Longitude");
        lngInput.setText("-117.");

        final LinearLayout layout = new LinearLayout(this);
        layout.setDividerPadding(8);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(latInput);
        layout.addView(lngInput);

        mockLocation(latInput, lngInput, layout);

    }

    private boolean onLaterSelectedExhibit(String focus, String currentLocation) {

        //conver currentLocation to nearestExhibitInPlan
        String currentVisitingExhibit = nearestExhibitInPlan(currentLocation, sortedID);

        //filter out current visiting exhibit
        List<String> laterExhibits = remainingPlan
                .stream()
                .filter(exhibit -> !(exhibit.equals(currentVisitingExhibit)) )
                .collect(Collectors.toList());

        //check if focus is on on a later selected exhibit in plan (excluding currentLocation)
        return laterExhibits.contains(focus);

    }

    private void mockLocation(EditText latInput, EditText lngInput, LinearLayout layout) {

        //Alert Builder - the pop up window
        var mockWindowBuilder = new AlertDialog.Builder(this)
                .setTitle("Inject a Mock Location")
                .setView(layout)
                .setPositiveButton("Submit", (dialog, which) -> {
                    var lat = Double.parseDouble(latInput.getText().toString());
                    var lng = Double.parseDouble(lngInput.getText().toString());
                    updateCurrentLocation(lat, lng);

                    //update currentLocationID (current exhibit ID) according to currLocation (lat, lng)
                    currentLocationID = findNearestLocationID(currLocation);

                    //save current information for future resume
                    save();

                    //declare and initialize currentLayout for prompt messages
                    final LinearLayout currentLayout = new LinearLayout(this);
                    currentLayout.setDividerPadding(8);
                    currentLayout.setOrientation(LinearLayout.VERTICAL);

                    //detect if current user location is on focus location
                    //trigger: plan unchanged; prompt user you are already on the site
                    if (focus.equals(currentLocationID)) {
                        onsitePrompt(currentLayout);
                    }

                    //detect current User location is on a later planned exhibit
                    //trigger: prompt "it seems like you are on a planned exhibit already, replan?"
                    if (onLaterSelectedExhibit(focus, currentLocationID)) {
                        replanPrompt(currentLayout);
                    }

                    /*
                    Section for update displayed directional message
                     */
                    updateDirectionInfo();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.cancel();
                });
        mockWindowBuilder.show();

    }

    private String nearestExhibitInPlan(String currentLocationID, List<String> plan) {

        String cloestExhibitInPlan = "";
        double minDistance = Double.MAX_VALUE;
        GraphPath<String, IdentifiedWeightedEdge> path;

        for(String exhibitID : plan) {
            //find path of closetExhibitID to currentLocationID and check if the distance is less than minDistance
            //if yes, update minDistance

            path = DijkstraShortestPath.findPathBetween(graphInfoMap, currentLocationID, exhibitID);

            if(path.getWeight() < minDistance) {
                minDistance = path.getWeight();
                cloestExhibitInPlan = exhibitID;
            }
        }

        return cloestExhibitInPlan;

    }

    private int reorientFocusIndex(String currentLocationID, List<String> sortedPlan) {

        //if the currentLocationID is found at index X in sortedPlan, return index
        for (int i = 0; i < sortedPlan.size(); i++) {
            if (sortedPlan.get(i).equals(currentLocationID)) {
                return i;
            }
        }

        return -1;

    }

    //replan can only work for later selected exhibit in current plan
    private List<String> adjustedPlan() {

        List<String> newSortedPlan = new ArrayList<String>();

        //find where the user is near to
        String currentExhibit = nearestExhibitInPlan(currentLocationID, sortedID);
        //update focusIndex
        focusIndex = reorientFocusIndex(currentExhibit, sortedID);
        //piazza @831
        visitedExhibits = new ArrayList<>();
        for (int i = 0; i < focusIndex; i++) {
            visitedExhibits.add(sortedID.get(i));
        }

        newSortedPlan.addAll(visitedExhibits);

        //remainingPlan = sortedID - visitedExhibits
        for (String visited : visitedExhibits) {
            remainingPlan.remove(visited);
        }

        //add remaining plan to newSortedPlan
        RoutePlanner newRoute = new RoutePlanner(remainingPlan, true);
        List<String> replannedRemainingPlan = newRoute.getRoute();
        newSortedPlan.addAll(replannedRemainingPlan);
        //catch back the remainingPlan from newRoute
        remainingPlan.addAll(replannedRemainingPlan);

        //update focus index and focus
        //focus should be the first destination of replannedRemainingPlan
        focus = replannedRemainingPlan.get(0);
        focusIndex = reorientFocusIndex(focus, newSortedPlan);

        //add exit gate to the sorted plan
        newSortedPlan.add(ZooData.findGate(vertexInfoMap));

        return newSortedPlan;

    }

    private List<String> adjustedSkipPlan(int currFocusIndex) {

        List<String> newSortedPlan = new ArrayList<String>();

        visitedExhibits = new ArrayList<>();
        for (int i = 0; i < currFocusIndex; i++) {
            visitedExhibits.add(sortedID.get(i));
        }

        newSortedPlan.addAll(visitedExhibits);

        //remainingPlan = sortedID - visitedExhibits
        for (String visited : visitedExhibits) {
            remainingPlan.remove(visited);
        }

        //replan the sortedID
        String gateID = ZooData.findGate(vertexInfoMap);
        sortedID.remove(gateID);
        RoutePlanner newRoute = new RoutePlanner(remainingPlan, true);
        newSortedPlan.addAll(newRoute.getRoute());

        //find out which location is user closest to
        currentLocationID = findNearestLocationID(currLocation);

        //first destination (focus) becomes the current destination (closest exhibit location in plan)
        String currentFocusLocation = nearestExhibitInPlan(currentLocationID, newSortedPlan);
        focusIndex = reorientFocusIndex(currentFocusLocation, newSortedPlan);

        //add gateID back to the new sorted plan
        newSortedPlan.add(gateID);

        return newSortedPlan;

    }

    private void onsitePrompt(LinearLayout linearLayout) {

        var onsitePromptBuilder = new AlertDialog.Builder(this)
                .setTitle("Onsite!")
                .setView(linearLayout)
                .setMessage("You are at the exhibit that you have selected to visit, " +
                        "click NEXT button to move on to the exhibit in plan!")
                .setPositiveButton("Okay!", (dialog, which) -> {

                });
        onsitePromptBuilder.show();

    }

    private void replanPrompt(LinearLayout linearLayout) {

        var replanPromptBuilder = new AlertDialog.Builder(this)
                .setTitle("Replan?")
                .setView(linearLayout)
                .setMessage("It seems like you are currently near " +
                        findNearestLocationName(currLocation) +
                        ", which is closer to a later selected exhibit on the original plan" +
                        ", do you want a replan?")
                .setPositiveButton("Replan", (dialog, which) -> {
                    sortedID = adjustedPlan();
                    updateDirectionInfo();
                }).setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.cancel();
                });
        replanPromptBuilder.show();

    }

    public void updateCurrentLocation(double lat, double lng) {

        //set location's lat and lng by params
        currLocation.setLatitude(lat);
        currLocation.setLongitude(lng);

        //LOG TEST IT should print out
        // the lat and lng if it is successful
        System.out.println("You have set a new location");
        System.out.println(currLocation.getLatitude());
        System.out.println(currLocation.getLongitude());

    }

    public void load() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);//this.getPreferences(MODE_PRIVATE);

        latitude = preferences.getFloat("latitude", 32.73561f);
        longitude = preferences.getFloat("longitude",-117.14936f);
        focusIndex = preferences.getInt("focusIndex", 0);
        focus = sortedID.get(focusIndex);

        //load currLocation from saved latitude and longitude
        currLocation.setLatitude(latitude);
        currLocation.setLongitude(longitude);

        currentLocationID = findNearestLocationID(currLocation);

    }

    public void save() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putFloat("latitude", (float) currLocation.getLatitude());
        editor.putFloat("longitude", (float) currLocation.getLongitude());
        editor.putInt("focusIndex", focusIndex);

        editor.apply();

    }

    private void updateDirectionInfo() {

        focus = sortedID.get(focusIndex);
        directionToNextExhibit = FindDirection.printPath(currentLocationID, focus, detailed);
        directionText.setText(directionToNextExhibit);
        nextExhibitDistance = FindDirection.printDistance(currentLocationID, focus);
        distanceText.setText(nextExhibitDistance);

        save();

    }

    public void styleClicked(View view) {

        this.detailed = !this.detailed;
        updateDirectionInfo();

    }

    public void skipClicked(View view) {

        String skippedExhibit = "";

        //edge case: skip the first in plan
        if (focusIndex == 0) {
            skippedExhibit = sortedID.get(focusIndex);
            sortedID.remove(skippedExhibit);
            remainingPlan.remove(skippedExhibit);

            //edge case: only one exhibit in plan, and user skipped it
            if (sortedID.size() == 0) {
                finish();
                return;
            }

            updateDirectionInfo();

            return;
        }

        focusIndex--;
        skippedExhibit = sortedID.get(focusIndex + 1);
        remainingPlan.remove(skippedExhibit);
        sortedID.remove(skippedExhibit);
        sortedID = adjustedSkipPlan(focusIndex + 1);
        updateDirectionInfo();

    }
}
