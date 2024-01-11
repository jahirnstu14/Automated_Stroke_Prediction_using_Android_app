package com.example.strokeprediction;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class SecondActivity extends AppCompatActivity {
    private Button button2;
    private Interpreter interpreter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        button2 = findViewById(R.id.button2);

        try {
            interpreter = new Interpreter(loadModelFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float[] input = {1.0f, 67.0f, 0.0f, 1.0f, 1.0f, 2.0f, 1.0f, 228.69f, 36.6f, 1.0f};//(not free form stroke)
//                float[] input={0f, 80.0f, 0f, 0f, 1f, 2f,1f, 20f, 24.0f, 0f};//free from stroke
                float output = doInference(input);
                long prediction = Math.round(output);
                if (prediction == 1) {
                    showAlertDialog();
                    Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);
                    startActivity(intent);
                } else {
                    notshowAlertDialog();
                }
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // Set the title and message for the AlertDialog
        alertDialogBuilder.setTitle("Warning");
        alertDialogBuilder.setMessage("You are not free from stroke.");

        // Set the positive button and its click listener
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Close the dialog if the user clicks "OK"
                dialog.dismiss();
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void notshowAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // Set the title and message for the AlertDialog
        alertDialogBuilder.setTitle("Warning");
        alertDialogBuilder.setMessage("You are  free from stroke.");

        // Set the positive button and its click listener
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Close the dialog if the user clicks "OK"
                dialog.dismiss();
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    private ByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor assetFileDescriptor = this.getAssets().openFd("Stroke_Prediction.tflite");
        FileInputStream fileInputStream = new FileInputStream(assetFileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();

        long startOffset = assetFileDescriptor.getStartOffset();
        long length = assetFileDescriptor.getLength();

        ByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, length);
        fileInputStream.close(); // Close the FileInputStream after mapping

        return buffer;
    }

    public float doInference(float[] input) {
        float[][] output = new float[1][1];
        interpreter.run(input, output);
        return output[0][0];
    }
}
