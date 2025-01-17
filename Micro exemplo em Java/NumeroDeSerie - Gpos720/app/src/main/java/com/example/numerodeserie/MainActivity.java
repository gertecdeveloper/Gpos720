package com.example.numerodeserie;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import br.com.gertec.gedi.GEDI;
import br.com.gertec.gedi.enums.GEDI_INFO_e_ControlNumber;
import br.com.gertec.gedi.exceptions.GediException;
import br.com.gertec.gedi.interfaces.IGEDI;

public class MainActivity extends AppCompatActivity {

    private IGEDI mGedi;
    private TextView textNumberSerie;
    private Button btnSN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(new Runnable() {
            @Override
            public void run() {
                mGedi = GEDI.getInstance(MainActivity.this);
            }
        }).start();

        btnSN = findViewById(R.id.btnSN);
        textNumberSerie = findViewById(R.id.textNumberSerie);

        btnSN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String SN = getNS();
                textNumberSerie.setText(SN);
                Log.i("TAG", "Get Serial Number: " + SN);
            }
        });
    }
    private String getNS() {
        try {
            return mGedi.getINFO().ControlNumberGet(GEDI_INFO_e_ControlNumber.SN);
        } catch (GediException e) {
            return "Serial Number not found.";
        }
    }

}