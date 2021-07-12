package com.example.logistics.fragment;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.logistics.R;
import com.example.logistics.Utilities;
import com.example.logistics.notification.NotificationReceiver;
import com.example.logistics.recyclercompany.CardItemCompany;

import java.util.Calendar;

import static com.example.logistics.fragment.CompanyFragment.COMPANY_FRAGMENT;
import static com.example.logistics.fragment.CompanyLoginFragment.COMPANY_LOGIN_FRAGMENT;
import static com.example.logistics.fragment.DriversLoginFragment.DRIVER_LOGIN_FRAGMENT;
import static com.example.logistics.fragment.NewDriverFragment.NEW_DRIVER_FRAGMENT;

public class HomeFragment extends Fragment {

    public static final String HOME_FRAGMENT = "Home_Fragment";
private Activity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        if(activity != null) {
            Utilities.setUpToolbar((AppCompatActivity) activity, "Logistics");
            view.findViewById(R.id.companyBt).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //companyButton
                    Utilities.insertFragment((AppCompatActivity)activity, new CompanyLoginFragment(), COMPANY_LOGIN_FRAGMENT);
                }
            });
            view.findViewById(R.id.carrierBt).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //carrierButton
                    Utilities.insertFragment((AppCompatActivity)activity, new DriversLoginFragment(), DRIVER_LOGIN_FRAGMENT);
                }
            });
            view.findViewById(R.id.newCarrierBt).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utilities.insertFragment((AppCompatActivity)activity, new NewDriverFragment(), NEW_DRIVER_FRAGMENT);
                }
            });
            setAlarm();
        }
    }

    public void setAlarm() {
        String notificationMessage = "aless you have to do transport iron";
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 19);
        calendar.set(Calendar.MINUTE, 27);
        calendar.set(Calendar.SECOND, 30);
        calendar.set(Calendar.MILLISECOND, 0);

        Calendar cur = Calendar.getInstance();

        if (cur.after(calendar)) {
            calendar.add(Calendar.DATE, 1);
        }

        Intent myIntent = new Intent(activity.getApplicationContext(), NotificationReceiver.class);
        int ALARM1_ID = 10000;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                activity.getApplicationContext(), ALARM1_ID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) activity.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }



}
