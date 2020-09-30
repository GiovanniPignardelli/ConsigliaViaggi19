package it.gpgames.consigliaviaggi19.DAO.firebaseDAO;

import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import it.gpgames.consigliaviaggi19.DAO.DatabaseCallback;
import it.gpgames.consigliaviaggi19.DAO.LoginDAO;
import it.gpgames.consigliaviaggi19.DAO.UserDAO;
import it.gpgames.consigliaviaggi19.DAO.models.users.User;
import it.gpgames.consigliaviaggi19.LoginActivity;
import it.gpgames.consigliaviaggi19.home.MainActivity;

public class LoginFirebaseDAO implements LoginDAO {

    FirebaseAuth fAuth = FirebaseAuth.getInstance();

    @Override
    public void isAuthenticated(DatabaseCallback callback, int callbackCode) {
        if(fAuth == null) callback.callback((String) null,callbackCode);
        else callback.callback(fAuth.getUid(),callbackCode);
    }



    @Override
    public void authentication(String email, String password, final DatabaseCallback callback, final int callbackCode) {
        fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            callback.callback(callbackCode);
                        }
                        else
                        {
                            callback.manageError(task.getException(),callbackCode);
                        }
                    }
                });
    }

    @Override
    public void signOut(DatabaseCallback callback, int callbackCode) {
        FirebaseAuth.getInstance().signOut();
        callback.callback(callbackCode);
    }

    @Override
    public void resetPasswordRequest(final DatabaseCallback callback, final int callbackCode) {
        fAuth.sendPasswordResetEmail(User.getLocalInstance().getEmail())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            callback.callback(callbackCode);
                        }
                        else callback.manageError(task.getException(),callbackCode);
                    }
                });
    }

    @Override
    public void isTokenExpired(final DatabaseCallback callback, final int callbackCode) {
        // Inizializzazione listener sullo stato di autenticazione. In caso di scadenza token, il listener si attiva.
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(FirebaseAuth.getInstance().getCurrentUser()==null)
                {
                    callback.callback(callbackCode);
                }
                else{
                    callback.callback(FirebaseAuth.getInstance().getCurrentUser().getUid(),callbackCode);
                }
            }
        });
    }
}
