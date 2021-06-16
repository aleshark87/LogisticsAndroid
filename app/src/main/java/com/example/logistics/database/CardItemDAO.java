package com.example.logistics.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.logistics.recycler.CardItem;

import java.util.List;

@Dao
public interface CardItemDAO {
    // The selected on conflict strategy ignores a new CardItem
    // if it's exactly the same as one already in the list.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addCardItem(CardItem CardItem);

    @Transaction
    @Query("SELECT * from item ORDER BY item_id DESC")
    LiveData<List<CardItem>> getCardItems();

    @Query("DELETE FROM item")
    public void nukeTable();

}
