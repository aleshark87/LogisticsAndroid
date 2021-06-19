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
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.logistics.CardViewModel;
import com.example.logistics.R;
import com.example.logistics.Utilities;
import com.mapbox.mapboxsdk.Mapbox;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<ViewHolder>  {

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Activity activity;
    private CardViewModel cardViewModel;

    private List<CardItem> itemsList = new ArrayList<>();

    // data is passed into the constructor
    public Adapter(Activity activity, Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.activity = activity;
        cardViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(CardViewModel.class);
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
        if((removeIfOld(currentCardItem) == false)) {
            holder.getTitleTextView().setText(currentCardItem.getTitle());
            holder.getDestArrTextView().setText("Origin: " + currentCardItem.getOriginLocality() +
                    ", Destination: " + currentCardItem.getDestinationLocality());
            holder.getDateTextView().setText("Departure time: " + currentCardItem.getDate());
            holder.getQuantityProductTv().setText("Product: " + currentCardItem.getProductType() + ", Quantity: " + currentCardItem.getQuantityKg());
            Resources res = activity.getResources();
            Drawable drawable = ResourcesCompat.getDrawable(res, currentCardItem.getImgResource(), null);
            holder.getImgView().setImageDrawable(drawable);
        }
    }

    private boolean removeIfOld(CardItem currentCardItem){
        boolean removed = false;
        try {
            String dateString = currentCardItem.getDate().split(",")[0];
            String timeString = currentCardItem.getDate().split(" ")[1];
            if (new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateString + " " + timeString).before(new Date())) {
                Log.d("tag", "past " + currentCardItem.getTitle());
                cardViewModel.removeCardItem(currentCardItem.getId());
                removed = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return removed;
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
