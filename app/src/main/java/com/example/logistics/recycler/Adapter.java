package com.example.logistics.recycler;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.logistics.R;
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
        holder.getDestArrTextView().setText(currentCardItem.getDestArr());
        holder.getDateTextView().setText(currentCardItem.getDate());

        holder.getImgView().setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_launcher_foreground));
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
