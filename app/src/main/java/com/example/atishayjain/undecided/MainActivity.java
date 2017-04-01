package com.example.atishayjain.undecided;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import com.google.gson.Gson;


import java.util.ArrayList;
import java.util.List;

import Models.MainModel;
import Models.Resource;

public class MainActivity extends AppCompatActivity implements ImageData.ImagesLoaded, ImageAdapter.retryButtonClicked {

    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager layoutManager;
    private ImageAdapter adapter;
    public List<Resource> mlist = new ArrayList<Resource>();
    private String nextCursor ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        //layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        layoutManager.setAutoMeasureEnabled(false);
        mRecyclerView.setLayoutManager(layoutManager);
        //mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnScrollListener(mRecyclerScrollListener);
        new ImageData(this).execute("");
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
            if((firstItem[0] + visibleItemAmount) >= totalCount && firstItem[0] >= 0 && totalCount >= 9){

//                if(i == 1){

                if(!isLoading) {
//                        progressBar.setVisibility(View.VISIBLE);
                    //
                    isLoading = true;
                    Log.d("size", nextCursor);
//                        new MoviesData(MainActivity.this).execute(String.valueOf(currentPage));
                    loadNextPage();
                }

//                    i = 2;
//                }
            }



        }
    };


    public void loadNextPage(){

        new ImageData(MainActivity.this).execute(nextCursor);
        if(adapter.progressBar != null && adapter.tryAgain != null && adapter.noInternet != null) {
            adapter.progressBar.setVisibility(View.VISIBLE);
            adapter.tryAgain.setVisibility(View.GONE);
            adapter.noInternet.setVisibility(View.GONE);
        }
    }



    MainModel data = new MainModel();
    @Override
    public void getImages(String stringBuilder, int responseCode) {
        if (stringBuilder != "Error" && responseCode == 200) {
            Gson gson = new Gson();
            data = gson.fromJson(String.valueOf(stringBuilder), MainModel.class);
            currentPage ++;
            Log.d("MoviesData", String.valueOf(data));
            Log.d("D" +
                    "ataMeta", String.valueOf(data.getResources()));
            Log.d("Size", String.valueOf(data.getResources().size()));
            mlist.addAll(data.getResources());
            nextCursor = data.getNextCursor();
            if (adapter == null) {
                adapter = new ImageAdapter(this, mlist, responseCode, this);
                mRecyclerView.setAdapter(adapter);
            } else {
                //adapter.notifyDataSetChanged();
                adapter.notifyItemRangeInserted(adapter.getItemCount(), mlist.size() - 1 );
            }
//            if (progressBar != null) {
//                progressBar.setVisibility(View.GONE);
//            }
        } else {
            // Toast.makeText(MainActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
            if (currentPage != 1) {
                adapter.progressBar.setVisibility(View.GONE);
                adapter.tryAgain.setVisibility(View.VISIBLE);
                adapter.noInternet.setVisibility(View.VISIBLE);
            }
        }
        isLoading = false;
    }

}
