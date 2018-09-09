package com.training.dr.androidtraining.ulils;


import android.util.Log;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public final class ApiUtils {
    private static final String TAG = ApiUtils.class.getSimpleName();

    private ApiUtils() {
    }

    public static String parseDate(String dateFromApi) {
        SimpleDateFormat myDateFormat = new SimpleDateFormat("E MMM D hh:mm:ss Z yyyy");
        try {
            Date date = myDateFormat.parse(dateFromApi);
            myDateFormat.applyPattern("dd.MM.yyyy hh:mm:ss");
            return myDateFormat.format(date);
        } catch (ParseException e) {
            Log.d(TAG, e.getMessage());
            return "";
        }
    }

    public static String getPostDataString(JSONObject params) throws Exception {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator<String> itr = params.keys();
        while (itr.hasNext()) {
            String key = itr.next();
            Object value = params.get(key);
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }
        return result.toString();
    }


}
