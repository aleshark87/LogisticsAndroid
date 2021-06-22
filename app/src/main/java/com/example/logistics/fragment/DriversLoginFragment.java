package com.example.logistics.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.example.logistics.R;
import com.example.logistics.Utilities;
import com.example.logistics.database.CardItemRepo;
import com.example.logistics.recyclerdriver.CardItemDriver;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import static com.example.logistics.fragment.DriverFragment.DRIVER_FRAGMENT;

public class DriversLoginFragment extends Fragment {

    public static final String DRIVER_LOGIN_FRAGMENT = "Driver_Login_Fragment";
    private Activity activity;
    private TextInputEditText editTextId;
    private CardItemRepo repository;
    private TextView label;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (AppCompatActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.drivers_login_fragment, container, false);
        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new CardItemRepo(activity.getApplication());
        Utilities.setUpToolbar((AppCompatActivity)activity, "Drivers");
        editTextId = view.findViewById(R.id.editTextName);
        label = view.findViewById(R.id.textLogin);
        view.findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameWritten = editTextId.getText().toString();
                repository.getCardItemDriverFromName(nameWritten).observe((LifecycleOwner) activity, new Observer<CardItemDriver>() {
                    @Override
                    public void onChanged(CardItemDriver cardItemDriver) {
                        //switch fragment
                        if(cardItemDriver != null){
                            if(nameWritten.matches(cardItemDriver.getDriverName())){
                                Utilities.insertFragment((AppCompatActivity)activity, new DriverFragment(cardItemDriver), DRIVER_FRAGMENT);
                            }
                        }
                        else{
                            label.setText("Can't find " + nameWritten);
                        }
                    }
                });
            }
        });
    }
}
