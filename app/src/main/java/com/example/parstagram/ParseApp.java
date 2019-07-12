package com.example.parstagram;

import android.app.Application;

import com.example.parstagram.models.Komment;
import com.example.parstagram.models.Post;
import com.parse.Parse;
import com.parse.ParseObject;

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

        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Komment.class);

        Parse.initialize(configuration);



    }
}
