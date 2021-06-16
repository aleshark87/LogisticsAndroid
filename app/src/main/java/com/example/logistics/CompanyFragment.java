package com.example.logistics;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
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

import com.example.logistics.recycler.Adapter;
import com.example.logistics.recycler.CardItem;
import com.example.logistics.recycler.ItemClickListener;
import com.google.gson.Gson;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.ArrayList;
import java.util.List;

public class CompanyFragment extends Fragment implements ItemClickListener {

    private MapView mapView;
    private Activity activity;
    private Adapter adapter;
    private CardViewModel cardViewModel;
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
        adapter = new Adapter(activity, activity);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cardViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(CardViewModel.class);

        view.findViewById(R.id.fab).setOnClickListener(v -> Utilities.insertFragment((AppCompatActivity)activity, new AddTransportFragment(), "AddCompanyFragment"));

        cardViewModel.getCardItems().observe((LifecycleOwner) activity, new Observer<List<CardItem>>() {
            @Override
            public void onChanged(List<CardItem> cardItems) {
                List<CardItem> deSerializedCardItems = new ArrayList<>();
                for(CardItem item : cardItems){
                    Address originAddress = serializator.fromJson(item.getSerializedAddressOrigin(), Address.class);
                    Address destinationAddress = serializator.fromJson(item.getSerializedAddressDestination(), Address.class);
                    //System.out.println(item.getSerializedAddressDestination());
                }
                adapter.setData(cardItems);
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(activity, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }
}
