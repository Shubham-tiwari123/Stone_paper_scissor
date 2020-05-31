package com.example.project.classifier;

import android.app.Activity;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.ops.NormalizeOp;

import java.io.IOException;

/** This TensorFlow Lite classifier works with the rock_paper_sci model. */
public class ClassifierRockPaperScissor extends Classifier {

    /** Float MobileNet requires additional normalization of the used input. */
    private static final float IMAGE_MEAN = 0.0f;

    private static final float IMAGE_STD = 255.0f;


    public ClassifierRockPaperScissor(Activity activity) throws IOException {
        super(activity);
    }

    @Override
    protected String getModelPath() {
        return "rock_paper_sci_model.tflite";
    }

    @Override
    protected String getLabelPath() {
        return "labels.txt";
    }

    @Override
    protected TensorOperator getPreProcessNormalizeOp() {
        return new NormalizeOp(IMAGE_MEAN, IMAGE_STD);
    }

}
