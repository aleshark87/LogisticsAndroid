package com.example.logistics;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.logistics.database.CardItemRepo;
import com.example.logistics.fragment.CardMapViewFragment;
import com.example.logistics.fragment.HomeFragment;
import com.example.logistics.fragment.NewDriverFragment;
import com.example.logistics.recyclercompany.CardItemCompany;
import com.example.logistics.viewmodel.DriverPhotoViewModel;

import static com.example.logistics.Utilities.REQUEST_IMAGE_CAPTURE;
import static com.example.logistics.fragment.CardMapViewFragment.CARD_MAP_FRAGMENT;
import static com.example.logistics.fragment.HomeFragment.HOME_FRAGMENT;

public class MainActivity extends AppCompatActivity {

    private DriverPhotoViewModel viewModelPhoto;
    private CardItemRepo repo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = this.getIntent();
        if(intent != null){
            if(intent.getIntExtra("companyId", -1) != -1){
                //prendere cardItemCompany
                repo = new CardItemRepo(this.getApplication());
                int id = intent.getIntExtra("companyId", 1);
                repo.getCardItemCompanyFromId(id).observe((LifecycleOwner) this, new Observer<CardItemCompany>() {
                    @Override
                    public void onChanged(CardItemCompany cardItemCompany) {
                        Utilities.insertFragment(MainActivity.this, new CardMapViewFragment(cardItemCompany, false, false, false), CARD_MAP_FRAGMENT);
                    }
                });
            }
        }
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
