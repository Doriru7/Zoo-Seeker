package edu.ucsd.cse110.ZooSeeker;

import androidx.annotation.NonNull;
import androidx.room.*;

@Entity(tableName = "exhibit_list_items")
public class ExhibitListItem {
    //public fields

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String exhibitName;
    //public map<String,Double> neighbors;
    public boolean selected;
    public int order;

    //constructor matching fields above

    public ExhibitListItem(@NonNull String exhibitName, boolean selected, int order) {
        this.exhibitName = exhibitName;
        this.selected = selected;
        this.order = order;
    }

    public String getExhibitName(){
        return exhibitName;
    }


}
