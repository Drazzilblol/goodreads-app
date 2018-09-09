package com.training.dr.androidtraining.data.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.training.dr.androidtraining.BuildConfig;
import com.training.dr.androidtraining.data.api.ApiMethods;
import com.training.dr.androidtraining.data.services.ApiGetService;


public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = BootReceiver.class.getSimpleName();
    private static final String QUERY = "neal+stephenson";

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(Intent.ACTION_SYNC, null, context, ApiGetService.class);
        i.putExtra("url", BuildConfig.BASE_URL + ApiMethods.SEARCH + "?key=" + BuildConfig.GOODREAD_API_KEY + "&q=" + QUERY);
        PendingIntent pintent = PendingIntent.getService(context, 0, i, 0);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60 * 10 * 1000, pintent);


        Toast.makeText(context, TAG, Toast.LENGTH_LONG).show();

    }
}
