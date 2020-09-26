package com.nnoboa.istudy.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtil {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String PREF;
    Context context;
    public PreferenceUtil(Context context, String PREF) {
        this.context = context;
        this.PREF = PREF;
    }

    public void writePref(String key, String value){
        preferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        editor.putString(key,value);
        editor.commit();
    }

    public String readStringPref(String key){
        String value = preferences.getString(key,"");
        return value;
    }

    public int readIntPref(String key){
        preferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        int value = preferences.getInt(key,0);
        return value;
    }

}
