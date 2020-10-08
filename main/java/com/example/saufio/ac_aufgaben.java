package com.example.saufio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

public class ac_aufgaben extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String menu_loeschen;
    String menu_bearbeiten;
    Spinner spinner;
    ListView listView;
    List<String> aufgabe;
    ArrayAdapter<String> arrayAdapter;
    List<String> kategorien;
    int auswahl;
    DatabaseHandler db = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ac_aufgaben);
        //Listview adapter
        listView = (ListView) findViewById(R.id.listview);
        DatabaseHandler db = new DatabaseHandler(this);
        aufgabe = db.getAllAufgabeString();
        for (int i=0;i<aufgabe.size();i++){
            String aufgabetxt = aufgabe.get(i);
            aufgabetxt=aufgabetxt.replace("$",String.valueOf(new Random().nextInt(5)+1));
            aufgabe.set(i, aufgabetxt);
        }
        aufgabe.add(0,getResources().getString(R.string.Aufgabeadd));
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, aufgabe);
        listView.setAdapter(arrayAdapter);
        registerForContextMenu(listView);
        menu_bearbeiten=getResources().getString(R.string.change);
        menu_loeschen=getResources().getString(R.string.delete);
        spinner=(Spinner)findViewById(R.id.spinner_filter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                int selectedFromList =(int) (listView.getItemIdAtPosition(myItemInt));
            }
        });
        //Spinner
        kategorien=db.getKategorien();
        kategorien.add(0,"Alle Aufgaben");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, kategorien);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        ListView lv = (ListView) listView;
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
        auswahl = (int) lv.getItemIdAtPosition(acmi.position);
        if (auswahl != 0) {
            menu.setHeaderTitle(getResources().getString(R.string.record));
            menu.add(0, v.getId(), 0, menu_bearbeiten);
            menu.add(0, v.getId(), 0, menu_loeschen);
        } else {
            Intent intent = new Intent(ac_aufgaben.this,AC_bearbeiten.class);
            startActivity(intent);
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Aufgabe a= db.sucheUeberTxt(aufgabe.get(auswahl));
        if (menu_loeschen==item.getTitle()) {
            if (a.getAenderung()=="N"){

            } else {
                db.deleteAufgabe(a);
                listviewRefresh();
            }
        } else if (menu_bearbeiten==item.getTitle()){
            Intent intent = new Intent(ac_aufgaben.this,AC_bearbeiten.class);
            intent.putExtra("IDAufgabe",a.getId());
            startActivity(intent);
        }
        return true;
    }

    public void listviewRefresh(){
        aufgabe.clear();
        aufgabe=db.getAllAufgabeString();
        arrayAdapter.clear();
        aufgabe.add(0,getResources().getString(R.string.Aufgabeadd));
        arrayAdapter.addAll(aufgabe);
        listView.setAdapter(arrayAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (i == 0) {
            aufgabe.clear();
            aufgabe=db.getAllAufgabeString();
            arrayAdapter.clear();
            arrayAdapter.addAll(aufgabe);
            listView.setAdapter(arrayAdapter);
        }
        else {
            setzeFilter(kategorien.get(i));
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void setzeFilter(String kategorie){
        aufgabe.clear();
        aufgabe=db.aufgabeKategorie("'"+kategorie+"'");
        arrayAdapter.clear();
        arrayAdapter.addAll(aufgabe);
        listView.setAdapter(arrayAdapter);
    }

}