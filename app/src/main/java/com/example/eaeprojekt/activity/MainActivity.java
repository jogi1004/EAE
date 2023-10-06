package com.example.eaeprojekt.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.eaeprojekt.DatabaseManager;
import com.example.eaeprojekt.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ConstraintLayout recipes;
    ConstraintLayout shoppingBag;

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

        recipes = findViewById(R.id.recipes);
        recipes.setOnClickListener(this);

        shoppingBag = findViewById(R.id.shoppingBag);
        shoppingBag.setOnClickListener(this);
    }

    public void logAllIngredients(DatabaseManager dbMan) {
     List<IngredientDTO> ingredients = dbMan.getAllIngredients();
     for (IngredientDTO ingredient : ingredients) {
         Log.d("HSKL", "Name: " + ingredient.getName() + ", Einheit: " + ingredient.getUnit());
     }

    }

    @Override
    public void onClick(View view) {
        if(view == recipes){
            Intent recipeIntent = new Intent(this, RecipeActivity.class);
            startActivity(recipeIntent);
        }
        if (view == shoppingBag){

        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
