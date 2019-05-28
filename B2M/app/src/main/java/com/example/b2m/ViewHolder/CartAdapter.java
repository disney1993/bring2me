package com.example.b2m.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.b2m.Common.Common;
import com.example.b2m.Interface.ItemClickListener;
import com.example.b2m.Model.Order;
import com.example.b2m.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        View.OnCreateContextMenuListener {

    public TextView txt_cart__name, txt_price;
    public ImageView img_cart_count;

    private ItemClickListener itemClickListener;

    public void setTxt_cart__name(TextView txt_cart__name) {
        this.txt_cart__name = txt_cart__name;
    }

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        txt_cart__name = (TextView) itemView.findViewById(R.id.cart_item_name);
        txt_price = (TextView) itemView.findViewById(R.id.cart_item_price);
        img_cart_count = (ImageView) itemView.findViewById(R.id.cart_item_count);

        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextmenu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        contextmenu.setHeaderTitle("Seleccionar acción");
        contextmenu.add(0, 0, getAdapterPosition(), Common.DELETE);

    }
}

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {
    private List<Order> ListData = new ArrayList<>();
    private Context context;

    public CartAdapter(List<Order> listData, Context context) {
        ListData = listData;
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.cart_layout, parent, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        TextDrawable drawable = TextDrawable.builder()
                .buildRound("" + ListData.get(position).getQuantity(), Color.RED);
        holder.img_cart_count.setImageDrawable(drawable);
        Locale locale = new Locale("es", "ES");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        float price = (Float.parseFloat(ListData.get(position).getPrice())) * (Float.parseFloat(ListData.get(position).getQuantity()));
        holder.txt_price.setText(fmt.format(price));
        holder.txt_cart__name.setText(ListData.get(position).getProductName());
    }

    @Override
    public int getItemCount() {
        return ListData.size();
    }
}
