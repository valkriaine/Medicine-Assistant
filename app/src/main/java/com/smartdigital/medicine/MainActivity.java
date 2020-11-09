package com.smartdigital.medicine;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.ramotion.fluidslider.FluidSlider;
import com.smartdigital.medicine.util.CustomSuggestionsAdapter;
import com.smartdigital.medicine.util.DrugsDatabaseTable;
import com.smartdigital.medicine.util.OnPageChangeListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.valkriaine.factor.HomePager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;




public class MainActivity extends AppCompatActivity
{

    private final UserDataManager userDataManager = new UserDataManager();
    private DrugsDatabaseTable drugsDatabaseTable;
    private MaterialSearchBar searchBar;
    private CustomSuggestionsAdapter suggestionsAdapter;
    public static UserMedicine currentMed;

    private final float[] positionDuration = {0f};


    private ImageView imageView;
    private Bitmap imageBitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;




    private Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //access drugs.db
        drugsDatabaseTable = new DrugsDatabaseTable(this);


        initializeComponents();



        //alarm code






    }

    public void confirmAddDrug(View view)
    {
        if (currentMed != null)
        {
            currentMed.setDuration(positionDuration[0]);
            if (currentMed.getDuration() != 0)
            {
                userDataManager.add(currentMed);
                SlidingUpPanelLayout drawer = findViewById(R.id.drawer);
                drawer.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        }
    }
    private void initializeComponents()
    {


        //setup HomePager
        HomePager homePager = findViewById(R.id.view_pager);

        CardView searchCard = findViewById(R.id.search_box);
        imageView = findViewById(R.id.image_view);
        AppCompatButton openCamera = findViewById(R.id.takePicture);


        homePager.addView(findViewById(R.id.search_page), 0);
        homePager.addView(findViewById(R.id.setup_page), 1);

        searchBar = findViewById(R.id.searchBar);
        searchBar.setSuggestionsEnabled(false);

        //setup search bar suggestions
        suggestionsAdapter = new CustomSuggestionsAdapter(homePager, findViewById(R.id.searchBar));
        RecyclerView r = findViewById(R.id.suggestions_list);
        r.setLayoutManager(new LinearLayoutManager(this));
        r.setAdapter(suggestionsAdapter);
        homePager.addOnPageChangeListener(new OnPageChangeListener(findViewById(R.id.search_box), suggestionsAdapter, findViewById(R.id.drug_name)));


        //initialize UserDataManager
        userDataManager = new UserDataManager();


        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
                readTextFromImage();
            }
        });



        //todo: remove this line
        //generate dummy data for test purpose
        userDataManager.generateDummyData();

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


        //setup RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userDataManager.getAdapter());


    }

    private void dispatchTakePictureIntent() {
        int permissionCheck = ContextCompat.checkSelfPermission(imageView.getContext(), Manifest.permission.CAMERA);
        if(permissionCheck!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {Manifest.permission.CAMERA}, 1888);

        }else{
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(takePictureIntent.resolveActivity(getPackageManager())!=null){
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }

    private void readTextFromImage() {
        FirebaseVisionImage firstFirebaseVisionImage= FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getCloudTextRecognizer();
        detector.processImage(firstFirebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                displayTextFromImage(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }
    //todo: remove this method
    //add new UserMedicine to the RecyclerView
    public void dummyButtonClicked(View view)
    {
        userDataManager.add(new UserMedicine("Random Drug"));
    }




        //initialize sliders
        FluidSlider durationSlider = findViewById(R.id.duration_slider);
        TextView durationText = findViewById(R.id.duration_text);
        //setup duration slider
        durationSlider.setStartText("0");
        durationSlider.setEndText("365");
        durationSlider.setPosition(0);
        durationSlider.setPositionListener(aFloat -> {
            //do not update UI here
            positionDuration[0] = (int) (365 * aFloat);
            durationSlider.setBubbleText(String.valueOf(positionDuration[0]));
            return null;
        });
        durationSlider.setEndTrackingListener(() -> {
            durationSlider.setPosition(positionDuration[0] /365);
            //update UI here
            durationText.setText("I am taking medicine for: " + positionDuration[0] + " days.");
            return null;
        });


}