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
}
