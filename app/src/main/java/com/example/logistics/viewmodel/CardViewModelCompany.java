package com.example.logistics.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.logistics.database.CardItemRepo;
import com.example.logistics.recyclercompany.CardItemCompany;

import java.util.List;

public class CardViewModelCompany extends AndroidViewModel {
    private LiveData<List<CardItemCompany>> cardItems;
    private CardItemRepo repository;

    public CardViewModelCompany(@NonNull Application application) {
        super(application);
        repository = new CardItemRepo(application);
        cardItems = repository.getCardItemList();
        /*for db cleaning*/
        //repository.nukeDbCompany();
    }

    public LiveData<List<CardItemCompany>> getCardItems() {
        return cardItems;
    }

    public CardItemCompany getCardItem(int position){
        return cardItems.getValue() == null ? null : cardItems.getValue().get(position);
    }

    public void addCardItem(CardItemCompany item){
        repository.addCardItemCompany(item);
    }

    public void removeCardItem(int cardItemId) { repository.deleteItemCompany(cardItemId); }
}
