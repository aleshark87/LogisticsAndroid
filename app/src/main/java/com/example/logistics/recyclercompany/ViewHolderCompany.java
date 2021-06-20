package com.example.logistics.recyclercompany;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.logistics.R;

public class ViewHolderCompany extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView titleTextView;
    private TextView destArrTextView;
    private TextView dateTextView;
    private TextView quantityProductTv;
    private ImageView imgView;
    private ItemClickListener mClickListener;

    ViewHolderCompany(View itemView, ItemClickListener listener) {
        super(itemView);
        titleTextView = itemView.findViewById(R.id.cardTitle);
        destArrTextView = itemView.findViewById(R.id.cardDepArr);
        imgView = itemView.findViewById(R.id.mapImageView);
        dateTextView = itemView.findViewById(R.id.cardDate);
        quantityProductTv = itemView.findViewById(R.id.productQuantityTVcard);
        itemView.setOnClickListener(this);
        mClickListener = listener;
    }

    @Override
    public void onClick(View view) {
        if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
    }

    public TextView getTitleTextView(){
        return this.titleTextView;
    }

    public TextView getDestArrTextView() { return this.destArrTextView; }

    public ImageView getImgView() { return this.imgView; }

    public TextView getDateTextView() { return this.dateTextView; }

    public TextView getQuantityProductTv(){ return this.quantityProductTv; }
}