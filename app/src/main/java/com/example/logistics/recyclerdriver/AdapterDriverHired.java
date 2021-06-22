package com.example.logistics.recyclerdriver;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.logistics.R;
import com.example.logistics.Utilities;
import com.example.logistics.recyclercompany.ItemClickListener;
import com.example.logistics.viewmodel.HiredViewModel;
import com.example.logistics.viewmodel.NotHiredViewModel;

import java.util.ArrayList;
import java.util.List;

public class AdapterDriverHired extends RecyclerView.Adapter<ViewHolderDriver> {

    private LayoutInflater inflater;
    private ItemClickListener clickListener;
    private Activity activity;
    private HiredViewModel hiredViewModel;

    private List<CardItemDriver> itemsList = new ArrayList<>();

    public AdapterDriverHired(Activity activity, Context context){
        this.inflater = LayoutInflater.from(context);
        this.activity = activity;
        hiredViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(HiredViewModel.class);
    }

    @NonNull
    @Override
    public ViewHolderDriver onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.itemview_driver, parent, false);
        return new ViewHolderDriver(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDriver holder, int position) {
        CardItemDriver currentCardItem = itemsList.get(position);
        holder.getTitleTextView().setText(currentCardItem.getDriverName());
        holder.getTruckCapacity().setText("Truck capacity: " + currentCardItem.getCapacity());
        String timeStart = currentCardItem.getTimeWork().split("_")[0];
        String timeFinish = currentCardItem.getTimeWork().split("_")[1];
        holder.getWorkingHoursTextView().setText("Start of work: " + timeStart + ", End of work: " +
                timeFinish);
        String image_path = currentCardItem.getImgResource();
        if(image_path.matches("profile")){
            holder.getImgView().setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.profile));
        }
        else{
            Bitmap bitmap = Utilities.getImageBitmap(activity, Uri.parse(image_path));
            holder.getImgView().setImageBitmap(bitmap);
        }
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    // convenience method for getting data at click position
    public CardItemDriver getItem(int id) {
        return itemsList.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public void setData(List<CardItemDriver> items){
        this.itemsList = items;
        notifyDataSetChanged();
    }
}
