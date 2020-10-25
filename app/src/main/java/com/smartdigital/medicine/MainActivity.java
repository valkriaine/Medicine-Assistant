package com.smartdigital.medicine;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity{

    private final UserDataManager userDataManager = new UserDataManager();
    private DrugsDatabaseTable drugsDatabaseTable;
    private MaterialSearchBar searchBar;
    private CustomSuggestionsAdapter customSuggestionsAdapter;
    private List<UserMedicine> suggestions = new ArrayList<>();
    private Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //access drugs.db
        drugsDatabaseTable = new DrugsDatabaseTable(this);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        customSuggestionsAdapter = new CustomSuggestionsAdapter(inflater);


        //setup HomePager
        HomePager homePager = findViewById(R.id.view_pager);
        homePager.addView(findViewById(R.id.search_page), 0);
        homePager.addView(findViewById(R.id.setup_page), 1);
        homePager.addOnPageChangeListener(new OnPageChangeListener(findViewById(R.id.search_box)));
        searchBar = findViewById(R.id.searchBar);
        searchBar.setSuggestionsEnabled(true);
        searchBar.setMaxSuggestionCount(10);
        searchBar.setCustomSuggestionAdapter(customSuggestionsAdapter);
        customSuggestionsAdapter.clearSuggestions();




        //search for drug after text change in the search bar
        searchBar.addTextChangeListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                suggestions.clear();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                c = drugsDatabaseTable.getWordMatches(searchBar.getText());
                c.moveToFirst();
                while(!c.isAfterLast()&&suggestions.size() <= 10)
                {
                    suggestions.add(new UserMedicine(String.valueOf(c.getString(c.getColumnIndex("DRUG_NAME")))));
                    c.moveToNext();
                }

                //todo: suggestions are not being displayed, need fix
                customSuggestionsAdapter.setSuggestions(suggestions);

                //debug: display the first item in the suggestion list
                if (suggestions.size() != 0)
                Toast.makeText(MainActivity.this,  suggestions.get(0).toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
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