package it.gpgames.consigliaviaggi19.DAO.firebaseDAO;

import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import it.gpgames.consigliaviaggi19.DAO.DatabaseCallback;
import it.gpgames.consigliaviaggi19.DAO.RegisterDAO;
import it.gpgames.consigliaviaggi19.DAO.models.users.User;

/**Implementazione Firebase dell'interfaccia RegisterDAO*/
public class RegisterFirebaseDAO implements RegisterDAO {
    FirebaseAuth fAuth=FirebaseAuth.getInstance();

    @Override
    /**Registra un utente al FirebaseAuth e genera un elemento della collezione userPool che ne detiene le informazioni secondarie.
     * @param user Utente da registrare
     * @param password scelta dall'utente*/
    public void register(final User user, String password, final DatabaseCallback callback, final int callbackCode) {
        fAuth.createUserWithEmailAndPassword(user.getEmail(), password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(user.getDisplayName())
                                    .build();
                            fAuth.getCurrentUser().updateProfile(profileUpdates);

                            user.setUserID(task.getResult().getUser().getUid());

                            FirebaseFirestore.getInstance().collection("userPool").add(user).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if(task.isSuccessful())
                                    {
                                        callback.callback("Ricorda di verificare la tua mail.",callbackCode);
                                        callback.callback(user,callbackCode);
                                    }
                                    else
                                        callback.manageError(task.getException(),callbackCode);
                                }
                            });
                        }
                        else
                            callback.manageError(task.getException(),callbackCode);
                    }
                });

    }
}
