package com.example.b2mserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.b2mserver.Common.Common;
import com.example.b2mserver.Model.DataMessage;
import com.example.b2mserver.Model.MyResponse;
import com.example.b2mserver.Remote.APIService;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendMessage extends AppCompatActivity {

    private EditText edtMessage, edtTitle;
    private FButton btnSendMessage;

    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        mService = Common.getFCMService();

        edtMessage = (EditText) findViewById(R.id.edtMessage);
        edtTitle = (EditText) findViewById(R.id.edtTitle);

        btnSendMessage = (FButton) findViewById(R.id.btnSendMessage);
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, String> dataSend = new HashMap<>();
                dataSend.put("title", edtTitle.getText().toString());
                dataSend.put("message", edtMessage.getText().toString());
                DataMessage dataMessage = new DataMessage("/topics/" + Common.TOPICNAME, dataSend);

                String test = new Gson().toJson(dataMessage);
                Log.d("Contenido: ", test);

                mService.sendNotification(dataMessage)
                        .enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                if (response.isSuccessful())
                                    Toast.makeText(SendMessage.this, "Mensaje enviado", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {
                                Toast.makeText(SendMessage.this, "Fallo" + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
