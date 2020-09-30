package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class game extends AppCompatActivity {
    String CEPservidor;
    String CEPcliente;
    String logradouroText,logradouroOponenteText,localidadeText,localidadeOponenteText;
    String ipServidor;
    boolean Soucliente,CepValido,PartidaEncerrada;
    ServerSocket welcomeSocket;
    DataOutputStream socketOutput;
    Button iniciar,buscarCep;
    Socket connectionSocket;
    TextView tentativas,status;
    int NumTentativas=0;

    String subCEPOponente;

    String fim;

    CheckBox logradouroCheck,localidadeCheck;

    TextView logradouro,t,cidade;
    EditText CEPField,Cepbuscarfield;
    private Socket clientSocket;
    private DataInputStream socketInput;
    private String CEPOponente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        logradouro = (TextView) findViewById(R.id.logradouro);
        cidade = (TextView) findViewById(R.id.cidade);
        CEPField = (EditText) findViewById(R.id.cepInicial);
        iniciar = (Button)  findViewById(R.id.iniciar);
        buscarCep = (Button) findViewById(R.id.verificaCep);
        t = (TextView) findViewById(R.id.textoCEP);
        status = (TextView) findViewById(R.id.status);
        Cepbuscarfield = (EditText) findViewById(R.id.cep3digitos);
        Intent info = getIntent();
        Bundle cep = info.getExtras();
        ipServidor = cep.getString("ipServidor");
        Soucliente = cep.getBoolean("Soucliente");

        PartidaEncerrada=false;

        tentativas = (TextView) findViewById(R.id.tentativas);

        logradouroCheck =(CheckBox) findViewById(R.id.logradouroCheck);

        localidadeCheck = (CheckBox) findViewById(R.id.cidadeCheck);

        if(Soucliente){
            CEPcliente = cep.getString("Cepcliente");
            CEPservidor = cep.getString("Cepservidor");
            CEPOponente = CEPservidor;
            subCEPOponente = CEPOponente.substring(3,CEPOponente.length());
            CEPField.setText(subCEPOponente);
            iniciar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clientTCP();
                }
            });
            buscarCep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            testeDeHttp();
                        }
                    });
                    t.start();
                }
            });
            Thread inicio = new Thread(new Runnable() {
                @Override
                public void run() {
                    capturaLogradouroOponente();
                }
            });

            inicio.start();


        }else{
            iniciar.setEnabled(false);
            CEPcliente = cep.getString("Cepcliente");
            CEPservidor = cep.getString("Cepservidor");
            CEPOponente = CEPcliente;
            subCEPOponente = CEPOponente.substring(3,CEPOponente.length());
            CEPField.setText(subCEPOponente);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    ligarServidorSocket();
                }
            });
            thread.start();
            buscarCep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            testeDeHttp();
                        }
                    });
                   t.start();
                }
            });

            Thread inicio = new Thread(new Runnable() {
                @Override
                public void run() {
                    capturaLogradouroOponente();
                }
            });

            inicio.start();


        }

    }
    private void ligarServidorSocket() {
        try {
            welcomeSocket = new ServerSocket(9091);
            connectionSocket = welcomeSocket.accept();
            Log.v("pdm","conexao feita");
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
                    clientSocket = new Socket(ipServidor,9091);

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void testeDeHttp() {
        String CEP="";
        try {
            CEP = Cepbuscarfield.getText().toString()+CEPField.getText().toString();
            URL url = new URL("https://viacep.com.br/ws/"+CEP+"/json/");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            int resposta = conn.getResponseCode();

            if(resposta == HttpsURLConnection.HTTP_OK){
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
                StringBuilder response = new StringBuilder();
                String line =null;
                while ((line = br.readLine())!=null){
                    response.append(line.trim());

                }
                JSONObject resultado = new JSONObject(response.toString());
                logradouroText = resultado.getString("logradouro");
                localidadeText = resultado.getString("localidade");

                logradouro.post(new Runnable() {
                    @Override
                    public void run() {
                        logradouro.setText(logradouroText);
                    }
                });
                cidade.post(new Runnable() {
                    @Override
                    public void run() {
                        cidade.setText(localidadeText);
                    }
                });
                CepValido = true;

                if(logradouroText.equals(logradouroOponenteText)){
                    logradouroCheck.post(new Runnable() {
                        @Override
                        public void run() {
                            logradouroCheck.setChecked(true);
                        }
                    });

                }else{
                    logradouroCheck.post(new Runnable() {
                        @Override
                        public void run() {
                            logradouroCheck.setChecked(false);
                        }
                    });
                }

                if(localidadeText.equals(localidadeOponenteText)){
                    localidadeCheck.post(new Runnable() {
                        @Override
                        public void run() {
                            localidadeCheck.setChecked(true);
                        }
                    });

                }else{
                    localidadeCheck.post(new Runnable() {
                        @Override
                        public void run() {
                            localidadeCheck.setChecked(false);
                        }
                    });
                }

                if(Integer.parseInt(Cepbuscarfield.getText().toString())>Integer.parseInt(CEPOponente.substring(0,2))){
                    status.post(new Runnable() {
                        @Override
                        public void run() {
                            status.setText("MENOR");
                        }
                    });
                }else if(Integer.parseInt(Cepbuscarfield.getText().toString())<Integer.parseInt(CEPOponente.substring(0,2))){
                    status.post(new Runnable() {
                        @Override
                        public void run() {
                            status.setText("MAIOR");
                        }
                    });
                }

                if(localidadeText.equals(localidadeOponenteText)&&logradouroText.equals(logradouroOponenteText)){
                    PartidaEncerrada = true;
                }

                NumTentativas++;
                tentativas.post(new Runnable() {
                    @Override
                    public void run() {
                        tentativas.setText(Integer.toString(NumTentativas));
                    }
                });

                if(PartidaEncerrada){
                    if(Soucliente){
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                fimdepartidaCliente();
                            }
                        });
                        t.start();
                    }else{
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                fimDePartidaServidor();
                            }
                        });
                        t.start();
                    }
                }
            }else{
                t.post(new Runnable() {
                    @Override
                    public void run() {
                        t.setText("Cep invalido, digite um cep valido");
                    }
                });
                CepValido = false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    void EnviarDados(){
        try {
            socketOutput = new DataOutputStream(connectionSocket.getOutputStream());
            socketOutput.writeUTF(logradouroText+","+localidadeText);

            socketOutput.flush();
            socketOutput.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    void ReceberDados(){
        String resultado;
        try{
            socketInput = new DataInputStream(connectionSocket.getInputStream());
            resultado = socketInput.readUTF();
            Log.v("PDM",resultado);
        }catch (Exception e){

        }
    }

    void capturaLogradouroOponente(){

        try{
            
            URL url = new URL("https://viacep.com.br/ws/"+CEPOponente+"/json/");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            int resposta = conn.getResponseCode();

            if(resposta == HttpsURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder response = new StringBuilder();
                String line = null;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }
                JSONObject resultado = new JSONObject(response.toString());
                logradouroOponenteText = resultado.getString("logradouro");
                localidadeOponenteText = resultado.getString("localidade");
                CepValido = true;

                Log.v("pdm",logradouroOponenteText+" "+localidadeOponenteText);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    void fimDePartidaServidor(){
        if(PartidaEncerrada){
            t.post(new Runnable() {
                @Override
                public void run() {
                    t.setText("Você venceu parabens!");
                }
            });

            try {
                socketOutput = new DataOutputStream(connectionSocket.getOutputStream());
                socketOutput.writeUTF("perdeu");
                socketOutput.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{


            try {
                if(connectionSocket!=null){
                    socketInput = new DataInputStream(connectionSocket.getInputStream());
                    fim = socketInput.readUTF();
                    if(fim.equals("perdeu")){
                        t.post(new Runnable() {
                            @Override
                            public void run() {
                                t.setText("Voce Perdeu");
                            }
                        });

                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    void fimdepartidaCliente(){
        if(PartidaEncerrada){
            t.post(new Runnable() {
                @Override
                public void run() {
                    t.setText("Você venceu parabens!");
                }
            });
            try {
                socketOutput = new DataOutputStream(clientSocket.getOutputStream());
                socketOutput.writeUTF("perdeu");
                socketOutput.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }else{
            try {
                if(connectionSocket!=null){
                    socketInput = new DataInputStream(clientSocket.getInputStream());
                    fim = socketInput.readUTF();
                    if(fim.equals("perdeu")){
                        t.post(new Runnable() {
                            @Override
                            public void run() {
                                t.setText("Voce Perdeu");
                            }
                        });


                    }
                    return;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }




}