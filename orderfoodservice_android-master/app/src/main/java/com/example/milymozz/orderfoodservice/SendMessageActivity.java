package com.example.milymozz.orderfoodservice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.milymozz.orderfoodservice.Common.Common;
import com.example.milymozz.orderfoodservice.Model.DataMessage;
import com.example.milymozz.orderfoodservice.Model.MyResponse;
import com.example.milymozz.orderfoodservice.Remote.APIService;
import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendMessageActivity extends AppCompatActivity {
    private MaterialEditText edtMessage, edtTitle;
    private FButton btnSendMessage;

    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        mService = Common.getFCMService();

        edtMessage = (MaterialEditText) findViewById(R.id.edtMessage);
        edtTitle = (MaterialEditText) findViewById(R.id.edtTitle);

        btnSendMessage = (FButton) findViewById(R.id.btnSendMessage);
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> dataSend = new HashMap<>();
                dataSend.put("title", edtTitle.getText().toString());
                dataSend.put("message", edtMessage.getText().toString());
                DataMessage dataMessage = new DataMessage("/topics/" + Common.TOPICNAME, dataSend);

                String test = new Gson().toJson(dataMessage);
                Log.d("Content", test);

                mService.sendNotification(dataMessage)
                        .enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                if (response.isSuccessful())
                                    Toast.makeText(SendMessageActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {
                                Toast.makeText(SendMessageActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
