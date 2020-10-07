package it.gpgames.consigliaviaggi19.search.filters.order;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.search.ResultsActivity;

public class OrderSelectorActivity extends Activity {

    public static final int FLAG_ASC=0;
    public static final int FLAG_DESC=1;

    public static final int FLAG_BEST_MATCH=0;
    public static final int FLAG_RATING=1;
    public static final int FLAG_N_REVIEW=2;
    public static final int FLAG_ALPHABETICAL=3;
    public static final int FLAG_AGE=4;

    private Spinner orderSpinner;
    private RadioButton ascButton,descButton;
    private Button applyButton,cancelButton;

    private OrderCallback orderCallback;

    private int actualOrder=FLAG_BEST_MATCH;
    private int actualDirection=FLAG_DESC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_selector);
        orderSpinner=findViewById(R.id.order_spinner);
        ascButton=findViewById(R.id.order_asc_button);
        descButton=findViewById(R.id.order_desc_button);
        applyButton=findViewById(R.id.order_apply_button);
        cancelButton=findViewById(R.id.order_cancel_button);
        init();
    }

    private void init() {
        orderCallback= ResultsActivity.getLastOrderInstance();
        initListeners();
    }

    private void initListeners() {
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("order__", Integer.valueOf(position).toString());
                actualOrder=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(),"Seleziona un criterio, per favore.", Toast.LENGTH_LONG).show();
            }
        });

        ascButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualDirection=FLAG_ASC;
            }
        });

        descButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualDirection=FLAG_DESC;
            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderCallback.setDirection(actualDirection);
                orderCallback.setOrder(actualOrder);
                orderCallback.refreshOrder();
                finish();
            }
        });
    }

    public interface OrderCallback
    {
        void setOrder(int orderFlag);
        void setDirection(int directionFlag);
        void refreshOrder();
    }
}
