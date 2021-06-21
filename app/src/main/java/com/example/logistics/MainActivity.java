package com.example.logistics;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.logistics.fragment.HomeFragment;
import com.example.logistics.fragment.NewDriverFragment;
import com.example.logistics.viewmodel.DriverPhotoViewModel;

import static com.example.logistics.Utilities.REQUEST_IMAGE_CAPTURE;
import static com.example.logistics.fragment.HomeFragment.HOME_FRAGMENT;

public class MainActivity extends AppCompatActivity {

    private DriverPhotoViewModel viewModelPhoto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //if there was no configuration change, create the fragment
        if (savedInstanceState == null)
            Utilities.insertFragment(this, new HomeFragment(), HOME_FRAGMENT);

        viewModelPhoto = new ViewModelProvider(this).get(DriverPhotoViewModel.class);
    }

    /**
     * Called after the picture is taken
     * @param requestCode requestCode of the intent
     * @param resultCode result of the intent
     * @param data data of the intent (picture)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                viewModelPhoto.setPhoto(imageBitmap);
            }
        }
    }
}
