package edu.ucsd.cse110.ZooSeeker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.Executors;

@Database(entities = {ExhibitListItem.class}, version = 1)
public abstract class ExhibitTodoDatabase extends RoomDatabase {
    private static ExhibitTodoDatabase singleton = null;

    public abstract ExhibitListItemDao exhibitListItemDao();

    public synchronized static ExhibitTodoDatabase getSingleton(Context context) {
        if (singleton == null) {
            singleton = ExhibitTodoDatabase.makeDatabase(context);
        }
        return singleton;
    }

    private static ExhibitTodoDatabase makeDatabase(Context context) {
        return Room.databaseBuilder(context, ExhibitTodoDatabase.class, "todo_app.db")
                .allowMainThreadQueries()
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadScheduledExecutor().execute(() -> {
                            /*getSingleton(context).exhibitListItemDao().insert(new ExhibitListItem("entrance_exit_gate", true, 1));
                            getSingleton(context).exhibitListItemDao().insert(new ExhibitListItem("entrance_plaza", true, 2)); */
                        });
                    }
                })
                .build();
    }

    @VisibleForTesting
    public static void injectTestDatabase(ExhibitTodoDatabase testDatabase) {
        if (singleton != null ) {
            singleton.close();
        }
        singleton = testDatabase;
    }

}
