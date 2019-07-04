package com.example.b2m.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.b2m.Cart;
import com.example.b2m.Common.Common;
import com.example.b2m.Database.Database;
import com.example.b2m.Interface.ItemClickListener;
import com.example.b2m.Model.Order;
import com.example.b2m.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {
    private List<Order> listData = new ArrayList<>();
    private Cart cart;

    public CartAdapter(List<Order> listData, Cart cart) {
        this.listData = listData;
        this.cart = cart;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(cart);
        View itemView = inflater.inflate(R.layout.cart_layout, parent, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, final int position) {
        // TextDrawable drawable = TextDrawable.builder()
        //       .buildRound("" + ListData.get(position).getQuantity(), Color.RED);
        // holder.img_cart_count.setImageDrawable(drawable);

        Picasso.with(cart.getBaseContext())
                .load(listData.get(position).getImage())
                .resize(70, 70)
                .centerCrop()
                .into(holder.cart_image);
        holder.btn_quantity.setNumber(listData.get(position).getQuantity());
        holder.btn_quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Order order = listData.get(position);
                order.setQuantity(String.valueOf(newValue));
                new Database(cart).updateCart(order);

                //actualizar el txt total
                //Calcular precio total
                float total = 0;
                List<Order> orders = new Database(cart).getCarts(Common.currentUser.getPhone());
                for (Order item : orders) {
                    total += (Float.parseFloat(order.getPrice())) * (Float.parseFloat(item.getQuantity()));
                }
                Locale locale = new Locale("es", "EC");
                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

                cart.txtTotalPrice.setText(fmt.format(total));


            }
        });

        Locale locale = new Locale("es", "EC");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        float price = (Float.parseFloat(listData.get(position).getPrice())) * (Float.parseFloat(listData.get(position).getQuantity()));
        holder.txt_cart_price.setText(fmt.format(price));
        holder.txt_cart_name.setText(listData.get(position).getProductName());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public Order getItem(int position) {
        return listData.get(position);
    }

    public void removeItem(int position) {
        listData.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Order item, int position) {
        listData.add(position, item);
        notifyItemInserted(position);
    }
}
