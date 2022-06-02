package com.example.flyttdaxab;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;


public class CameraActivity extends AppCompatActivity {


    private ImageView imageView;
    String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        imageView = findViewById(R.id.imgFoto);
        Button btnCam = findViewById(R.id.btnCam);

        btnCam.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File imageArchive = null;

            try {
                imageArchive = createImage();
            } catch (IOException e){
                Log.e("Error", e.toString());
            }

            if (imageArchive != null){
                Uri fotoUri = FileProvider.getUriForFile(CameraActivity.this
                        , "com.example.flyttdaxab.fileprovider", imageArchive);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
                startActivityForResult(intent, 1);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {

            Bitmap imgBitmap = BitmapFactory.decodeFile(image);
            imageView.setImageBitmap(imgBitmap);
        }
    }

    private File createImage() throws IOException {
        String imageName = "picture_";
        File location = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File takenImage = File.createTempFile(imageName, ".jpg", location);

        image = takenImage.getAbsolutePath();
        return takenImage;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}