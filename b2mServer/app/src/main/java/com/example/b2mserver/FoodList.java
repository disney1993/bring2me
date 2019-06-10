package com.example.b2mserver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.b2mserver.Common.Common;
import com.example.b2mserver.Interface.ItemClickListener;
import com.example.b2mserver.Model.Food;
import com.example.b2mserver.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import info.hoang8f.widget.FButton;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    RelativeLayout rootLayout;

    FloatingActionButton fab;

    //Firebase
    FirebaseDatabase db;
    DatabaseReference foodList;
    FirebaseStorage storage;
    StorageReference storageReference;

    String categoryId = "";
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    //agregar nuevo producto
    EditText etName, etDescription, etPrice, etDiscount;
    FButton btnSelect, btnUpload;

    Food newFood;

    Uri saveUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        //firebase
        db = FirebaseDatabase.getInstance();
        foodList = db.getReference("Foods");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        //inicializar
        recyclerView = (RecyclerView) findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFoodDialog();
            }
        });
        if (getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");
        if (!categoryId.isEmpty())
            loadListFood(categoryId);
    }

    private void showAddFoodDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodList.this);
        alertDialog.setTitle("Agregar nuevo producto");
        alertDialog.setMessage("Por favor, completa toda la información");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_food_layout, null);

        etName = add_menu_layout.findViewById(R.id.etName);
        etDescription = add_menu_layout.findViewById(R.id.etDescription);
        etPrice = add_menu_layout.findViewById(R.id.etPrice);
        etDiscount = add_menu_layout.findViewById(R.id.etDiscount);

        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUpload);

        //evento para el boton
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();//imagen de la galery y guardar la url
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        //configurar boton
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                //crear la nueva categoria
                if (newFood != null) {
                    foodList.push().setValue(newFood);
                    Snackbar.make(rootLayout, "Nueva cateoría " + newFood.getName() + " fue agregada", Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    private void uploadImage() {
        if (saveUri != null) {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Subiendo...");
            mDialog.show();

            final String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                  @Override
                                                                  public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                      mDialog.dismiss();
                                                                      Toast.makeText(FoodList.this, "Archivo subido!", Toast.LENGTH_SHORT).show();
                                                                      imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                          @Override
                                                                          public void onSuccess(Uri uri) {
                                                                              //si la imagen se subio y podemos obtener el link entonces enviamos la info de la categor'ia
                                                                              newFood = new Food();
                                                                              newFood.setName(etName.getText().toString());
                                                                              newFood.setDescription(etDescription.getText().toString());
                                                                              newFood.setPrice(etPrice.getText().toString());
                                                                              newFood.setDiscount(etDiscount.getText().toString());
                                                                              newFood.setMenuId(categoryId);
                                                                              newFood.setImage(uri.toString());

                                                                          }
                                                                      });
                                                                  }
                                                              }
            )
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(FoodList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Subido " + progress + "%");
                        }
                    });

        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Elegir Imagen"), Common.PICK_IMAGE_REQUEST);
    }

    private void loadListFood(String categoryId) {
        Query listFoodByCategoryId = foodList.orderByChild("menuId").equalTo(categoryId);
        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(listFoodByCategoryId, Food.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder viewHolder, int position, @NonNull Food model) {
                viewHolder.food_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.food_image);
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //despues 2
                    }
                });
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(ViewGroup parent, int i) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item, parent, false);
                return new FoodViewHolder(itemView);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            saveUri = data.getData();
            btnSelect.setText("Imagen Seleccionada!");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)) {
            showUpdateFoodDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        } else if (item.getTitle().equals(Common.DELETE)) {

            deleteFood(adapter.getRef(item.getOrder()).getKey());
        }

        return super.onContextItemSelected(item);

    }

    private void deleteFood(String key) {
        foodList.child(key).removeValue();
    }

    private void showUpdateFoodDialog(final String key, final Food item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodList.this);
        alertDialog.setTitle("Editar producto");
        alertDialog.setMessage("Por favor, completa toda la información");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_food_layout, null);

        etName = add_menu_layout.findViewById(R.id.etName);
        etDescription = add_menu_layout.findViewById(R.id.etDescription);
        etPrice = add_menu_layout.findViewById(R.id.etPrice);
        etDiscount = add_menu_layout.findViewById(R.id.etDiscount);

        //configurar valores por defecto en la vista
        etName.setText(item.getName());
        etDescription.setText(item.getDescription());
        etPrice.setText(item.getPrice());
        etDiscount.setText(item.getDiscount());


        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUpload);

        //evento para el boton
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();//imagen de la galery y guardar la url
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage(item);
            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        //configurar boton
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                //actualizar informacion
                item.setName(etName.getText().toString());
                item.setDescription(etDescription.getText().toString());
                item.setPrice(etPrice.getText().toString());
                item.setDiscount(etDiscount.getText().toString());

                foodList.child(key).setValue(item);
                Snackbar.make(rootLayout, " El producto " + item.getName() + " fue editado", Snackbar.LENGTH_SHORT)
                        .show();
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    private void changeImage(final Food item) {
        if (saveUri != null) {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Subiendo...");
            mDialog.show();

            final String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDialog.dismiss();
                    Toast.makeText(FoodList.this, "Archivo subido!", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //si la imagen se subio y podemos obtener el link entonces enviamos la info de la categor'ia
                            item.setImage(uri.toString());
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(FoodList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Subido " + progress + "%");
                        }
                    });
        }
    }

}
