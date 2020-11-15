package com.smartdigital.medicine;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.room.Room;
import com.smartdigital.medicine.database.UserMedicineDatabase;
import com.smartdigital.medicine.model.UserMedicine;
import com.smartdigital.medicine.uihelper.CovertManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;

//ViewModel class for managing user data in a singleton manner
//create an instance of UserDataManager when app starts
//call the methods below to access/modify/save user data
public class UserDataManager
{
    private final ArrayList <UserMedicine> drugs = new ArrayList<>();
    private final DrugsAdapter adapter;
    private final UserMedicineDatabase db;
    private final Context context;

    private CovertManager covertManager;

    private Activity activity;


    //constructor for the broadcast receiver to re-enable alarms after certain broadcasts (ie. system reboot)
    public UserDataManager(Context context)
    {
        this.context = context;

        adapter = new DrugsAdapter();
        db = Room.databaseBuilder(context, UserMedicineDatabase.class, "medicine_database").build();
        getUserData();
    }

    //constructor for when the app is running in the foreground
    public UserDataManager(Activity activity, RecyclerView rc)
    {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        covertManager = new CovertManager(rc, this);
        adapter = new DrugsAdapter();
        db = Room.databaseBuilder(context, UserMedicineDatabase.class, "medicine_database").build();
        getUserData();
    }

    //call this method to retrieve the location list from a local file or a database
    public void getUserData()
    {
        new Thread(() ->
        {
            drugs.addAll(db.dao().getAll());
            activity.runOnUiThread(() ->
            {
                adapter.notifyDataSetChanged();
                restartAlarms();
            });
        }).start();

    }

    //re-add alarms to system after reboot
    public void restartAlarms(Context c)
    {
        new Thread(() ->
        {
            drugs.addAll(db.dao().getAll());
            SystemClock.sleep(10000);
            restartAlarms(c);
        }).start();
    }

    //re-add alarms to system, will skip alarms that already exist
    private void restartAlarms()
    {
        new Thread(() -> activity.runOnUiThread(() ->
        {
            for (UserMedicine u: drugs)
            {
                u.schedule(context);
            }
        })).start();
    }


    //call this method to add a location to the list
    public void add(UserMedicine med)
    {
        for (UserMedicine u: drugs)
        {
            if (u.getName().equals(med.getName()) && u.getTargetName().equals(med.getTargetName()))
                return;
        }

        med.setId(new Random().nextInt(Integer.MAX_VALUE));
        while (drugs.contains(med))
            med.setId(new Random().nextInt(Integer.MAX_VALUE));

        med.schedule(context);
        new Thread(() ->
        {
            drugs.add(med);
            db.dao().insert(med);
            activity.runOnUiThread(adapter::notifyDataSetChanged);
        }).start();

    }


    //check if user drug list contains drug at give position
    //needed for swipe-to-remove
    public boolean contains(int adapterPosition)
    {
        return drugs.get(adapterPosition) == null;
    }


    //call this method to remove saved location from the list
    public void remove(int adapterPosition)
    {
        UserMedicine med = drugs.get(adapterPosition);
        Log.d("isitnull", med.toString());
        new Thread(() ->
        {
            AlarmManager al = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            al.cancel(med.generateIntent(context));
            drugs.remove(med);
            db.dao().delete(med);
            activity.runOnUiThread(adapter::notifyDataSetChanged);
        }).start();
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
            covertManager.getCovert().drawCornerFlag(holder, true);
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
