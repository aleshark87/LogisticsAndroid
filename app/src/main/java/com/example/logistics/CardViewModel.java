package com.example.logistics;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.logistics.database.CardItemRepo;
import com.example.logistics.recycler.CardItem;

import java.util.List;

public class CardViewModel extends AndroidViewModel {
    private LiveData<List<CardItem>> cardItems;
    private CardItemRepo repository;

    public CardViewModel(@NonNull Application application) {
        super(application);
        repository = new CardItemRepo(application);
        cardItems = repository.getCardItemList();
        /*for db cleaning*/
        repository.nukeDb();
    }

    public LiveData<List<CardItem>> getCardItems() {
        return cardItems;
    }

    public CardItem getCardItem(int position){
        return cardItems.getValue() == null ? null : cardItems.getValue().get(position);
    }

    public void addCardItem(CardItem item){
        repository.addCardItem(item);
    }
}
