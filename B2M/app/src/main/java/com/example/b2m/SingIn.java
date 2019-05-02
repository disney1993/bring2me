package com.example.b2m;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class SingIn extends AppCompatActivity {
    EditText etPassword, etPhone;
    Button btnSingIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_in);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etPhone = (EditText) findViewById(R.id.etPhone);
        btnSingIn = (Button) findViewById(R.id.btnSingIn);
        //Inicializar firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("Users");
        btnSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog mDialog = new ProgressDialog(SingIn.this);
                mDialog.setMessage("Espera por favor...");
                mDialog.show();
                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //revisar si no existe ya el usuario en la database
                        if (dataSnapshot.child(etPhone.getText().toString()).exists()){
                    //obtener informacion de usuario
                        mDialog.dismiss();
                        User user = dataSnapshot.child(etPhone.getText().toString()).getValue(User.class);
                        user.setPhone(etPhone.getText().toString());//setPhone
                        if (user.getPassword().equals(etPassword.getText().toString())){
                            {
                                Intent homeIntent = new Intent(SingIn.this,Home.class);
                                Common.currentUser = user;
                                startActivity(homeIntent);
                                finish();
                            }
                            Toast.makeText(SingIn.this, "Ingreso exitoso", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(SingIn.this, "Ingreso fallido", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                            Toast.makeText(SingIn.this, "El usuario no existe en la BDD", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }
}