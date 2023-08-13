package com.example.nice_login;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitArray;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class QRCodeGenerator {

    private static String generateSVGString(String text, ErrorCorrectionLevel errorCorrectionLevel, int size) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);
        hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());

        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, size, size, hints);

        StringBuilder sbPath = new StringBuilder();
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BitArray row = new BitArray(width);
        for (int y = 0; y < height; ++y) {
            row = bitMatrix.getRow(y, row);
            for (int x = 0; x < width; ++x) {
                if (row.get(x)) {
                    sbPath.append(" M").append(x).append(",").append(y).append("h1v1h-1z");
                }
            }
        }

        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" viewBox=\"0 0 " + width + " " + height + "\" stroke=\"none\">\n" +
                "<style type=\"text/css\">\n" +
                ".black {fill:#000000;}\n" +
                "</style>\n" +
                "<path class=\"black\"  d=\"" + sbPath + "\"/>\n" +
                "</svg>\n";
    }

//    public static void saveQRCodeToExternalStorage(String text) {
//        try {
//            String svgString = generateSVGString(text);
//
//            // Save the SVG String to a file in external storage
//            File file = new File(Environment.getExternalStorageDirectory(), "qr_code.svg");
//            try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(file.toPath()))) {
//                writer.write(svgString);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        } catch (WriterException e) {
//            e.printStackTrace();
//        }
//    }

    public static void saveVectorQR(String text, ErrorCorrectionLevel errorCorrectionLevel, String name) {
        try {
            String svgString = generateSVGString(text, errorCorrectionLevel, 0);

            // Save the SVG String to a file in the Downloads directory
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), name + ".svg");
            try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(file.toPath()))) {
                writer.write(svgString);
            }
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void savePNGQR(String text, ErrorCorrectionLevel errorCorrectionLevel, int size, String name) throws IOException {

        Bitmap qrBitmap = generateBitmapQRCode(text, errorCorrectionLevel, size);
        FileOutputStream outputStream = null;
        try {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(path, name + ".png");
            outputStream = new FileOutputStream(file);
            // Compress the bitmap, write it to the output stream, and flush the output stream.
            assert qrBitmap != null;
            qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }


    public static Bitmap generateBitmapQRCode(String text, ErrorCorrectionLevel errorCorrectionLevel, int size) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            HashMap<EncodeHintType, ErrorCorrectionLevel> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);

            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size, hints);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bmp;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
}
