package com.example.parstagram;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.parstagram.adapters.KommentAdapter;
import com.example.parstagram.models.Komment;
import com.example.parstagram.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class DetailActivity extends AppCompatActivity {
    public static final String TAG = "DetailActivity";
    TextView tvUsername;
    TextView tvDescription;
    TextView tvTimestamp;
    ImageView ivImage;
    ImageView ivProfile;

    RecyclerView rvComments;
    EditText etComment;
    Button btPostComment;

    Button btLike;
    Button btComment;

    ArrayList<Komment> komments;
    KommentAdapter kommentAdapter;

    public MenuItem miActionProgressItem;


    Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        rvComments = findViewById(R.id.rvComments);

        komments = new ArrayList<>();
        kommentAdapter = new KommentAdapter(komments);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvComments.setLayoutManager(linearLayoutManager);
        rvComments.setAdapter(kommentAdapter);


        String postId = getIntent().getStringExtra("post_id");
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.include("user");

        try {
            post = query.get(postId);
        } catch(Exception e) {
            Log.d(TAG, "Error: " + e.getMessage());
        }

        tvUsername = findViewById(R.id.tvUsername);
        tvDescription = findViewById(R.id.tvDescription);
        tvTimestamp = findViewById(R.id.tvTimestamp);
        ivImage = findViewById(R.id.ivImage);
        ivProfile = findViewById(R.id.ivProfile);
        etComment = findViewById(R.id.etComment);
        btPostComment = findViewById(R.id.btPostComment);
        btLike = findViewById(R.id.btLike);
        btComment = findViewById(R.id.btComment);

        tvUsername.setText(post.getUser().getUsername());
        tvDescription.setText(post.getDescription());
        tvTimestamp.setText(post.getRelativeTime());
        if (post.isLiked(ParseUser.getCurrentUser().getObjectId())) {
            btLike.setBackgroundResource(R.drawable.ufi_heart_active);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Glide.with(this).load(post.getImage().getUrl())
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
                }).into(ivImage);

        ParseUser user = post.getUser();

        if (user.getParseFile("profilePhoto") != null) {
            Glide.with(this).load(user.getParseFile("profilePhoto").getUrl())
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
                    .bitmapTransform(new CropCircleTransformation(this)).into(ivProfile);
        }

        loadComments();

        btPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    postComment(etComment.getText().toString(), ParseUser.getCurrentUser());
                } catch (Exception e) {
                    Log.d(TAG, "Error: " + e.getMessage());
                }
            }
        });

        btLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (post.isLiked(ParseUser.getCurrentUser().getObjectId())) {
                    sendRemoveLike();
                } else {
                    sendAddLike();
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        return true;
    }

    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }

    private void setUserClick() {
        tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUser();
            }
        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUser();
            }
        });
    }

    private void sendAddLike() {
        post.addLike(ParseUser.getCurrentUser().getObjectId());
        post.saveInBackground();
        btLike.setBackgroundResource(R.drawable.ufi_heart_active);
    }

    private void sendRemoveLike() {
        post.removeLike(ParseUser.getCurrentUser().getObjectId());
        post.saveInBackground();
        btLike.setBackgroundResource(R.drawable.ufi_heart);
    }

    private void goToUser() {
        // nope this is too hard sorry

    }

    private void loadComments() {
        ParseQuery<Komment> query = ParseQuery.getQuery("Komment");
        query.include("user");
        query.whereEqualTo(Komment.KEY_POST, post);
        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<Komment>() {
            @Override
            public void done(List<Komment> kommentList, ParseException e) {
                if (e == null) {
                    kommentAdapter.clear();
                    Log.d(TAG, "Got " + kommentList.size() + " comments");
                    kommentAdapter.addAll(kommentList);
                } else {
                }
            }
        });
    }

    private void postComment(String text, ParseUser user) throws ParseException {
        Komment comment = new Komment();
        comment.setText(text);
        comment.setUser(user);
        comment.setPost(post);
        kommentAdapter.add(comment);
        comment.save();
        Toast.makeText(DetailActivity.this, "Comment posted!", Toast.LENGTH_SHORT).show();
        etComment.setText("");
        etComment.clearFocus();
        loadComments();

    }

}
