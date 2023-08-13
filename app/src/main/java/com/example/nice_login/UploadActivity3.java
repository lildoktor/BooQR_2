package com.example.nice_login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
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

public class UploadActivity3 extends AppCompatActivity {
    ImageView uploadImage;
    Button saveButton;
    EditText collectionName, bookName;
    String imageURL, timestamp, collection, key;
    int pageNum;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload3);

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
                        uploadImage.setImageURI(uri);
                    } else {
                        Toast.makeText(UploadActivity3.this, "No Image Selected", Toast.LENGTH_SHORT).show();
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
        if (uri == null) {
            uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getResources().getResourcePackageName(R.drawable.books) + '/' + getResources().getResourceTypeName(R.drawable.books) + '/' + getResources().getResourceEntryName(R.drawable.books));
        }
        collection = collectionName.getText().toString();
        pageNum = Integer.parseInt(bookName.getText().toString());
        if (collection.isEmpty()) {
            collectionName.setError("Enter Collection Name");
            collectionName.requestFocus();
            return;
        }

        timestamp = String.valueOf(Instant.now().getEpochSecond());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("UID").child(key)
                .child(timestamp);

        AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity3.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isComplete()) ;
            Uri urlImage = uriTask.getResult();
            imageURL = urlImage.toString();
            uploadData();
            dialog.dismiss();
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(UploadActivity3.this, "Error uploading image: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    public void uploadData() {
        DataClass2 dataClass = new DataClass2(2, collection, pageNum, imageURL, timestamp);
        FirebaseDatabase.getInstance().getReference("UID").child(key).child(timestamp)
                .setValue(dataClass).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(UploadActivity3.this, "Saved", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(e -> Toast.makeText(UploadActivity3.this, "Error creating collection: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}