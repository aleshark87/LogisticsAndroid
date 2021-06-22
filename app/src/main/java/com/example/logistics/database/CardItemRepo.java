package com.example.logistics.database;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.logistics.recyclercompany.CardItemCompany;
import com.example.logistics.recyclerdriver.CardItemDriver;

import java.util.List;

public class CardItemRepo {
    private CardItemDAO cardItemDAO;
    private LiveData<List<CardItemCompany>> cardItemCompanyList;
    private LiveData<List<CardItemDriver>> cardItemDriverList;

    public CardItemRepo(Application application) {
        CardItemDb db = CardItemDb.getDatabase(application);
        cardItemDAO = db.cardItemDAO();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<CardItemCompany>> getCardItemCompanyList(){
        cardItemCompanyList = cardItemDAO.getCardItemsCompany();
        return cardItemCompanyList;
    }

    public LiveData<List<CardItemDriver>> getCardItemDriverList(boolean hireValue){
        cardItemDriverList = cardItemDAO.getCardItemsDriver(hireValue);
        return cardItemDriverList;
    }

    public void addCardItemDriver(final CardItemDriver CardItemDriver) {
        CardItemDb.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                cardItemDAO.addCardItemDriver(CardItemDriver);
            }
        });
    }

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

    public void updateHiredDriver(boolean hireValue, String card_name){
        CardItemDb.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                cardItemDAO.updateDriverHired(hireValue, card_name);
            }
        });
    }

    public LiveData<CardItemDriver> getCardItemDriverFromName(String name){
        LiveData<CardItemDriver> live = cardItemDAO.getCardDriverFromName(name);
        return live;
    }
}
