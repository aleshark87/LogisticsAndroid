package com.example.logistics.recycler;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.logistics.R;

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView myTextView;
    private ImageView myImageView;
    private ItemClickListener mClickListener;

    ViewHolder(View itemView, ItemClickListener listener) {
        super(itemView);
        myTextView = itemView.findViewById(R.id.cardTV);
        myImageView = itemView.findViewById(R.id.cardIMG);
        itemView.setOnClickListener(this);
        mClickListener = listener;
    }

    @Override
    public void onClick(View view) {
        if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
    }

    public TextView getMyTextView(){
        return this.myTextView;
    }

    public ImageView getMyImageView() { return  this.myImageView; }
}