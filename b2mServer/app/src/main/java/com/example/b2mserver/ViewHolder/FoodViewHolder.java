package com.example.b2mserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.b2mserver.Common.Common;
import com.example.b2mserver.Interface.ItemClickListener;
import com.example.b2mserver.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener,
        View.OnCreateContextMenuListener {
    public TextView food_name;
    public ImageView food_image;
    private ItemClickListener itemClickListener;

    public FoodViewHolder(View itemView) {
        super(itemView);
        food_name = (TextView) itemView.findViewById(R.id.food_name);
        food_image = (ImageView) itemView.findViewById(R.id.food_image);
        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        contextMenu.setHeaderTitle("Seleccione una acción:");
        contextMenu.add(0, 0, getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0, 1, getAdapterPosition(), Common.DELETE);

    }
}
