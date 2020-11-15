package com.smartdigital.medicine;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

import static com.smartdigital.medicine.util.Constants.*;

//todo: deprecated code
public class AlarmReceiver extends BroadcastReceiver
{
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        this.context = context;
        String TAG = "alarm";
        Log.d(TAG, "received");
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
        {
            String toastText = "Alarm Reboot";
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            //reschedule alarms aftr reboot
            new UserDataManager(context).restartAlarms(context);
        }
        else
            {
            String toastText = "Alarm Received";
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "is alarm today " + alarmIsToday(intent));
            if (alarmIsToday(intent))
            {
                createNotificationChannel();
                showNotification(intent.getStringExtra(NAME));
            }
        }

    }

    private boolean alarmIsToday(Intent intent)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int today = calendar.get(Calendar.DAY_OF_WEEK);
        switch(today)
        {
            case Calendar.MONDAY:
                return intent.getBooleanExtra(MONDAY, false);
            case Calendar.TUESDAY:
                return intent.getBooleanExtra(TUESDAY, false);
            case Calendar.WEDNESDAY:
                return intent.getBooleanExtra(WEDNESDAY, false);
            case Calendar.THURSDAY:
                return intent.getBooleanExtra(THURSDAY, false);
            case Calendar.FRIDAY:
                return intent.getBooleanExtra(FRIDAY, false);
            case Calendar.SATURDAY:
                return intent.getBooleanExtra(SATURDAY, false);
            case Calendar.SUNDAY:
                return intent.getBooleanExtra(SUNDAY, false);
        }
        return false;
    }

    private void showNotification(String name)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PACKAGE_NAME)
                .setSmallIcon(R.drawable.ic_magnify_black_48dp)
                .setContentTitle(name)
                .setContentText("Medicine time")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }



    private void createNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String description = "Medicine App";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(PACKAGE_NAME, PACKAGE_NAME, importance);
            channel.setDescription(description);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}