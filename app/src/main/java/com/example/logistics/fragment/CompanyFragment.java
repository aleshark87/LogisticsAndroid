package com.example.logistics.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import com.example.logistics.viewmodel.CardViewModelCompany;
import com.example.logistics.R;
import com.example.logistics.Utilities;
import com.example.logistics.recyclercompany.AdapterCompany;
import com.example.logistics.recyclercompany.CardItemCompany;
import com.example.logistics.recyclercompany.ItemClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.example.logistics.fragment.AddTransportFragment.ADD_TRANSPORT_FRAGMENT;
import static com.example.logistics.fragment.CardMapViewFragment.CARD_MAP_FRAGMENT;
import static com.example.logistics.fragment.DetailedMapFragment.DETAILED_MAP_FRAGMENT;
import static com.example.logistics.fragment.DoneMapDetailFragment.DONE_MAP_FRAGMENT;
import static com.example.logistics.fragment.DriversHiredFragment.DRIVERS_HIRED_FRAGMENT;
import static com.example.logistics.fragment.DriversToHireFragment.DRIVERS_TO_HIRE_FRAGMENT;

public class CompanyFragment extends Fragment implements ItemClickListener{

    public static final String COMPANY_FRAGMENT = "Company_Fragment";
    private Activity activity;
    private AdapterCompany adapterCompany;
    private CardViewModelCompany cardViewModelCompany;
    private List<CardItemCompany> listAvailable;
    private List<CardItemCompany> listInProgress;
    private List<CardItemCompany> listDone;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.company, container, false);

        // set up the RecyclerView
        RecyclerView recyclerView = layout.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(layout.getContext()));
        adapterCompany = new AdapterCompany(activity, activity);
        adapterCompany.setClickListener(this);
        recyclerView.setAdapter(adapterCompany);

        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Utilities.setUpToolbar((AppCompatActivity) activity, "Company");
        cardViewModelCompany = new ViewModelProvider((ViewModelStoreOwner) activity).get(CardViewModelCompany.class);
        view.findViewById(R.id.fab).setOnClickListener(v -> Utilities.insertFragment((AppCompatActivity)activity, new AddTransportFragment(), ADD_TRANSPORT_FRAGMENT));

        cardViewModelCompany.getCardItems().observe((LifecycleOwner) activity, new Observer<List<CardItemCompany>>() {
            @Override
            public void onChanged(List<CardItemCompany> cardItemCompanies) {
                adapterCompany.setData(cardItemCompanies);
                filterListAvailable(cardItemCompanies);
                filterListInProgress(cardItemCompanies);
                filterListDone(cardItemCompanies);
            }
        });

        view.findViewById(R.id.driversButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(activity, R.style.MaterialAlertDialog)
                        .setMessage("Want to hire new Drivers?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Utilities.insertFragment((AppCompatActivity)activity, new DriversToHireFragment(), DRIVERS_TO_HIRE_FRAGMENT);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Utilities.insertFragment((AppCompatActivity)activity, new DriversHiredFragment(), DRIVERS_HIRED_FRAGMENT);
                            }
                        })
                        .show();
            }
        });

        view.findViewById(R.id.detailedMapBt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start detailed map fragment
                new MaterialAlertDialogBuilder(activity, R.style.MaterialAlertDialog)
                        .setMessage("Want to see Done Transports?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //DONE TRANSPORTS
                                Utilities.insertFragment((AppCompatActivity)activity, new DoneMapDetailFragment(listDone), DONE_MAP_FRAGMENT);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Utilities.insertFragment((AppCompatActivity)activity, new DetailedMapFragment(listAvailable, listInProgress), DETAILED_MAP_FRAGMENT);
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Utilities.insertFragment((AppCompatActivity)activity, new CardMapViewFragment(adapterCompany.getItem(position), false, false, true), CARD_MAP_FRAGMENT);
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
        listAvailable = copy;
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
            }
        }
        for (CardItemCompany card : transportToRemove) {
            copy.remove(card);
        }
        listInProgress = copy;
        return copy;
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
        listDone = copy;
        return copy;
    }
}
