package com.example.b2mserver.Common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.b2mserver.Model.Request;
import com.example.b2mserver.Model.User;
import com.example.b2mserver.Remote.APIService;
import com.example.b2mserver.Remote.FcmRetrofitClient;
import com.example.b2mserver.Remote.IGeoCoordinates;
import com.example.b2mserver.Remote.RetrofitClient;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class Common {
    public static final String SHIPPER_TABLE = "Shippers";
    public static final String ORDER_NEDD_SHIP_TABLE = "OrdersNeedShip";

    public static User currentUser;
    public static Request currentRequest;

    public static String TOPICNAME = "News";

    public static String PHONE_TEXT = "userPhone";

    public static final int PICK_IMAGE_REQUEST = 71;

    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";

    private static final String BASE_URL = "https://maps.googleapis.com";
    private static final String FCM_URL = "https://fcm.googleapis.com";

    public static String convertCodeToStatus(String status) {
        switch (status) {
            case "0":
                return "Placed";
            case "1":
                return "On my way";
            default:
                return "Shipping";
        }
    }

    public static IGeoCoordinates getGeoCodeService() {
        return RetrofitClient.getClient(BASE_URL).create(IGeoCoordinates.class);
    }

    public static APIService getFCMService() {
        return FcmRetrofitClient.getClient(FCM_URL).create(APIService.class);
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float scaleX = newWidth / (float) bitmap.getWidth();
        float scaleY = newHeight / (float) bitmap.getHeight();
        float pivotX = 0, pivotY = 0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            Log.d("INFO1", "" + Arrays.toString(info));

            if (info != null) {

                for (int i = 0; i < info.length; i++) {

                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        Log.d("INFO2", "" + info[i].getState());
                    return true;
                }
            }
        }

        return false;
    }

    public static String getDate(long time) {
        Calendar calendar = Calendar.getInstance(Locale.KOREA);
        calendar.setTimeInMillis(time);

        return android.text.format.DateFormat.format("dd-MM-yyyy HH:mm", calendar).toString();
    }

}
