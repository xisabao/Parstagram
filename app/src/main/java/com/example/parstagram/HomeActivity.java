package com.example.parstagram;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private final String TAG ="HomeActivity";
    public Button logoutBtn;
    public Button postBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        logoutBtn = findViewById(R.id.logoutBtn);
        postBtn = findViewById(R.id.postBtn);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(HomeActivity.this, PostActivity.class);
                startActivity(intent);
            }
        });

        queryPosts();

    }

    private void logout() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            ParseUser.logOut();
        }
        final Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void queryPosts() {
        ParseQuery<Post> postQuery = new ParseQuery<Post>(Post.class);
        postQuery.include(Post.KEY_USER);
        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                for (int i = 0; i < posts.size(); i++) {
                    Log.d(TAG, "Post: " + posts.get(i).getDescription() + " username: " + posts.get(i).getUser().getUsername());
                }
            }
        });
    }
}
