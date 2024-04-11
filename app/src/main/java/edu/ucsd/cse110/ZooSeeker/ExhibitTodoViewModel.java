package edu.ucsd.cse110.ZooSeeker;

import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.*;

import android.app.Application;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ExhibitTodoViewModel extends AndroidViewModel {
    private LiveData<List<ExhibitListItem>> exhibitListLiveItems;
    private final ExhibitListItemDao exhibitListItemDao;


    public ExhibitTodoViewModel(@NonNull Application application) {
        super(application);
        Context context = getApplication().getApplicationContext();
        ExhibitTodoDatabase db = ExhibitTodoDatabase.getSingleton(context);
        exhibitListItemDao = db.exhibitListItemDao();
    }

    public LiveData<List<ExhibitListItem>> getTodoListItems() {
        if (exhibitListLiveItems == null) {
            loadUsers();
        }

        return exhibitListLiveItems;
    }

    private void loadUsers() {
        exhibitListLiveItems = exhibitListItemDao.getAllLive();
    }

    public void toggleSelected(ExhibitListItem exhibitListItem) {
        exhibitListItem.selected = !exhibitListItem.selected;
        exhibitListItemDao.update(exhibitListItem);
    }

    public void updateText(ExhibitListItem exhibitListItem, String newText) {
        exhibitListItem.exhibitName = newText;
        exhibitListItemDao.update(exhibitListItem);
    }

    public void createTodo(String exhibitName) {
        int endOfListOrder = exhibitListItemDao.getOrderForAppend2();
        //int endOfListOrder = todoListItemDao.getOrderForAppend();
        ExhibitListItem newItem = new ExhibitListItem(exhibitName,false, endOfListOrder);
        exhibitListItemDao.insert(newItem);
        nameToItemMap.put(exhibitName, newItem);

        //Add new entry to the selectedExhibitList
        selectedExhibitList.add(exhibitName);

        //Get data from Dao and update total selected count
        exhibitListItems = exhibitListItemDao.getAll();
        exhibitCountTextView.setText(SELECTED_TOTAL + " " + exhibitListItems.size());

        //set resume to be invisible
        resumeBtn.setVisibility(View.INVISIBLE);
    }

    public void setDeleted(ExhibitListItem exhibitListItem){

        exhibitListItemDao.delete(exhibitListItem);
        selectedExhibitList.remove(exhibitListItem.exhibitName);
        nameToItemMap.remove(exhibitListItem);

        //set resume to be invisible
        resumeBtn.setVisibility(View.INVISIBLE);

    }
}
