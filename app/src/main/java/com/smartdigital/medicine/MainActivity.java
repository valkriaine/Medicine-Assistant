package com.smartdigital.medicine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ramotion.fluidslider.FluidSlider;
import com.smartdigital.medicine.Database.DBHelper;
import com.valkriaine.factor.HomePager;


public class MainActivity extends AppCompatActivity {

    private final UserDataManager userDataManager = new UserDataManager();
    private DBHelper databaseHelper;
    private SQLiteDatabase drugs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //access drugs.db
        databaseHelper = new DBHelper(this);
        drugs = databaseHelper.getWritableDatabase();

        //setup HomePager
        HomePager homePager = findViewById(R.id.view_pager);
        CardView searchCard = findViewById(R.id.search_box);
        homePager.addView(findViewById(R.id.search_page), 0);
        homePager.addView(findViewById(R.id.setup_page), 1);
        homePager.addOnPageChangeListener(new OnPageChangeListener(searchCard));

        //todo: remove this line
        //generate dummy data for test purpose
        userDataManager.generateDummyData();

        //setup RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userDataManager.getAdapter());



        //example on how to retrieve position from slider
        final float[] positionDuration = {0f};
        FluidSlider slider = findViewById(R.id.duration_slider);
        slider.setPositionListener(aFloat -> {
            //do not update UI here
            positionDuration[0] = aFloat;
            return null;
        });
        slider.setEndTrackingListener(() -> {
            //update UI here
            Toast.makeText(MainActivity.this, String.valueOf(positionDuration[0]), Toast.LENGTH_SHORT).show();
            return null;
        });



        //example call to access the drugs database
        //this prints the index of the column with name "ACCESSION"
        Cursor c = drugs.rawQuery("select * from drugs limit 1 offset $index", null);
        int index = c.getColumnIndex("ACCESSION");
        Toast.makeText(MainActivity.this,  String.valueOf(index), Toast.LENGTH_SHORT).show();

    }



    //todo: remove this method
    //add new UserMedicine to the RecyclerView
    public void dummyButtonClicked(View view)
    {
        userDataManager.add(new UserMedicine("Random Drug"));
    }
}