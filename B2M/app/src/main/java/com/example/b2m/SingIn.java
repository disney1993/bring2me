package com.example.b2m;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.b2m.Common.Common;
import com.example.b2m.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;

import info.hoang8f.widget.FButton;
import io.paperdb.Paper;

public class SingIn extends AppCompatActivity {
    EditText etPassword, etPhone;
    FButton btnSingIn;
    CheckBox ckbRemember;
    TextView txtForgotPwd;

    FirebaseDatabase database;
    DatabaseReference table_user;
/*

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);

        /*//para el estilo de la fuente siempre agregar antes del setContentView
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/cf.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());*/

        setContentView(R.layout.activity_sing_in);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etPhone = (EditText) findViewById(R.id.etPhone);
        btnSingIn = (FButton) findViewById(R.id.btnSingIn);
        ckbRemember = (com.rey.material.widget.CheckBox) findViewById(R.id.ckbRemember);
        txtForgotPwd = (TextView) findViewById(R.id.txtForgotPwd);

        //inicializar ppaper
        Paper.init(this);

        //Inicializar firebase
        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("Users");

        txtForgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showForgotPwdDialog();
            }
        });
        btnSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConectedToInternet(getBaseContext())) {
                    //recordar ususario y contrasenia
                    if (ckbRemember.isChecked()) {
                        Paper.book().write(Common.USER_KEY, etPhone.getText().toString());
                        Paper.book().write(Common.PWD_KEY, etPassword.getText().toString());
                    }

                    final ProgressDialog mDialog = new ProgressDialog(SingIn.this);
                    mDialog.setMessage("Espera por favor...");
                    mDialog.show();
                    table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //revisar si no existe ya el usuario en la database
                            if (dataSnapshot.child(etPhone.getText().toString()).exists()) {
                                //obtener informacion de usuario
                                mDialog.dismiss();
                                User user = dataSnapshot.child(etPhone.getText().toString()).getValue(User.class);
                                user.setPhone(etPhone.getText().toString());//setPhone
                                if (user.getPassword().equals(etPassword.getText().toString())) {
                                    {
                                        Intent homeIntent = new Intent(SingIn.this, Home.class);
                                        Common.currentUser = user;
                                        startActivity(homeIntent);
                                        finish();

                                        table_user.removeEventListener(this);
                                    }
                                    Toast.makeText(SingIn.this, "Ingreso exitoso", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SingIn.this, "Ingreso fallido", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(SingIn.this, "El usuario no existe en la BDD", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    Toast.makeText(SingIn.this, "Por favor, revisa tu conexión!!!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

    }

    private void showForgotPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Contraseña olvidada");
        builder.setMessage("Ingrese su código de seguridad");

        LayoutInflater inflater = this.getLayoutInflater();
        View forgot_view = inflater.inflate(R.layout.forgot_password_layout, null);

        builder.setView(forgot_view);
        builder.setIcon(R.drawable.ic_security_black_24dp);

        final MaterialEditText etPhone = (MaterialEditText) forgot_view.findViewById(R.id.etPhone);
        final MaterialEditText etSecureCode = (MaterialEditText) forgot_view.findViewById(R.id.etSecureCode);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //revisar si el usuario esta disponible
                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.child(etPhone.getText().toString())
                                .getValue(User.class);
                        if (user.getSecureCode().equals(etSecureCode.getText().toString()))
                            Toast.makeText(SingIn.this, "Tu contraseña :" + user.getPassword(), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(SingIn.this, "Código incorrecto!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
}