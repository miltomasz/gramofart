package com.soldiersofmobile.app;

import android.app.Application;

import com.parse.Parse;

public class GramOfArtApplication extends Application {

    @Override
    public void onCreate() {
        Parse.initialize(this, "xJQaSn6ajsrGUPePno8TQo2E260J7VkqXRafLuuE", "ugkkL7Qq30qumwVOm0Mujr5PwMBLS53Pqkix4zXE");
    }
}
