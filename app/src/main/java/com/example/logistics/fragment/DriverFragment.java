package com.example.logistics.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.logistics.R;
import com.example.logistics.Utilities;
import com.example.logistics.recyclercompany.AdapterCompany;
import com.example.logistics.recyclercompany.CardItemCompany;
import com.example.logistics.recyclercompany.ItemClickListener;
import com.example.logistics.recyclerdriver.AdapterDriverToHire;
import com.example.logistics.recyclerdriver.CardItemDriver;
import com.example.logistics.viewmodel.CardViewModelCompany;
import com.example.logistics.viewmodel.NotHiredViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DriverFragment extends Fragment implements ItemClickListener {

    public static final String DRIVER_FRAGMENT = "Driver_Fragment";
    private Activity activity;
    private CardItemDriver driver;
    private CardViewModelCompany cardViewModelCompany;
    private AdapterCompany adapterAvailable;
    private AdapterCompany adapterInProgress;
    private AdapterCompany adapterDone;

    public DriverFragment(CardItemDriver driver) {
        this.driver = driver;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (AppCompatActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.driver_fragment, container, false);
        setAdapterAvailable(layout);
        setAdapterInProgress(layout);
        return layout;
    }

    private void setAdapterInProgress(View layout){
        RecyclerView recyclerInProgress = layout.findViewById(R.id.recyclerInProgressTransports);
        recyclerInProgress.setHasFixedSize(true);
        recyclerInProgress.setLayoutManager(new LinearLayoutManager(layout.getContext()));
        adapterInProgress = new AdapterCompany(activity, activity);
        adapterInProgress.setClickListener(this);
        recyclerInProgress.setAdapter(adapterInProgress);
    }

    private void setAdapterAvailable(View layout){
        // set up the RecyclerView
        RecyclerView recyclerViewAvailable = layout.findViewById(R.id.recyclerAvailableTransports);
        recyclerViewAvailable.setHasFixedSize(true);
        recyclerViewAvailable.setLayoutManager(new LinearLayoutManager(layout.getContext()));
        adapterAvailable = new AdapterCompany(activity, activity);
        //adapterAvailable.setClickListener(this);
        recyclerViewAvailable.setAdapter(adapterAvailable);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Utilities.setUpToolbar((AppCompatActivity) activity, "Drivers to Hire");
        cardViewModelCompany = new ViewModelProvider((ViewModelStoreOwner) activity).get(CardViewModelCompany.class);
        cardViewModelCompany.getCardItems().observe((LifecycleOwner) activity, new Observer<List<CardItemCompany>>() {
            @Override
            public void onChanged(List<CardItemCompany> cardItemCompanies) {
                adapterAvailable.setData(filterListAvailable(cardItemCompanies));
                adapterInProgress.setData(filterListInProgress(cardItemCompanies));
            }

        });
    }

    private List<CardItemCompany> filterListInProgress(List<CardItemCompany> cardItemCompanies) {
        List<CardItemCompany> copy = new ArrayList<>(new ArrayList<>(cardItemCompanies));
        List<CardItemCompany> transportToRemove = new ArrayList<>();
        for (CardItemCompany card : copy) {
            if (!card.getTransportState().matches("progress")/* && !driver.getDriverName().matches(card.getDriverName()*/) {
                transportToRemove.add(card);
            }
        }
        for (CardItemCompany card : transportToRemove) {
            copy.remove(card);
        }
        return copy;
    }

    private List<CardItemCompany> filterListAvailable(List<CardItemCompany> cardItemCompanies) {
        //NO REFERENCE
        List<CardItemCompany> copy = new ArrayList<>(new ArrayList<>(cardItemCompanies));
        List<CardItemCompany> transportToRemove = new ArrayList<>();
        for (CardItemCompany card : copy) {
            if (!card.getTransportState().matches("insered")) {
                transportToRemove.add(card);
            }
        }
        for (CardItemCompany card : transportToRemove) {
            copy.remove(card);
        }

        return copy;
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d("tag", "clicked");
    }
}
