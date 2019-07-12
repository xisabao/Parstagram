package com.example.parstagram.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.parstagram.DetailActivity;
import com.example.parstagram.HomeActivity;
import com.example.parstagram.R;
import com.example.parstagram.fragments.ProfileFragment;
import com.example.parstagram.models.Post;
import com.parse.ParseUser;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    public static final String TAG = "PostAdapter";
    public static List<Post> mPosts;
    public static Context context;
    private boolean isLinear = true;

    public PostAdapter(List<Post> posts, boolean linear) {
        mPosts = posts;
        isLinear = linear;
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if (isLinear) {
            View postView = inflater.inflate(R.layout.item_post, parent, false);
            ViewHolder viewHolder = new LinearViewHolder(postView);
            return viewHolder;
        }

        View postView = inflater.inflate(R.layout.item_post_grid, parent, false);
        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Post post = mPosts.get(position);

        if (isLinear &&  holder instanceof LinearViewHolder) {
            ((LinearViewHolder) holder).tvUsername.setText(post.getUser().getUsername());
            ((LinearViewHolder) holder).tvDescription.setText(post.getDescription());
            ((LinearViewHolder) holder).tvTimestamp.setText(post.getRelativeTime());

            ParseUser user = post.getUser();

            if (user.getParseFile("profilePhoto") != null) {
                Glide.with(context).load(user.getParseFile("profilePhoto").getUrl())
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
                        .bitmapTransform(new CropCircleTransformation(context)).into(((LinearViewHolder) holder).ivProfile);
            }

            if (post.isLiked(ParseUser.getCurrentUser().getObjectId())) {
                ((LinearViewHolder) holder).btLike.setBackgroundResource(R.drawable.ufi_heart_active);
            } else {
                ((LinearViewHolder) holder).btLike.setBackgroundResource(R.drawable.ufi_heart);
            }

            ((LinearViewHolder) holder).btLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (post.isLiked(ParseUser.getCurrentUser().getObjectId())) {
                        sendRemoveLike(post);
                        ((LinearViewHolder) holder).btLike.setBackgroundResource(R.drawable.ufi_heart);
                    } else {
                        sendAddLike(post);
                        ((LinearViewHolder) holder).btLike.setBackgroundResource(R.drawable.ufi_heart_active);
                    }
                }
            });


            ((LinearViewHolder) holder).tvUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ParseUser user = post.getUser();
                    goToUser(user);
                }
            });

            ((LinearViewHolder) holder).ivProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ParseUser user = post.getUser();
                    goToUser(user);
                }
            });


        }
        Log.d(TAG, "URL: " + post.getImage().getUrl());
        Glide.with(context).load(post.getImage().getUrl())
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
                }).into(holder.ivImage);

    }

    public void clear() {
        mPosts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> list) {
        mPosts.addAll(list);
        notifyDataSetChanged();
    }

    private void sendAddLike(Post post) {
        post.addLike(ParseUser.getCurrentUser().getObjectId());
        post.saveInBackground();
    }

    private void sendRemoveLike(Post post) {
        post.removeLike(ParseUser.getCurrentUser().getObjectId());
        post.saveInBackground();

    }

    private void goToUser(ParseUser user) {
          if (context instanceof HomeActivity) {
              Log.d(TAG, "WE ARE TRYING TO GO TO THIS USER: " + user.getObjectId());
              HomeActivity activity = (HomeActivity) context;
              activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, ProfileFragment.newInstance(user.getObjectId())).commit();
          }
    }



    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView ivImage;

        public ViewHolder(View itemView) {
            super(itemView);

            ivImage = itemView.findViewById(R.id.ivImage);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            // TODO: Move this out and set different listeners for each part of the post
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {
                // get the movie at the position
                Post post = mPosts.get(position);
                // create intent for the new activity
                Intent intent = new Intent(context, DetailActivity.class);
                // serialize the movie using parceler, use its short name as a key
                intent.putExtra("post_id", post.getObjectId());
                // show the activity
                context.startActivity(intent);
            }
        }
    }

    public static class LinearViewHolder extends ViewHolder implements View.OnClickListener {
        public TextView tvUsername;
        public TextView tvDescription;
        public TextView tvTimestamp;
        public ImageView ivProfile;
        public Button btLike;
        public Button btComment;

        public LinearViewHolder(View itemView) {
            super(itemView);

            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            btLike = itemView.findViewById(R.id.btLike);
            btComment = itemView.findViewById(R.id.btComment);



        }

    }


}
