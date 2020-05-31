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
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.classifier.Classifier;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button button;
    TextView textView;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private static Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image_view);
        button = findViewById(R.id.choose_image);
        textView = findViewById(R.id.set_result);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        //permission not granted
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        //show popup
                        requestPermissions(permissions,PERMISSION_CODE);
                    }
                    else{
                        //permission already granted
                        pickImageFromGallery();
                    }
                }
                else{
                    pickImageFromGallery();
                }
            }
        });
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            imageView.setImageURI(data.getData());
            bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
            Bitmap resize = Bitmap.createScaledBitmap(bitmap,300,300,true);
            imageView.setImageBitmap(resize);
            try {
                processImage(resize);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void processImage(Bitmap bitmap) throws Exception {
        Classifier classifier =  Classifier.create(this);
        float[] output = classifier.recognizeImage(bitmap);
        int predictLabel = calculateResult(output);
        if (predictLabel==0){
            textView.setText("Rock");
        }
        else if (predictLabel==1){
            textView.setText("Paper");
        }
        else if (predictLabel==2){
            textView.setText("Scissor");
        }
        else{
            textView.setText("Invalid Image");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private int calculateResult(float[] output) throws Exception{

        //Reading vector file
        InputStream vectorFile = getAssets().open("rps_vecs.tsv");

        //Reading label file
        InputStream labelFile = getAssets().open("rps_labels.tsv");
        List<StoreValue> list = new ArrayList<>();

        try {
            StringTokenizer st1;
            StringTokenizer st2;
            BufferedReader vectorTSVFile = new BufferedReader(new InputStreamReader(vectorFile, "UTF-8"));
            BufferedReader labelTSVFile = new BufferedReader(new InputStreamReader(labelFile, "UTF-8"));
            String vectorDataRow = vectorTSVFile.readLine();
            String labelDataRow = labelTSVFile.readLine();
            float dis;
            while (vectorDataRow != null) {
                dis=0;
                st1 = new StringTokenizer(vectorDataRow, "\t");
                st2 = new StringTokenizer(labelDataRow);
                List<String> dataArray = new ArrayList<>();
                String label = null;
                while (st2.hasMoreElements()){
                    label = st2.nextElement().toString();
                }
                while (st1.hasMoreElements()) {
                    dataArray.add(st1.nextElement().toString());
                }
                for (int i=0;i<dataArray.size();i++) {
                    float num1 = output[i];
                    float num2 = Float.parseFloat(dataArray.get(i));
                    // calculating distance
                    dis = dis+(float) Math.pow((num2-num1),2);
                }
                // storing distance in list
                StoreValue storeValue = new StoreValue((float) Math.sqrt(dis),Integer.parseInt(label));
                list.add(storeValue);
                vectorDataRow = vectorTSVFile.readLine();
                labelDataRow = labelTSVFile.readLine();
            }

            // sorting list on basic of distance
            list.sort(new Comparator<StoreValue>() {
                @Override
                public int compare(StoreValue o1, StoreValue o2) {
                    return Float.compare(o1.getDistance(),o2.getDistance());
                }
            });

            // returning the minimum distance
            return list.get(0).getLabel();
        }finally {
            vectorFile.close();
        }
    }
}
