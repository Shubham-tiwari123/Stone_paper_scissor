package com.example.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.classifier.Classifier;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinner;
    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private ImageView image4;
    private ImageView image5;
    private ImageView image6;

    private LinearLayout layout1;
    private LinearLayout layout2;
    private LinearLayout layout3;
    private LinearLayout layout4;
    private LinearLayout layout5;
    private LinearLayout layout6;

    private TextView result1 ;
    private TextView result2 ;
    private TextView result3 ;
    private TextView result4 ;
    private TextView result5 ;
    private TextView result6 ;

    private Button predictResult;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private static int totalImagesToDisplay;
    private static int countImage;

    private static Map<Integer,Bitmap> images;
    @RequiresApi(api = Build.VERSION_CODES.M)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        predictResult = findViewById(R.id.predict_result);
        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
        layout3 = findViewById(R.id.layout3);
        layout4 = findViewById(R.id.layout4);
        layout5 = findViewById(R.id.layout5);
        layout6 = findViewById(R.id.layout6);

        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        image4 = findViewById(R.id.image4);
        image5 = findViewById(R.id.image5);
        image6 = findViewById(R.id.image6);

        result1 = findViewById(R.id.result1);
        result2 = findViewById(R.id.result2);
        result3 = findViewById(R.id.result3);
        result4 = findViewById(R.id.result4);
        result5 = findViewById(R.id.result5);
        result6 = findViewById(R.id.result6);

        spinner = findViewById(R.id.spinner);
        String[] items = new String[]{"Select no of images","1","2","3","4","5","6"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, items);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForPermission()) {
                    startActivityForResult(Intent.createChooser(new Intent().
                            setAction(Intent.ACTION_GET_CONTENT).
                            setType("image/*"), "Selecting one image"), 1);
                }else{
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions,PERMISSION_CODE);
                }
            }
        });

        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForPermission()) {
                    startActivityForResult(Intent.createChooser(new Intent().
                            setAction(Intent.ACTION_GET_CONTENT).
                            setType("image/*"), "Selecting second image"), 2);
                }else{
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions,PERMISSION_CODE);
                }
            }
        });

        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForPermission()) {
                    startActivityForResult(Intent.createChooser(new Intent().
                            setAction(Intent.ACTION_GET_CONTENT).
                            setType("image/*"), "Selecting third image"), 3);
                }else{
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions,PERMISSION_CODE);
                }
            }
        });

        image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForPermission()) {
                    startActivityForResult(Intent.createChooser(new Intent().
                            setAction(Intent.ACTION_GET_CONTENT).
                            setType("image/*"), "Selecting fourth image"), 4);
                }else{
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions,PERMISSION_CODE);
                }
            }
        });

        image5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForPermission()) {
                    startActivityForResult(Intent.createChooser(new Intent().
                            setAction(Intent.ACTION_GET_CONTENT).
                            setType("image/*"), "Selecting one image"), 5);
                }else{
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions,PERMISSION_CODE);
                }
            }
        });

        image6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForPermission()) {
                    startActivityForResult(Intent.createChooser(new Intent().
                            setAction(Intent.ACTION_GET_CONTENT).
                            setType("image/*"), "Selecting one image"), 6);
                }else{
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions,PERMISSION_CODE);
                }
            }
        });

        predictResult.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                images = collectAllImages();
                try {
                    AsyncProcessImage asyncProcessImage =
                            new AsyncProcessImage(MainActivity.this,images);
                    asyncProcessImage.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Map<Integer,Bitmap> collectAllImages(){
        Map<Integer,Bitmap> images = new HashMap<>();
        Bitmap bitmap;
        switch (totalImagesToDisplay){
            case 1:
                bitmap = ((BitmapDrawable)image1.getDrawable()).getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap,300,300,true);
                images.put(1,bitmap);
                break;

            case 2:
                bitmap = ((BitmapDrawable)image1.getDrawable()).getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap,300,300,true);
                images.put(1,bitmap);
                bitmap = ((BitmapDrawable)image2.getDrawable()).getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap,300,300,true);
                images.put(2,bitmap);
                break;

            case 3:
                bitmap = ((BitmapDrawable)image1.getDrawable()).getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap,300,300,true);
                images.put(1,bitmap);
                bitmap = ((BitmapDrawable)image2.getDrawable()).getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap,300,300,true);
                images.put(2,bitmap);
                bitmap = ((BitmapDrawable)image3.getDrawable()).getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap,300,300,true);
                images.put(3,bitmap);
                break;

            case 4:
                bitmap = ((BitmapDrawable)image1.getDrawable()).getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap,300,300,true);
                images.put(1,bitmap);
                bitmap = ((BitmapDrawable)image2.getDrawable()).getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap,300,300,true);
                images.put(2,bitmap);
                bitmap = ((BitmapDrawable)image3.getDrawable()).getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap,300,300,true);
                images.put(3,bitmap);
                bitmap = ((BitmapDrawable)image4.getDrawable()).getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap,300,300,true);
                images.put(4,bitmap);
                break;

            case 5:
                bitmap = ((BitmapDrawable)image1.getDrawable()).getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap,300,300,true);
                images.put(1,bitmap);
                bitmap = ((BitmapDrawable)image2.getDrawable()).getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap,300,300,true);
                images.put(2,bitmap);
                bitmap = ((BitmapDrawable)image3.getDrawable()).getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap,300,300,true);
                images.put(3,bitmap);
                bitmap = ((BitmapDrawable)image4.getDrawable()).getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap,300,300,true);
                images.put(4,bitmap);
                bitmap = ((BitmapDrawable)image5.getDrawable()).getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap,300,300,true);
                images.put(5,bitmap);
                break;

            case 6:
                bitmap = ((BitmapDrawable)image1.getDrawable()).getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap,300,300,true);
                images.put(1,bitmap);
                bitmap = ((BitmapDrawable)image2.getDrawable()).getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap,300,300,true);
                images.put(2,bitmap);
                bitmap = ((BitmapDrawable)image3.getDrawable()).getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap,300,300,true);
                images.put(3,bitmap);
                bitmap = ((BitmapDrawable)image4.getDrawable()).getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap,300,300,true);
                images.put(4,bitmap);
                bitmap = ((BitmapDrawable)image5.getDrawable()).getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap,300,300,true);
                images.put(5,bitmap);
                bitmap = ((BitmapDrawable)image6.getDrawable()).getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap,300,300,true);
                images.put(6,bitmap);
                break;
        }

        return images;
    }

    private boolean checkForPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            Uri imageUri = data.getData();
            if (imageUri!=null) {
                image1.setImageURI(imageUri);
                countImage++;
                if (countImage==totalImagesToDisplay)
                    predictResult.setVisibility(View.VISIBLE);
            }
        }else if(resultCode == RESULT_OK && requestCode == 2){
            Uri  imageUri = data.getData();
            if (imageUri!=null) {
                image2.setImageURI(imageUri);
                countImage++;
                if (countImage==totalImagesToDisplay)
                    predictResult.setVisibility(View.VISIBLE);
            }
        }else if(resultCode == RESULT_OK && requestCode == 3){
            Uri  imageUri = data.getData();
            if (imageUri!=null) {
                image3.setImageURI(imageUri);
                countImage++;
                if (countImage==totalImagesToDisplay)
                    predictResult.setVisibility(View.VISIBLE);
            }
        }else if(resultCode == RESULT_OK && requestCode == 4){
            Uri  imageUri = data.getData();
            if (imageUri!=null) {
                image4.setImageURI(imageUri);
                countImage++;
                if (countImage==totalImagesToDisplay)
                    predictResult.setVisibility(View.VISIBLE);
            }
        }else if(resultCode == RESULT_OK && requestCode == 5){
            Uri  imageUri = data.getData();
            if (imageUri!=null) {
                image5.setImageURI(imageUri);
                countImage++;
                if (countImage==totalImagesToDisplay)
                    predictResult.setVisibility(View.VISIBLE);
            }
        }else if(resultCode == RESULT_OK && requestCode == 6){
            Uri  imageUri = data.getData();
            if (imageUri!=null) {
                image6.setImageURI(imageUri);
                countImage++;
                if (countImage==totalImagesToDisplay)
                    predictResult.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        countImage=0;
        predictResult.setVisibility(View.INVISIBLE);
        switch (position){
            case 0:
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.GONE);
                layout3.setVisibility(View.GONE);
                layout4.setVisibility(View.GONE);
                layout5.setVisibility(View.GONE);
                layout6.setVisibility(View.GONE);
                result1.setText("Prediction Result");
                result2.setText("Prediction Result");
                result3.setText("Prediction Result");
                result4.setText("Prediction Result");
                result5.setText("Prediction Result");
                result6.setText("Prediction Result");
                totalImagesToDisplay=0;
                break;

            case 1:
                layout1.setVisibility(View.VISIBLE);
                image1.setImageResource(R.drawable.ic_image_black_24dp);
                result1.setText("Prediction Result");
                layout2.setVisibility(View.GONE);
                layout3.setVisibility(View.GONE);
                layout4.setVisibility(View.GONE);
                layout5.setVisibility(View.GONE);
                layout6.setVisibility(View.GONE);
                totalImagesToDisplay=1;
                break;

            case 2:
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.VISIBLE);
                image1.setImageResource(R.drawable.ic_image_black_24dp);
                image2.setImageResource(R.drawable.ic_image_black_24dp);
                result1.setText("Prediction Result");
                result2.setText("Prediction Result");
                layout3.setVisibility(View.GONE);
                layout4.setVisibility(View.GONE);
                layout5.setVisibility(View.GONE);
                layout6.setVisibility(View.GONE);
                totalImagesToDisplay=2;
                break;

            case 3:
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.VISIBLE);
                layout3.setVisibility(View.VISIBLE);
                image1.setImageResource(R.drawable.ic_image_black_24dp);
                image2.setImageResource(R.drawable.ic_image_black_24dp);
                image3.setImageResource(R.drawable.ic_image_black_24dp);
                result1.setText("Prediction Result");
                result2.setText("Prediction Result");
                result3.setText("Prediction Result");
                layout4.setVisibility(View.GONE);
                layout5.setVisibility(View.GONE);
                layout6.setVisibility(View.GONE);
                totalImagesToDisplay=3;
                break;

            case 4:
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.VISIBLE);
                layout3.setVisibility(View.VISIBLE);
                layout4.setVisibility(View.VISIBLE);
                image1.setImageResource(R.drawable.ic_image_black_24dp);
                image2.setImageResource(R.drawable.ic_image_black_24dp);
                image3.setImageResource(R.drawable.ic_image_black_24dp);
                image4.setImageResource(R.drawable.ic_image_black_24dp);
                result1.setText("Prediction Result");
                result2.setText("Prediction Result");
                result3.setText("Prediction Result");
                result4.setText("Prediction Result");
                layout5.setVisibility(View.GONE);
                layout6.setVisibility(View.GONE);
                totalImagesToDisplay=4;
                break;

            case 5:
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.VISIBLE);
                layout3.setVisibility(View.VISIBLE);
                layout4.setVisibility(View.VISIBLE);
                layout5.setVisibility(View.VISIBLE);
                result1.setText("Prediction Result");
                result2.setText("Prediction Result");
                result3.setText("Prediction Result");
                result4.setText("Prediction Result");
                result5.setText("Prediction Result");
                image1.setImageResource(R.drawable.ic_image_black_24dp);
                image2.setImageResource(R.drawable.ic_image_black_24dp);
                image3.setImageResource(R.drawable.ic_image_black_24dp);
                image4.setImageResource(R.drawable.ic_image_black_24dp);
                image5.setImageResource(R.drawable.ic_image_black_24dp);
                layout6.setVisibility(View.GONE);
                totalImagesToDisplay=5;
                break;

            case 6:
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.VISIBLE);
                layout3.setVisibility(View.VISIBLE);
                layout4.setVisibility(View.VISIBLE);
                layout5.setVisibility(View.VISIBLE);
                layout6.setVisibility(View.VISIBLE);
                result1.setText("Prediction Result");
                result2.setText("Prediction Result");
                result3.setText("Prediction Result");
                result4.setText("Prediction Result");
                result5.setText("Prediction Result");
                result6.setText("Prediction Result");
                image1.setImageResource(R.drawable.ic_image_black_24dp);
                image2.setImageResource(R.drawable.ic_image_black_24dp);
                image3.setImageResource(R.drawable.ic_image_black_24dp);
                image4.setImageResource(R.drawable.ic_image_black_24dp);
                image5.setImageResource(R.drawable.ic_image_black_24dp);
                image6.setImageResource(R.drawable.ic_image_black_24dp);
                totalImagesToDisplay=6;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
