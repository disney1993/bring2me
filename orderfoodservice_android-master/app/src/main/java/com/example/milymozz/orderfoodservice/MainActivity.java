package com.example.milymozz.orderfoodservice;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button btnSignUp, btnSignIn;
    private TextView txtMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignIn = (Button) findViewById(R.id.btnSignIn);

        txtMain = (TextView) findViewById(R.id.txtMain);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Nabila.ttf");
        txtMain.setTypeface(face);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent SignInIntent = new Intent(MainActivity.this, SignIn.class);
                startActivity(SignInIntent);
            }
        });

    }
}
