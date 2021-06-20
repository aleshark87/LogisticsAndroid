package com.example.logistics.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.logistics.R;
import com.example.logistics.Utilities;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class NewDriverFragment extends Fragment {
    private Activity activity;
    private TextInputEditText editTextName;
    private TextInputEditText editTextCapacity;
    private Button captureButton;
    private MaterialButton submitButton;
    private ImageView imgView;
    private TextView hourStartTV;
    private TextView hourFinishTV;
    private Button buttonStartHour;
    private Button buttonFinishHour;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (AppCompatActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.new_carrier, container, false);
        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Utilities.setUpToolbar((AppCompatActivity)activity, "New Driver");
        getItemsFromView(view);

    }

    private void getItemsFromView(View view){
        editTextName = view.findViewById(R.id.editTextName);
        editTextCapacity = view.findViewById(R.id.editTextCapacity);
        captureButton = view.findViewById(R.id.captureButton);
        submitButton = view.findViewById(R.id.submitButtonDriver);
        imgView = view.findViewById(R.id.imageView);
        hourStartTV = view.findViewById(R.id.hourSetStartTV);
        hourFinishTV = view.findViewById(R.id.hourSetFinishTV);
    }
}
