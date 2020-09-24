package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class game extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent info = getIntent();
        Bundle cep = info.getExtras();
        String coordenadas = cep.getString("CEP");

        Log.v("PDM",coordenadas);

    }


}