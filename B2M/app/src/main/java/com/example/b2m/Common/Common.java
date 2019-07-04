package com.example.b2m.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.b2m.Model.User;
import com.example.b2m.Remote.APIService;
import com.example.b2m.Remote.GoogleRetrofitCient;
import com.example.b2m.Remote.IGeoRetrofit;
import com.example.b2m.Remote.IGoogleService;
import com.example.b2m.Remote.RetrofitClient;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;

public class Common {
    public static User currentUser;
    public static String PHONE_TEXT = "userPhone";
    public static String TOPICNAME = "Noticias";

    public static final String INTENT_FOOD_ID = "FoodId";

    private static final String BASE_URL = "https://fcm.googleapis.com/";
    private static final String GOOGLE_API_URL = "https://maps.googleapis.com/";

    public static APIService getFCMService() {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static IGoogleService getGoogleMapAPI() {
        return GoogleRetrofitCient.getGoogleClient(GOOGLE_API_URL).create(IGoogleService.class);
    }


    public static String covertCodeToStatus(String status) {
        if (status.equals("0"))
            return "Realizado";
        else if (status.equals("1"))
            return "En camino";
        else
            return "Entregado";
    }

    public static final String DELETE = "Eliminar";
    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";


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

    public static IGoogleService getGoogleMapApi() {
        return IGeoRetrofit.getGoogleClient(GOOGLE_API_URL).create(IGoogleService.class);
    }

    public static BigDecimal formatCurrency(String amount, Locale locale) throws ParseException {
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        if (format instanceof DecimalFormat)
            ((DecimalFormat) format).setParseBigDecimal(true);

        return (BigDecimal) format.parse(amount.replace("[^\\d.,]", ""));
    }
}
