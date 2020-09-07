package it.gpgames.consigliaviaggi19.userpanel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.home.MainActivity;
import it.gpgames.consigliaviaggi19.home.slider.SliderItemsGetter;

import static it.gpgames.consigliaviaggi19.home.slider.SliderItemsGetter.*;

public class UserPanelActivity extends AppCompatActivity {

    public static final int IMGPRV=1;
    ImageView bBack;
    ImageView iUserPicture;
    TextView tUserDisplayName;
    Button bChangeProfilePicture;
    UserData currentUserData;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();

    @Override
    protected void onResume() {
        super.onResume();
        Bundle userDataBundle = getIntent().getExtras();
        currentUserData = userDataBundle.getParcelable("UserData");
        loadViewWithUserData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_panel);

        bBack=findViewById(R.id.back);
        bChangeProfilePicture=findViewById(R.id.change_image_button);
        iUserPicture = findViewById(R.id.userPicture);
        tUserDisplayName = findViewById(R.id.userDisplayName);

        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bChangeProfilePicture.setOnClickListener(new View.OnClickListener() {
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
                Bitmap picToUpload;
                try {
                    picToUpload = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    uploadUserPicture(picToUpload);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }

    /**Carica il media selezionato dalla galleria (in jpeg) sul FirebaseStorage.*/
    private void uploadUserPicture(Bitmap pic)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pic.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final StorageReference userProfileImagesRef = storageReference.child("Users/Avatars/avatar_"+
                uid+".jpg");
        bChangeProfilePicture.setEnabled(false);
        UploadTask uploadTask = userProfileImagesRef.putBytes(data);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(),"Si prega di riprovare, l'upload non è andato a buon fine!",Toast.LENGTH_SHORT);
                    throw task.getException();
                }
                return userProfileImagesRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    if (downloadUri == null){
                        Toast.makeText(getApplicationContext(), "Si prega di riprovare, l'upload non è andato a buon fine!", Toast.LENGTH_SHORT);
                        return;
                    }
                    else {
                        updateUserPictureAfterUpload(downloadUri);
                    }
                }
            }
        });
    }

    /**Aggiorna l'immagine dell'utente dall'URI del FirebaseStorage dopo averla reimpostata.*/
    private void updateUserPictureAfterUpload(Uri uri){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UserPanelActivity.this, "La tua foto profilo è stata aggiornata!", Toast.LENGTH_SHORT);
                            Intent backToMain = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(backToMain);
                            finish();
                        }
                    }
                });
    }

    /**Inizializza le View con i dati ottenuti dall'oggetto UserData. */
    private void loadViewWithUserData(){
        ExecutorService executor = Executors.newFixedThreadPool(1);
        // Esegue il Runnable necessario per effettuare la NetworkOperation getBitmapFromURL() su Thread secondario.
        executor.execute(new Runnable(){
            @Override
            public void run() {
                Uri picURI = currentUserData.getAvatar();
                boolean isUserPicLoadable = false;
                Bitmap picToSet = null;
                if(picURI !=null) {
                    picToSet = getBitmapFromURL(picURI.toString());
                    isUserPicLoadable = true;
                }
                final boolean finalIsUserPicLoadable = isUserPicLoadable;
                final Bitmap finalPicToSet = picToSet;
                // Effettuare le modifiche sull'UIThread.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(finalIsUserPicLoadable) iUserPicture.setImageBitmap(finalPicToSet);
                        tUserDisplayName.setText(currentUserData.getDisplayName());
                    }
                });

            }
        });
    }
}
