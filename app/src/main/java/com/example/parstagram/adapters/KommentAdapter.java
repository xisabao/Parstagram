package com.example.parstagram.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.parstagram.R;
import com.example.parstagram.models.Komment;
import com.parse.ParseUser;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class KommentAdapter  extends RecyclerView.Adapter<KommentAdapter.ViewHolder> {
    public static final String TAG = "KommentAdapter";
    public static List<Komment> mKomments;
    public static Context context;

    public KommentAdapter(List<Komment> komments) {
        mKomments = komments;
    }

    @NonNull
    @Override
    public KommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View kommentView = inflater.inflate(R.layout.item_comment, parent, false);
        KommentAdapter.ViewHolder viewHolder = new ViewHolder(kommentView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull KommentAdapter.ViewHolder holder, int position) {
        final Komment komment = mKomments.get(position);

        holder.tvUsername.setText(komment.getUser().getUsername());
        holder.tvText.setText(komment.getText());
        holder.tvTimestamp.setText(komment.getRelativeTime());

        ParseUser user = komment.getUser();

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
                    .bitmapTransform(new CropCircleTransformation(context)).into(holder.ivProfile);
        }

    }

    @Override
    public int getItemCount() {
        return mKomments.size();
    }

    public void clear() {
        mKomments.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Komment> list) {
        mKomments.addAll(list);
        notifyDataSetChanged();
    }

    public void add(Komment komment) {
        mKomments.add(komment);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfile;
        TextView tvUsername;
        TextView tvText;
        TextView tvTimestamp;

        public ViewHolder(View itemView) {
            super(itemView);

            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvText = itemView.findViewById(R.id.tvText);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            ivProfile = itemView.findViewById(R.id.ivProfile);

        }
    }
}
