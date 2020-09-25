package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class game extends AppCompatActivity {
    String CEPservidor;
    String CEPcliente;
    boolean Soucliente;
    ServerSocket welcomeSocket;
    DataOutputStream socketOutput;

    TextView logradouro;
    private Socket clientSocket;
    private DataInputStream socketInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        logradouro = (TextView) findViewById(R.id.logradouro);
        Intent info = getIntent();
        Bundle cep = info.getExtras();
        Soucliente = cep.getBoolean("Soucliente");
        if(Soucliente){
            CEPcliente = cep.getString("Cepcliente");
            logradouro.setText("Sou cliente");
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    clientTCP();
                }
            });
        }else{
            logradouro.setText("Sou servidor");
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {

                }
            });
            t.start();
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ligarServidorSocket();
            }
        });
        thread.start();
    }



    private void ligarServidorSocket() {
        try {
            welcomeSocket = new ServerSocket(9091);
            Socket connectionSocket = welcomeSocket.accept();
            while (!welcomeSocket.isClosed()){
                Log.v("pdm","conexao feita");
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    void clientTCP(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    clientSocket = new Socket("192.168.100.114",9091);
                    socketOutput = new DataOutputStream(clientSocket.getOutputStream());
                    socketInput  = new DataInputStream(clientSocket.getInputStream());
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}