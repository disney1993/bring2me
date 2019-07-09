package com.example.milymozz.orderfoodservice.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.milymozz.orderfoodservice.R;


public class OrderViewHolder extends RecyclerView.ViewHolder {

    public TextView txtOrderId, txtOrderDate, txtOrderStatus, txtOrderPhone, txtOrderAddress;

    public Button btnEdit, btnRemove, btnDetail, btnDirection;


    public OrderViewHolder(View itemView) {
        super(itemView);

        txtOrderId = (TextView) itemView.findViewById(R.id.order_id);
        txtOrderDate = (TextView) itemView.findViewById(R.id.order_date);
        txtOrderPhone = (TextView) itemView.findViewById(R.id.order_phone);
        txtOrderStatus = (TextView) itemView.findViewById(R.id.order_status);
        txtOrderAddress = (TextView) itemView.findViewById(R.id.order_address);

        btnEdit = (Button) itemView.findViewById(R.id.btnEdit);
        btnRemove = (Button) itemView.findViewById(R.id.btnRemove);
        btnDetail = (Button) itemView.findViewById(R.id.btnDetail);
        btnDirection = (Button) itemView.findViewById(R.id.btnDirection);
    }
}
