package com.example.nice_login;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UploadActivity6Helper extends AppCompatActivity {

    private DrawableView drawableView;
    private ImageView imageToEdit;
    private ImageView imageOkay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_activity6_helper);

        imageToEdit = findViewById(R.id.image_to_edit);
        drawableView = findViewById(R.id.drawable_view);
        imageOkay = findViewById(R.id.image_okay);

        // Optional: Set up the DrawableView (e.g. set paint color, stroke width, etc.)
        // drawableView.setSomeProperties(...);

        imageOkay.setOnClickListener(v -> saveImageWithDrawings());
    }

    private void saveImageWithDrawings() {
        Bitmap originalBitmap = drawableToBitmap(imageView.getDrawable());
        Bitmap overlayBitmap = drawableView.getBitmap();
        Bitmap combinedBitmap = combineBitmaps(originalBitmap, overlayBitmap);
        Bitmap editedBitmap = combineBitmaps(((BitmapDrawable) imageToEdit.getDrawable()).getBitmap(), drawableView.getBitmap());
        // Now you have the bitmap `editedBitmap` which you can save or use as needed.
        // For demonstration purposes, let's set it to `imageToEdit`:
        imageToEdit.setImageBitmap(editedBitmap);

        // Show a toast for demonstration
        Toast.makeText(this, "Image edited!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Combines the background and the drawing into a single bitmap
     */
    private Bitmap combineBitmaps(Bitmap background, Bitmap foreground) {
        if (background == null) {
            return null;
        }

        int width = background.getWidth();
        int height = background.getHeight();
        Bitmap combinedBitmap = Bitmap.createBitmap(width, height, background.getConfig());

        Canvas canvas = new Canvas(combinedBitmap);
        canvas.drawBitmap(background, 0, 0, null);
        canvas.drawBitmap(foreground, 0, 0, null);
        return combinedBitmap;
    }
}
//    public void saveData() {
//        if (uri == null) {
//            Toast.makeText(UploadActivity6Helper.this, "Select an image to upload", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        collection = collectionName.getText().toString();
//        if (collection.isEmpty()) {
//            collectionName.setError("Enter Qr Code Name");
//            collectionName.requestFocus();
//            return;
//        }
//        if (description.getText().toString().isEmpty()) {
//            pageNum = -1;
//        } else {
//            pageNum = Integer.parseInt(description.getText().toString());
//        }
//
//        timestamp = String.valueOf(Instant.now().getEpochSecond());
//        StorageReference storageReference = FirebaseStorage.getInstance().getReference(uid).child(key)
//                .child(timestamp);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity6Helper.this);
//        builder.setCancelable(false);
//        builder.setView(R.layout.progress_layout);
//        dialog = builder.create();
//        dialog.show();
//
//        storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
//            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
//            uriTask.addOnSuccessListener(uri -> {
//                imageURL = uri.toString();
//                uploadData();
//            }).addOnFailureListener(e -> {
//                dialog.dismiss();
//                Toast.makeText(UploadActivity6Helper.this, "Error uploading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            });
//        }).addOnFailureListener(e -> {
//            Toast.makeText(UploadActivity6Helper.this, "Error uploading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            dialog.dismiss();
//        });
//    }
//
//    public void uploadData() {
//        DataClass2 dataClass = new DataClass2(2, collection, pageNum, imageURL, timestamp);
//        FirebaseDatabase.getInstance().getReference(uid).child(key).child(timestamp)
//                .setValue(dataClass).addOnCompleteListener(task -> {
//                    dialog.dismiss();
//                    if (task.isSuccessful()) {
//                        Toast.makeText(UploadActivity6Helper.this, "Saved", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(UploadActivity6Helper.this, QRActivity.class);
//                        intent.putExtra("uri", uid + "/" + key + "/" + timestamp);
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        Toast.makeText(UploadActivity6Helper.this, "Error saving data: " + task.getException(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
