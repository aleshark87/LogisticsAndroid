package com.example.logistics.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.logistics.recycler.CardItem;

import java.util.List;

public class CardItemRepo {
    private CardItemDAO cardItemDAO;
    private LiveData<List<CardItem>> cardItemList;

    public CardItemRepo(Application application) {
        CardItemDb db = CardItemDb.getDatabase(application);
        cardItemDAO = db.cardItemDAO();
        cardItemList = cardItemDAO.getCardItems();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<CardItem>> getCardItemList(){
        return cardItemList;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void addCardItem(final CardItem CardItem) {
        CardItemDb.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                cardItemDAO.addCardItem(CardItem);
            }
        });
    }

    public void nukeDb(){
        CardItemDb.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                cardItemDAO.nukeTable();
            }
        });
    }
}
