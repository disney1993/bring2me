package com.example.b2mserver.Common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.example.b2mserver.Model.Request;
import com.example.b2mserver.Model.User;
import com.example.b2mserver.Remote.IGeoCoordinates;
import com.example.b2mserver.Remote.RetrofitClient;

import retrofit2.Retrofit;

public class Common {
    public static User currentUser;
    public static Request currentRequest;

    public static final String UPDATE = "Actualizar";
    public static final String DELETE = "Eliminar";

    public static final int PICK_IMAGE_REQUEST = 71;

    public static final String baseUrl = "https://maps.googleapis.com";

    public static String covertirCodigoAStatus(String code) {
        if (code.equals("0"))
            return "Realizado";
        else if (code.equals("1"))
            return "En camino";
        else
            return "Entregado";

    }

    public static IGeoCoordinates getGeoCodeService(){
        return RetrofitClient.getClient(baseUrl).create(IGeoCoordinates.class);

    }

    public static Bitmap scaleBitmap(Bitmap bitmap,int newWidth,int newHeight)
    {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth,newHeight,Bitmap.Config.ARGB_8888);
        float scaleX = newWidth/(float)bitmap.getWidth();
        float scaleY = newHeight/(float)bitmap.getHeight();
        float pivotX=0,pivotY=0;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX,scaleY,pivotX,pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap,0,0,new Paint(Paint.FILTER_BITMAP_FLAG));
        return scaledBitmap;
    }
}