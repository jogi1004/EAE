package com.example.eaeprojekt;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.example.eaeprojekt.database.DatabaseManager;

import java.util.List;

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
     List<IngredientDTO> ingredients = dbMan.getAllIngredients();
     for (IngredientDTO ingredient : ingredients) {
         Log.d("HSKL", "Name: " + ingredient.getName() + ", Einheit: " + ingredient.getUnit());
     }

    }
}