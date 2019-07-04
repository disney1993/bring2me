package com.example.b2m.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.b2m.Common.Common;
import com.example.b2m.Database.Database;
import com.example.b2m.FoodDetail;
import com.example.b2m.Interface.ItemClickListener;
import com.example.b2m.Model.Favorites;
import com.example.b2m.Model.Order;
import com.example.b2m.R;
import com.squareup.picasso.Picasso;

import java.util.List;


public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesViewHolder> {
    private Context context;
    private List<Favorites> favoritesList;

    public FavoritesAdapter(Context context, List<Favorites> favoritesList) {
        this.context = context;
        this.favoritesList = favoritesList;
    }

    @Override
    public FavoritesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.favorites_item, parent, false);

        return new FavoritesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FavoritesViewHolder viewHolder, final int position) {
        viewHolder.food_name.setText(favoritesList.get(position).getFoodName());
        viewHolder.food_price.setText(String.format("$ %s", favoritesList.get(position).getFoodPrice()));
        Picasso.with(context)
                .load(favoritesList.get(position).getFoodImage())
                .into(viewHolder.food_image);


        // carrito rapido
        viewHolder.quickCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //carrito rapido
                boolean isExists = new Database(context).checkFoodExists(favoritesList.get(position).getFoodId(), Common.currentUser.getPhone());

                if (!isExists) {
                    new Database(context).addToCart(new Order(
                            Common.currentUser.getPhone(),
                            favoritesList.get(position).getFoodId(),
                            favoritesList.get(position).getFoodName(),
                            "1",
                            favoritesList.get(position).getFoodPrice(),
                            favoritesList.get(position).getFoodDiscount(),
                            favoritesList.get(position).getFoodImage()
                    ));

                } else {
                    new Database(context).increaseCart(Common.currentUser.getPhone(),
                            favoritesList.get(position).getFoodId());
                }
                Toast.makeText(context, "Agregado al carrito", Toast.LENGTH_SHORT).show();
            }
        });

        final Favorites local = favoritesList.get(position);
        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                //Start new Activity
                //Get CategoryId and send to new Activity
                Intent foodDetailIntent = new Intent(context, FoodDetail.class);
                foodDetailIntent.putExtra("FoodId", favoritesList.get(position).getFoodId());
                context.startActivity(foodDetailIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

    public Favorites getItem(int position) {
        return favoritesList.get(position);
    }

    public void removeItem(int position) {
        favoritesList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Favorites item, int position) {
        favoritesList.add(position, item);
        notifyItemInserted(position);
    }
}
