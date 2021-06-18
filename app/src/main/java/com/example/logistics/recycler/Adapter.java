package com.example.logistics.recycler;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.logistics.R;
import com.example.logistics.Utilities;
import com.mapbox.mapboxsdk.Mapbox;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<ViewHolder>  {

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Activity activity;

    private List<CardItem> itemsList = new ArrayList<>();

    // data is passed into the constructor
    public Adapter(Activity activity, Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.itemview, parent, false);
        return new ViewHolder(view, mClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CardItem currentCardItem = itemsList.get(position);

        holder.getTitleTextView().setText(currentCardItem.getTitle());
        holder.getDestArrTextView().setText("Origin: " + currentCardItem.getOriginLocality() +
                ", Destination: " + currentCardItem.getDestinationLocality());
        holder.getDateTextView().setText("Departure time: " + currentCardItem.getDate());
        holder.getQuantityProductTv().setText("Product: " + currentCardItem.getProductType() + ", Quantity: " + currentCardItem.getQuantityKg());
        Resources res = activity.getResources();
        Drawable drawable = ResourcesCompat.getDrawable(res, currentCardItem.getImgResource(), null);
        holder.getImgView().setImageDrawable(drawable);
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // convenience method for getting data at click position
    public CardItem getItem(int id) {
        return itemsList.get(id);
    }

    public void setData(List<CardItem> items){
        this.itemsList = items;
        notifyDataSetChanged();
    }
}
