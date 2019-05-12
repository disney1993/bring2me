package com.example.b2m.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.example.b2m.Common.Common;
import com.example.b2m.Model.Request;
import com.example.b2m.OrderStatus;
import com.example.b2m.R;
import com.example.b2m.SingIn;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ListenOrder extends Service implements ChildEventListener {
    FirebaseDatabase db;
    DatabaseReference requests;

    public ListenOrder() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        db = FirebaseDatabase.getInstance();
        requests = db.getReference("Requests");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        requests.addChildEventListener(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        //trigger aqui
        Request request = dataSnapshot.getValue(Request.class);
        showNotification(dataSnapshot.getKey(), request);

    }

    /*  private void showNotification(String key, Request request) {
          Intent intent= new Intent(getBaseContext(), OrderStatus.class);
          intent.putExtra("userPhone",request.getPhone());
          PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
          NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());

          builder.setAutoCancel(true)
                  .setDefaults(Notification.DEFAULT_ALL)
                  .setWhen(System.currentTimeMillis())
                  .setTicker("Bring2Me")
                  .setContentInfo("Su pedido ha sido actualizado")
                  .setContentText("Pedido #"+key+"estado actualizado a "+ Common.covertCodeToStatus(request.getStatus()))
                  .setContentIntent(contentIntent)
                  .setContentInfo("Info")
                  .setSmallIcon(R.mipmap.ic_launcher);
          NotificationManager notificationManager = (NotificationManager)getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
          notificationManager.notify(1,builder.build());
      }*/
    private void showNotification(String key, Request request) {
        Intent intent = new Intent(getBaseContext(), OrderStatus.class);
        intent.putExtra("userPhone", request.getPhone()); //necesitamos poner el teléfono del usuario
        PendingIntent contentIntent = PendingIntent
                .getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel("foodStatus", "foodStatus", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(), "foodStatus");
        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("Bring2Me")
                .setContentInfo("Tu pedido fue actualizado")
                .setContentText("El pedido #" + key + " se actualizó a " + Common.covertCodeToStatus(request.getStatus()))
                .setContentIntent(contentIntent)
                .setContentInfo("Info")
                .setSmallIcon(R.mipmap.ic_launcher);
        NotificationManager notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
