package com.example.notas;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.*;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScannerActivity extends AppCompatActivity {

    private PreviewView previewView;
    private TextView textResult;
    private ProcessCameraProvider cameraProvider;
    private Executor executor = Executors.newSingleThreadExecutor();
    private ImageAnalysis imageAnalysis;
    private boolean scanning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        previewView = findViewById(R.id.previewView);
        textResult = findViewById(R.id.textResult);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        } else {
            startCamera();
        }

        startCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(executor, imageProxy -> {
                    if (!scanning) {
                        imageProxy.close();
                        return;
                    }

                    @SuppressLint("UnsafeOptInUsageError")
                    InputImage image = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());

                    TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
                    recognizer.process(image)
                            .addOnSuccessListener(visionText -> {
                                String rawText = visionText.getText();
                                Log.d("OCR", rawText);

                                String dni = extractDNI(rawText);
                                String nombre = extractNombreCompletoDesdeMRZ(rawText);

                                if (!dni.isEmpty() && !nombre.isEmpty()) {
                                    scanning = false;
                                    Intent resultIntent = new Intent();
                                    resultIntent.putExtra("dni", dni);
                                    resultIntent.putExtra("nombre", nombre);
                                    setResult(RESULT_OK, resultIntent);
                                    finish();  // Finaliza esta actividad para retornar a Insertar
                                }

                            })
                            .addOnFailureListener(e -> Log.e("OCR", "Error", e))
                            .addOnCompleteListener(task -> imageProxy.close());
                });

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        }, ContextCompat.getMainExecutor(this));
    }

    private String extractDNI(String text) {
        Pattern dniPattern = Pattern.compile("\\bDNI\\s*(\\d{8})\\b");
        Matcher matcher = dniPattern.matcher(text);
        return matcher.find() ? matcher.group(1) : "";
    }

    private String extractNombreCompletoDesdeMRZ(String text) {
        // Buscar la línea que contiene la MRZ
        Pattern mrzPattern = Pattern.compile("([A-Z<]{5,})");
        Matcher matcher = mrzPattern.matcher(text);

        String mrzLinea = "";
        while (matcher.find()) {
            String linea = matcher.group(1);
            if (linea.contains("<<")) {
                mrzLinea = linea;
            }
        }

        if (!mrzLinea.isEmpty()) {
            // Separar apellidos y nombres
            String[] partes = mrzLinea.split("<<", 2);
            if (partes.length == 2) {
                String apellidoCompleto = partes[0].replace("<", " ").trim();
                String nombres = partes[1].replace("<", " ").trim();

                return (apellidoCompleto + " " + nombres).replaceAll("\\s+", " ");
            }
        }

        return ""; // Fallback si no se encuentra
    }


}
