package edu.ucsd.cse110.ZooSeeker;

import static org.junit.Assert.assertEquals;

import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DirectionActivityTest {
    @Rule
    public ActivityScenarioRule<DirectionActivity> scenarioRule
            = new ActivityScenarioRule<>(DirectionActivity.class);

    @Test
    public void test_modified_input(){
        ActivityScenario<DirectionActivity> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity ->  {
            TextView direction = activity.findViewById(R.id.direction_inf);
            direction.setText("Error 404");
            assertEquals("Error 404", direction.getText().toString());
        });
    }
}
