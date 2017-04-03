package com.example.atishayjain.undecided;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Models.Resource;

import static android.support.v4.app.ActivityCompat.requestPermissions;


/**
 * Created by atishayjain on 31/03/17.
 */

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int WRITE_STORAGE_REQUEST = 1001;
    private LayoutInflater inflater;

    public static final int VIEW_ITEM = 0;
    public static final int VIEW_PROGRESS = 1;

    public interface retryButtonClicked {
        void onClickRetry();
    }

    retryButtonClicked mretryButtonClicked;


    Context cont;
    int responseCode=0;
    //Model data  = new Model();
    public List<Resource> mlist = new ArrayList<Resource>();
    //= Collections.emptyList();
    public ImageAdapter(Context context, List<Resource> mlist, int responseCode, retryButtonClicked mretryButtonClicked){
        inflater = LayoutInflater.from(context);
//        this.data = data;
        cont = context;
        this.mretryButtonClicked = mretryButtonClicked;
//        mlist.addAll(data.getResults());
//        Log.d("List", String.valueOf(mlist));
//        Log.d("Size", String.valueOf(mlist.size()))
        this.mlist = mlist;
        this.responseCode = responseCode;
        //this.mlist = mlist;
    }



//    @Override
//    public MoviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//
//        MoviewViewHolder holder;
//        ProgressViewHolder progressViewHolder;
//        if(viewType == VIEW_ITEM) {
//            View view = inflater.inflate(R.layout.movieblock, parent, false);
//            holder = new MoviewViewHolder(view);
//        }
//        else{
//            View view = inflater.inflate(R.layout.progress, parent, false);
//            progressViewHolder = new ProgressViewHolder(view);
//        }
//
//    }
//
//


    MoviewViewHolder moviewViewHolderholder;
    ProgressViewHolder progressViewHolder;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        if(viewType == VIEW_ITEM){
            view = inflater.inflate(R.layout.movieblock, parent, false);
            Log.d("Response code", String.valueOf(responseCode));
            return moviewViewHolderholder = new MoviewViewHolder(view);
        }
        else{
            view = inflater.inflate(R.layout.progress, parent, false);
            Log.d("Response code", String.valueOf(responseCode));
            return progressViewHolder = new ProgressViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof MoviewViewHolder){
            final MoviewViewHolder movieNewHolder = (MoviewViewHolder) holder;
            movieNewHolder.mImageProgressBar.setVisibility(View.VISIBLE);
            if(mlist.get(position).getUrl() != null && !mlist.get(position).getUrl().isEmpty()) {
                final String link = mlist.get(position).getUrl();
               // Picasso.with(cont).load(link).placeholder(R.mipmap.black16).into(movieNewHolder.movieImage);
                //Glide.with(cont).load(link).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.black16).dontAnimate().into(movieNewHolder.movieImage);
                Glide.with(cont).load(link).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).dontAnimate().into(new BitmapImageViewTarget(movieNewHolder.movieImage)
                {
                   @Override
                    public void onResourceReady(final Bitmap bmp, GlideAnimation anim) {
                       Glide.with(cont).load(link).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.black16).dontAnimate().into(movieNewHolder.movieImage);
                       movieNewHolder.mImageProgressBar.setVisibility(View.INVISIBLE);
                       movieNewHolder.shareImage.setOnClickListener(new View.OnClickListener(){
                           @Override
                           public void onClick(View view){
                               shareImage(bmp);
                           }
                       });

                       movieNewHolder.downloadImage.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               if(permissionGranted()) {
                                   saveImageToGallery(bmp);
                               }
                               else{

                               }
                           }
                       });
                   }
                   });

            }
            else{
                Glide.clear(movieNewHolder.movieImage);
                movieNewHolder.movieImage.setImageDrawable(null);
            }


        }
        else if(holder instanceof ProgressViewHolder){
            StaggeredGridLayoutManager.LayoutParams ls = (StaggeredGridLayoutManager.LayoutParams) progressViewHolder.itemView.getLayoutParams();
            ls.setFullSpan(true);
        }
    }

    private boolean permissionGranted() {
        if(cont instanceof MainActivity) {
            if(ContextCompat.checkSelfPermission(cont, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(cont, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE_REQUEST);
            }
        }
        return false;
    }




    private void shareImage(final Bitmap bmp) {
        final Uri bmpUri = getLocalBitmapUrl(bmp);
        Intent shareIntent = new Intent();
        //shareIntent.setPackage("com.whatsapp");
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Shared via UNDECIDED");
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        shareIntent.setType("image/*");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cont.startActivity(Intent.createChooser(shareIntent, "SHARE"));
    }

    private void saveToGallery(String absolutePath) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.MediaColumns.DATA, absolutePath);
        cont.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Toast toast = Toast.makeText(cont, "Saved to Gallery", Toast.LENGTH_SHORT);
        toast.show();
    }

    private void saveImageToGallery(Bitmap bmp){
        File file = new File(cont.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            try {
                out.close();
                saveToGallery(file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private Uri getLocalBitmapUrl(Bitmap bmp) {
        Uri bmpUri = null;
        File file = new File(cont.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bmpUri = Uri.fromFile(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }


//    @Override
//    public void onBindViewHolder(MoviewViewHolder holder, final int position) {
//
//        if(position < mlist.size() ) {
//            //MoviesList current = data.get(position);
//            holder.movieName.setText(mlist.get(position).getTitle());
//            Log.d("title", mlist.get(position).getTitle());
//            Log.d("Position", String.valueOf(position));
////        Log.d("test", mlist.get(position).getPosterPath());
//            Picasso.with(cont).load("https://image.tmdb.org/t/p/w500" + mlist.get(position).getPosterPath()).into(holder.movieImage);
//            holder.movieImage.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    Bundle bundle = new Bundle();
//                    bundle.putString("over", mlist.get(position).getOverview());
//                    bundle.putString("id", String.valueOf(mlist.get(position).getId()));
//                    Intent intent = new Intent(cont, MovieDetails.class);
//                    intent.putExtras(bundle);
//                    cont.startActivity(intent);
//
//                }
//            });
//        }
//        else{
//
////            holder instanceof  ? (() holder) : null;
//        }
//
//        //Glide.with(cont).load("https://image.tmdb.org/t/p/w500" + mlist.get(position).getPosterPath()).placeholder(R.mipmap.black16).into(holder.movieImage);
//        //Glide.with(cont).load("https://image.tmdb.org/t/p/w500" + mlist.get(position).getPosterPath()).into(holder.movieImage);
//        //Glide.with(this).load(mlist.get(position).getPosterPath()).into(holder.movieImage);
////        holder.movieImage.setImageResource(Integer.parseInt(mlist.get(position).getPosterPath()));
//    }

    @Override
    public int getItemCount() {
        return mlist.size() + 1 ;
    }

    ProgressBar progressBar;
    TextView noInternet;

    @Override
    public long getItemId(int position) {
        return  position;
    }

    Button tryAgain;

    class MoviewViewHolder extends RecyclerView.ViewHolder{

        ImageView movieImage, shareImage, downloadImage;
        ProgressBar mImageProgressBar;


        public MoviewViewHolder(View itemView) {
            super(itemView);
            movieImage = (ImageView) itemView.findViewById(R.id.movieImage);
            shareImage = (ImageView) itemView.findViewById(R.id.share);
            downloadImage = (ImageView) itemView.findViewById(R.id.download);
            mImageProgressBar = (ProgressBar) itemView.findViewById(R.id.imageProgressBar);
        }
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {


        public ProgressViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
            noInternet = (TextView) itemView.findViewById(R.id.noInternet);
            tryAgain = (Button) itemView.findViewById(R.id.tryAgain);
            tryAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Try Agaon Pressed", String.valueOf(responseCode));
                    mretryButtonClicked.onClickRetry();
                }
            });
        }
    }


    @Override
    public int getItemViewType(int position) {
        Log.d("position", String.valueOf(position));
        Log.d("Size", String.valueOf(mlist.size()));
        if(position >= mlist.size()){
            return VIEW_PROGRESS;
        }
        else{
            return VIEW_ITEM;
        }
        //return super.getItemViewType(position);
    }
}
