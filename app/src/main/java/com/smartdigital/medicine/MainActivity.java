package com.smartdigital.medicine;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.valkriaine.factor.HomePager;



public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserDataManager userDataManager;
    private ImageView imageView;
    private Bitmap imageBitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //setup HomePager
        HomePager homePager = findViewById(R.id.view_pager);
        CardView searchCard = findViewById(R.id.search_box);
        imageView = findViewById(R.id.image_view);
        AppCompatButton openCamera = findViewById(R.id.takePicture);
        homePager.addView(findViewById(R.id.search_page), 0);
        homePager.addView(findViewById(R.id.setup_page), 1);
        homePager.addOnPageChangeListener(new OnPageChangeListener(searchCard));



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

        //setup RecyclerView
        recyclerView = findViewById(R.id.recyclerview);
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


}