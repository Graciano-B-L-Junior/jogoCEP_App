package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

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
    BufferedReader socketEntrada;
    DataInputStream socketInput;
    String IP="192.168.100.114";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_partida);
        ip = (EditText) findViewById(R.id.campoIP);

        porta = (EditText) findViewById(R.id.campoPorta);
    }


    public void buscarJogador(View view){
        clientTCP();
    }

    void clientTCP(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket cliente = new Socket("192.168.100.114",9090);
                    socketOutput =
                            new DataOutputStream(clientSocket.getOutputStream());
                    socketInput=
                            new DataInputStream (clientSocket.getInputStream());
                    while (socketInput!=null) {
                        socketOutput.writeUTF("teste");
                        socketOutput.flush();
                        Log.v("PDM","mensagem enviada");
                        }

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }



}