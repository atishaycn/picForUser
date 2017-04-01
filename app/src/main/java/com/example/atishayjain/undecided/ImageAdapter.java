package com.example.atishayjain.undecided;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StringLoader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import Models.Resource;


/**
 * Created by atishayjain on 31/03/17.
 */

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private LayoutInflater inflater;

    public static final int VIEW_ITEM = 0;
    public static final int VIEW_PROGRESS = 1;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.share:
                break;
            case R.id.download:
            break;
        }


    }

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
    ArrayList<String> linkArray = new ArrayList<String>();
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof MoviewViewHolder){
            final MoviewViewHolder movieNewHolder = (MoviewViewHolder) holder;
            if(mlist.get(position).getUrl() != null && !mlist.get(position).getUrl().isEmpty()) {
                String link = mlist.get(position).getUrl();
                boolean same = false;
                if(linkArray.size() == 0){
                    linkArray.add(link);
                    same = true;
                }
                else{
                    for(int i = 0; i < linkArray.size(); i++){
                        if(linkArray.get(i).equalsIgnoreCase(link)){
                            Log.d("It is same", "SAME");
                            same = true;
                        }
                        else{
                            Log.d("It is NOT same", "NOT_same");
                        }
                    }
                }
                if(!same){
                    linkArray.add(link);
                }
                else{

                }
               // Picasso.with(cont).load(link).placeholder(R.mipmap.black16).into(movieNewHolder.movieImage);
                Glide.with(cont).load(link).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.black16).dontAnimate().into(movieNewHolder.movieImage);
                movieNewHolder.shareImage.setOnClickListener(this);
                movieNewHolder.downloadImage.setOnClickListener(this);
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


        public MoviewViewHolder(View itemView) {
            super(itemView);
            movieImage = (ImageView) itemView.findViewById(R.id.movieImage);
            shareImage = (ImageView) itemView.findViewById(R.id.share);
            downloadImage = (ImageView) itemView.findViewById(R.id.download);
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
