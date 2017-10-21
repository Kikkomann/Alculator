package com.example.runehou.alculator;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

/*
    Lav liste med øl/cider, så man kan slå dem op og se procenterne
    Forskellige currencies
    Hvor meget er en genstand i dit land
     - DK: 12 g.
     - USA: 14 g.
     - UK: 8 g.
     - Aus: 10 g.
 */
public class Side1 extends AppCompatActivity implements View.OnClickListener {
    private String accessKey = "1dd1245b6894d851e4e6e339855f6cdf";
    EditText vol, alc, price, currencyInput;
    TextView units, unitPrice, currencyOutput;
    Button calcBtn, convertBtn;
    Spinner currenciesFrom, currenciesTo;

    ProgressDialog prgDialog;

    double unitResult;
    final double MASSEFYLDE = 0.7873;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.side1);

        alc = (EditText) findViewById(R.id.alc);
        vol = (EditText) findViewById(R.id.vol);
        price = (EditText) findViewById(R.id.price);
        currencyInput = (EditText) findViewById(R.id.currencyInput);

        units = (TextView) findViewById(R.id.units);
        unitPrice = (TextView) findViewById(R.id.unitprice);
        currencyOutput = (TextView) findViewById(R.id.currencyOutput);

        calcBtn = (Button) findViewById(R.id.calculateBtn);
        calcBtn.setOnClickListener(this);
        convertBtn = (Button) findViewById(R.id.convertBtn);
        convertBtn.setOnClickListener(this);

        currenciesFrom = (Spinner) findViewById(R.id.currenciesFrom);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.currencies_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currenciesFrom.setAdapter(adapter);
        currenciesTo = (Spinner) findViewById(R.id.currenciesTo);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currenciesTo.setAdapter(adapter);

        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);
    }


    @Override
    public void onClick(View v) {
        if (v == calcBtn) {
            unitPrice.setText("");
            units.setText("");
            if (vol.getText().toString().trim().isEmpty() || alc.getText().toString().trim().isEmpty() || price.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please enter values in all three fields", Toast.LENGTH_SHORT).show();
            } else {
                double volInput = Double.parseDouble(vol.getText().toString()), alcInput = Double.parseDouble(alc.getText().toString());
                unitResult = volInput * alcInput * MASSEFYLDE / 120;
                if (alcInput == 0) {
                    unitPrice.setText("Price pr. cl.: " + Math.round(volInput / Double.parseDouble(price.getText().toString()) * 100) / 100);
                } else {
                    double pricePrUnit = Math.round(Double.parseDouble(price.getText().toString()) / (Math.round(unitResult * 10.0) / 10.0) * 100);
                    double roundedPrice = pricePrUnit / 100;

                    units.setText("Antal genstande: " +  Math.round(unitResult * 10.0) / 10.0);
                    unitPrice.setText("AUD pr. genstand: $" + roundedPrice + "\nDKK pr. genstand: " + roundedPrice * 5 + " kr.");
                }

            }
        } else if (v == convertBtn) {
//            String value = currencyOutput.getText().toString();
//            String currencyFrom = currenciesFrom.getSelectedItem().toString();
//            String currencyTo = currenciesTo.getSelectedItem().toString();
//            String param = currencyFrom + "_" + currencyTo;
            RequestParams params = new RequestParams();
            params.put("q", currenciesFrom.getSelectedItem().toString() + "_" + currenciesTo.getSelectedItem().toString());
            params.put("compact", "ultra");
            convertREST(params);
        }
    }

    public void convertREST(RequestParams params) {
        prgDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://free.currencyconverterapi.com/api/v4/convert", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                prgDialog.hide();
                try {
                    JSONObject obj = new JSONObject(response);
                    int value = Integer.parseInt(currencyInput.getText().toString()) * Integer.parseInt(obj.getString("val"));
                    currencyOutput.setText(value);
                } catch (JSONException e) {
                    Toast.makeText(Side1.this, "JSON Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                prgDialog.hide();
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Ressource not found...", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Server error...", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(getApplicationContext(), "Unexpected error...", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }

            }

        });

    }
}
