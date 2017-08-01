package com.example.runehou.alculator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/*
    Lav liste med øl/cider, så man kan slå dem op og se procenterne
    Forskellige currencies
    Hvor meget er en genstand i dit land
     - DK: 12 g.
     - USA: 14 g.
     - UK: 8 g.
 */
public class Side1 extends AppCompatActivity implements View.OnClickListener {
    EditText vol, alc, price;
    TextView units, unitPrice;
    Button calcBtn;

    double unitResult;
    final double MASSEFYLDE = 0.7873;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.side1);

        alc = (EditText) findViewById(R.id.alc);
        vol = (EditText) findViewById(R.id.vol);
        units = (TextView) findViewById(R.id.units);
        unitPrice = (TextView) findViewById(R.id.unitprice);
        calcBtn = (Button) findViewById(R.id.calculateBtn);
        calcBtn.setOnClickListener(this);



    }


    @Override
    public void onClick(View v) {
        if (v == calcBtn) {
            double volInput = Double.parseDouble(vol.getText().toString()), alcInput = Double.parseDouble(alc.getText().toString());
            unitResult = volInput * alcInput * MASSEFYLDE / 120;


            unitPrice.setText("Antal genstande: " +  Math.round(unitResult * 10.0) / 10.0);
        }
    }
}
