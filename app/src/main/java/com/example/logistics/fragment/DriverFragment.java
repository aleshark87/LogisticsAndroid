package com.example.logistics.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.example.logistics.database.CardItemRepo;
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

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

import static com.example.logistics.fragment.CardMapViewFragment.CARD_MAP_FRAGMENT;
import static com.example.logistics.fragment.QrReaderFragment.QR_FRAGMENT;

public class DriverFragment extends Fragment implements ItemClickListener {

    public static final String DRIVER_FRAGMENT = "Driver_Fragment";
    private Activity activity;
    private CardItemDriver driver;
    private CardViewModelCompany cardViewModelCompany;
    private AdapterCompany adapterAvailable;
    private AdapterCompany adapterInProgress;
    private AdapterCompany adapterDone;
    private List<CardItemCompany> filteredAvailable;
    private List<CardItemCompany> filteredInProgress;
    private List<CardItemCompany> filteredDone;
    private CardItemRepo repository;

    public DriverFragment(CardItemDriver driver) {
        this.driver = driver;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (AppCompatActivity) context;
        repository = new CardItemRepo(activity.getApplication());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.driver_fragment, container, false);
        setAdapterAvailable(layout);
        setAdapterInProgress(layout);
        setAdapterDone(layout);
        TextView title = layout.findViewById(R.id.welcomeTextView);
        title.setText("Welcome " + driver.getDriverName() + "!");
        return layout;
    }

    private void setAdapterDone(View layout){
        RecyclerView recyclerDone = layout.findViewById(R.id.recyclerDoneTransports);
        recyclerDone.setHasFixedSize(true);
        recyclerDone.setLayoutManager(new LinearLayoutManager(layout.getContext()));
        adapterDone = new AdapterCompany(activity, activity);
        adapterDone.setClickListener(this);
        recyclerDone.setAdapter(adapterDone);
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
        adapterAvailable.setClickListener(this);
        recyclerViewAvailable.setAdapter(adapterAvailable);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Utilities.setUpToolbar((AppCompatActivity) activity, "Drivers");
        cardViewModelCompany = new ViewModelProvider((ViewModelStoreOwner) activity).get(CardViewModelCompany.class);
        cardViewModelCompany.getCardItems().observe((LifecycleOwner) activity, new Observer<List<CardItemCompany>>() {
            @Override
            public void onChanged(List<CardItemCompany> cardItemCompanies) {
                adapterAvailable.setData(filterListAvailable(cardItemCompanies));
                adapterInProgress.setData(filterListInProgress(cardItemCompanies));
                adapterDone.setData(filterListDone(cardItemCompanies));
            }
        });

        view.findViewById(R.id.detailedMapDriver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //UGUALE IDENTICA A COMPANY
            }
        });
        view.findViewById(R.id.scanQrCode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.insertFragment((AppCompatActivity)activity, new QrReaderFragment(), QR_FRAGMENT);
            }
        });
    }

    private List<CardItemCompany> filterListDone(List<CardItemCompany> cardItemCompanies) {
        List<CardItemCompany> copy = new ArrayList<>(new ArrayList<>(cardItemCompanies));
        List<CardItemCompany> transportToRemove = new ArrayList<>();
        for (CardItemCompany card : copy) {
            //da fare presa in carico
            if (!card.getTransportState().matches("done")/* && !driver.getDriverName().matches(card.getDriverName()*/) {
                transportToRemove.add(card);
            }
        }
        for (CardItemCompany card : transportToRemove) {
            copy.remove(card);
        }
        filteredDone = copy;
        return copy;
    }


    private List<CardItemCompany> filterListInProgress(List<CardItemCompany> cardItemCompanies) {
        List<CardItemCompany> copy = new ArrayList<>(new ArrayList<>(cardItemCompanies));
        List<CardItemCompany> transportToRemove = new ArrayList<>();
        for (CardItemCompany card : copy) {
            //da fare presa in carico
            if(card.getDriverName() == null){
                transportToRemove.add(card);
            }
            else{
                if (!card.getTransportState().matches("progress")) {
                    transportToRemove.add(card);
                }
                else{
                    if(!driver.getDriverName().matches(card.getDriverName())){
                        transportToRemove.add(card);
                    }
                }
            }
        }
        for (CardItemCompany card : transportToRemove) {
            copy.remove(card);
        }
        filteredInProgress = copy;
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
        filteredAvailable = copy;
        return copy;
    }

    @Override
    public void onItemClick(View view, int position) {
        String fullName = getResources().getResourceName(((View) view.getParent()).getId());
        String name = fullName.substring(fullName.lastIndexOf("/") + 1);

        if(name.matches("recyclerAvailableTransports")){
            takeJob(filteredAvailable.get(position));
        }
        if(name.matches("recyclerInProgressTransports")){
            Utilities.insertFragment((AppCompatActivity)activity, new CardMapViewFragment(filteredInProgress.get(position), true, false), CARD_MAP_FRAGMENT);
        }
    }

    private void takeJob(CardItemCompany cardItemCompany) {
        if(cardItemCompany.getQuantityKg() > driver.getCapacity()){
            Toast.makeText(activity, "Not enough capacity on your truck, " + driver.getDriverName(), Toast.LENGTH_SHORT).show();
        }
        else{
            repository.updateTransportState("progress", cardItemCompany.getId(), driver.getDriverName());
            Toast.makeText(activity, "Transport " + cardItemCompany.getTitle() + " taken", Toast.LENGTH_SHORT).show();
        }
    }


}
