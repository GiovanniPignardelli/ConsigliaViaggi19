package it.gpgames.consigliaviaggi19.search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.home.MainActivity;
import it.gpgames.consigliaviaggi19.places.Place;

public class ResultsActivity extends AppCompatActivity {

    ImageView bBack;
    RecyclerView resultQueries;
    FirebaseFirestore dbRef = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        bBack = findViewById(R.id.back2);
        resultQueries = findViewById(R.id.resultQueries);
        initListeners();
        String query = getIntent().getStringExtra("searchString");

    }

    public class QueryResultsAdapter extends FirestoreRecyclerAdapter<Place, QueryResultsAdapter.ResultsViewHolder> {
        public class ResultsViewHolder extends RecyclerView.ViewHolder {

            View mView;

            public ResultsViewHolder(@NonNull View itemView) {
                super(itemView);
                mView = itemView;
            }
        }
    }

    public static String[] parseString(String in, String pivot){
        return in.split(pivot);
    }

    private void initListeners(){
        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResultsActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}