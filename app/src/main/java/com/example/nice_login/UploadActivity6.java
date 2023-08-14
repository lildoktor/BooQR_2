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
import androidx.appcompat.app.AppCompatActivity;


public class UploadActivity6 extends AppCompatActivity {
    ImageView uploadImage;
    Button saveButton;
    EditText collectionName, description;
    String collection, key;
    int pageNum;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload6);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        key = bundle.getString("Key");

        uploadImage = findViewById(R.id.uploadImage);
        description = findViewById(R.id.bookName);
        collectionName = findViewById(R.id.collectionName);
        saveButton = findViewById(R.id.saveButton);

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
                        Toast.makeText(UploadActivity6.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        uploadImage.setOnClickListener(view -> {
            Intent videoPicker = new Intent(Intent.ACTION_PICK);
            videoPicker.setType("image/*");
            activityResultLauncher.launch(videoPicker);
        });
        saveButton.setOnClickListener(view -> {
                    if (uri == null) {
                        Toast.makeText(UploadActivity6.this, "Select an image to upload", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    collection = collectionName.getText().toString();
                    if (collection.isEmpty()) {
                        collectionName.setError("Enter Qr Code Name");
                        collectionName.requestFocus();
                        return;
                    }
                    if (description.getText().toString().isEmpty()) {
                        pageNum = -1;
                    } else {
                        pageNum = Integer.parseInt(description.getText().toString());
                    }
                    Intent intent = new Intent(UploadActivity6.this, UploadActivity6Helper.class);
                    intent.putExtra("Key", key);
                    intent.putExtra("Collection", collection);
                    intent.putExtra("PageNum", pageNum);
                    intent.putExtra("Uri", uri.toString());
                    startActivity(intent);
                    finish();
                }

        );
    }
}