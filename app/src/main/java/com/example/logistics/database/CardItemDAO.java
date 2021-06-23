package com.example.logistics.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.logistics.recyclercompany.CardItemCompany;
import com.example.logistics.recyclerdriver.CardItemDriver;

import java.util.List;

@Dao
public interface CardItemDAO {
    // The selected on conflict strategy ignores a new CardItem
    // if it's exactly the same as one already in the list.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addCardItemCompany(CardItemCompany CardItemCompany);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addCardItemDriver(CardItemDriver cardItemDriver);

    @Transaction
    @Query("SELECT * from card_driver WHERE card_driver_hired=:hireValue ORDER BY card_driver_name DESC ")
    LiveData<List<CardItemDriver>> getCardItemsDriver(boolean hireValue);

    @Transaction
    @Query("SELECT * from card_company ORDER BY item_id DESC")
    LiveData<List<CardItemCompany>> getCardItemsCompany();

    @Query("DELETE FROM card_company")
    public void nukeTableCompany();

    @Query("DELETE from card_company WHERE item_id=:item_id")
    public void deleteItemCompany(int item_id);

    @Query("UPDATE card_driver SET card_driver_hired=:hireValue WHERE card_driver_name=:item_name")
    public void updateDriverHired(boolean hireValue, String item_name);

    @Query("UPDATE card_company SET item_transportState=:state, item_driverName=:name WHERE item_id=:id")
    void updateTransportState(String state, int id, String name);

    @Transaction
    @Query("SELECT * FROM card_driver WHERE card_driver_name=:name")
    LiveData<CardItemDriver> getCardDriverFromName(String name);

    @Transaction
    @Query("SELECT * FROM card_company WHERE item_id=:id")
    LiveData<CardItemCompany> getCardCompanyFromId(int id);

}
