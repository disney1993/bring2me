package com.example.milymozz.orderfoodservice.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.milymozz.orderfoodservice.Common.Common;
import com.example.milymozz.orderfoodservice.R;

/**
 * Created by milymozz on 2018. 3. 8..
 */

public class BannerViewHolder extends RecyclerView.ViewHolder implements
        View.OnCreateContextMenuListener {

    public TextView bannerName;
    public ImageView bannerImage;


    public BannerViewHolder(View itemView) {
        super(itemView);

        bannerName = (TextView) itemView.findViewById(R.id.banner_name);
        bannerImage = (ImageView) itemView.findViewById(R.id.banner_image);

        itemView.setOnCreateContextMenuListener(this);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select the action");

        menu.add(0, 0, getAdapterPosition(), Common.UPDATE);
        menu.add(0, 1, getAdapterPosition(), Common.DELETE);

    }
}
