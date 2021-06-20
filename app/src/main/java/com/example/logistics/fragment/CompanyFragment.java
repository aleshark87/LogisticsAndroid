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

import com.example.logistics.viewmodel.CardViewModelCompany;
import com.example.logistics.R;
import com.example.logistics.Utilities;
import com.example.logistics.recyclercompany.AdapterCompany;
import com.example.logistics.recyclercompany.CardItemCompany;
import com.example.logistics.recyclercompany.ItemClickListener;
import com.google.gson.Gson;
import com.mapbox.mapboxsdk.maps.MapView;

import java.util.List;

public class CompanyFragment extends Fragment implements ItemClickListener {

    private MapView mapView;
    private Activity activity;
    private AdapterCompany adapterCompany;
    private CardViewModelCompany cardViewModelCompany;
    private Gson serializator = new Gson();

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
        view.findViewById(R.id.fab).setOnClickListener(v -> Utilities.insertFragment((AppCompatActivity)activity, new AddTransportFragment(), "AddCompanyFragment"));

        cardViewModelCompany.getCardItems().observe((LifecycleOwner) activity, new Observer<List<CardItemCompany>>() {
            @Override
            public void onChanged(List<CardItemCompany> cardItemCompanies) {
                adapterCompany.setData(cardItemCompanies);
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(activity, "You clicked " + adapterCompany.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        Utilities.insertFragment((AppCompatActivity)activity, new CardMapViewFragment(adapterCompany.getItem(position)), "CardMapViewFragment");
    }
}
