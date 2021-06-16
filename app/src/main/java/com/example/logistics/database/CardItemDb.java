package com.example.logistics.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.logistics.recycler.CardItem;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {CardItem.class}, version = 1)
public abstract class CardItemDb extends RoomDatabase {
    public abstract CardItemDAO cardItemDAO();

    private static volatile CardItemDb INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static CardItemDb getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (CardItemDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            CardItemDb.class, "word_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
