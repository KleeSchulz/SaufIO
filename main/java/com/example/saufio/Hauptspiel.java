package com.example.saufio;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class Hauptspiel extends AppCompatActivity {

    private VideoView videoView1, videoView2, videoView3, videoView4;
    private MediaController mediaController;
    private final int Code_BB = 1;
    private final int Code_BZ = 2;
    private final int Code_ZB = 3;
    private final int Code_ZZ = 4;
    //ImageView
    ImageView img_muenze;
    //Button
    Button  btn_zufall;
    //ImageButton
    ImageButton btnimg_ton, btnimg_zahl, btnimg_kopf;
    //TextView
    TextView tv_spieler1111, tv_aufgabe, tv_aufgabenzahl;
    //INT
    int zufallszahl, zufallspieler, max_shots;
    //Strings
    String spieler;
    //Array
    ArrayList<String> Spieler;
    ArrayList<String> aufgabelist;
    //Zufallgenerator
    Random r= new java.util.Random();
    //TextToSpeech
    TextToSpeech textspeech;
    //Imports
    DatabaseHandler db = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hauptspiel);
        tv_spieler1111 = (TextView)findViewById(R.id.tv_spieleranzeige);
        tv_aufgabe=(TextView)findViewById(R.id.tv_aufgabe);
        tv_aufgabenzahl=(TextView)findViewById(R.id.tv_aufgabenvorhanden);
        btnimg_ton=(ImageButton)findViewById(R.id.btn_ton);
        btnimg_zahl=(ImageButton)findViewById(R.id.btn_zahl);
        btnimg_kopf=(ImageButton)findViewById(R.id.btn_kopf);
        Spieler = (ArrayList<String>) getIntent().getSerializableExtra("key");
        max_shots = getIntent().getIntExtra("key2",1);
        //Aufgabe vorlesen
        textspeech=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textspeech.setLanguage(Locale.GERMANY);
                }
            }
        });
        aufgabelist= (ArrayList<String>) db.aufgabeKategorie(getIntent().getStringExtra("key3"));
        Spielerauswaehlen();
        tv_aufgabenzahl.setText(getResources().getString(R.string.aufgabevorhanden)+aufgabelist.size());
        setzeTonbild(Allgemein.gebeTon(this));


        initV();
    }

    public void initV() {
        this.videoView1 = findViewById(R.id.videoBB);
        this.videoView2 = findViewById(R.id.videoBZ);
        this.videoView3 = findViewById(R.id.videoZB);
        this.videoView4 = findViewById(R.id.videoZZ);
        videoView1.setVisibility(View.INVISIBLE);
        videoView2.setVisibility(View.INVISIBLE);
        videoView3.setVisibility(View.INVISIBLE);
        videoView4.setVisibility(View.INVISIBLE);
        this.mediaController = new MediaController(this);

    }
    //Zurücktaste
    @Override
    public void onBackPressed(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Todo: Spielerstatistiken und Gesamtstatistik (Kopf und Zahl)
                        Intent intent = new Intent(Hauptspiel.this,MainActivity.class);
                        startActivity(intent);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.alter_spielende_title));
        builder.setMessage(getResources().getString(R.string.alter_spielende_title)).setPositiveButton(getResources().getString(R.string.yes), dialogClickListener)
                .setNegativeButton(getResources().getString(R.string.no), dialogClickListener).show();
    }

    //Buttons
    public void btnimg_zahl(View view){
        kopf_o_zahl(0);
    }
    public void btnimg_kopf(View view){
        kopf_o_zahl(1);
    }
    public void btn_zufall(View view){
       Spielerauswaehlen();
       einblenden_k_o_z();
       //tODO: Farbwert + 1;
    }
    public void btn_spielerloeschen(View view){
        Spieler.add("Spieler hinzufügen");
        CharSequence spieler[] = Spieler.toArray(new CharSequence[Spieler.size()]);;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.alter_spielerfrage));
        builder.setItems(spieler, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Spieler.size()-1==which){
                    Spieler.remove(Spieler.size()-1);
                    Spieleradd();
                }
                else if (Spieler.size()>3){
                    Toast.makeText(getApplicationContext(),Spieler.get(which)+" "+ getResources().getString(R.string.t_loschen),Toast.LENGTH_SHORT).show();
                    Spieler.remove(which);
                    Spieler.remove(Spieler.size()-1);

                } else {
                    wenigSpieler();
                }
                tv_aufgabe.setText(String.valueOf(which));
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Spieler.remove(Spieler.size()-1);
            }
        });
        builder.show();
    }


    public void btn_tonsetzen(View view) {
        setzeTonbild(Allgemein.tonaendern(this));
    }


    //Eigene Methoden
    //Suche einen Zufälligen  Spieler
    public void Spielerauswaehlen(){
        if (aufgabelist.size()==0) {
            AlertDialog.Builder alterDialog = new AlertDialog.Builder(this);
            alterDialog.setTitle("Keine Aufgaben mehr");
            alterDialog.setMessage("Es gibt keine Aufgaben mehr! Ihr habt das Spiel fertig");
            alterDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(Hauptspiel.this,MainActivity.class));
                }
            });
            alterDialog.create();
            alterDialog.show();
        }
        zufallspieler = r.nextInt(Spieler.size());
        spieler =Spieler.get(zufallszahl);
        tv_spieler1111.setText(spieler+" "+getResources().getString(R.string.entscheidung));
        //img_muenze=(ImageView) findViewById(R.id.img_muenze);
        //img_muenze.setVisibility(View.INVISIBLE);
    }

    //ermittle welches Ergebnis
    public void kopf_o_zahl(int wahl){
        //img_muenze=(ImageView) findViewById(R.id.img_muenze);
       // img_muenze.setVisibility(View.INVISIBLE);
        zufallszahl = r.nextInt(2);
        if (zufallszahl == 1) {
            if (zufallszahl == wahl) {
                //Animation Bier auf Bier
                String videoPath1 = "android.resource://" + getPackageName() + "/" +R.raw.bier_auf_bier;
                Uri uri = Uri.parse(videoPath1);
                videoView1.setVideoURI(uri);
                videoView1.setVisibility(View.VISIBLE);
                videoView1.start();
                //(new Handler()).postDelayed(this::yourMethod, 5000); Warten
                tv_spieler1111.setText(getResources().getString(R.string.glueck_kopf));
            } else {
                //Animation Bier auf 69
                String videoPath2 = "android.resource://" + getPackageName() + "/" +R.raw.bier_auf_zahl;
                Uri uri = Uri.parse(videoPath2);
                videoView2.setVideoURI(uri);
                videoView2.setVisibility(View.VISIBLE);
                videoView2.start();
                tv_spieler1111.setText(spieler + ": " + getResources().getString(R.string.pech_kopf));
                String aufgabetxt = Aufgabe.getAufgabe(this, aufgabelist, max_shots);
                tv_aufgabenzahl.setText(getResources().getString(R.string.aufgabevorhanden) + aufgabelist.size());
                tv_aufgabe.setText(aufgabetxt);
                sprachausgabe(aufgabetxt);
            }
           // img_muenze.setImageResource(R.drawable.kopf);
           // img_muenze.setVisibility(View.VISIBLE);
        } else {
            if (zufallszahl == wahl) {
                String videoPath3 = "android.resource://" + getPackageName() + "/" +R.raw.zahl_auf_zahl;
                Uri uri = Uri.parse(videoPath3);
                videoView3.setVideoURI(uri);
                videoView3.setVisibility(View.VISIBLE);
                videoView3.start();
                //Animation 69 auf 69
                tv_spieler1111.setText(getResources().getString(R.string.glueck_zahl));
            } else {
                //Animation 69 auf Bier
                String videoPath4 = "android.resource://" + getPackageName() + "/" +R.raw.zahl_auf_bier;
                Uri uri = Uri.parse(videoPath4);
                videoView4.setVideoURI(uri);
                videoView4.setVisibility(View.VISIBLE);
                videoView4.start();
                tv_spieler1111.setText(spieler+": "+getResources().getString(R.string.pech_zahl));
                String aufgabetxt=Aufgabe.getAufgabe(this,aufgabelist,max_shots);
                tv_aufgabenzahl.setText(getResources().getString(R.string.aufgabevorhanden)+aufgabelist.size());
                tv_aufgabe.setText(aufgabetxt);
                sprachausgabe(aufgabetxt);
            }
           // img_muenze.setImageResource(R.drawable.zahl);
           // img_muenze.setVisibility(View.VISIBLE);
        }
        ausblenden_k_o_z();
    }
    //Ausblenden von Kopf und Zahl Buttons -> Zufallspieler wird angezeigt
    public void ausblenden_k_o_z(){
        btnimg_kopf = (ImageButton)findViewById(R.id.btn_kopf);
        btnimg_zahl = (ImageButton)findViewById(R.id.btn_zahl);
        btn_zufall = (Button)findViewById(R.id.btn_zufall);
        btnimg_zahl.setVisibility(View.INVISIBLE);
        btnimg_kopf.setVisibility(View.INVISIBLE);
        btn_zufall.setVisibility(View.VISIBLE);
    }

    //Einblenden von Kopf und Zahl -> Zufallspieler wird nicht angezeigt
    public void einblenden_k_o_z(){
        btnimg_kopf = (ImageButton)findViewById(R.id.btn_kopf);
        btnimg_zahl = (ImageButton)findViewById(R.id.btn_zahl);
        btn_zufall = (Button)findViewById(R.id.btn_zufall);
        btnimg_zahl.setVisibility(View.VISIBLE);
        btnimg_kopf.setVisibility(View.VISIBLE);
        btn_zufall.setVisibility(View.INVISIBLE);
        tv_aufgabe.setText("");
    }

    //Text to Speech, abbrechen bzw. Ausgabe
    public void sprachausgabe(String aufgabe){
        if (Allgemein.gebeTon(this)) {
            if (textspeech != null) {
                textspeech.stop();
            }
            textspeech.speak(aufgabe, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    //Alert wenn zu Wenig Spieler bei löschen
    public void wenigSpieler(){
        AlertDialog.Builder alterDialog = new AlertDialog.Builder(this);
        alterDialog.setTitle(getResources().getString(R.string.alter_spielerloeschen_title));
        alterDialog.setMessage(getResources().getString(R.string.alter_spieleerloeschen_message));
        alterDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onBackPressed();
            }
        });
        alterDialog.create();
        alterDialog.show();
    }
    //Spieler nachträglich hinzufügen
    public void Spieleradd(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.alter_spielerhinzufügen_title));
        builder.setMessage(getResources().getString(R.string.alter_spielerhinzufügen_message));
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_DATETIME_VARIATION_NORMAL);
        builder.setView(input);
        builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Spieler.add(input.getText().toString());
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.abbrechen), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void setzeTonbild(boolean b){
        if (b){
            btnimg_ton.setImageResource(R.drawable.ic_launcher_foreground);
        }
        else {
            btnimg_ton.setImageResource(R.drawable.ic_launcher_background);
        }

    }
    public void setzKopfundZahl(){
        btnimg_kopf.setImageResource(R.drawable.kopf);
        btnimg_zahl.setImageResource(R.drawable.zahl);
    }

}