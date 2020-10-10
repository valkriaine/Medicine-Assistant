package com.smartdigital.medicine;

import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.valkriaine.factor.HomePager;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserDataManager userDataManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup HomePager
        HomePager homePager = findViewById(R.id.view_pager);
        CardView searchCard = findViewById(R.id.search_box);
        homePager.addView(findViewById(R.id.search_page), 0);
        homePager.addView(findViewById(R.id.setup_page), 1);
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