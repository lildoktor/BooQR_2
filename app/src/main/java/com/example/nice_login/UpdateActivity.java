package com.example.nice_login;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.Instant;

public class UpdateActivity extends AppCompatActivity {

    ImageView updateImage;
    Button updateButton;
    EditText updateDesc, updateTitle;
    String collection, desc, uid;
    String imageUrl;
    String key, oldImageURL;
    Uri uri;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    FirebaseAuth fAuth;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        updateButton = findViewById(R.id.updateButton);
        updateDesc = findViewById(R.id.updateDesc);
        updateImage = findViewById(R.id.updateImage);
        updateTitle = findViewById(R.id.updateTitle);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if ("".equals(bundle.getString("Image")))
                updateImage.setImageResource(R.drawable.books);
            else
                Glide.with(UpdateActivity.this).load(bundle.getString("Image")).into(updateImage);
            updateTitle.setText(bundle.getString("Title"));
            updateDesc.setText(bundle.getString("Description"));
            key = bundle.getString("Key");
            oldImageURL = bundle.getString("Image");
        }
        fAuth = FirebaseAuth.getInstance();
        uid = fAuth.getCurrentUser().getUid();

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            uri = data.getData();
                            updateImage.setImageURI(uri);
                        }
                    } else {
                        Toast.makeText(UpdateActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        updateImage.setOnClickListener(view -> {
            Intent photoPicker = new Intent(Intent.ACTION_PICK);
            photoPicker.setType("image/*");
            activityResultLauncher.launch(photoPicker);
        });
        updateButton.setOnClickListener(view -> {
            saveData();
        });
    }

    public void saveData() {
        collection = updateTitle.getText().toString();
        desc = updateDesc.getText().toString();
        if (collection.isEmpty()) {
            updateTitle.setError("Enter Collection Name");
            updateTitle.requestFocus();
            return;
        }

        storageReference = FirebaseStorage.getInstance().getReference().child(uid).child(key);

        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();
        dialog.show();

        if (uri == null) {
            imageUrl = "";
            updateData();
            return;
        }

        storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            uriTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    imageUrl = task.getResult().toString();
                    updateData();
                } else {
                    dialog.dismiss();
                    Toast.makeText(UpdateActivity.this, "Error uploading image: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            });
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(UpdateActivity.this, "Error uploading image: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    public void updateData() {
        String time = String.valueOf(Instant.now().getEpochSecond());
        DataClass dataClass = new DataClass(collection, desc, imageUrl, time);
        FirebaseDatabase.getInstance().getReference(uid).child(key)
                .setValue(dataClass).addOnCompleteListener(task -> {
                    dialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(UpdateActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(UpdateActivity.this, "Error creating collection: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}