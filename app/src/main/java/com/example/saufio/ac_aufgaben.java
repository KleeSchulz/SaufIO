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
import android.widget.Toast;

import java.util.List;

public class ac_aufgaben extends AppCompatActivity {

    String menu_loeschen;
    String menu_bearbeiten;

    ListView listView;
    List<String> aufgabe;
    ArrayAdapter<String> arrayAdapter;
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
        aufgabe.add(0,"Aufgabe hinzufügen");
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, aufgabe);
        listView.setAdapter(arrayAdapter);
        registerForContextMenu(listView);
        menu_bearbeiten="bearbeiten";
        menu_loeschen="löschen";

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                int selectedFromList =(int) (listView.getItemIdAtPosition(myItemInt));
                Log.i("DATABASE",String.valueOf(myItemInt));
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        ListView lv = (ListView) listView;
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
        auswahl = (int) lv.getItemIdAtPosition(acmi.position);
        if (auswahl != 0) {
            menu.setHeaderTitle("Datensatz");
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
            //todo: Aufgabe in nächste Activty
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
        aufgabe.add(0,"Aufgabe hinzufügen");
        arrayAdapter.addAll(aufgabe);
        listView.setAdapter(arrayAdapter);
    }
}