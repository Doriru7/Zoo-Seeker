package edu.ucsd.cse110.ZooSeeker;

import static org.junit.Assert.assertEquals;

import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.vertexInfoMap;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



@RunWith(AndroidJUnit4.class)
public class RoutePlannerTest {
    @Rule
    public ActivityScenarioRule<SearchListActivity> scenarioRule
            = new ActivityScenarioRule<>(SearchListActivity.class);


    @Test
    public void test_sort_plan(){
        ActivityScenario<SearchListActivity> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            List<String> selectedExhibitIDs = new ArrayList<>();
            selectedExhibitIDs.add("capuchin");
            selectedExhibitIDs.add("koi");
            selectedExhibitIDs.add("flamingo");

            List<String> sortedExhibitIDs = new ArrayList<>();

            sortedExhibitIDs.add("koi");
            sortedExhibitIDs.add("flamingo");
            sortedExhibitIDs.add("capuchin");

            RoutePlanner planner = new RoutePlanner(selectedExhibitIDs, false);
            List<String> sortedID = planner.getRoute();

            assertEquals(sortedID, sortedExhibitIDs);
        });
    }
}
