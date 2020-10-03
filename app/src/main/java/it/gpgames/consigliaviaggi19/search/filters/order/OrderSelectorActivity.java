package it.gpgames.consigliaviaggi19.search.filters.order;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;

import it.gpgames.consigliaviaggi19.R;

public class OrderSelectorActivity extends AppCompatActivity {

    public static final int FLAG_ASC=0;
    public static final int FLAG_DESC=1;

    private ArrayList<String> orderTags;

    private Spinner orderSpinner;
    private RadioButton ascButton,descButton;
    private Button applyButton,cancelButton;

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
        orderTags=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.order_tags)));
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


    }
}
