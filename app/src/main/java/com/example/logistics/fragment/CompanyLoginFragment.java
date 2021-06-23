package com.example.logistics.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import static com.example.logistics.fragment.CompanyFragment.COMPANY_FRAGMENT;
import static com.example.logistics.fragment.DriverFragment.DRIVER_FRAGMENT;

public class CompanyLoginFragment extends Fragment {

    public static final String COMPANY_LOGIN_FRAGMENT = "Company_Login_Fragment";
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
        Utilities.setUpToolbar((AppCompatActivity)activity, "Company");
        editTextId = view.findViewById(R.id.editTextName);
        editTextId.setHint("Write the company password");
        //editTextId.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        editTextId.setTransformationMethod(PasswordTransformationMethod.getInstance());

        label = view.findViewById(R.id.textLogin);
        view.findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameWritten = editTextId.getText().toString();
                if(nameWritten.matches("0000")){
                    editTextId.getText().clear();
                    Utilities.insertFragment((AppCompatActivity)activity, new CompanyFragment(), COMPANY_FRAGMENT);
                }
                else{
                    label.setText("Wrong password!");
                }
            }
        });
    }
}