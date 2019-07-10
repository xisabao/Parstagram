package com.example.parstagram.fragments;

import android.util.Log;

import com.example.parstagram.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ProfileFragment extends PostsFragment {
    private final String TAG = "ProfileFragment";


    @Override
    protected void queryPosts(final int offset) {
        ParseQuery<Post> postQuery = ParseQuery.getQuery("Post");
        postQuery.include(Post.KEY_USER);
        postQuery.setLimit(POST_LIMIT);
        postQuery.setSkip(offset * POST_LIMIT);
        postQuery.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        postQuery.orderByDescending("createdAt");
        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> postList, ParseException e) {
                if (e == null) {
                    if (offset == 0) {
                        postAdapter.clear();
                    }
                    Log.d(TAG, "Got " + postList.size() + " posts");
                    postAdapter.addAll(postList);
                } else {
                    Log.d(TAG, "Error: " + e.getMessage());
                }
            }
        });
    }
}
