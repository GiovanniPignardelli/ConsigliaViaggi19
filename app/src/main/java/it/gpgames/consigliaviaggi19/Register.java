package it.gpgames.consigliaviaggi19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

import it.gpgames.consigliaviaggi19.home.MainActivity;

public class Register extends AppCompatActivity {
    EditText eUser,ePsw,eEmail;
    Button bLogin,bRegister;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        eUser=findViewById(R.id.usernametxt);
        ePsw=findViewById(R.id.passwordtxt);
        eEmail=findViewById(R.id.emailtxt);
        bLogin=findViewById(R.id.login);
        bRegister=findViewById(R.id.signin);
        fAuth=FirebaseAuth.getInstance();

        if(fAuth.getCurrentUser()!=null)
        {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=eEmail.getText().toString().trim();
                final String username=eUser.getText().toString().trim();
                String password=ePsw.getText().toString().trim();

                if(TextUtils.isEmpty(email))
                {
                    eEmail.setError("Inserire una mail valida.");
                    return;
                }

                if(TextUtils.isEmpty(username))
                {
                    eUser.setError("Inserire un username valido.");
                    return;
                }

                if(TextUtils.isEmpty(password) || password.length()<8)
                {
                    ePsw.setError("Inserire una password valida. Almeno 8 caratteri.");
                    return;
                }

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(Register.this,"Account Creato", Toast.LENGTH_LONG).show();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .build();
                            fAuth.getCurrentUser().updateProfile(profileUpdates);

                            startActivity(new Intent(Register.this, MainActivity.class));
                            finish();
                        }
                        else if(!task.isSuccessful())
                        {
                            Toast.makeText(Register.this,task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        bLogin.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
                finish();
            }
        });
    }
}
