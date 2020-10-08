package it.gpgames.consigliaviaggi19.userpanel;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.w3c.dom.Text;

import it.gpgames.consigliaviaggi19.DAO.models.users.User;
import it.gpgames.consigliaviaggi19.R;

/**Activity che permette ad un utente di reimpostare o resettare il proprio nome completo.
 * Al termine dell'activity viene posto come result (con il metodo setResult) le informazioni ottenute.*/
public class ChangeNameActivity extends Activity {

    private Button confirmButton,resetButton;
    private EditText nameText,surnameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);
        confirmButton=findViewById(R.id.nameConfirm);
        resetButton=findViewById(R.id.nameReset);
        nameText=findViewById(R.id.changeNameTxt);
        surnameText=findViewById(R.id.surnameChangeTxt);
        initListeners();
    }

    private void initListeners() {
        confirmButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                String name=nameText.getText().toString();
                if(name.length()<2)
                {
                    nameText.setError("Il nome deve avere almeno 2 caratteri.");
                    return;
                }

                String surname=surnameText.getText().toString();

                if(surname.length()<2)
                {
                    nameText.setError("Il cognome deve avere almeno 2 caratteri.");
                    return;
                }

                Intent returnIntent=new Intent();
                returnIntent.putExtra("change", true);
                returnIntent.putExtra("reset",false);
                returnIntent.putExtra("name",name);
                returnIntent.putExtra("surname",surname);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent=new Intent();
                returnIntent.putExtra("change", true);
                returnIntent.putExtra("reset",true);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
    }

}
