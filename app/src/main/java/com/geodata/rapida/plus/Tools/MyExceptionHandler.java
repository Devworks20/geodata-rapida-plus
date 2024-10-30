package com.geodata.rapida.plus.Tools;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.geodata.rapida.plus.Activity.FirstActivity;

public class MyExceptionHandler implements Thread.UncaughtExceptionHandler
{
    private Activity activity;

    public MyExceptionHandler(Activity a)
    {
        activity = a;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e)
    {
        Intent intent = new Intent(activity, FirstActivity.class);
        intent.putExtra("crash", true);
        intent.putExtra("crashed", e.getMessage());

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(MyApplication.getInstance().getBaseContext(),
                0, intent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager mgr = (AlarmManager) MyApplication.getInstance().getBaseContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);

        activity.finish();

        System.exit(2);
    }
}
