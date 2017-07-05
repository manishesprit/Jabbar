package com.jabbar.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jabbar.R;
import com.jabbar.Ui.AddStoryActivity;
import com.jabbar.Ui.PreviewStoryActivity;

import java.util.ArrayList;


public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyHolder> {

    private ArrayList<String> imageArrayList;
    private Context context;

    public GalleryAdapter(Context context, ArrayList<String> imageArrayList) {
        this.context = context;
        this.imageArrayList = imageArrayList;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_gallery_image, null);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {

        Glide.with(context).load(imageArrayList.get(position)).fitCenter().into(holder.imgGallery);

        holder.imgGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = PreviewStoryActivity.newIntentPhoto(context, imageArrayList.get(position));
                ((Activity) context).startActivityForResult(intent, AddStoryActivity.REQUEST_PREVIEW_CODE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageArrayList.size();
    }


    public class MyHolder extends RecyclerView.ViewHolder {

        public ImageView imgGallery;

        public MyHolder(View view) {
            super(view);
            imgGallery = (ImageView) view.findViewById(R.id.imgGallery);
        }
    }
}
