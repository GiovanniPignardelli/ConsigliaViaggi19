package it.gpgames.consigliaviaggi19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class Register extends AppCompatActivity {
    EditText usr,psw,mail;
    Button loginbtn,registerbtn;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        usr=findViewById(R.id.usernametxt);
        psw=findViewById(R.id.passwordtxt);
        mail=findViewById(R.id.emailtxt);
        loginbtn=findViewById(R.id.login);
        registerbtn=findViewById(R.id.signin);
        fAuth=FirebaseAuth.getInstance();

        if(fAuth.getCurrentUser()!=null)
        {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=mail.getText().toString().trim();
                String username=usr.getText().toString().trim();
                String password=psw.getText().toString().trim();

                if(TextUtils.isEmpty(email))
                {
                    mail.setError("Inserire una mail valida.");
                    return;
                }

                if(TextUtils.isEmpty(username))
                {
                    usr.setError("Inserire un username valido.");
                    return;
                }

                if(TextUtils.isEmpty(password) || password.length()<=8)
                {
                    psw.setError("Inserire una password valida. Almeno 8 caratteri.");
                    return;
                }

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(Register.this,"Account Creato", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                        else if(!task.isSuccessful())
                        {
                            Toast.makeText(Register.this,"Errore", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}
