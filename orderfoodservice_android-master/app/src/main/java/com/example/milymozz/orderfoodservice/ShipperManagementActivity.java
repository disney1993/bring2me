package com.example.milymozz.orderfoodservice;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.milymozz.orderfoodservice.Common.Common;
import com.example.milymozz.orderfoodservice.Model.Shipper;
import com.example.milymozz.orderfoodservice.ViewHolder.ShipperViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

public class ShipperManagementActivity extends AppCompatActivity {
    private FloatingActionButton fabAdd;

    private FirebaseDatabase database;
    private DatabaseReference shipperReference;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseRecyclerAdapter<Shipper, ShipperViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_management);

        fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateShipperLayout();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Firebase init
        database = FirebaseDatabase.getInstance();
        shipperReference = database.getReference(Common.SHIPPER_TABLE);

        // Load all Shippers
        loadAllShippers();

    }

    private void loadAllShippers() {
        FirebaseRecyclerOptions<Shipper> ShipperOption = new FirebaseRecyclerOptions.Builder<Shipper>()
                .setQuery(shipperReference, Shipper.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Shipper, ShipperViewHolder>(ShipperOption) {
            @Override
            protected void onBindViewHolder(@NonNull ShipperViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final Shipper model) {
                holder.shipperName.setText(model.getName());
                holder.shipperPhone.setText(model.getPhone());

                holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showEditDialog(adapter.getRef(position).getKey(), model);
                    }
                });

                holder.btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removeShipper(adapter.getRef(position).getKey());
                    }
                });

            }

            @Override
            public ShipperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.shipper_layout, parent, false);

                return new ShipperViewHolder(itemView);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

    private void showEditDialog(String key, final Shipper model) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ShipperManagementActivity.this);
        alertDialog.setTitle("Update Shipper");

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.shipper_layout_sign_in, null);

        final MaterialEditText edtName = (MaterialEditText) view.findViewById(R.id.edtName);
        final MaterialEditText edtPhone = (MaterialEditText) view.findViewById(R.id.edtPhone);
        final MaterialEditText edtPassword = (MaterialEditText) view.findViewById(R.id.edtPassword);

        // Set Data
        edtName.setText(model.getName());
        edtPhone.setText(model.getPhone());
        edtPassword.setText(model.getPassword());

        alertDialog.setView(view);
        alertDialog.setIcon(R.drawable.ic_local_shipping_black_24dp);

        alertDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();

                Map<String, Object> update = new HashMap<>();
                update.put("name", edtName.getText().toString());
                update.put("phone", edtPhone.getText().toString());
                update.put("password", edtPassword.getText().toString());


                shipperReference.child(edtPhone.getText().toString())
                        .updateChildren(update)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ShipperManagementActivity.this, "Shipper Update!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ShipperManagementActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });

    }

    private void removeShipper(String key) {
        shipperReference.child(key)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ShipperManagementActivity.this, "Remove Succeed!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ShipperManagementActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showCreateShipperLayout() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ShipperManagementActivity.this);
        alertDialog.setTitle("Create Shipper");

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.shipper_layout_sign_in, null);

        final MaterialEditText edtName = (MaterialEditText) view.findViewById(R.id.edtName);
        final MaterialEditText edtPhone = (MaterialEditText) view.findViewById(R.id.edtPhone);
        final MaterialEditText edtPassword = (MaterialEditText) view.findViewById(R.id.edtPassword);

        alertDialog.setView(view);
        alertDialog.setIcon(R.drawable.ic_local_shipping_black_24dp);

        alertDialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();

                Shipper shipper = new Shipper();
                shipper.setName(edtName.getText().toString());
                shipper.setPhone(edtPhone.getText().toString());
                shipper.setPassword(edtPassword.getText().toString());

                shipperReference.child(edtPhone.getText().toString()).setValue(shipper).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ShipperManagementActivity.this, "Shipper Create!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ShipperManagementActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });

    }

}
