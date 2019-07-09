package com.example.milymozz.orderfoodservice.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.milymozz.orderfoodservice.Model.Order;
import com.example.milymozz.orderfoodservice.R;

import java.util.List;

/**
 * Created by milymozz on 2018. 2. 14..
 */

class MyViewHolder extends RecyclerView.ViewHolder {
    TextView name, quantity, price, discount;

    MyViewHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.product_name);
        quantity = (TextView) itemView.findViewById(R.id.product_quantity);
        price = (TextView) itemView.findViewById(R.id.product_price);
        discount = (TextView) itemView.findViewById(R.id.product_discount);

    }
}

public class OrderDetailAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private List<Order> myOrder;

    public OrderDetailAdapter(List<Order> myOrder) {
        this.myOrder = myOrder;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.order_detail_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Order order = myOrder.get(position);
        holder.name.setText(String.format("Name : %s", order.getProductName()));
        holder.quantity.setText(String.format("Quantity : %s", order.getQuantity()));
        holder.price.setText(String.format("Price : %s", order.getPrice()));
        holder.discount.setText(String.format("Discount : %s", order.getDiscount()));

    }

    @Override
    public int getItemCount() {
        return myOrder.size();
    }
}

