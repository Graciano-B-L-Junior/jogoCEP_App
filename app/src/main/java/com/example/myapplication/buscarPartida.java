package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

public class buscarPartida extends AppCompatActivity {
    EditText ip,porta;
    Socket clientSocket;
    DataOutputStream socketOutput;
    DataInputStream socketInput;
    String IP="192.168.100.114";
    String CepCliente,CepServidor;
    Boolean comecaPartida=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_partida);
        ip = (EditText) findViewById(R.id.campoIP);
        porta = (EditText) findViewById(R.id.campoPorta);
        Intent i = getIntent();
        Bundle info = i.getExtras();
        CepCliente = info.getString("CEPcliente");
        CepServidor="";
    }


    public void buscarJogador(View view){
        clientTCP();
    }

    void clientTCP(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    clientSocket = new Socket(ip.getText().toString(),Integer.parseInt(porta.getText().toString()));
                    socketOutput = new DataOutputStream(clientSocket.getOutputStream());
                    socketInput  = new DataInputStream (clientSocket.getInputStream());
                        CepServidor = socketInput.readUTF();
                        socketOutput.writeUTF(CepCliente);
                        socketOutput.flush();
                        socketOutput.close();

                        Intent t = new Intent(getApplicationContext(),game.class);
                        Bundle infos = new Bundle();
                        infos.putString("Cepcliente",CepCliente);
                        infos.putString("Cepservidor",CepServidor);
                        infos.putBoolean("Soucliente",true);
                        infos.putString("ipServidor",ip.getText().toString());
                        t.putExtras(infos);
                        startActivity(t);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }



}