package com.example.project.classifier;

import android.app.Activity;
import android.graphics.Bitmap;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Classifier {

    /** The loaded TensorFlow Lite model. */
    private MappedByteBuffer tfliteModel;

    /** Image size along the x axis. */
    private final int imageSizeX;

    /** Image size along the y axis. */
    private final int imageSizeY;

    /** An instance of the driver class to run model inference with Tensorflow Lite. */
    private Interpreter tflite;

    /** Options for configuring the Interpreter. */
    private final Interpreter.Options tfliteOptions = new Interpreter.Options();

    /** Labels corresponding to the output of the vision model. */
    private List<String> labels;

    /** Input image TensorBuffer. */
    private TensorImage inputImageBuffer;

    /** Output probability TensorBuffer. */
    private final TensorBuffer outputProbabilityBuffer;

    /** Gets the name of the model file stored in Assets. */
    protected abstract String getModelPath();

    /** Gets the name of the label file stored in Assets. */
    protected abstract String getLabelPath();

    /** Gets the TensorOperator to normalize the input image in preProcessing. */
    protected abstract TensorOperator getPreProcessNormalizeOp();


    public Classifier(Activity activity) throws IOException {
        // Load model
        tfliteModel = FileUtil.loadMappedFile(activity, getModelPath());
        tflite =new Interpreter(tfliteModel,tfliteOptions);

        // Loads labels out from the label file.
        labels = FileUtil.loadLabels(activity, getLabelPath());

        // Reads type and shape of input and output tensors.
        int imageTensorIndex = 0;
        int[] imageShape = tflite.getInputTensor(imageTensorIndex).shape();
        imageSizeY = imageShape[1];
        imageSizeX = imageShape[2];
        DataType imageDataType = tflite.getInputTensor(imageTensorIndex).dataType();
        int probabilityTensorIndex = 0;
        int[] probabilityShape =
                tflite.getOutputTensor(probabilityTensorIndex).shape();
        DataType probabilityDataType = tflite.getOutputTensor(probabilityTensorIndex).dataType();

        // Creates the input and output tensor.
        inputImageBuffer = new TensorImage(imageDataType);
        outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);
    }

    /** Creates a classifier with the provided configuration.*/
    public static Classifier create(Activity activity) throws IOException {
        return new ClassifierRockPaperScissor(activity);
    }

    /** Loads input image, and applies preprocessing. */
    private TensorImage loadImage(final Bitmap bitmap) {
        // Loads bitmap into a TensorImage.
        inputImageBuffer.load(bitmap);
        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        .add(new ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                        .add(getPreProcessNormalizeOp())
                        .build();
        return imageProcessor.process(inputImageBuffer);
    }

    /*public float[] recognizeImage(final Bitmap bitmap) {
        inputImageBuffer = loadImage(bitmap);Rz
        tflite.run(inputImageBuffer.getBuffer(), outputProbabilityBuffer.getBuffer());
        float[] res = outputProbabilityBuffer.getFloatArray();
        return res;
    }*/

    public Map<Integer,float[]> recognizeImage(Map<Integer,Bitmap> imageList) {
        Map<Integer,float[]> resultList = new HashMap<>();
        for (Map.Entry<Integer,Bitmap> image:imageList.entrySet()) {
            inputImageBuffer = loadImage(image.getValue());
            tflite.run(inputImageBuffer.getBuffer(), outputProbabilityBuffer.getBuffer());
            float[] res = outputProbabilityBuffer.getFloatArray();
            resultList.put(image.getKey(),res);
        }
        return resultList;
    }
}
