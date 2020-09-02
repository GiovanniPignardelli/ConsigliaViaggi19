package it.gpgames.consigliaviaggi19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    Button login,signin;
    EditText e,p;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login=findViewById(R.id.login);
        signin=findViewById(R.id.signin);
        e=findViewById(R.id.emailtxt);
        p=findViewById(R.id.passwordtxt);
        fAuth=FirebaseAuth.getInstance();

        if(fAuth.getCurrentUser()!=null)
        {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;
                email=e.getText().toString().trim();
                password=p.getText().toString().trim();

                if(TextUtils.isEmpty(email))
                {
                    e.setError("Inserire una mail valida.");
                }

                else if(password.length()<8)
                {
                    p.setError("Ricorda che la password ha almeno 8 caratteri.");
                }
                else
                {
                    fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(Login.this,"Accesso eseguito", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                    }
                                    else
                                    {
                                        Log.d("my tag", "eccomi");
                                        Toast.makeText(Login.this,"Credenziali non valide. Riprovare", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }

            }
        });

        signin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
                finish();
            }
        });

    }
}
