package it.gpgames.consigliaviaggi19.DAO.firebaseDAO;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import it.gpgames.consigliaviaggi19.DAO.DatabaseCallback;
import it.gpgames.consigliaviaggi19.DAO.UserDAO;
import it.gpgames.consigliaviaggi19.DAO.models.users.User;
import it.gpgames.consigliaviaggi19.search.place_details.reviews.ReviewsAdapter;

public class UserFirebaseDAO implements UserDAO {

    FirebaseFirestore dbRef = FirebaseFirestore.getInstance();
    StorageReference stoRef = FirebaseStorage.getInstance().getReference();

    public void getUserByID(final String userID, final DatabaseCallback callback, final ReviewsAdapter.ReviewViewHolder holder, final int callbackCode)
    {
        dbRef.collection("userPool").whereEqualTo("userID",userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful() && task.getResult().size()>0)
                {
                    if(task.getResult().size()>1)   Log.d("query", "E' stato trovato più di un utente con id: "+userID+". Verrà restituito il primo.");
                    User toShow=task.getResult().toObjects(User.class).get(0);
                    Log.d("query", "ho trovato un utente");
                    if(holder!=null) callback.callback(toShow, holder, callbackCode);
                    else callback.callback(toShow, callbackCode);
                }
                else  callback.manageError(new Exception("Errore nel caricamento degli utenti."), callbackCode);
            }
        });
    }

    public void getUserByID(String userID, final DatabaseCallback callback, int callbackCode){
        getUserByID(userID,callback,null, callbackCode);
    }

    public void setAvatarByID(final String uid, byte[] data, final DatabaseCallback callback, final int callbackCode){
        final StorageReference userProfileImagesRef = stoRef.child("Users/Avatars/avatar_"+
                uid+".jpg");
        UploadTask uploadTask = userProfileImagesRef.putBytes(data);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    callback.manageError(task.getException(), callbackCode);
                }
                return userProfileImagesRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    final Uri downloadUri = task.getResult();
                    if (downloadUri == null){
                        callback.manageError(new Exception("Data not found by given URL."), callbackCode);
                    }
                    else {
                        dbRef.collection("userPool").whereEqualTo("userID", uid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    dbRef.collection("userPool").document(task.getResult().getDocuments().get(0).getId()).update("avatar",downloadUri.toString());
                                }
                            }
                        });
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(downloadUri)
                                .build();
                        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            callback.callback("La tua foto profilo è stata aggiornata!", callbackCode);
                                            callback.callback(callbackCode);
                                        }
                                        else callback.manageError(new Exception("Qualcosa è andato storto, riprova! :("), callbackCode);
                                    }
                                });
                    }
                }
            }
        });
    }

}
