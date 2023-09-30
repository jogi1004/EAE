package com.example.eaeprojekt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Beispieldaten hinzufügen
        DatabaseManager dbMan = new DatabaseManager(this);
        Log.d("HSKL", "Holen ging noch");
        dbMan.open();
        String zutatName = "Mehl";
        String zutatEinheit = "g";
        dbMan.insertIngredient(zutatName, zutatEinheit);


        // Datenbank löschen
        // deleteDatabase(DatabaseManager.DATABASE_NAME);

        // Optional: Schließe die Datenbankverbindung
        dbMan.close();
    }
}