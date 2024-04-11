package edu.ucsd.cse110.ZooSeeker;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ExhibitListItemDao {
    @Insert
    long insert(ExhibitListItem exhibitListItem);

    @Query("SELECT * FROM `exhibit_list_items` WHERE `id`=:id")
    ExhibitListItem get(long id);

    @Query("SELECT * FROM `exhibit_list_items` ORDER BY `order`")
    List<ExhibitListItem> getAll();

    @Update
    int update(ExhibitListItem exhibitListItem);

    @Delete
    int delete(ExhibitListItem exhibitListItem);

    //5/24 hao
    //@Delete
    //void deleteAll(List<ExhibitListItem> exhibitListItemList);
    @Query("DELETE FROM exhibit_list_items")
    void nukeTable();

    //Test1
    @Insert
    List<Long> insertAll(List<ExhibitListItem> exhibitListItem);

    @Query("SELECT * FROM `exhibit_list_items` ORDER BY `order`")
    LiveData<List<ExhibitListItem>> getAllLive();

    @Query("SELECT `order` + 1 FROM `exhibit_list_items` ORDER BY `order` DESC LIMIT 1")
    int getOrderForAppend2();
}
