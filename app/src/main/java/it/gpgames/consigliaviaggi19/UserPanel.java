package it.gpgames.consigliaviaggi19;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.Objects;

public class UserPanel extends AppCompatActivity {

    public static final int IMGPRV=1;
    ImageView backButton;
    Button changeProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_panel);

        backButton=findViewById(R.id.back);
        changeProfilePicture=findViewById(R.id.change_image_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        changeProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), IMGPRV);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMGPRV)
            if (resultCode == Activity.RESULT_OK) {
                Uri pic = data.getData();
                updateUserPicture(pic);
            }
    }

    private void updateUserPicture(Uri pic)
    {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //continuare

    }
}
