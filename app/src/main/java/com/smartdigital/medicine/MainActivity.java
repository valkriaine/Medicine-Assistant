package com.smartdigital.medicine;

import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    public static HomePager homePager;
    public static CardView searchCard;
    private RecyclerView recyclerView;
    private UserDataManager userDataManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup HomePager
        homePager = findViewById(R.id.view_pager);
        searchCard = findViewById(R.id.search_box);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter();
        homePager.setAdapter(sectionsPagerAdapter);
        homePager.addOnPageChangeListener(new OnPageChangeListener(searchCard));

        //initialize UserDataManager
        userDataManager = new UserDataManager();

        //todo: remove this line
        //generate dummy data for test purpose
        userDataManager.generateDummyData();

        //setup RecyclerView
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userDataManager.getAdapter());

    }



    //todo: remove this method
    //add new UserMedicine to the RecyclerView
    public void dummyButtonClicked(View view)
    {
        userDataManager.add(new UserMedicine("Random Drug"));
    }
}