package com.example.milymozz.orderfood.Service;

import com.example.milymozz.orderfood.Common.Common;
import com.example.milymozz.orderfood.Model.Token;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class MyFirebaseIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String tokenRefreshed = FirebaseInstanceId.getInstance().getToken();
        if (Common.currentUser != null)
            updateTokenToFirebase(tokenRefreshed);
    }

    private void updateTokenToFirebase(String tokenRefreshed) {
        if (Common.currentUser != null) {
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference tokens = db.getReference("Tokens");
            Token token = new Token(tokenRefreshed, false); // false because this token send from Client app
            tokens.child(Common.currentUser.getPhone()).setValue(token);
        }

    }
}
