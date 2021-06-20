package com.example.logistics.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.logistics.recyclercompany.CardItemCompany;

import java.util.List;

public class CardItemRepo {
    private CardItemDAO cardItemDAO;
    private LiveData<List<CardItemCompany>> cardItemList;

    public CardItemRepo(Application application) {
        CardItemDb db = CardItemDb.getDatabase(application);
        cardItemDAO = db.cardItemDAO();
        cardItemList = cardItemDAO.getCardItemsCompany();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<CardItemCompany>> getCardItemList(){
        return cardItemList;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void addCardItemCompany(final CardItemCompany CardItemCompany) {
        CardItemDb.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                cardItemDAO.addCardItemCompany(CardItemCompany);
            }
        });
    }

    public void nukeDbCompany(){
        CardItemDb.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                cardItemDAO.nukeTableCompany();
            }
        });
    }

    public void deleteItemCompany(int card_id){
        CardItemDb.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                cardItemDAO.deleteItemCompany(card_id);
            }
        });
    }
}
