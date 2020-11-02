package com.smartdigital.medicine;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.ramotion.fluidslider.FluidSlider;
import com.smartdigital.medicine.util.CustomSuggestionsAdapter;
import com.smartdigital.medicine.util.DrugsDatabaseTable;
import com.smartdigital.medicine.util.OnPageChangeListener;
import com.valkriaine.factor.HomePager;


public class MainActivity extends AppCompatActivity{

    private final UserDataManager userDataManager = new UserDataManager();
    private DrugsDatabaseTable drugsDatabaseTable;
    private MaterialSearchBar searchBar;
    private CustomSuggestionsAdapter suggestionsAdapter;

    private Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //access drugs.db
        drugsDatabaseTable = new DrugsDatabaseTable(this);




        //setup HomePager
        HomePager homePager = findViewById(R.id.view_pager);
        homePager.addView(findViewById(R.id.search_page), 0);
        homePager.addView(findViewById(R.id.setup_page), 1);

        searchBar = findViewById(R.id.searchBar);
        searchBar.setSuggestionsEnabled(false);


        //setup search bar suggestions
        suggestionsAdapter = new CustomSuggestionsAdapter(homePager);
        RecyclerView r = findViewById(R.id.suggestions_list);
        r.setLayoutManager(new LinearLayoutManager(this));
        r.setAdapter(suggestionsAdapter);
        homePager.addOnPageChangeListener(new OnPageChangeListener(findViewById(R.id.search_box), suggestionsAdapter, findViewById(R.id.drug_name)));





        //search for drug after text change in the search bar
        searchBar.addTextChangeListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                suggestionsAdapter.clear();
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (homePager.getCurrentItem() != 0)
                    homePager.setCurrentItem(0, true);
                c = drugsDatabaseTable.getWordMatches(searchBar.getText());
                if (!searchBar.getText().isEmpty()) {
                    c.moveToFirst();
                    while (!c.isAfterLast() && suggestionsAdapter.getItemCount() <= 40) {
                        UserMedicine u = new UserMedicine(String.valueOf(c.getString(c.getColumnIndex("DRUG_NAME"))), String.valueOf(c.getString(c.getColumnIndex("TARGET_NAME"))));
                        if (!suggestionsAdapter.contains(u))
                            suggestionsAdapter.addSuggestions(u);
                        c.moveToNext();
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s)
            {}
        });




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
    }


    //todo: remove this method
    //add new UserMedicine to the RecyclerView
    public void dummyButtonClicked(View view)
    {
        userDataManager.add(new UserMedicine("Random Drug"));
    }




}