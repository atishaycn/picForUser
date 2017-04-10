package com.example.atishayjain.undecided;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import Models.FirebaseData;
import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.List;

import Models.MainModel;
import Models.Resource;

public class MainActivity extends AppCompatActivity implements ImageData.ImagesLoaded, ImageAdapter.retryButtonClicked, View.OnClickListener {

    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager layoutManager;
    private ImageAdapter adapter;
    public List<Resource> mlist = new ArrayList<Resource>();
    private String nextCursor ="";
    private TextView mNoInternetTextView;
    private Button mtryAgainButton;
    private ProgressBar mProgressBar;
    private LinearLayout internetLL;
    private FirebaseAnalytics mFirebaseAnalytics;
    private String mFirebaseUser, mFirebasePassword;
    private final String FIREBASE_ADDRESS = "https://picsforuser.firebaseio.com/";
    private boolean firstTime = true;
    private Answers mFabricAnswers;
    private static final int WRITE_STORAGE_REQUEST = 1001;
    private double mStartTime,mEndTime, mStartCTime, mEndCTime;
    private boolean stillLoading = true;
    private String mFirebaseUrl;
    private SharedPrefrencesManager mSharedPrefrencesManager;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPrefrencesManager = new SharedPrefrencesManager(getApplicationContext());
        mStartTime = System.currentTimeMillis();
        Fabric.with(this, new Crashlytics());
        mFabricAnswers = Answers.getInstance();
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        getUserFromFirebase();
        initViews();
    }

    private void getUserFromFirebase() {
        DatabaseReference userDetails = FirebaseDatabase.getInstance().getReferenceFromUrl(FIREBASE_ADDRESS);
        userDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                stillLoading = false;
                FirebaseData mFirebase = dataSnapshot .getValue(FirebaseData.class);
                    mFirebaseUser = mFirebase.getUsername();
                    mFirebasePassword = mFirebase.getPassword();
                    mFirebaseUrl = mFirebase.getAppurl();
                    mEndTime = System.currentTimeMillis();
                double mTotalTime = mEndTime - mStartTime;
                Bundle bundle = new Bundle();
                if(mFirebaseUser != null && mFirebasePassword != null && firstTime){
                    firstTime = false;
                    loadImages();
                    bundle.putString("User_Name", mFirebaseUser);
                    bundle.putString("User_Password", mFirebasePassword);
                    tagFabricDataEvent(mFirebaseUser, mFirebasePassword, mTotalTime);
                }
                else{
                    mProgressBar.setVisibility(View.GONE);
                    mNoInternetTextView.setVisibility(View.VISIBLE);
                    mtryAgainButton.setVisibility(View.VISIBLE);
                    internetLL.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, "Problem Loading Data Please Try Again Later", Toast.LENGTH_LONG).show();
                    if(mFirebaseUser == null && mFirebasePassword == null){
                        bundle.putString("User_Name", "Error");
                        bundle.putString("User_Password", "Error");
                        tagFabricDataEvent("Error", "Error", mTotalTime);
                    }
                    else if(mFirebaseUser == null){
                        bundle.putString("User_Name", "Error");
                        bundle.putString("User_Password", mFirebasePassword);
                        tagFabricDataEvent("Error", mFirebasePassword, mTotalTime);
                    }
                    else {
                        bundle.putString("User_Name", mFirebaseUser);
                        bundle.putString("User_Password", "Error");
                        tagFabricDataEvent(mFirebaseUser,"Error", mTotalTime) ;
                    }
                }
                bundle.putString("Total_Time", String.valueOf(mTotalTime));
                mFirebaseAnalytics.logEvent("Firebase_Data", bundle);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(stillLoading){
                    mProgressBar.setVisibility(View.GONE);
                    mNoInternetTextView.setVisibility(View.VISIBLE);
                    mtryAgainButton.setVisibility(View.VISIBLE);
                    internetLL.setVisibility(View.VISIBLE);
                    tagFirebaseTimeOutEvent();

                }
            }
        }, 15000);
    }

    private void tagFirebaseTimeOutEvent() {
        mFabricAnswers.logCustom(new CustomEvent("Firebase_Timeout"));
        Bundle bundle = new Bundle();
        bundle.putString("Timeout", "Firebase_Timeout");
        mFirebaseAnalytics.logEvent("FirebaseTimeout", bundle);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == WRITE_STORAGE_REQUEST) {
            ImageAdapter.onResult(grantResults[0]);
        }

    }


    private void tagFabricDataEvent(String userName, String password, Double totalTime) {
        mFabricAnswers.logCustom(new CustomEvent(
                "FirebaseData")
        .putCustomAttribute("User Name", userName)
        .putCustomAttribute("Password", password)
        .putCustomAttribute("Total Time Firebase", String.valueOf(totalTime))
        );
    }

    private void loadImages() {
        String nextCursorSaved = "";
        if(!mSharedPrefrencesManager.getNext_Cursor().equalsIgnoreCase("new")){
            nextCursorSaved = mSharedPrefrencesManager.getNext_Cursor();
        }
        new ImageData(this).execute(nextCursorSaved, mFirebaseUser, mFirebasePassword);
        mStartCTime = System.currentTimeMillis();
    }

    private void initViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mNoInternetTextView = (TextView) findViewById(R.id.internet);
        mtryAgainButton = (Button) findViewById(R.id.tryAgain);
        mtryAgainButton.setOnClickListener(this);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        //layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        layoutManager.setAutoMeasureEnabled(false);
        mRecyclerView.setLayoutManager(layoutManager);
        //mRecyclerView.setHasFixedSize(true);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mRecyclerView.addOnScrollListener(mRecyclerScrollListener);
        internetLL = (LinearLayout) findViewById(R.id.internetLL);
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
            if((firstItem[0] + visibleItemAmount) >= totalCount - 5 && firstItem[0] >= 0 && totalCount >= 9 ){
                if(!isLoading) {
                    isLoading = true;
                    Log.d("size", nextCursor);
                    loadNextPage();
                }
            }
        }
    };


    public void loadNextPage(){

        Bundle bundle = new Bundle();
        bundle.putString("page", String.valueOf(currentPage));
        mFirebaseAnalytics.logEvent("Page_Numbers", bundle);
        mFabricAnswers.logCustom(new CustomEvent(
                "Page_Numbers")
                .putCustomAttribute("page", String.valueOf(currentPage))
        );
        mStartCTime = System.currentTimeMillis();
        new ImageData(MainActivity.this).execute(nextCursor, mFirebaseUser, mFirebasePassword);
        if(adapter.progressBar != null && adapter.tryAgain != null && adapter.noInternet != null) {
            adapter.progressBar.setVisibility(View.VISIBLE);
            adapter.tryAgain.setVisibility(View.GONE);
            adapter.noInternet.setVisibility(View.GONE);
        }
    }



    MainModel data = new MainModel();
    @Override
    public void getImages(String stringBuilder, int responseCode) {
        Bundle bundle = new Bundle();
        mEndCTime = System.currentTimeMillis();
        double mTotalCTime = mEndCTime - mStartCTime;
        bundle.putString("Total_CloudinaryTime", String.valueOf(mTotalCTime));
        if (stringBuilder != "Error" && responseCode == 200) {
            bundle.putString("Response_Code", String.valueOf(responseCode));
            bundle.putString("Response", stringBuilder);
            tagFabricResponse(String.valueOf(responseCode), stringBuilder, mTotalCTime);
            mProgressBar.setVisibility(View.GONE);
            mNoInternetTextView.setVisibility(View.GONE);
            mtryAgainButton.setVisibility(View.GONE);
            internetLL.setVisibility(View.GONE);
            Gson gson = new Gson();
            data = gson.fromJson(String.valueOf(stringBuilder), MainModel.class);
            currentPage ++;
            Log.d("MoviesData", String.valueOf(data));
            Log.d("D" +
                    "ataMeta", String.valueOf(data.getResources()));
            Log.d("Size", String.valueOf(data.getResources().size()));
            mlist.addAll(data.getResources());
            if(data.getNextCursor() != null) {
                nextCursor = data.getNextCursor();
            }
            else{
                nextCursor = "";
            }
            mSharedPrefrencesManager.setNextCursor(nextCursor);
            if (adapter == null) {
                adapter = new ImageAdapter(this, mlist, responseCode, this, mFirebaseAnalytics);
                mRecyclerView.setAdapter(adapter);
                adapter.setShareLink(mFirebaseUrl);
            } else {
                //adapter.notifyDataSetChanged();
                adapter.notifyItemRangeInserted(adapter.getItemCount(), mlist.size() - 1 );
            }
//            if (progressBar != null) {
//                progressBar.setVisibility(View.GONE);
//            }
        } else {
            // Toast.makeText(MainActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
            bundle.putString("Response_Code", String.valueOf(responseCode));
            bundle.putString("Response", stringBuilder);
            bundle.putString("Cursor_is", nextCursor);
            tagFabricResponse(String.valueOf(responseCode), stringBuilder, mTotalCTime);
            if (currentPage != 1) {
                if(adapter != null && adapter.progressBar != null && adapter.tryAgain != null && adapter.noInternet != null) {
                    adapter.progressBar.setVisibility(View.GONE);
                    adapter.tryAgain.setVisibility(View.VISIBLE);
                    adapter.noInternet.setVisibility(View.VISIBLE);
                    adapter.notifyItemChanged(mlist.size());
                }
            }
            else{
                if(mNoInternetTextView.getVisibility() == View.GONE) {
                    mProgressBar.setVisibility(View.GONE);
                    mNoInternetTextView.setVisibility(View.VISIBLE);
                    mtryAgainButton.setVisibility(View.VISIBLE);
                    internetLL.setVisibility(View.VISIBLE);
                }
            }
        }
        mFirebaseAnalytics.logEvent("LoadedPageResponse", bundle);

        isLoading = false;
    }

    private void tagFabricResponse(String responseCode, String response, Double mTotalTime) {
        mFabricAnswers.logCustom(new CustomEvent(
                "LoadedPageResponse")
                .putCustomAttribute("Response_Code", responseCode)
                .putCustomAttribute("Response", response)
                .putCustomAttribute("Total Cloudinary Time", String.valueOf(mTotalTime))
        );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tryAgain:
                new ImageData(this).execute("", mFirebaseUser, mFirebasePassword);
                mNoInternetTextView.setVisibility(View.GONE);
                mtryAgainButton.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
                internetLL.setVisibility(View.GONE);
                Bundle bundle = new Bundle();
                bundle.putString("Try_Again_Clicked", "Clicked");
                mFirebaseAnalytics.logEvent("Try_Again", bundle);
                mFabricAnswers.logCustom(new CustomEvent(
                        "Try_Again")
                        .putCustomAttribute("Try_Again_Clicked", "Clicked")
                );
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mSharedPrefrencesManager.setNextCursor(nextCursor);
    }
}
