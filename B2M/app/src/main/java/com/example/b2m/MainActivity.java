package com.example.b2m;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.b2m.Common.Common;
import com.example.b2m.Model.User;
import com.facebook.FacebookSdk;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 7171;
    //private static final String TAG = "MainActivity";
    Button btnSingIn, btnSingUp;
    Button btnContinue;
    TextView txtSlogan;

    FirebaseDatabase database;
    DatabaseReference users;

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


        FacebookSdk.sdkInitialize(getApplicationContext());
        AccountKit.initialize(this);
        setContentView(R.layout.activity_main);

        //imprimirKeyHash();

        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");

        btnContinue = (Button) findViewById(R.id.btn_continuar);
        btnSingIn = (Button) findViewById(R.id.btnSingIn);
        btnSingUp = (Button) findViewById(R.id.btnSingUp);
        txtSlogan = (TextView) findViewById(R.id.txtSlogan);

//        Button logTokenButton = findViewById(R.id.logTokenButton);


        //letra nabila en el slogan
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF");
        txtSlogan.setTypeface(face);

        //incializzar Paper
        Paper.init(this);

        btnSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent singIn = new Intent(MainActivity.this, SingIn.class);
                startActivity(singIn);
            }
        });

        btnSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent singUp = new Intent(MainActivity.this, SingUp.class);
                startActivity(singUp);
            }
        });
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogSystem();
            }
        });

        if (AccountKit.getCurrentAccessToken() != null)
        {
            //crear un alertdialog
            final AlertDialog waitingDialog = new SpotsDialog.Builder().setContext(this).build();
            waitingDialog.show();
            waitingDialog.setMessage("Por favor, espera...");
            waitingDialog.setCancelable(false);

            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(Account account) {
                    users.child(account.getPhoneNumber().toString())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    User localUser = dataSnapshot.getValue(User.class);
                                    //mismo codigo q el login normal
                                    Intent homeIntent = new Intent(MainActivity.this, Home.class);
                                    Common.currentUser = localUser;
                                    startActivity(homeIntent);
                                    waitingDialog.dismiss();
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }

                @Override
                public void onError(AccountKitError accountKitError) {

                }
            });
        }
        //ya no sirve por el login con facebook
        //Revisar el Recordarme
        String user = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PWD_KEY);
        if (user != null && pwd != null) {
            if (!user.isEmpty() && !pwd.isEmpty()) ;
            login(user, pwd);
        }

       /*logTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get token
                // [START retrieve_current_token]
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "getInstanceId failed", task.getException());
                                    return;
                                }

                                // Get new Instance ID token
                                String token = task.getResult().getToken();

                                // Log and toast
                                String msg = getString(R.string.msg_token_fmt, token);
                                Log.d(TAG, msg);
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                // [END retrieve_current_token]
            }
        });*/

    }

    private void startLogSystem() {
        Intent intent = new Intent(MainActivity.this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configurationBuilder.build());
        startActivityForResult(intent, REQUEST_CODE);
    }

    private void imprimirKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.b2m",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void login(final String phone, final String pwd) {

        //Inicializar firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("Users");
        if (Common.isConectedToInternet(getBaseContext())) {


            final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage("Espera por favor...");
            mDialog.show();
            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //revisar si no existe ya el usuario en la database
                    if (dataSnapshot.child(phone).exists()) {
                        //obtener informacion de usuario
                        mDialog.dismiss();
                        User user = dataSnapshot.child(phone).getValue(User.class);
                        user.setPhone(phone);//setPhone
                        if (user.getPassword().equals(pwd)) {
                            {
                                Intent homeIntent = new Intent(MainActivity.this, Home.class);
                                Common.currentUser = user;
                                startActivity(homeIntent);
                                finish();
                            }
                            Toast.makeText(MainActivity.this, "Ingreso exitoso", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Ingreso fallido", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "El usuario no existe en la BDD", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(MainActivity.this, "Por favor, revisa tu conexión!!!", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (result.getError() != null) {
                Toast.makeText(this, "" + result.getError().getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
                return;
            } else if (result.wasCancelled()) {
                Toast.makeText(this, "Cancelar", Toast.LENGTH_SHORT).show();
                return;
            } else {
                if (result.getAccessToken() != null) {
                    //ver el alertdialog
                    final AlertDialog waitingDialog = new SpotsDialog.Builder().setContext(this).build();
                    waitingDialog.show();
                    waitingDialog.setMessage("Por favor, espera...");
                    waitingDialog.setCancelable(false);

                    //obtener el telefono actual
                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(Account account) {
                            final String userPhone = account.getPhoneNumber().toString();
                            //ver si ya existe en el firebase
                            users.orderByKey().equalTo(userPhone)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (!dataSnapshot.child(userPhone).exists())//si no existe
                                            {
                                                //creamos el nuevo usuario y lo logueamos
                                                User newUser = new User();
                                                newUser.setPhone(userPhone);
                                                newUser.setName("");
                                                //agregar a firebase
                                                users.child(userPhone)
                                                        .setValue(newUser)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful())
                                                                    Toast.makeText(MainActivity.this, "Registro exitoso!", Toast.LENGTH_SHORT).show();
                                                                //loguear
                                                                users.child(userPhone)
                                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                User localUser = dataSnapshot.getValue(User.class);
                                                                                //localUser.setPhone(userPhone);
                                                                                //mismo codigo q el login normal
                                                                                Intent homeIntent = new Intent(MainActivity.this, Home.class);
                                                                                Common.currentUser = localUser;
                                                                                startActivity(homeIntent);
                                                                                waitingDialog.dismiss();
                                                                                finish();
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                            }
                                                                        });
                                                            }
                                                        });
                                            } else//si existe
                                            {
                                                //solo loguear
                                                users.child(userPhone)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                User localUser = dataSnapshot.getValue(User.class);
                                                                //mismo codigo q el login normal
                                                                Intent homeIntent = new Intent(MainActivity.this, Home.class);
                                                                Common.currentUser = localUser;
                                                                //homeIntent.putExtra("userPhone",Common.currentUser.getPhone());

                                                                startActivity(homeIntent);
                                                                waitingDialog.dismiss();
                                                                finish();
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                        }

                        @Override
                        public void onError(AccountKitError accountKitError) {
                            Toast.makeText(MainActivity.this, "" + accountKitError.getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }
}