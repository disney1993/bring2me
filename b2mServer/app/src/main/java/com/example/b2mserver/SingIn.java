package com.example.b2mserver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.b2mserver.Common.Common;
import com.example.b2mserver.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import info.hoang8f.widget.FButton;

public class SingIn extends AppCompatActivity {
    EditText etPassword, etPhone;
    FButton btnSingIn;
    FirebaseDatabase db;
    DatabaseReference users;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_in);

        etPassword = (EditText) findViewById(R.id.etPassword);
        etPhone = (EditText) findViewById(R.id.etPhone);
        btnSingIn = (FButton) findViewById(R.id.btnSingIn);

        //inicializar firebase
        db=FirebaseDatabase.getInstance();
        users=db.getReference("Users");

        btnSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singInUser(etPhone.getText().toString(),etPassword.getText().toString());

            }
        });
    }

    private void singInUser(final String phone, String password) {
        final ProgressDialog mDialog = new ProgressDialog(SingIn.this);
        mDialog.setMessage("Espere por favor...");
        mDialog.show();

        final String localPhone=phone;
        final String localPassword=password;

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(localPhone).exists()){
                    mDialog.dismiss();
                    User user = dataSnapshot.child(localPhone).getValue(User.class);
                    user.setPhone(localPhone);
                    if (Boolean.parseBoolean(user.getIsStaff()))//si es staff *(empleado) = true
                    {
                        if (user.getPassword().equals(localPassword)){
                            //ingreso exitoso
                            Intent login = new Intent(SingIn.this,Home.class);
                            Common.currentUser=user;
                            startActivity(login);
                            finish();
                        }
                        else{
                            Toast.makeText(SingIn.this, "Contraseña incorrecta!!!", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(SingIn.this, "Por favor, ingrese una cuenta de empleado", Toast.LENGTH_SHORT).show();
                    }
                }else
                {
                    mDialog.dismiss();
                    Toast.makeText(SingIn.this, "Usuario no registrado!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
