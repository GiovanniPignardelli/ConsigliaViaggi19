package it.gpgames.consigliaviaggi19.DAO.firebaseDAO;

import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import it.gpgames.consigliaviaggi19.DAO.DatabaseCallback;
import it.gpgames.consigliaviaggi19.DAO.LoginDAO;
import it.gpgames.consigliaviaggi19.DAO.UserDAO;
import it.gpgames.consigliaviaggi19.DAO.models.users.User;
import it.gpgames.consigliaviaggi19.LoginActivity;
import it.gpgames.consigliaviaggi19.home.MainActivity;

/**Implementazione Firebase dell'interfaccia LoginDAO
 * @see it.gpgames.consigliaviaggi19.DAO.LoginDAO*/
public class LoginFirebaseDAO implements LoginDAO {

    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase fDat = FirebaseDatabase.getInstance();
    private FirebaseFirestore fFir = FirebaseFirestore.getInstance();

    @Override
    /**Restituisce al chiamante, tramite callback(String, int), l'uid dell'utente autenticato, se esiste, altrimenti null.*/
    public void isAuthenticated(DatabaseCallback callback, int callbackCode) {
        if(fAuth == null) callback.callback((String) null,callbackCode);
        else callback.callback(fAuth.getUid(),callbackCode);
    }

    @Override
    /**Restituisce al chiamante (DatabaseCallback) l'uid dell'utente nel caso venga correttamente autenticato,
     * altrimenti richiama il metodo manageError con l'eccezione generata.*/
    public void authentication(String email, String password, final DatabaseCallback callback, final int callbackCode) {
        fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            callback.callback(fAuth.getUid(),callbackCode);
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
    /**Inizializzazione di un listener sullo stato di autenticazione. In caso di scadenza token, il listener si attiva.*/
    public void isTokenExpired(final DatabaseCallback callback, final int callbackCode) {
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


    @Override
    public void checkHandshakeRequests(final String userID, final DatabaseCallback callback, final int callbackCode) {
        fDat.getReference().child("backendTokens").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.hasChild(userID)){
                   String token = (String) snapshot.child(userID).child("requestToken").getValue();
                   callback.callback(new HandshakeResponse(token,"0"),callbackCode);
               }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.manageError(error.toException(),callbackCode);
            }
        });
    }

    @Override
    public void sendHandshakeResponse(HandshakeResponse hreq, final DatabaseCallback callback, final int callbackCode) {
        fDat.getReference().child("backendResponseTokens").child(fAuth.getUid()).setValue(hreq);
        callback.callback(callbackCode);
    }
}
