package edu.ucsd.cse110.ZooSeeker;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set UI
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // enter search list UI
        Intent intent = new Intent(this, SearchListActivity.class);
        startActivity(intent);
    }
}