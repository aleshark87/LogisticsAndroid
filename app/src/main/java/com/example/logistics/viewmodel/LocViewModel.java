package com.example.logistics.viewmodel;

import android.content.ClipData;
import android.graphics.Bitmap;
import android.location.Address;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LocViewModel extends ViewModel {
    private final MutableLiveData<Address> startLocation = new MutableLiveData<>();
    private final MutableLiveData<Address> stopLocation = new MutableLiveData<>();

    public void setStartLocation(Address addr) {
        startLocation.setValue(addr);
    }
    public LiveData<Address> getStartLocation() {
        return startLocation;
    }

    public void setStopLocation(Address addr) {
        stopLocation.setValue(addr);
    }
    public LiveData<Address> getStopLocation() {
        return stopLocation;
    }
}