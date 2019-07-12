package com.example.parstagram.models;

import android.text.format.DateUtils;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_LIKES = "likes";

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser parseUser) {
        put(KEY_USER, parseUser);
    }

    public ArrayList<String> getLikes() {
        return (ArrayList<String>) get(KEY_LIKES);
    }

    public void setLikes() {
        put(KEY_LIKES, new ArrayList<String>());
    }

    public void addLike(String userId) {
        ArrayList<String> likes = getLikes();
        likes.add(userId);
        put(KEY_LIKES, likes);
    }

    public void removeLike(String userId) {
        ArrayList<String> likes = getLikes();
        likes.remove(userId);
        put(KEY_LIKES, likes);
    }

    public int numberOfLikes() {
        return ((ArrayList<String>) get(KEY_LIKES)).size();
    }

    public boolean isLiked(String currentUser) {
        ArrayList<String> likes = getLikes();
        return likes.contains(currentUser);
    }

    public String getRelativeTime() {
        return (String) DateUtils.getRelativeTimeSpanString(getCreatedAt().getTime());
    }
}
