package com.example.logistics.fragment;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.logistics.notification.NotificationReceiver;
import com.example.logistics.recyclercompany.AdapterCompany;
import com.example.logistics.recyclercompany.CardItemCompany;
import com.example.logistics.recyclercompany.ItemClickListener;
import com.example.logistics.recyclerdriver.AdapterDriverToHire;
import com.example.logistics.recyclerdriver.CardItemDriver;
import com.example.logistics.viewmodel.CardViewModelCompany;
import com.example.logistics.viewmodel.NotHiredViewModel;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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
    public static String notificationMessage;

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
            Utilities.insertFragment((AppCompatActivity)activity, new CardMapViewFragment(filteredInProgress.get(position), false, false, true, driver), CARD_MAP_FRAGMENT);
        }
    }

    private void takeJob(CardItemCompany cardItemCompany) {
        if(cardItemCompany.getQuantityKg() > driver.getCapacity()){
            Toast.makeText(activity, "Not enough capacity on your truck, " + driver.getDriverName(), Toast.LENGTH_SHORT).show();
        }
        else{
            if(timePassed(cardItemCompany)){
                repository.updateTransportState("progress", cardItemCompany.getId(), driver.getDriverName());
                Toast.makeText(activity, "Transport " + cardItemCompany.getTitle() + " taken", Toast.LENGTH_SHORT).show();
                setAlarm(cardItemCompany);
            }
            else{
                Toast.makeText(activity, "You're not working in that hour", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean timePassed(CardItemCompany cardItemCompany) {
        int startTimeHour = Integer.parseInt(driver.getTimeWork().split("_")[0].split(":")[0]);
        int startTimeMinute = Integer.parseInt(driver.getTimeWork().split("_")[0].split(":")[1]);
        int hourJob = Integer.parseInt(cardItemCompany.getDate().split(" ")[1].split(":")[0]);
        int minuteJob = Integer.parseInt(cardItemCompany.getDate().split(" ")[1].split(":")[1]);
        if (startTimeHour <= hourJob) {
            if (startTimeHour == hourJob) {
                if (startTimeMinute <= minuteJob) {
                    return true;
                } else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public void setAlarm(CardItemCompany cardItemCompany) {
        SharedPreferences sharedPref = getContext().getSharedPreferences("notificationTransport", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("notificationMessage", driver.getDriverName() + " you have to do transport " + cardItemCompany.getTitle());
        editor.putInt("companyId", cardItemCompany.getId());
        editor.apply();
        Calendar calendar = Calendar.getInstance();
        String date = cardItemCompany.getDate().split(",")[0];
        String time = cardItemCompany.getDate().split(" ")[1];
        String dayOfMonth = date.split("06")[1].split("-")[1];
        String hour = time.split(":")[0];
        String minute = time.split(":")[1];

        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dayOfMonth));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
        calendar.set(Calendar.MINUTE, Integer.parseInt(minute));
        calendar.set(Calendar.SECOND, 0);

       /* calendar.set(Calendar.DAY_OF_MONTH, 27);
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        calendar.set(Calendar.MINUTE, 57);
        calendar.set(Calendar.SECOND, 0);*/
        Calendar cur = Calendar.getInstance();
        if (cur.after(calendar)) {
            calendar.add(Calendar.DATE, 1);
        }
        NotificationReceiver receiver = new NotificationReceiver();
        receiver.setNotificationText(notificationMessage);
        Intent myIntent = new Intent(activity.getApplicationContext(), NotificationReceiver.class);
        int ALARM1_ID = 10000;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                activity.getApplicationContext(), ALARM1_ID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) activity.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

}
