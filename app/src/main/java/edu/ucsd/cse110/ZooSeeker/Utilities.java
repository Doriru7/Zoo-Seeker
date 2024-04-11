package edu.ucsd.cse110.ZooSeeker;

import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.exhibitListItemDao;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.nameToIDMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.nameToItemMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.nameToParentIDMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.vertexInfoMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

public class Utilities {
    //alert for undefined content in search list
    public static void showAlert(Activity activity, String message) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);

        alertBuilder
                .setTitle("Alert!")
                .setMessage(message)
                .setPositiveButton("Ok", (dialog, id) -> {
                    dialog.cancel();
                })
                .setCancelable(true);

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    //Delete exhibit plan
    public static void deleteExhibitPlan() {
        exhibitListItemDao.nukeTable();
        List<ExhibitListItem> exhibits = exhibitListItemDao.getAll();

        //print all for Log testing
        {
            String all = "LOG TEST: It should not have anything {";
            for (ExhibitListItem i : exhibits) {
                all += i.exhibitName + ", ";
            }
            all += "}";
            System.out.println("You have deleted everything" + all);
            Log.d("TEST", all + "\n");
        }
    }
}
