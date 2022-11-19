package com.specknet.pdiotapp.utils;

import android.app.Application;

import androidx.appcompat.app.AppCompatActivity;

public class GlobalStates extends Application {

    private boolean userLoginState;

    public boolean getUserLoginState() {
        return userLoginState;
    }

    public void setUserLoginState(boolean state) {
        this.userLoginState = state;
    }
}