package com.example.milymozz.orderfoodservice.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.milymozz.orderfoodservice.Interface.ItemClickListener;
import com.example.milymozz.orderfoodservice.R;

import info.hoang8f.widget.FButton;

/**
 * Created by milymozz on 2018. 4. 6..
 */

public class ShipperViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView shipperName, shipperPhone;
    public FButton btnEdit, btnRemove;
    private ItemClickListener itemClickListener;

    public ShipperViewHolder(View itemView) {
        super(itemView);

        shipperName = (TextView) itemView.findViewById(R.id.shipper_name);
        shipperPhone = (TextView) itemView.findViewById(R.id.shipper_phone);
        btnEdit = (FButton) itemView.findViewById(R.id.btnEdit);
        btnRemove = (FButton) itemView.findViewById(R.id.btnRemove);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

}
