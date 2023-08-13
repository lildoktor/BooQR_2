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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.Instant;

public class UploadActivity extends AppCompatActivity {
    ImageView uploadImage;
    Button saveButton;
    EditText collectionName, bookName;
    String imageURL, timestamp, collection, book, uid;
    Uri uri;
    AlertDialog dialog;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        uploadImage = findViewById(R.id.uploadImage);
        bookName = findViewById(R.id.bookName);
        collectionName = findViewById(R.id.collectionName);
        saveButton = findViewById(R.id.saveButton);
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            uri = data.getData();
                            uploadImage.setImageURI(uri);
                        }
                    } else {
                        Toast.makeText(UploadActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        uploadImage.setOnClickListener(view -> {
            Intent photoPicker = new Intent(Intent.ACTION_PICK);
            photoPicker.setType("image/*");
            activityResultLauncher.launch(photoPicker);
        });
        saveButton.setOnClickListener(view -> saveData());
    }

    public void saveData() {
        collection = collectionName.getText().toString();
        book = bookName.getText().toString();
        if (collection.isEmpty()) {
            collectionName.setError("Enter Collection Name");
            collectionName.requestFocus();
            return;
        }

        timestamp = String.valueOf(Instant.now().getEpochSecond());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(uid)
                .child(timestamp);

        AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();
        dialog.show();

        if (uri == null) {
            imageURL = "";
            uploadData();
            return;
        }

        storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            uriTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    imageURL = uri.toString();
                    uploadData();
                } else {
                    dialog.dismiss();
                    Toast.makeText(UploadActivity.this, "Error uploading image: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            });
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(UploadActivity.this, "Error uploading image: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    public void uploadData() {
        DataClass dataClass = new DataClass(collection, book, imageURL, timestamp);
        FirebaseDatabase.getInstance().getReference(uid).child(timestamp)
                .setValue(dataClass).addOnCompleteListener(task -> {
                    dialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(UploadActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(UploadActivity.this, "Error creating collection: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
