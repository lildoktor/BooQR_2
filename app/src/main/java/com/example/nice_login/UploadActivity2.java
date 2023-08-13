package com.example.nice_login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.Instant;

public class UploadActivity2 extends AppCompatActivity {
    ImageView uploadImage;
    Button saveButton;
    EditText collectionName, bookName;
    String imageURL, timestamp, collection, key;
    int pageNum;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload2);

        uri = null;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            key = bundle.getString("Key");
        }

        uploadImage = findViewById(R.id.uploadImage);
        bookName = findViewById(R.id.bookName);
        collectionName = findViewById(R.id.collectionName);
        saveButton = findViewById(R.id.saveButton);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        uri = data.getData();
                    } else {
                        Toast.makeText(UploadActivity2.this, "No Video Selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        uploadImage.setOnClickListener(view -> {
            Intent photoPicker = new Intent(Intent.ACTION_PICK);
            photoPicker.setType("video/*");
            activityResultLauncher.launch(photoPicker);
        });
        saveButton.setOnClickListener(view -> saveData());
    }

    public void saveData() {
        if (uri == null) {
            Toast.makeText(UploadActivity2.this, "Select a video to upload", Toast.LENGTH_SHORT).show();
            return;
        }
        collection = collectionName.getText().toString();
        if (collection.isEmpty()) {
            collectionName.setError("Enter Collection Name");
            collectionName.requestFocus();
            return;
        }
        if (bookName.getText().toString().isEmpty()) {
            pageNum = -1;
        } else {
            pageNum = Integer.parseInt(bookName.getText().toString());
        }

        timestamp = String.valueOf(Instant.now().getEpochSecond());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("UID").child(key)
                .child(timestamp);

        AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity2.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            uriTask.addOnSuccessListener(uri -> {
                imageURL = uri.toString();
                uploadData();
                dialog.dismiss();
            }).addOnFailureListener(e -> {
                dialog.dismiss();
                Toast.makeText(UploadActivity2.this, "Error uploading video: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
//            while (!uriTask.isComplete()) ;
//            Uri urlImage = uriTask.getResult();
//            imageURL = urlImage.toString();
//            uploadData();
//            dialog.dismiss();
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(UploadActivity2.this, "Error uploading video: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    public void uploadData() {
        DataClass2 dataClass = new DataClass2(1, collection, pageNum, imageURL, timestamp);
        FirebaseDatabase.getInstance().getReference("UID").child(key).child(timestamp)
                .setValue(dataClass).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(UploadActivity2.this, "Saved", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UploadActivity2.this, QRActivity.class);
                        intent.putExtra("uri", "UID/" + key + "/" + timestamp);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(UploadActivity2.this, "Error saving data: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}