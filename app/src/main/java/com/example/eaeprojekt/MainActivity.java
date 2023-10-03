package com.example.eaeprojekt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
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
        dbMan.open();
        String zutatName1 = "Mehl";
        String zutatName2 = "Milch";
        String zutatEinheit1 = "g";
        String zutatEinheit2 = "ml";
        dbMan.insertIngredient(zutatName1, zutatEinheit1);
        dbMan.insertIngredient(zutatName2, zutatEinheit2);
        logAllIngredients(dbMan);


        // Datenbank löschen
         deleteDatabase(DatabaseManager.DATABASE_NAME);

        // Optional: Schließe die Datenbankverbindung
        dbMan.close();
    }

    public void logAllIngredients(DatabaseManager dbMan) {
        // Hole alle Zutaten aus der Datenbank
        Cursor cursor = dbMan.getAllIngredients();

        // Überprüfe, ob der Cursor Daten enthält
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Holen Sie sich die Zutatennamen und Einheiten aus dem Cursor
                int nameIndex = cursor.getColumnIndex(DatabaseManager.COLUMN_INGREDIENT_NAME);
                int unitIndex = cursor.getColumnIndex(DatabaseManager.COLUMN_INGREDIENT_UNIT);

                if (nameIndex != -1 && unitIndex != -1) {
                    String name = cursor.getString(nameIndex);
                    String unit = cursor.getString(unitIndex);

                    // Gib die Zutat als Log-Ausgabe aus
                    Log.d("HSKL", "Name: " + name + ", Einheit: " + unit);
                }
            } while (cursor.moveToNext());

            // Cursor schließen, nachdem er nicht mehr benötigt wird
            cursor.close();
        }
    }
}