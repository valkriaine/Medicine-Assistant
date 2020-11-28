package com.smartdigital.medicine;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.smartdigital.medicine.model.UserMedicine;

import java.util.Calendar;

import static com.smartdigital.medicine.util.Constants.*;

public class AlarmReceiver extends BroadcastReceiver
{
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        this.context = context;
        String TAG = "alarm";
        Log.d(TAG, "broadcast received");
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
        {
            String toastText = "Restored alarms";
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            //reschedule alarms after reboot
            new UserDataManager(context).restartAlarms(context);
        }
        else {
            UserMedicine med = new UserMedicine(intent.getExtras());
            if (alarmIsToday(intent))
            {
                createNotificationChannel();
                showNotification(med.getName(), med.getTargetName());
            }
            med.reSchedule(context);
        }

    }


    //check if alarm should ring today
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

    //display notification
    private void showNotification(String name, String target)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PACKAGE_NAME)
                .setSmallIcon(R.drawable.pill)
                .setContentTitle("Time to take medicine!")
                .setContentText(target)
                .setSubText(name)
                .setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.alarm))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }



    //create notification channel for medicine assistant
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