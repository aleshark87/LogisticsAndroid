package com.example.logistics.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.logistics.R;
import com.example.logistics.Utilities;

import static com.example.logistics.fragment.CompanyFragment.COMPANY_FRAGMENT;
import static com.example.logistics.fragment.CompanyLoginFragment.COMPANY_LOGIN_FRAGMENT;
import static com.example.logistics.fragment.DriversLoginFragment.DRIVER_LOGIN_FRAGMENT;
import static com.example.logistics.fragment.NewDriverFragment.NEW_DRIVER_FRAGMENT;

public class HomeFragment extends Fragment {

    public static final String HOME_FRAGMENT = "Home_Fragment";

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
                    Utilities.insertFragment((AppCompatActivity)activity, new CompanyLoginFragment(), COMPANY_LOGIN_FRAGMENT);
                }
            });
            view.findViewById(R.id.carrierBt).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //carrierButton
                    Utilities.insertFragment((AppCompatActivity)activity, new DriversLoginFragment(), DRIVER_LOGIN_FRAGMENT);
                }
            });
            view.findViewById(R.id.newCarrierBt).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utilities.insertFragment((AppCompatActivity)activity, new NewDriverFragment(), NEW_DRIVER_FRAGMENT);
                }
            });
        }
    }

}
