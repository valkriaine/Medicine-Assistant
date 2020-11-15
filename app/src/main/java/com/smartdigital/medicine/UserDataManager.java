package com.smartdigital.medicine;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;

//ViewModel class for managing user data in a singleton manner
//create an instance of UserDataManager when app starts
//call the methods below to access/modify/save user data
public class UserDataManager
{
    private final ArrayList <UserMedicine> drugs = new ArrayList<>();
    private final DrugsAdapter adapter;
    private Context context;
    private final int notificationId = 1;

    private PendingIntent alarmIntent;

    //constructor of the view model
    //add anything in the constructor to be initialized when the view model is created
    public UserDataManager(Context context)
    {
        this.context = context;
        getUserData();
        adapter = new DrugsAdapter();
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("notificationId", notificationId);

        alarmIntent = PendingIntent.getBroadcast(context,0,intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    //call this method to retrieve the location list from a local file or a database
    public void getUserData()
    {
        //todo: get saved medicines here
    }

    //call this method to save the list to a local file or a database
    public void updateUserData()
    {
        adapter.notifyDataSetChanged();
        //todo: update current save of user data
    }

    //call this method to add a location to the list
    public void add(UserMedicine med)
    {
        med.setId(drugs.size());


        boolean[] daysOfWeek = med.getDayOfWeekList();

        for (int i = 0; i < 7; i++)
        {
            Log.d("hahaha", String.valueOf(med.getHour()));
            Log.d("hahaha", String.valueOf(med.getMinute()));
            Log.d("hahaha", String.valueOf(i));
            if (daysOfWeek[i])
            {
                long _id = (long)med.getId();

                AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                Calendar calendar = Calendar.getInstance();


                calendar.set(Calendar.HOUR_OF_DAY, med.getHour());
                calendar.set(Calendar.MINUTE, med.getMinute());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                calendar.set(Calendar.DAY_OF_WEEK, i);

                Intent intent = new Intent(context, AlarmReceiver.class);
                alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);


                /** Converting the date and time in to milliseconds elapsed since epoch */
                long alarm_time = calendar.getTimeInMillis();

                if (calendar.before(Calendar.getInstance()))
                    alarm_time += AlarmManager.INTERVAL_DAY * 7;

                assert alarmManager != null;
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, alarm_time,
                        AlarmManager.INTERVAL_DAY * 7, alarmIntent);

                updateUserData();
            }
        }

        drugs.add(med);



    }

    //call this method to remove saved location from the list
    public void remove(UserMedicine med)
    {
        drugs.remove(med);
        updateUserData();
    }

    //return the RecyclerView adapter
    public DrugsAdapter getAdapter()
    {
        return this.adapter;
    }





    //custom adapter for the list
    private class DrugsAdapter extends Adapter
    {
        @NonNull
        @NotNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.medicine_list_item_layout, parent, false);
            return new LocationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position)
        {
            UserMedicine m = drugs.get(position);
            LocationViewHolder locationViewHolder = (LocationViewHolder)holder;
            locationViewHolder.name.setText(m.getName());
            locationViewHolder.time.setText(m.getHour() + ":" + m.getMinute());
        }

        @Override
        public int getItemCount() {
            return drugs.size();
        }

        //custom ViewHolder for displaying each item in the list
        private class LocationViewHolder extends RecyclerView.ViewHolder
        {

            private final TextView name;
            private final TextView time;
            public LocationViewHolder(View itemView)
            {
                super(itemView);
                name = itemView.findViewById(R.id.text);
                time = itemView.findViewById(R.id.time);

            }
        }
    }
}
