package com.example.logistics.viewmodel;

import android.graphics.Bitmap;
import android.location.Address;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DriverPhotoViewModel extends ViewModel {

    private final MutableLiveData<Bitmap> photo = new MutableLiveData<>();

    public void setPhoto(Bitmap bitmap) { this.photo.setValue(bitmap); }

    public LiveData<Bitmap> getPhoto() { return photo; }

}
