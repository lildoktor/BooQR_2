package com.example.nice_login;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.IOException;
import java.time.Instant;

public class QRActivity extends AppCompatActivity {

    String text;
    ImageView qrImageView;
    Button optionsButton, saveAsPngButton, saveAsSvgButton;
    LinearLayout extraOptions;
    TextInputEditText qrName;
    EditText editWidth, editHeight;
    Spinner errorCorrectionLevelSpinner;
    private boolean isUpdating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qractivity);  // replace with your layout file name

        qrImageView = findViewById(R.id.qr);
        optionsButton = findViewById(R.id.extraOptionsButton);
        extraOptions = findViewById(R.id.extraOptionsLayout);
        saveAsPngButton = findViewById(R.id.saveAsPngButton);
        saveAsSvgButton = findViewById(R.id.saveAsSvgButton);
        editWidth = findViewById(R.id.editWidth);
        editHeight = findViewById(R.id.editHeight);
        errorCorrectionLevelSpinner = findViewById(R.id.errorCorrectionSpinner);
        qrName = findViewById(R.id.qrName);

        qrName.setText(String.valueOf(Instant.now().getEpochSecond()));

        text = "1234124124124/123124124/123123123";

        Bitmap bitmap = QRCodeGenerator.generateBitmapQRCode(text, ErrorCorrectionLevel.L, 1000);
        qrImageView.setImageBitmap(bitmap);
//        QRCodeGenerator.saveQRCodeToExternalStorage(uri);


        editWidth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isUpdating) {
                    isUpdating = true;
                    editHeight.setText(s.toString());
                    isUpdating = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        editHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isUpdating) {
                    isUpdating = true;
                    editWidth.setText(s.toString());
                    isUpdating = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        optionsButton.setOnClickListener(v -> toggleExtraOptions());
        saveAsPngButton.setOnClickListener(v -> {
            if (editHeight.getText().toString().isEmpty()) {
                editHeight.setError("Please enter size");
                editHeight.requestFocus();
                return;
            }
            ErrorCorrectionLevel L;
            switch (errorCorrectionLevelSpinner.getSelectedItemPosition()) {
                case 0:
                    L = ErrorCorrectionLevel.L;
                    break;
                case 1:
                    L = ErrorCorrectionLevel.M;
                    break;
                case 2:
                    L = ErrorCorrectionLevel.Q;
                    break;
                case 3:
                    L = ErrorCorrectionLevel.H;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + errorCorrectionLevelSpinner.getSelectedItemPosition());
            }
            try {
                QRCodeGenerator.savePNGQR(text, L, Integer.parseInt(editHeight.getText().toString()), qrName.getText().toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        saveAsSvgButton.setOnClickListener(v -> {
            ErrorCorrectionLevel L;
            switch (errorCorrectionLevelSpinner.getSelectedItemPosition()) {
                case 0:
                    L = ErrorCorrectionLevel.L;
                    break;
                case 1:
                    L = ErrorCorrectionLevel.M;
                    break;
                case 2:
                    L = ErrorCorrectionLevel.Q;
                    break;
                case 3:
                    L = ErrorCorrectionLevel.H;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + errorCorrectionLevelSpinner.getSelectedItemPosition());
            }
            QRCodeGenerator.saveVectorQR(text, L, qrName.getText().toString());

        });

    }

    private void toggleExtraOptions() {
        if (extraOptions.getVisibility() == View.VISIBLE) {
            extraOptions.setVisibility(View.GONE);
        } else {
            extraOptions.setVisibility(View.VISIBLE);
        }
    }
}