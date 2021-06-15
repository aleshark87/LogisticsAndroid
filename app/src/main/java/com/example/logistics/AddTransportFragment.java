package com.example.logistics;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class AddTransportFragment extends Fragment {
    private Activity activity;
    private LocViewModel viewModel;

    //TODO database-viewmodel(carditem)
    //TODO temi, colore sfondo delle card, colore del bottone submit(c'è già lo style)

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.addtransport, container, false);
        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //view.findViewById(R.id.selectLoc).setOnClickListener(v -> Utilities.insertFragment((AppCompatActivity)activity, new LocationPickerFragment(), "LocationPickerFragment"));
        viewModel = new ViewModelProvider(requireActivity()).get(LocViewModel.class);

        viewModel.getLocation().observe(getViewLifecycleOwner(), item -> {
            // Perform an action with the latest item data
            //TextView textView = view.findViewById(R.id.test);
            //textView.setText(item);
        });
    }
}
