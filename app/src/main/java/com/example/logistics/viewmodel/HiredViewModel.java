package com.example.logistics.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.logistics.database.CardItemRepo;
import com.example.logistics.recyclerdriver.CardItemDriver;

import java.util.List;

public class HiredViewModel extends AndroidViewModel {

    private LiveData<List<CardItemDriver>> cardItems;
    private CardItemRepo repository;

    public HiredViewModel(@NonNull Application application) {
        super(application);
        repository = new CardItemRepo(application);
    }

    public LiveData<List<CardItemDriver>> getCardItems() {
        cardItems = repository.getCardItemDriverList(true);
        return cardItems;
    }

    public void addCardItem(CardItemDriver item) { repository.addCardItemDriver(item);}
}
