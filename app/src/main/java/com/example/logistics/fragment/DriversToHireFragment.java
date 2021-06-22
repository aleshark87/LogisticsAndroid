package com.example.logistics.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.logistics.recyclercompany.ItemClickListener;
import com.example.logistics.recyclerdriver.AdapterDriverToHire;
import com.example.logistics.recyclerdriver.CardItemDriver;
import com.example.logistics.viewmodel.HiredViewModel;
import com.example.logistics.viewmodel.NotHiredViewModel;

import java.util.List;

public class DriversToHireFragment extends Fragment implements ItemClickListener {

    public static final String DRIVERS_TO_HIRE_FRAGMENT = "Drivers_tohire_Fragment";
    private Activity activity;
    private AdapterDriverToHire adapter;
    private NotHiredViewModel notHiredViewModel;
    private CardItemRepo repository;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (AppCompatActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.drivers_to_hire, container, false);
        repository = new CardItemRepo(activity.getApplication());

        // set up the RecyclerView
        RecyclerView recyclerView = layout.findViewById(R.id.recyclerDriversHire);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(layout.getContext()));
        adapter = new AdapterDriverToHire(activity, activity);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Utilities.setUpToolbar((AppCompatActivity) activity, "Drivers to Hire");
        notHiredViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(NotHiredViewModel.class);
        notHiredViewModel.getCardItems().observe((LifecycleOwner) activity, new Observer<List<CardItemDriver>>() {
            @Override
            public void onChanged(List<CardItemDriver> cardItemDrivers) {
                adapter.setData(cardItemDrivers);
            }
        });
        Toast.makeText(activity, "Tap a card to hire a driver", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(activity, "Hired " + adapter.getItem(position).getDriverName(), Toast.LENGTH_SHORT).show();
        //update al carditem
        repository.updateHiredDriver(true, adapter.getItem(position).getDriverName());
    }
}
