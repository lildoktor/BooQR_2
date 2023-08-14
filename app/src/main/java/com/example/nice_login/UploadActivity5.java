package com.example.nice_login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.List;

public class UploadActivity5 extends AppCompatActivity {
    ImageView uploadImage;
    Button saveButton, customButton;
    EditText collectionName, description;
    String imageURL, timestamp, collection, key, uid;
    FirebaseAuth mAuth;
    int pageNum;
    AlertDialog dialog;
    Uri uri;
    Spinner numAnswers, correctAnswer;
    EditText[] answers = new EditText[5];
    LinearLayout answer1_layout, answer2_layout, answer3_layout, answer4_layout, answer5_layout;
    int numOfAnswers = 2;
    boolean switcher = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload5);

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
        customButton = findViewById(R.id.customAnwersBtn);
        numAnswers = findViewById(R.id.numberOfAnswers);
        correctAnswer = findViewById(R.id.correctAnswer);
        answers[0] = findViewById(R.id.customanswer1);
        answers[1] = findViewById(R.id.customanswer2);
        answers[2] = findViewById(R.id.customanswer3);
        answers[3] = findViewById(R.id.customanswer4);
        answers[4] = findViewById(R.id.customanswer5);
        answer1_layout = findViewById(R.id.answer1_layout);
        answer2_layout = findViewById(R.id.answer2_layout);
        answer3_layout = findViewById(R.id.answer3_layout);
        answer4_layout = findViewById(R.id.answer4_layout);
        answer5_layout = findViewById(R.id.answer5_layout);

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
                        Toast.makeText(UploadActivity5.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        uploadImage.setOnClickListener(view -> {
            Intent videoPicker = new Intent(Intent.ACTION_PICK);
            videoPicker.setType("image/*");
            activityResultLauncher.launch(videoPicker);
        });
        saveButton.setOnClickListener(view -> saveData());

        numAnswers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                numOfAnswers = i + 2;
                int arrayId = 0;
                switch (i) {
                    case 0:
                        arrayId = R.array.correct_answer_2;
                        break;
                    case 1:
                        arrayId = R.array.correct_answer_3;
                        break;
                    case 2:
                        arrayId = R.array.correct_answer_4;
                        break;
                    case 3:
                        arrayId = R.array.correct_answer;
                        break;
                }
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(UploadActivity5.this,
                        arrayId, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                correctAnswer.setAdapter(adapter);

                if (answer1_layout.isShown()) {
                    if (numOfAnswers == 2) {
                        answer1_layout.setVisibility(View.VISIBLE);
                        answer2_layout.setVisibility(View.VISIBLE);
                        answer3_layout.setVisibility(View.GONE);
                        answer4_layout.setVisibility(View.GONE);
                        answer5_layout.setVisibility(View.GONE);
                    } else if (numOfAnswers == 3) {
                        answer1_layout.setVisibility(View.VISIBLE);
                        answer2_layout.setVisibility(View.VISIBLE);
                        answer3_layout.setVisibility(View.VISIBLE);
                        answer4_layout.setVisibility(View.GONE);
                        answer5_layout.setVisibility(View.GONE);
                    } else if (numOfAnswers == 4) {
                        answer1_layout.setVisibility(View.VISIBLE);
                        answer2_layout.setVisibility(View.VISIBLE);
                        answer3_layout.setVisibility(View.VISIBLE);
                        answer4_layout.setVisibility(View.VISIBLE);
                        answer5_layout.setVisibility(View.GONE);
                    } else if (numOfAnswers == 5) {
                        answer1_layout.setVisibility(View.VISIBLE);
                        answer2_layout.setVisibility(View.VISIBLE);
                        answer3_layout.setVisibility(View.VISIBLE);
                        answer4_layout.setVisibility(View.VISIBLE);
                        answer5_layout.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });


        customButton.setOnClickListener(view -> {
            Log.e("TAG", "onCreate: " + answer1_layout.isShown());
            Log.e("TAG", String.valueOf(numOfAnswers));
            if (!switcher) {
                customButton.setBackgroundColor(Color.parseColor("#FF5733"));
                if (numOfAnswers == 2) {
                    answer1_layout.setVisibility(View.VISIBLE);
                    answer2_layout.setVisibility(View.VISIBLE);
                    answer3_layout.setVisibility(View.GONE);
                    answer4_layout.setVisibility(View.GONE);
                    answer5_layout.setVisibility(View.GONE);
                } else if (numOfAnswers == 3) {
                    answer1_layout.setVisibility(View.VISIBLE);
                    answer2_layout.setVisibility(View.VISIBLE);
                    answer3_layout.setVisibility(View.VISIBLE);
                    answer4_layout.setVisibility(View.GONE);
                    answer5_layout.setVisibility(View.GONE);
                } else if (numOfAnswers == 4) {
                    answer1_layout.setVisibility(View.VISIBLE);
                    answer2_layout.setVisibility(View.VISIBLE);
                    answer3_layout.setVisibility(View.VISIBLE);
                    answer4_layout.setVisibility(View.VISIBLE);
                    answer5_layout.setVisibility(View.GONE);
                } else if (numOfAnswers == 5) {
                    answer1_layout.setVisibility(View.VISIBLE);
                    answer2_layout.setVisibility(View.VISIBLE);
                    answer3_layout.setVisibility(View.VISIBLE);
                    answer4_layout.setVisibility(View.VISIBLE);
                    answer5_layout.setVisibility(View.VISIBLE);
                }
                switcher = true;
            } else {
                customButton.setBackgroundColor(Color.parseColor("#8692f7"));
                answer1_layout.setVisibility(View.GONE);
                answer2_layout.setVisibility(View.GONE);
                answer3_layout.setVisibility(View.GONE);
                answer4_layout.setVisibility(View.GONE);
                answer5_layout.setVisibility(View.GONE);
                switcher = false;
            }

        });
    }

    public void saveData() {
        if (uri == null) {
            Toast.makeText(UploadActivity5.this, "Select an image to upload", Toast.LENGTH_SHORT).show();
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

        if (switcher) {
            for (int i = 0; i < numOfAnswers; i++) {
                if (answers[i].getText().toString().isEmpty()) {
                    answers[i].setError("Enter Answer");
                    answers[i].requestFocus();
                    return;
                }
            }
        }

        timestamp = String.valueOf(Instant.now().getEpochSecond());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(uid).child(key)
                .child(timestamp);

        AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity5.this);
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
                Toast.makeText(UploadActivity5.this, "Error uploading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(UploadActivity5.this, "Error uploading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }

    public void uploadData() {
        List<String> answersList = new ArrayList<>();
        if (switcher) {
            for (int i = 0; i < numOfAnswers; i++) {
                answersList.add(answers[i].getText().toString());
            }

        }
        DataClass2 dataClass = new DataClass2(4, collection, pageNum, imageURL, timestamp, numOfAnswers, correctAnswer.getSelectedItemPosition(), answersList);
        FirebaseDatabase.getInstance().getReference(uid).child(key).child(timestamp)
                .setValue(dataClass).addOnCompleteListener(task -> {
                    dialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(UploadActivity5.this, "Saved", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UploadActivity5.this, QRActivity.class);
                        intent.putExtra("uri", uid + "/" + key + "/" + timestamp);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(UploadActivity5.this, "Error saving data: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}