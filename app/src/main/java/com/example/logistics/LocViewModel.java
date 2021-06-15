package com.example.logistics;

import android.content.ClipData;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LocViewModel extends ViewModel {
    private final MutableLiveData<String> selectedItem = new MutableLiveData<String>();

    public void setLocation(String item) {
        selectedItem.setValue(item);
    }
    public LiveData<String> getLocation() {
        return selectedItem;
    }
}