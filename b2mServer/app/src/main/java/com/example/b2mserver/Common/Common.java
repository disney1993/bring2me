package com.example.b2mserver.Common;

import com.example.b2mserver.Model.Request;
import com.example.b2mserver.Model.User;

public class Common {
    public static User currentUser;
    public static Request currentRequest;

    public static final String UPDATE = "Actualizar";
    public static final String DELETE = "Eliminar";

    public static final int PICK_IMAGE_REQUEST = 71;

    public static String covertirCodigoAStatus(String code) {
        if (code.equals("0"))
            return "Realizado";
        else if (code.equals("1"))
            return "En camino";
        else
            return "Entregado";

    }
}
