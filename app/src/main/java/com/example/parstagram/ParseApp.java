package com.example.parstagram;

import android.app.Application;

import com.parse.Parse;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("xisabao-parstagram")
                .clientKey("letuschangethis")
                .server("https://xisabao-parstagram.herokuapp.com/parse")
                .build();

        Parse.enableLocalDatastore(this);

        Parse.initialize(configuration);



    }
}
