package com.example.logistics;

import android.content.ClipData;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LocViewModel extends ViewModel {
    private final MutableLiveData<String> startLocation = new MutableLiveData<String>();
    private final MutableLiveData<String> stopLocation = new MutableLiveData<String>();

    public void setStartLocation(String item) {
        startLocation.setValue(item);
    }
    public LiveData<String> getStartLocation() {
        return startLocation;
    }

    public void setStopLocation(String item) {
        stopLocation.setValue(item);
    }
    public LiveData<String> getStopLocation() {
        return stopLocation;
    }
}