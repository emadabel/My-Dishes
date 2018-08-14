package com.emadabel.mydishes.firebase;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class Analytics {

    public static void logEventSearch(Context context, String query) {
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.SEARCH_TERM, query);
        FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.SEARCH, params);
    }
}
