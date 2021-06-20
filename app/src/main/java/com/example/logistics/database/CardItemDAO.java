package com.example.logistics.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.logistics.recyclercompany.CardItemCompany;

import java.util.List;

@Dao
public interface CardItemDAO {
    // The selected on conflict strategy ignores a new CardItem
    // if it's exactly the same as one already in the list.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addCardItemCompany(CardItemCompany CardItemCompany);

    @Transaction
    @Query("SELECT * from card_company ORDER BY item_id DESC")
    LiveData<List<CardItemCompany>> getCardItemsCompany();

    @Query("DELETE FROM card_company")
    public void nukeTableCompany();

    @Query("DELETE from card_company WHERE item_id=:item_id")
    public void deleteItemCompany(int item_id);

}
