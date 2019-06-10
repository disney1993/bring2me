package com.example.b2m;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.b2m.Common.Common;
import com.example.b2m.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import info.hoang8f.widget.FButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SingUp extends AppCompatActivity {
    EditText etPhone, etName, etPassword, etSecureCode;
    FButton btnSingUp;
/*
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);

 /*       //para el estilo de la fuente siempre agregar antes del setContentView
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/cf.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());*/

        setContentView(R.layout.activity_sing_up);

        etName = (EditText) findViewById(R.id.etName);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etSecureCode = (EditText) findViewById(R.id.etSecureCode);

        btnSingUp = (FButton) findViewById(R.id.btnSingUp);

        //Inicializar firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("Users");

        btnSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConectedToInternet(getBaseContext())) {
                    final ProgressDialog mDialog = new ProgressDialog(SingUp.this);
                    mDialog.setMessage("Espera por favor...");
                    mDialog.show();

                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //Revisar si el numero de telefono ya existe en la BDD
                            if (dataSnapshot.child(etPhone.getText().toString()).exists()) {
                                mDialog.dismiss();
                                Toast.makeText(SingUp.this, "Teléfono ya registrado!!!", Toast.LENGTH_SHORT).show();
                            } else {
                                mDialog.dismiss();
                                User user = new User(etName.getText().toString(),
                                        etPassword.getText().toString(),
                                        etSecureCode.getText().toString());
                                table_user.child(etPhone.getText().toString()).setValue(user);
                                Toast.makeText(SingUp.this, "Registro exitoso!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    Toast.makeText(SingUp.this, "Por favor, revisa tu conexión!!!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

    }
}
