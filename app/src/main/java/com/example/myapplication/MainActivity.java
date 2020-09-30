package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    TextView t;
    EditText cep_field;
    Boolean CepValido;
    String logradouro;
    String cidade;
    String ipAddress;
    String CEP;

    ServerSocket welcomeSocket;
    DataOutputStream socketOutput;
    DataInputStream socketInput;
    BufferedReader socketEntrada;
    DataInputStream fromClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t = (TextView) findViewById(R.id.enderecoIP);
        cep_field = (EditText) findViewById(R.id.cep_field);
        CepValido = false;
    }
    public void criarPartida(View view){
        if(CepValido == false){
            Toast.makeText(getApplicationContext(),"Digite um CEP valido e verifique",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(),"Esperando outro jogador",Toast.LENGTH_LONG).show();
            Thread k = new Thread(new Runnable() {
                @Override
                public void run() {
                    ligarServidorSocket();
                }
            });
            k.start();
        }
    }
    public void buscarPartida(View view){
        if(CepValido == false){
            Toast.makeText(getApplicationContext(),"Digite um CEP valido e verifique",Toast.LENGTH_LONG).show();
        }else{
            Intent game = new Intent(this,buscarPartida.class);
            Bundle CEPcliente = new Bundle();
            CEPcliente.putString("CEPcliente",CEP);
            game.putExtras(CEPcliente);
            startActivity(game);
        }
    }
    public void onClick(View view){
        ConnectivityManager connManager;
        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = connManager.getAllNetworks();
        for(Network minhaRede:networks){
            NetworkInfo netInfo = connManager.getNetworkInfo(minhaRede);

            if(netInfo.getState().equals(NetworkInfo.State.CONNECTED)){

                NetworkCapabilities propRede = connManager.getNetworkCapabilities(minhaRede);

                if(propRede.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                    WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                    int ip =wifiManager.getConnectionInfo().getIpAddress();
                    ipAddress = String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));

                }
            }
        }
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                testeDeHttp();
            }
        });
        t.start();
    }

    private void testeDeHttp() {
        try {
            CEP = cep_field.getText().toString();
            URL url = new URL("https://viacep.com.br/ws/"+CEP+"/json/");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            /*
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
            writer.close();
            os.close();
            */
            int resposta = conn.getResponseCode();

            if(resposta == HttpsURLConnection.HTTP_OK){
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
                StringBuilder response = new StringBuilder();
                String line =null;
                while ((line = br.readLine())!=null){
                    response.append(line.trim());

                }
                JSONObject resultado = new JSONObject(response.toString());
                logradouro = resultado.getString("logradouro");
                cidade = resultado.getString("localidade");
                t.post(new Runnable() {
                    @Override
                    public void run() {
                        t.setText("Seu ip para criar servidor é esse: "+ipAddress+" :9090");
                    }
                });
                CepValido = true;
            }else{
                t.post(new Runnable() {
                    @Override
                    public void run() {
                        t.setText("Cep invalido");
                    }
                });
                CepValido = false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void ligarServidorSocket() {
        String Cepcliente ="";
        Boolean comecaPartida=false;
        try {
            welcomeSocket = new ServerSocket(9090);
            Socket connectionSocket = welcomeSocket.accept();
            socketOutput = new DataOutputStream(connectionSocket.getOutputStream());
            socketInput = new DataInputStream(connectionSocket.getInputStream());

            socketOutput.writeUTF(CEP);
            Cepcliente = socketInput.readUTF();
            socketOutput.flush();
            welcomeSocket.close();
            Intent t = new Intent(this,game.class);
            Bundle cep = new Bundle();
            cep.putString("Cepcliente",Cepcliente);
            cep.putString("CEP",CEP);
            t.putExtras(cep);
            startActivity(t);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}