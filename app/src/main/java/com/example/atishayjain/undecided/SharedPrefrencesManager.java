package com.example.atishayjain.undecided;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by atishayjain on 06/04/17.
 */

public class SharedPrefrencesManager {
    private Context mContext;
    private static SharedPreferences mSharedPrefrences;
    private static final String PREFRENCES = "weanddroid_prefrences";

    public SharedPrefrencesManager(Context context){
        mSharedPrefrences = context.getSharedPreferences(PREFRENCES, Context.MODE_PRIVATE);
        mContext = context;
    }

    private static final String Next_Cursor = "Next_Cursor";

    public void setNextCursor(String next_Cursor){
        mSharedPrefrences.edit().putString(Next_Cursor, next_Cursor).commit();
    }

    public static String getNext_Cursor() {
        return mSharedPrefrences.getString(Next_Cursor,"new");
    }
}
