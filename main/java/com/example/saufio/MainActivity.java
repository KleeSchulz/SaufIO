package com.example.saufio;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    //EditText
    EditText tp_spieler;
    //TextView
    TextView tv_spieler1;
    //ArrayListe
    ArrayList<String> Spieler = new ArrayList<>();
    //Strings
    String Spielertxt;
    //int
    int numberpicker = 1;
    //erstellen Objekte
    DatabaseHandler db = new DatabaseHandler(this);
    ListView lv_kategorie;

    //Options Menü (3 Punkte)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Menü welcher Button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Einstellungen:
                // do your code
                return true;
            case R.id.Aufgabe:
                startActivity(new Intent(MainActivity.this, ac_aufgaben.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Beenden der App mit Zurücktaste
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.finishAndRemoveTask();
        }
        this.finishAffinity();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tp_spieler = (EditText) findViewById(R.id.tp_spieler);
        tv_spieler1 = (TextView) findViewById(R.id.tv_Spieler);
        lv_kategorie = (ListView) findViewById(R.id.lv_kategorie);
        Spielertxt = getResources().getString(R.string.spielernamen);
        //erstellen Numberpicker
        NumberPicker np = findViewById(R.id.numberPicker);
        np.setMinValue(1);
        np.setMaxValue(5);
        np.setOnValueChangedListener(onValueChangeListener);
        //ListView erstellen
        lv_kategorie.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lv_kategorie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView v = (CheckedTextView) view;
                boolean currentCheck = v.isChecked();
                String user = (String) lv_kategorie.getItemAtPosition(position);

            }
        });

        //Aufgabe hinzufügen bei ersten start
        if (db.getLastID() == 0) {
            Aufgabe.getAddGameaufgabe(this);
        }
        //Kategorien anzeigen lassen
        initListViewKategorie();
    }


    public void btn_zumSpiel(View view) {
        if (Spieler.size() >= 2) {
            Intent i = new Intent(MainActivity.this, Hauptspiel.class);
            i.putExtra("key", Spieler);
            i.putExtra("key2", numberpicker);
            String s = gebeKategorie();//.substring(0,gebeKategorie().length()-1);
            if (s !="0"){
                i.putExtra("key3",s);
                startActivity(i);
            }
            else {
                Allgemein.alertOK(this,getResources().getString(R.string.alter_kategorie_title),getResources().getString(R.string.alter_kategorie_message),getResources().getString(R.string.ok));
            }
        }
        //wenn zu wenig Spieler
        else {
            Allgemein.alertOK(this, getResources().getString(R.string.alter_spieler_title), getResources().getString(R.string.alter_spieler_message), getResources().getString(R.string.ok));
        }
    }

    //Spieler hinzufügen
    public void btn_add(View view) throws InterruptedException {
        if (tp_spieler.getText().length() != 0) {
            String spielerstring = tp_spieler.getText().toString();
            if (Spieler.contains(spielerstring)){
                Toast x = Toast.makeText(getApplicationContext(), tp_spieler.getText().toString() + " konnte nicht hinzufegüt werden! Spielername exestiert bereits", Toast.LENGTH_SHORT);
                x.setGravity(Gravity.BOTTOM, 0, 0);
                x.show();
            } else {
                Spieler.add(spielerstring);
                Spielertxt = Spielertxt + " " + tp_spieler.getText().toString() + ",";
                tv_spieler1.setText(Spielertxt.toString().substring(0, Spielertxt.length() - 1));
                tp_spieler.setText("");
                // Verstecke Keybord
                InputMethodManager keybord = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                keybord.hideSoftInputFromWindow(tp_spieler.getWindowToken(), 0);
                Toast x = Toast.makeText(getApplicationContext(), tp_spieler.getText().toString() + " " + getResources().getString(R.string.t_add), Toast.LENGTH_SHORT);
                x.setGravity(Gravity.BOTTOM, 0, 0);
                x.show();
            }
        } else {
            Allgemein.alertOK(this, getResources().getString(R.string.alter_spielername_title), getResources().getString(R.string.alter_spielername_message), getResources().getString(R.string.ok));
        }
    }

    //Numberpiocker Change
    NumberPicker.OnValueChangeListener onValueChangeListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker numberPicker, int i, int i1) {
            numberpicker = numberPicker.getValue();
        }
    };

    //Listview(Kategorien) initialsieren
    private void initListViewKategorie() {
        String[] users = db.getKategorien().toArray(new String[0]);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, users);
        lv_kategorie.setAdapter(arrayAdapter);
        for (int i = 0; i < users.length; i++) {
            lv_kategorie.setItemChecked(i, false);
        }
    }

    //Übergabe für next Activity
    public String gebeKategorie(){
    SparseBooleanArray sp = lv_kategorie.getCheckedItemPositions();
    StringBuilder sb= new StringBuilder();
        for(int i=0;i<sp.size();i++){
        if(sp.valueAt(i)==true){
            sb.append("'");
            String s = ((CheckedTextView) lv_kategorie.getChildAt(i)).getText().toString();
            sb = sb.append(s+"',");
            }
        }
        if (sb.length()==0){
            return "0";
        }
        else{
            return sb.toString().substring(0, sb.length() - 1);
        }
    }
}

