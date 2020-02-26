package com.graspery.www.spicemeup.CustomAdapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.graspery.www.spicemeup.Dialogs.CastProfileDialog;
import com.graspery.www.spicemeup.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

public class CastRecyclerViewAdapter extends RecyclerView.Adapter<CastRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public CastRecyclerViewAdapter(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.cast_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        try {
            JSONObject jsonObject = new JSONObject(mData.get(position));

            holder.myTextView.setText(jsonObject.getString("name"));

            Glide.with(context).load("https://image.tmdb.org/t/p/w600_and_h900_bestv2" + jsonObject.getString("profile_path")).asBitmap()
                    .into(imageUpload(holder.actorImage));

            holder.actorImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CastProfileDialog castProfileDialog = new CastProfileDialog((Activity)context,jsonObject);
                    castProfileDialog.show();
                }
            });

        } catch (JSONException e) {}
/*
        String name = mData.get(position)[0];
        holder.myTextView.setText(animal);*/

        /* Glide.with(context).load(mData.get(position)[1]).asBitmap()
                            .into(imageUpload(holder.actorImage));*/
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    private SimpleTarget<Bitmap> imageUpload(ImageView poster) {
        return new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                //animFadeIn.reset();
                //poster3.clearAnimation();
                //poster.startAnimation(animFadeIn);
                poster.setImageBitmap(getRoundedCornerBitmap(resource, 40));
            }
            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                //animFadeIn.reset();
                //poster3.clearAnimation();
                //poster.startAnimation(animFadeIn);
                poster.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_foreground));
            }
        };
    }

    public Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        ImageView actorImage;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.actor_name);
            actorImage = itemView.findViewById(R.id.actor_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}