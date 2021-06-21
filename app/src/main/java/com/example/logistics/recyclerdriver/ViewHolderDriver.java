package com.example.logistics.recyclerdriver;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.logistics.R;
import com.example.logistics.recyclercompany.ItemClickListener;

public class ViewHolderDriver extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView titleTextView;
    private TextView workingHoursTextView;
    private TextView truckCapacity;
    private ImageView imgView;
    private ItemClickListener clickListener;

    ViewHolderDriver(View itemView, ItemClickListener listener) {
        super(itemView);
        titleTextView = itemView.findViewById(R.id.nameTitleCard);
        workingHoursTextView = itemView.findViewById(R.id.workingHours);
        imgView = itemView.findViewById(R.id.driverImgView);
        truckCapacity = itemView.findViewById(R.id.truckCapacity);
        itemView.setOnClickListener(this);
        clickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (clickListener != null) clickListener.onItemClick(v, getAdapterPosition());
    }

    public TextView getTitleTextView() {
        return titleTextView;
    }

    public TextView getWorkingHoursTextView() {
        return workingHoursTextView;
    }

    public TextView getTruckCapacity() {
        return truckCapacity;
    }

    public ImageView getImgView() {
        return imgView;
    }
}
