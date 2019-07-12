package com.example.parstagram.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.parstagram.EditProfileActivity;
import com.example.parstagram.EndlessRecyclerViewScrollListener;
import com.example.parstagram.MainActivity;
import com.example.parstagram.adapters.PostAdapter;
import com.example.parstagram.R;
import com.example.parstagram.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ProfileFragment extends Fragment {
    private final String TAG = "ProfileFragment";
    protected RecyclerView rvPosts;
    protected ArrayList<Post> posts;
    private PostAdapter postAdapter;

    private TextView tvUsername;
    private TextView tvDescription;
    private ImageView ivProfile;
    private Button logoutBtn;
    private Button btEditProfile;
    private SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;

    private ParseUser user;

    public static ProfileFragment newInstance(String user_id) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("user_id", user_id);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String user_id = getArguments().getString("user_id");
        try {
            user = getUser(user_id);
            Log.d(TAG, "we're getting this user: " + user_id);
        } catch (Exception e) {
            Log.e(TAG, "Error getting user", e);
        }

        rvPosts = view.findViewById(R.id.rvPosts);

        posts = new ArrayList<>();
        postAdapter = new PostAdapter(posts, false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        rvPosts.setLayoutManager(gridLayoutManager);
        rvPosts.setAdapter(postAdapter);

        swipeContainer = view.findViewById(R.id.swipeContainer);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvDescription = view.findViewById(R.id.tvDescription);
        ivProfile = view.findViewById(R.id.ivProfile);


        tvUsername.setText(user.getUsername());
        tvDescription.setText(user.getString("description"));
        if (user.getParseFile("profilePhoto") != null) {
            Glide.with(getContext()).load(user.getParseFile("profilePhoto").getUrl())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            Log.e("IMAGE_EXCEPTION", "Exception " + e.toString());
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            Log.d(TAG, "Sometimes the image is not loaded and this text is not displayed");
                            return false;
                        }
                    })
                    .bitmapTransform(new CropCircleTransformation(getContext())).into(ivProfile);
        }

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                postAdapter.clear();
                queryPosts(0);
                scrollListener.resetState();
                swipeContainer.setRefreshing(false);
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        queryPosts(0);

        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryPosts(page);
            }
        };

        rvPosts.addOnScrollListener(scrollListener);

        logoutBtn = view.findViewById(R.id.logoutBtn);
        btEditProfile = view.findViewById(R.id.btEditProfile);

        if (!user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
            btEditProfile.setVisibility(View.INVISIBLE);
            logoutBtn.setVisibility(View.INVISIBLE);
        }


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        btEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

    }

    private void queryPosts(final int offset) {
        ParseQuery<Post> postQuery = ParseQuery.getQuery("Post");
        postQuery.include(Post.KEY_USER);
        postQuery.setLimit(PostsFragment.POST_LIMIT);
        postQuery.setSkip(offset * PostsFragment.POST_LIMIT);
        postQuery.whereEqualTo(Post.KEY_USER, user);
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

    private ParseUser getUser(String user_id) throws ParseException {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", user_id);
        return query.getFirst();
    }

    private void logout() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            ParseUser.logOut();
        }
        final Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
