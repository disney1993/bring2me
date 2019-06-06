package com.example.b2m.Model;

import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.InstanceIdResult;

public class Token {
    private String token;
    private Boolean isServerToken;

    public Token(Task<InstanceIdResult> token, boolean isServerToken) {
    }

    public Token(String token, Boolean isServerToken) {
        this.token = token;
        this.isServerToken = isServerToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        token = token;
    }

    public Boolean getServerToken() {
        return isServerToken;
    }

    public void setServerToken(Boolean serverToken) {
        isServerToken = serverToken;
    }
}
