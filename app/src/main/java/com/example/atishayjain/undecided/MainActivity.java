package com.example.atishayjain.undecided;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import com.google.gson.Gson;


import Models.MainModel;

public class MainActivity extends AppCompatActivity implements ImageData.ImagesLoaded {

    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager layoutManager;
    private ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        //layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnScrollListener(mRecyclerScrollListener);
        new ImageData(this).execute("https://api.cloudinary.com/v1_1/dahkpcrbb/resources/image");
    }


    public void onClickRetry(){
        loadNextPage();
    }

    int currentPage = 1;
    boolean isLoading = false;
    ProgressBar progressBar;
    private RecyclerView.OnScrollListener mRecyclerScrollListener = new RecyclerView.OnScrollListener()
    {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemAmount = layoutManager.getChildCount();
            int totalCount = layoutManager.getItemCount();
            int [] firstItem = layoutManager.findFirstVisibleItemPositions(null);
            if((firstItem[0] + visibleItemAmount) >= totalCount && firstItem[0] >= 0 && totalCount >= 19){

//                if(i == 1){

                if(!isLoading) {
//                        progressBar.setVisibility(View.VISIBLE);
                    //
                    isLoading = true;
                    Log.d("size", String.valueOf(currentPage));
//                        new MoviesData(MainActivity.this).execute(String.valueOf(currentPage));
                    loadNextPage();
                }

//                    i = 2;
//                }
            }



        }
    };


    public void loadNextPage(){

        new ImageData(MainActivity.this).execute(String.valueOf(currentPage));
        if(adapter.progressBar != null && adapter.tryAgain != null && adapter.noInternet != null) {
            adapter.progressBar.setVisibility(View.VISIBLE);
            adapter.tryAgain.setVisibility(View.GONE);
            adapter.noInternet.setVisibility(View.GONE);
        }
    }



    MainModel data = new MainModel();
    @Override
    public void getImages(String stringBuilder, int responseCode) {
        if (stringBuilder != "There was an error" && responseCode == 200) {
            Gson gson = new Gson();
            data = gson.fromJson(String.valueOf(stringBuilder), MainModel.class);
            Log.d("MoviesData", String.valueOf(data));
            Log.d("D" +
                    "ataMeta", String.valueOf(data.getResources()));
            Log.d("Size", String.valueOf(data.getResources().size()));

        }
    }


}
