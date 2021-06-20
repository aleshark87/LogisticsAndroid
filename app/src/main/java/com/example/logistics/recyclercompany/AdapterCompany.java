package com.example.logistics.recyclercompany;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.logistics.viewmodel.CardViewModelCompany;
import com.example.logistics.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdapterCompany extends RecyclerView.Adapter<ViewHolderCompany>  {

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Activity activity;
    private CardViewModelCompany cardViewModelCompany;

    private List<CardItemCompany> itemsList = new ArrayList<>();

    // data is passed into the constructor
    public AdapterCompany(Activity activity, Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.activity = activity;
        cardViewModelCompany = new ViewModelProvider((ViewModelStoreOwner) activity).get(CardViewModelCompany.class);
    }

    @NonNull
    @Override
    public ViewHolderCompany onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.itemview, parent, false);
        return new ViewHolderCompany(view, mClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderCompany holder, int position) {
        CardItemCompany currentCardItemCompany = itemsList.get(position);
        if((removeIfOld(currentCardItemCompany) == false)) {
            holder.getTitleTextView().setText(currentCardItemCompany.getTitle());
            holder.getDestArrTextView().setText("Origin: " + currentCardItemCompany.getOriginLocality() +
                    ", Destination: " + currentCardItemCompany.getDestinationLocality());
            holder.getDateTextView().setText("Departure time: " + currentCardItemCompany.getDate());
            holder.getQuantityProductTv().setText("Product: " + currentCardItemCompany.getProductType() + ", Quantity: " + currentCardItemCompany.getQuantityKg());
            Resources res = activity.getResources();
            Drawable drawable = ResourcesCompat.getDrawable(res, currentCardItemCompany.getImgResource(), null);
            holder.getImgView().setImageDrawable(drawable);
        }
    }

    private boolean removeIfOld(CardItemCompany currentCardItemCompany){
        boolean removed = false;
        try {
            String dateString = currentCardItemCompany.getDate().split(",")[0];
            String timeString = currentCardItemCompany.getDate().split(" ")[1];
            if (new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateString + " " + timeString).before(new Date())) {
                Log.d("tag", "past " + currentCardItemCompany.getTitle());
                cardViewModelCompany.removeCardItem(currentCardItemCompany.getId());
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
    public CardItemCompany getItem(int id) {
        return itemsList.get(id);
    }

    public void setData(List<CardItemCompany> items){
        this.itemsList = items;
        notifyDataSetChanged();
    }
}
