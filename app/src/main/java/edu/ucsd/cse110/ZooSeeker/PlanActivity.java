package edu.ucsd.cse110.ZooSeeker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static edu.ucsd.cse110.ZooSeeker.RoutePlanner.sortedExhibitID;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.IDToNameMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.distance;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.selectedExhibitList;

public class PlanActivity extends AppCompatActivity {

    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> future;

    public RecyclerView recyclerView;
    private List<String> exhibitNames;
    private PlanListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //set UI
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        //initial adapter
        initialAdapterForUI();

        //recyclerView.isNestedScrollingEnabled = false
    }

    //initial the adapter for the UI
    private void initialAdapterForUI() {
        //Create adaoter
        adapter = new PlanListAdapter();
        adapter.setHasStableIds(true);
        recyclerView = findViewById(R.id.plan_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //add exhibit in list
        addExhbits();

        //setup adapter with content in list
        adapter.setPlanListItems(exhibitNames);
        adapter.setDistance(distance);
    }

    //add exhibit in list
    private void addExhbits() {
        exhibitNames = new ArrayList<>();
        for (String s : sortedExhibitID) {
            exhibitNames.add(IDToNameMap.get(s));
        }
    }


    //close current thread and enter direction thread
    public void DirectionClicked(View view) {
        Intent intent = new Intent(this, DirectionActivity.class);
        intent.putExtra("isResume",false);
        finish();
        startActivity(intent);
    }

    //Return to the plan activity and finish this thread
    public void ReturnClicked(View view) {
        finish();
    }

    //Delete all plan and back to plan activity
    public void deleteAllClicked(View view) {
        Utilities.deleteExhibitPlan();
        setContentView(R.layout.activity_plan);
        selectedExhibitList.clear();
        finish();
    }

}