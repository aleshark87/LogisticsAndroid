package com.example.logistics.recycler;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.logistics.R;

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView myTextView;
    private ItemClickListener mClickListener;

    ViewHolder(View itemView, ItemClickListener listener) {
        super(itemView);
        myTextView = itemView.findViewById(R.id.itemTV);
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
}