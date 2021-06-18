package com.example.logistics;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Activity activity = getActivity();
        if(activity != null) {
            Utilities.setUpToolbar((AppCompatActivity) activity, "Logistics");
            view.findViewById(R.id.companyBt).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //companyButton
                    Utilities.insertFragment((AppCompatActivity)activity, new CompanyFragment(), "CompanyFragment");
                }
            });
            view.findViewById(R.id.carrierBt).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //carrierButton
                }
            });
            view.findViewById(R.id.newCarrierBt).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //newCarrierBt
                }
            });
        }
    }

}
