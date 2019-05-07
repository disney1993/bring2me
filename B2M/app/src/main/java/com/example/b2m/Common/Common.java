package com.example.b2m.Common;

import com.example.b2m.Model.User;

public class Common {
    public static User currentUser;


    public static String covertCodeToStatus(String status) {
        if (status.equals("0"))
            return "Realizado";
        else if (status.equals("1"))
            return "En camino";
        else
            return "Entregado";
    }
}
