package com.example.b2m;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.b2m.Common.Common;
import com.example.b2m.Common.Config;
import com.example.b2m.Database.Database;
import com.example.b2m.Helper.RecyclerItemTouchHelper;
import com.example.b2m.Interface.RecyclerItemTouchHelperListener;
import com.example.b2m.Model.DataMessage;
import com.example.b2m.Model.MyResponse;
import com.example.b2m.Model.Notification;
import com.example.b2m.Model.Order;
import com.example.b2m.Model.Request;
import com.example.b2m.Model.Sender;
import com.example.b2m.Model.Token;
import com.example.b2m.Model.User;
import com.example.b2m.Remote.APIService;
import com.example.b2m.Remote.IGoogleService;
import com.example.b2m.ViewHolder.CartAdapter;
import com.example.b2m.ViewHolder.CartViewHolder;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Cart extends AppCompatActivity implements LocationListener, RecyclerItemTouchHelperListener {
    private static final int PAYPAL_REQUEST_CODE = 9999;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    public TextView txtTotalPrice;
    FButton btnRealizar;

    List<Order> cart = new ArrayList<>();

    CartAdapter adapter;
    // APIService mService;

    PlacesClient placesClient;
    Place shippingAddress;

    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
            Place.Field.NAME,
            Place.Field.LAT_LNG,
            Place.Field.ADDRESS);
    AutocompleteSupportFragment edtAddress;

    //pago con PAYPAL
    static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .acceptCreditCards(true)
            .clientId(Config.PAYPAL_CLIENT_ID);
    String address, comment;

    //obtener ubicacion del telefono
    private LocationRequest mLocationRequest;
    //  private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private static final int UPDATE_INTERVAL = 5000;
    private static final int FATEST_INTERVAL = 3000;
    private static final int DISPLACEMENT = 10;
    private static final int LOCATION_REQUEST_CODE = 9999;

    //declarar google map api retrofit
    IGoogleService mGoogleMapService;
    APIService mService;

    RelativeLayout rootLayout;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);

        //para el estilo de la fuente siempre agregar antes del setContentView
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/cf.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_cart);

        //inicializar
        mGoogleMapService = Common.getGoogleMapAPI();
        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        //permisos para ejecutar la ubicacion
        /*if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]
                    {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }, LOCATION_REQUEST_CODE);
        } else
        {
            if (checkPlayServices())//si hay play services en el telefono
            {
                buildGoogleApiClient();
                createLocationRequest();
            }
        }*/
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, LOCATION_REQUEST_CODE);
        } else {

            fetchLastLocation();
            createLocationRequest();

        }


        //inicializar PAYPAL
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        //inicializar service
        mService = Common.getFCMService();


        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");
        //Inicializar
        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //Swipe para eliminar
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        txtTotalPrice = findViewById(R.id.total);
        btnRealizar = findViewById(R.id.btnPlaceOrder);

        btnRealizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cart.size() > 0)
                    showAlertDialog();
                else {
                    Toast.makeText(Cart.this, "Carrito vacío!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadListFood();
    }


    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }, LOCATION_REQUEST_CODE);
        } else {
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        mLastLocation = location;
                        Toast.makeText(Cart.this, mLastLocation.getLatitude() + " " + mLastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                        Log.e("location: ", mLastLocation.getLatitude() + "," + mLastLocation.getLongitude());

                    } else {
                        Toast.makeText(Cart.this, "Revisa que tu GPS esté activado", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

    }


    /*private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }*/

    /*private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_REQUEST).show();
            else {
                Toast.makeText(this, "Dispositivo no sportado", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }
*/
    private void showAlertDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("El último paso!");
        alertDialog.setMessage("Ingresa tu dirección: ");
        //layout para poner el moentario y la direccion
        LayoutInflater inflater = this.getLayoutInflater();
        View order_address_comment = inflater.inflate(R.layout.order_address_comment, null);

        final AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        initPlaces();
        setupPlaceAutocomplete();

        final MaterialEditText etComment = order_address_comment.findViewById(R.id.etComment);
       /* autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                shippingAddress = place;
            }
            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(Cart.this, "" + status.getStatusMessage(), Toast.LENGTH_LONG).show();
            }
        });*/


        //radios
        final RadioButton rdiShipToAddress = order_address_comment.findViewById(R.id.rdiShipToAddress);
        rdiShipToAddress.setVisibility(View.INVISIBLE);
        final RadioButton rdiHomeAddress = order_address_comment.findViewById(R.id.rdiHomeAddress);

        final RadioButton rdiCOD = order_address_comment.findViewById(R.id.rdiCOD);
        final RadioButton rdiPaypal = order_address_comment.findViewById(R.id.rdiPaypal);
        final RadioButton rdiBalance = order_address_comment.findViewById(R.id.rdiBring2MeBalance);
        rdiBalance.setVisibility(View.INVISIBLE);

        //eventos radios

        rdiHomeAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    if (Common.currentUser.getHomeAddress() != null || !TextUtils.isEmpty(Common.currentUser.getHomeAddress())) {
                        address = Common.currentUser.getHomeAddress();
                        ((EditText) autocompleteSupportFragment.getView().findViewById(R.id.places_autocomplete_search_input))
                                .setText(address);
                    } else {
                        Toast.makeText(Cart.this, "Por favor, actualiza tu dirección", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        rdiShipToAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mGoogleMapService.getAddressName(String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&sensor=false",
                            mLastLocation.getLatitude(),
                            mLastLocation.getLongitude()))
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    //If fetch API ok
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.body().toString());
                                        JSONArray resultArray = jsonObject.getJSONArray("results");
                                        JSONObject firstObject = resultArray.getJSONObject(0);
                                        address = firstObject.getString("formatted_address");

                                        //Set this address to edtAddress
                                        ((EditText) edtAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                                                .setText(address);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Toast.makeText(Cart.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        alertDialog.setView(order_address_comment);

        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        alertDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //condicion si el usuario seleciona la direccion del fragmentplace, usar ese
                //si seleccion el envio a esta direccion ,,obtener la direccion y usar esa
                //si seleccion mi casa , obtener la direccion del perfily usar esa
                if (!rdiShipToAddress.isChecked() && !rdiHomeAddress.isChecked()) {
                    //si los dos radiobtn no estan selecionados entonces..
                    if (shippingAddress != null)

                        //address = shippingAddress.getAddress().toString();
                        address = shippingAddress.getAddress();
                        //mostrar PAYPAL para el pago
                        //primero obtener la direccion y el comentario del alertdialog
                    else {
                        Toast.makeText(Cart.this, "Por favor, ingresa una dirección o selecciona una opción", Toast.LENGTH_SHORT).show();
                        getSupportFragmentManager().beginTransaction()
                                .remove(getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                                .commit();
                        return;
                    }
                }
                if (TextUtils.isEmpty(address)) {
                    Toast.makeText(Cart.this, "Por favor, ingresa una dirección o selecciona una opción", Toast.LENGTH_SHORT).show();
                    getSupportFragmentManager().beginTransaction()
                            .remove(getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                            .commit();
                    return;
                }
                comment = etComment.getText().toString();
                //ver opcion de pago
                if (!rdiCOD.isChecked() && !rdiPaypal.isChecked() && !rdiBalance.isChecked())//si ambos estan desactivados
                //if (!rdiCOD.isChecked() && !rdiPaypal.isChecked())//si ambos estan desactivados

                {
                    Toast.makeText(Cart.this, "Por favor, selecciona una opción de pago", Toast.LENGTH_SHORT).show();
                    getSupportFragmentManager().beginTransaction()
                            .remove(getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                            .commit();
                    return;
                } else if (rdiPaypal.isChecked()) {
                    String formatearTotal = txtTotalPrice.getText().toString()
                            .replace("$", "")
                            .replace(",", ".");
                    PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(formatearTotal),
                            "USD",
                            "Pedido Bring2Me",
                            PayPalPayment.PAYMENT_INTENT_SALE);
                    Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                    intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                    intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
                    startActivityForResult(intent, PAYPAL_REQUEST_CODE);

                } else if (rdiCOD.isChecked()) {
                    //creamos el nuevo request
                    Request request = new Request(
                            Common.currentUser.getPhone(),
                            Common.currentUser.getName(),
                            address,
                            txtTotalPrice.getText().toString(),
                            "0",//stats por defecto
                            comment,
                            "COD",
                            "No Pagado",//el estado del json
                            String.format("%s,%s", mLastLocation.getLatitude(), mLastLocation.getLongitude()),
                            cart
                    );
                    //enviar a firebase el pedido
                    //usaremos el System.CurrentTimemillis para teclear

                    String order_number = String.valueOf(System.currentTimeMillis());
                    requests.child(order_number)
                            .setValue(request);
                    //eliminar carrito
                    new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());
                    sendNotificationOrder(order_number);
                    Toast.makeText(Cart.this, "Gracias, pedido realizado", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (rdiBalance.isChecked()) {
                    double amount = 0;
                    try {
                        amount = Common.formatCurrency(txtTotalPrice.getText().toString(), Locale.US).doubleValue();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (Double.parseDouble(Common.currentUser.getBalance().toString()) >= amount) {

                        //creamos el nuevo request
                        Request request = new Request(
                                Common.currentUser.getPhone(),
                                Common.currentUser.getName(),
                                address,
                                txtTotalPrice.getText().toString(),
                                "0",//stats por defecto
                                etComment.getText().toString(),
                                "Balance",
                                "Pagado", String.format("%s,%s", shippingAddress.getLatLng().latitude, shippingAddress.getLatLng().longitude),
                                cart);

                        final String order_number = String.valueOf(System.currentTimeMillis());
                        requests.child(order_number)
                                .setValue(request);
                        new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());

                        double balance = Double.parseDouble(Common.currentUser.getBalance().toString()) - amount;
                        Map<String, Object> update_balance = new HashMap<>();
                        update_balance.put("balance", balance);

                        FirebaseDatabase.getInstance()
                                .getReference("Users")
                                .child(Common.currentUser.getPhone())
                                .updateChildren(update_balance)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            //Refresh User
                                            FirebaseDatabase.getInstance()
                                                    .getReference("Users")
                                                    .child(Common.currentUser.getPhone())
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Common.currentUser = dataSnapshot.getValue(User.class);
                                                            //Send Order to Server
                                                            sendNotification(order_number);

                                                            //Delete cart
                                                            new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());
                                                            finish();
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
                                        }
                                    }
                                });

                    } else {
                        Toast.makeText(Cart.this, "saldo insuficiente", Toast.LENGTH_SHORT).show();
                    }

                }

                //quitar el fragment
                getSupportFragmentManager().beginTransaction()
                        .remove(getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                        .commit();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                //quitar el fragment
                getSupportFragmentManager().beginTransaction()
                        .remove(getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                        .commit();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createLocationRequest();
                    fetchLastLocation();
                } else {
                    Toast.makeText(Cart.this, "Falta aceptar premisos de ubicación", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private void sendNotification(final String order_number) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = tokens.orderByChild("serverToken").equalTo(true); // get all node with isServerToken is true
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Token serverToken = null;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    serverToken = postSnapshot.getValue(Token.class);
                }

                //Create raw payload to send
//                    Notification notification = new Notification("MOZZ DEV", "You have new order" + order_number);
//                    Sender content = new Sender(serverToken.getToken(), notification);

                Map<String, String> dataSend = new HashMap<>();
                dataSend.put("title", "MozzDev");
                dataSend.put("message", "주문이 들어왔어요! " + order_number);
                DataMessage dataMessage = new DataMessage(serverToken != null ? serverToken.getToken() : null, dataSend);

                String test = new Gson().toJson(dataMessage);
                Log.d("Content", test);

                mService.sendNotification(dataMessage)
                        .enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                //Only run when get result
                                if (response.code() == 200) {
                                    if (response.body().success == 1) {

                                    } else {
                                        Toast.makeText(Cart.this, "Failed !!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {
                                Log.e("ERROR", t.getMessage());
                            }
                        });
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setupPlaceAutocomplete() {
        edtAddress = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        //oculatr icono de busqueda
        edtAddress.getView().findViewById(R.id.places_autocomplete_search_button).setVisibility(View.GONE);
        //poner un hint para el autocomplete edittext
        ((EditText) edtAddress.getView().findViewById(R.id.places_autocomplete_search_input)).setHint("Ingresa tu dirección");
        //configurar el tama;o del texto
        ((EditText) edtAddress.getView().findViewById(R.id.places_autocomplete_search_input)).setTextSize(14);

        edtAddress.setPlaceFields(placeFields);
        edtAddress.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                shippingAddress = place;
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(Cart.this, "" + status.getStatusMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    private void initPlaces() {
//copia tu apli de strings
        Places.initialize(this, getString(R.string.google_place_api));
        placesClient = Places.createClient(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        String paymentDetail = confirmation.toJSONObject().toString(4);
                        JSONObject jsonObject = new JSONObject(paymentDetail);
                        //creamos el nuevo request
                        Request request = new Request(
                                Common.currentUser.getPhone(),
                                Common.currentUser.getName(),
                                address,
                                txtTotalPrice.getText().toString(),
                                "0",//stats por defecto
                                comment,
                                "Paypal",
                                jsonObject.getJSONObject("response").getString("state"),//el estado del json
                                String.format("%s,%s", shippingAddress.getLatLng().latitude, shippingAddress.getLatLng().longitude),
                                cart
                        );
                        //enviar a firebase el pedido
                        //usaremos el System.CurrentTimemillis para teclear

                        String order_number = String.valueOf(System.currentTimeMillis());

                        requests.child(order_number)
                                .setValue(request);
                        //eliminar carrito
                        new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());


                        sendNotificationOrder(order_number);
                        //comentar las 2 lineas
                        Toast.makeText(Cart.this, "Gracias, Pedido realizado", Toast.LENGTH_SHORT).show();
                        finish();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED)
                Toast.makeText(this, "Pago Cancelado", Toast.LENGTH_SHORT).show();
            else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
                Toast.makeText(this, "Pago no válido", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendNotificationOrder(final String order_number) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = tokens.orderByChild("serverToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    Token serverToken = postSnapShot.getValue(Token.class);

                    Notification notification = new Notification("BRING2ME", "Nuevo pedido " + order_number);
                    Sender content = new Sender(serverToken.getToken(), notification);
                    mService.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success == 1) {
                                            Toast.makeText(Cart.this, "Gracias, Pedido realizado", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(Cart.this, "Falló, Pedido no realizado!!!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("ERROR", t.getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadListFood() {
        cart = new Database(this).getCarts(Common.currentUser.getPhone());
        adapter = new CartAdapter(cart, this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        //Calcular precio total
        float total = 0;
        for (Order order : cart) {
            total += (Float.parseFloat(order.getPrice())) * (Float.parseFloat(order.getQuantity()));
        }
        Locale locale = new Locale("es", "EC");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        txtTotalPrice.setText(fmt.format(total));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return true;

    }

    private void deleteCart(int position) {
        //eliminar producto de la lista List<Order> by position
        cart.remove(position);
        //despues eliminar los datos viejos de SQlite
        new Database(this).cleanCart(Common.currentUser.getPhone());
        //al final actualizar la lista desde List<order> al SQLite
        for (Order item : cart)
            new Database(this).addToCart(item);
        //actualizar la vista despues de eliminar un producto
        loadListFood();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        fetchLastLocation();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CartViewHolder) {
            String name = ((CartAdapter) recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getProductName();

            final Order deleteItem = ((CartAdapter) recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()); // item
            final int deleteIndex = viewHolder.getAdapterPosition(); // position

            adapter.removeItem(deleteIndex);
            new Database(getBaseContext()).removeFromCart(deleteItem.getProductId(), Common.currentUser.getPhone());

            //update
            //Update txtTotal
            //Calculate total price
            //actualizar el txt total
            //Calcular precio total
            float total = 0;
            List<Order> orders = new Database(getBaseContext()).getCarts(Common.currentUser.getPhone());
            for (Order item : orders) {
                total += (Float.parseFloat(item.getPrice())) * (Float.parseFloat(item.getQuantity()));
            }
            Locale locale = new Locale("es", "EC");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

            txtTotalPrice.setText(fmt.format(total));

            //SnackBar
            Snackbar snackbar = Snackbar.make(rootLayout, name + " eliminado del carrito!", Snackbar.LENGTH_LONG);
            snackbar.setAction("Deshacer", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deleteItem, deleteIndex);
                    new Database(getBaseContext()).addToCart(deleteItem);

                    //Calcular precio total
                    float total = 0;
                    List<Order> orders = new Database(getBaseContext()).getCarts(Common.currentUser.getPhone());
                    for (Order item : orders)
                        total += (Float.parseFloat(item.getPrice())) * (Float.parseFloat(item.getQuantity()));

                    Locale locale = new Locale("es", "EC");
                    NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

                    txtTotalPrice.setText(fmt.format(total));
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}