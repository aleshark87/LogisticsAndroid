package com.example.logistics;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.logistics.fragment.HomeFragment;

public class MainActivity extends AppCompatActivity {

    private static final String FRAGMENT_TAG_HOME = "HomeFragment";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utilities.insertFragment(this, new HomeFragment(), FRAGMENT_TAG_HOME);
    }
}
