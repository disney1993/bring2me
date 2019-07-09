package com.example.milymozz.ordershipper.Common;

import com.example.milymozz.ordershipper.Model.Shipper;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by milymozz on 2018. 4. 8..
 */

public class Common {
    public static final String SHIPPER_TABLE = "Shippers";

    public static final String ORDER_NEED_SHIP_TABLE = "OrdersNeedShip";

    public static Shipper currentShipper;

    public static final int LOCATION_REQUEST_CODE = 1000;

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


    public static String getDate(long time) {
        Calendar calendar = Calendar.getInstance(Locale.KOREA);
        calendar.setTimeInMillis(time);

        return android.text.format.DateFormat.format("dd-MM-yyyy HH:mm", calendar).toString();
    }
}
