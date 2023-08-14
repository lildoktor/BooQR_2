package com.example.nice_login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.Instant;

public class UploadActivity4 extends AppCompatActivity {
    ImageView uploadImage;
    Button saveButton;
    EditText collectionName, description;
    String imageURL, timestamp, collection, key, uid;
    TextView textView;
    FirebaseAuth mAuth;
    int pageNum;
    AlertDialog dialog;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload4);

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            key = bundle.getString("Key");
        }

        uploadImage = findViewById(R.id.uploadImage);
        description = findViewById(R.id.bookName);
        collectionName = findViewById(R.id.collectionName);
        saveButton = findViewById(R.id.saveButton);
        textView = findViewById(R.id.clicktoUploadAudio);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            uri = data.getData();
                            uploadImage.setImageResource(R.drawable.baseline_check_24);
                            textView.setText("Audio selected, click again to change");
                        }
                    } else {
                        Toast.makeText(UploadActivity4.this, "No audio Selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        uploadImage.setOnClickListener(view -> {
            Intent videoPicker = new Intent(Intent.ACTION_GET_CONTENT);
            videoPicker.setType("audio/*");

            activityResultLauncher.launch(videoPicker);

        });
        saveButton.setOnClickListener(view -> saveData());
    }

    public void saveData() {
        if (uri == null) {
            Toast.makeText(UploadActivity4.this, "Select an image to audio", Toast.LENGTH_SHORT).show();
            return;
        }
        collection = collectionName.getText().toString();
        if (collection.isEmpty()) {
            collectionName.setError("Ente QR Code");
            collectionName.requestFocus();
            return;
        }
        if (description.getText().toString().isEmpty()) {
            pageNum = -1;
        } else {
            pageNum = Integer.parseInt(description.getText().toString());
        }

        timestamp = String.valueOf(Instant.now().getEpochSecond());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(uid).child(key)
                .child(timestamp);

        AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity4.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            uriTask.addOnSuccessListener(uri -> {
                imageURL = uri.toString();
                uploadData();
            }).addOnFailureListener(e -> {
                dialog.dismiss();
                Toast.makeText(UploadActivity4.this, "Error uploading audio: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(UploadActivity4.this, "Error uploading audio: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }

    public void uploadData() {
        DataClass2 dataClass = new DataClass2(3, collection, pageNum, imageURL, timestamp);
        FirebaseDatabase.getInstance().getReference(uid).child(key).child(timestamp)
                .setValue(dataClass).addOnCompleteListener(task -> {
                    dialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(UploadActivity4.this, "Saved", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UploadActivity4.this, QRActivity.class);
                        intent.putExtra("uri", uid + "/" + key + "/" + timestamp);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(UploadActivity4.this, "Error saving data: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}