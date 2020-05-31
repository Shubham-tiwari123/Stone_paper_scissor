package com.example.project;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
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

public class AsyncProcessImage extends AsyncTask<Void,Void,Void> {

    private Map<Integer, String> finalRes;
    private ProgressDialog dialog;
    private Activity activity;
    private Map<Integer,Bitmap> images;
    private static List<List<String>> tsvFileData;

    public AsyncProcessImage(Activity activity,Map<Integer,Bitmap> images){
        dialog =new ProgressDialog(activity);
        this.activity = activity;
        this.images = images;
        finalRes = new HashMap<>();
        tsvFileData = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.dialog.setMessage("\tProcessing...");
        this.dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected Void doInBackground(Void...voids) {
        try {
            Classifier classifier = Classifier.create(activity);
            Map<Integer, float[]> output = classifier.recognizeImage(images);
            for (Map.Entry<Integer, float[]> list : output.entrySet()) {
                int predictLabel=0;
                if (tsvFileData.isEmpty()){
                    readTSVFiles();
                    predictLabel = calculateResult(list.getValue());
                }else{
                    predictLabel = calculateResult(list.getValue());
                }
                if (predictLabel == 0) {
                    finalRes.put(list.getKey(), "Rock");
                } else if (predictLabel == 1) {
                    finalRes.put(list.getKey(), "Paper");
                } else if (predictLabel == 2) {
                    finalRes.put(list.getKey(), "Scissor");
                }
            }
        }catch (Exception e){
            Log.e("ex",e.toString());
        }
        return null;
    }

    private void readTSVFiles () throws Exception{
        InputStream vectorFile = activity.getAssets().open("rps_vecs.tsv");
        InputStream labelFile = activity.getAssets().open("rps_labels.tsv");

        try {
            StringTokenizer st1;
            StringTokenizer st2;
            BufferedReader vectorTSVFile = new BufferedReader(new InputStreamReader(vectorFile, "UTF-8"));
            BufferedReader labelTSVFile = new BufferedReader(new InputStreamReader(labelFile, "UTF-8"));
            String vectorDataRow = vectorTSVFile.readLine();
            String labelDataRow = labelTSVFile.readLine();

            while (vectorDataRow != null) {
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
                dataArray.add(dataArray.size(),label);
                tsvFileData.add(dataArray);
                vectorDataRow = vectorTSVFile.readLine();
                labelDataRow = labelTSVFile.readLine();
            }
        }finally {
            vectorFile.close();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private int calculateResult(float[] output) throws Exception{
        List<StoreValue> label = new ArrayList<>();
        for (List<String> dataArray:tsvFileData) {
            float dis=0;
            for (int i=0;i<dataArray.size()-1;i++) {
                float num1 = output[i];
                float num2 = Float.parseFloat(dataArray.get(i));
                dis = dis + (float) Math.pow((num2 - num1), 2);
            }
            StoreValue storeValue = new StoreValue((float) Math.sqrt(dis),
                    Integer.parseInt(dataArray.get(dataArray.size()-1)));
            label.add(storeValue);
        }

        label.sort(new Comparator<StoreValue>() {
            @Override
            public int compare(StoreValue o1, StoreValue o2) {
                return Float.compare(o1.getDistance(),o2.getDistance());
            }
        });

        // returning the minimum distance
        return label.get(0).getLabel();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        TextView result1 = activity.findViewById(R.id.result1);
        TextView result2 = activity.findViewById(R.id.result2);
        TextView result3 = activity.findViewById(R.id.result3);
        TextView result4 = activity.findViewById(R.id.result4);
        TextView result5 = activity.findViewById(R.id.result5);
        TextView result6 = activity.findViewById(R.id.result6);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<Integer, String> display : finalRes.entrySet()) {
                    if (display.getKey() == 1) {
                        result1.setText(display.getValue());
                    }
                    if (display.getKey() == 2) {
                        result2.setText(display.getValue());
                    }
                    if (display.getKey() == 3) {
                        result3.setText(display.getValue());
                    }
                    if (display.getKey() == 4) {
                        result4.setText(display.getValue());
                    }
                    if (display.getKey() == 5) {
                        result5.setText(display.getValue());
                    }
                    if (display.getKey() == 6) {
                        result6.setText(display.getValue());
                    }
                }
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        },1000);

    }
}
