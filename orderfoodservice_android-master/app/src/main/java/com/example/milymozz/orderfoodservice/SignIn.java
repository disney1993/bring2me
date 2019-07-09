package com.example.milymozz.orderfoodservice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.milymozz.orderfoodservice.Common.Common;
import com.example.milymozz.orderfoodservice.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {
    private EditText edtPassword, edtPhone;
    private Button btnSignIn;

    private FirebaseDatabase database;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtPhone = (EditText) findViewById(R.id.edtPhone);

        btnSignIn = (Button) findViewById(R.id.btnSignIn);

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        users = database.getReference("User");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser(edtPhone.getText().toString(), edtPassword.getText().toString());
            }
        });

    }

    private void signInUser(String phone, String password) {
        final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
        mDialog.setMessage("Please waiting...");
        mDialog.show();

        final String localPhone = phone;
        final String localPassword = password;

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(localPhone).exists()) {
                    mDialog.dismiss();
                    User user = dataSnapshot.child(localPhone).getValue(User.class);
                    if (user != null) {
                        user.setPhone(localPhone);
                    }
                    if (user != null) {
                        if (Boolean.parseBoolean(user.getIsStaff())) { // if IsStaff == true
                            if (user.getPassword().equals(localPassword)) {
                                // Login Ok
                                Intent loginIntent = new Intent(SignIn.this, Home.class);
                                Common.currentUser = user;
                                Log.d("MOOOOOO", "" + Common.currentUser.getName() + Common.currentUser.getPassword() + Common.currentUser.getIsStaff() + Common.currentUser.getPhone());
                                startActivity(loginIntent);
                                finish();

                            } else {
                                Toast.makeText(SignIn.this, "Wrong Password !", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SignIn.this, "Please login with Staff account", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    mDialog.dismiss();
                    Toast.makeText(SignIn.this, "User not exist in DB", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
